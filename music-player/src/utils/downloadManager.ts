import { DownloadTask, DownloadSettings, defaultDownloadSettings } from '@/types/download'
import { SearchResult } from '@/types/download'

type DownloadCallback = (task: DownloadTask) => void

const DEFAULT_DOWNLOAD_PATH = 'C:\\Users\\Public\\Music\\Downloads'

class DownloadManager {
  private tasks: Map<string, DownloadTask> = new Map()
  private activeDownloads: Set<string> = new Set()
  private settings: DownloadSettings
  private callbacks: Set<DownloadCallback> = new Set()
  private abortControllers: Map<string, AbortController> = new Map()
  private initialized: boolean = false

  constructor() {
    this.settings = { ...defaultDownloadSettings, downloadPath: DEFAULT_DOWNLOAD_PATH }
    this.loadSettings()
  }

  private loadSettings() {
    try {
      const saved = localStorage.getItem('download-settings')
      if (saved) {
        const parsed = JSON.parse(saved)
        this.settings = { 
          ...defaultDownloadSettings, 
          downloadPath: DEFAULT_DOWNLOAD_PATH,
          ...parsed 
        }
      }
    } catch {
      console.error('Failed to load download settings')
    }
  }

  private saveSettings() {
    try {
      localStorage.setItem('download-settings', JSON.stringify(this.settings))
    } catch {
      console.error('Failed to save download settings')
    }
  }

  private saveTasks() {
    try {
      const tasksArray = Array.from(this.tasks.values()).map(task => ({
        ...task,
        startTime: task.startTime?.toISOString(),
        endTime: task.endTime?.toISOString(),
      }))
      localStorage.setItem('download-tasks', JSON.stringify(tasksArray))
    } catch {
      console.error('Failed to save download tasks')
    }
  }

  private loadTasks() {
    try {
      const saved = localStorage.getItem('download-tasks')
      if (saved) {
        const tasksArray = JSON.parse(saved)
        tasksArray.forEach((taskData: any) => {
          if (taskData.status !== 'completed') {
            taskData.status = 'pending'
            taskData.progress = 0
            taskData.downloadedBytes = 0
          }
          taskData.startTime = taskData.startTime ? new Date(taskData.startTime) : undefined
          taskData.endTime = taskData.endTime ? new Date(taskData.endTime) : undefined
          this.tasks.set(taskData.id, taskData)
        })
        this.notifyAllCallbacks()
      }
    } catch {
      console.error('Failed to load download tasks')
    }
  }

  getSettings(): DownloadSettings {
    return { ...this.settings }
  }

  updateSettings(updates: Partial<DownloadSettings>) {
    this.settings = { ...this.settings, ...updates }
    this.saveSettings()
  }

  getAllTasks(): DownloadTask[] {
    return Array.from(this.tasks.values())
      .sort((a, b) => {
        const timeA = a.startTime?.getTime() || 0
        const timeB = b.startTime?.getTime() || 0
        return timeB - timeA
      })
  }

  getTask(id: string): DownloadTask | undefined {
    return this.tasks.get(id)
  }

  addTask(searchResult: SearchResult, savePath?: string): DownloadTask {
    const id = `download-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
    
    const finalSavePath = savePath || this.settings.downloadPath || DEFAULT_DOWNLOAD_PATH
    
    const task: DownloadTask = {
      id,
      searchResult,
      status: 'pending',
      progress: 0,
      downloadedBytes: 0,
      totalBytes: 0,
      speed: 0,
      savePath: finalSavePath,
    }

    this.tasks.set(id, task)
    this.saveTasks()
    this.notifyCallbacks(task)

    if (this.settings.autoStart && this.activeDownloads.size < this.settings.maxConcurrent) {
      setTimeout(() => this.startDownload(id), 100)
    }

    return task
  }

  async startDownload(id: string): Promise<void> {
    const task = this.tasks.get(id)
    if (!task) {
      console.error('Task not found:', id)
      return
    }
    
    if (task.status === 'downloading' || task.status === 'completed') {
      return
    }

    if (this.activeDownloads.size >= this.settings.maxConcurrent) {
      task.status = 'pending'
      this.notifyCallbacks(task)
      return
    }

    if (!task.searchResult.downloadUrl) {
      task.status = 'error'
      task.error = '下载地址无效'
      this.notifyCallbacks(task)
      return
    }

    task.status = 'downloading'
    task.startTime = new Date()
    task.error = undefined
    this.activeDownloads.add(id)
    this.notifyCallbacks(task)

    const controller = new AbortController()
    this.abortControllers.set(id, controller)

    try {
      const response = await fetch(task.searchResult.downloadUrl, {
        signal: controller.signal,
        mode: 'cors',
        headers: {
          'Accept': 'audio/mpeg,audio/*,*/*',
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP错误: ${response.status}`)
      }

      const contentLength = response.headers.get('content-length')
      task.totalBytes = contentLength ? parseInt(contentLength, 10) : 0

      const reader = response.body?.getReader()
      if (!reader) {
        throw new Error('无法读取响应数据')
      }

      const chunks: ArrayBuffer[] = []
      let lastTime = Date.now()
      let lastBytes = 0

      while (true) {
        const { done, value } = await reader.read()
        
        if (done) break

        chunks.push(value.buffer)
        task.downloadedBytes += value.length

        const now = Date.now()
        const timeDiff = (now - lastTime) / 1000
        
        if (timeDiff >= 0.5) {
          task.speed = (task.downloadedBytes - lastBytes) / timeDiff
          lastTime = now
          lastBytes = task.downloadedBytes
        }

        if (task.totalBytes > 0) {
          task.progress = (task.downloadedBytes / task.totalBytes) * 100
        } else {
          task.progress = Math.min(99, task.downloadedBytes / 100000)
        }

        this.notifyCallbacks(task)
      }

      const blob = new Blob(chunks, { type: 'audio/mpeg' })
      const arrayBuffer = await blob.arrayBuffer()

      const fileName = this.getSafeFileName(task.searchResult)
      
      if (window.electronAPI && window.electronAPI.saveFile) {
        const filePath = `${task.savePath}\\${fileName}`
        const result = await window.electronAPI.saveFile(filePath, arrayBuffer)
        
        if (result.success) {
          task.savePath = result.path || filePath
        } else {
          throw new Error(result.error || '保存文件失败')
        }
      } else {
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = fileName
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(url)
      }

      task.status = 'completed'
      task.progress = 100
      task.endTime = new Date()
      task.speed = 0

    } catch (error: any) {
      console.error('Download error:', error)
      if (error.name === 'AbortError') {
        task.status = 'paused'
      } else {
        task.status = 'error'
        task.error = error.message || '下载失败'
      }
    } finally {
      this.activeDownloads.delete(id)
      this.abortControllers.delete(id)
      this.saveTasks()
      this.notifyCallbacks(task)
      
      this.processQueue()
    }
  }

  private getSafeFileName(result: SearchResult): string {
    const sanitize = (str: string) => str.replace(/[<>:"/\\|?*]/g, '_').trim()
    return `${sanitize(result.artist)} - ${sanitize(result.title)}.mp3`
  }

  pauseDownload(id: string): void {
    const task = this.tasks.get(id)
    if (task && task.status === 'downloading') {
      const controller = this.abortControllers.get(id)
      if (controller) {
        controller.abort()
      }
    }
  }

  resumeDownload(id: string): void {
    const task = this.tasks.get(id)
    if (task && task.status === 'paused') {
      task.downloadedBytes = 0
      task.progress = 0
      this.startDownload(id)
    }
  }

  cancelDownload(id: string): void {
    const task = this.tasks.get(id)
    if (task) {
      if (task.status === 'downloading') {
        const controller = this.abortControllers.get(id)
        if (controller) {
          controller.abort()
        }
      }
      this.tasks.delete(id)
      this.saveTasks()
      this.notifyAllCallbacks()
    }
  }

  retryDownload(id: string): void {
    const task = this.tasks.get(id)
    if (task && (task.status === 'error' || task.status === 'paused')) {
      task.status = 'pending'
      task.progress = 0
      task.downloadedBytes = 0
      task.error = undefined
      this.saveTasks()
      this.notifyCallbacks(task)
      this.startDownload(id)
    }
  }

  clearCompleted(): void {
    const completedIds: string[] = []
    this.tasks.forEach((task, id) => {
      if (task.status === 'completed') {
        completedIds.push(id)
      }
    })
    completedIds.forEach(id => this.tasks.delete(id))
    this.saveTasks()
    this.notifyAllCallbacks()
  }

  clearAll(): void {
    this.tasks.forEach((task, id) => {
      if (task.status === 'downloading') {
        const controller = this.abortControllers.get(id)
        if (controller) {
          controller.abort()
        }
      }
    })
    this.tasks.clear()
    this.saveTasks()
    this.notifyAllCallbacks()
  }

  private processQueue(): void {
    if (this.activeDownloads.size >= this.settings.maxConcurrent) {
      return
    }

    const pendingTasks = Array.from(this.tasks.values())
      .filter(task => task.status === 'pending')
      .sort((a, b) => (a.startTime?.getTime() || 0) - (b.startTime?.getTime() || 0))

    const slotsAvailable = this.settings.maxConcurrent - this.activeDownloads.size
    
    for (let i = 0; i < Math.min(slotsAvailable, pendingTasks.length); i++) {
      this.startDownload(pendingTasks[i].id)
    }
  }

  onTaskUpdate(callback: DownloadCallback): () => void {
    this.callbacks.add(callback)
    return () => {
      this.callbacks.delete(callback)
    }
  }

  private notifyCallbacks(task: DownloadTask): void {
    this.callbacks.forEach(callback => {
      try {
        callback(task)
      } catch (err) {
        console.error('Callback error:', err)
      }
    })
  }

  private notifyAllCallbacks(): void {
    const tasks = this.getAllTasks()
    this.callbacks.forEach(callback => {
      try {
        tasks.forEach(task => callback(task))
      } catch (err) {
        console.error('Callback error:', err)
      }
    })
  }

  init() {
    if (this.initialized) return
    this.initialized = true
    this.loadTasks()
    this.processQueue()
  }
}

export const downloadManager = new DownloadManager()
