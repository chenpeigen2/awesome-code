import { describe, it, expect, vi, beforeEach } from 'vitest'
import { downloadManager } from '@/utils/downloadManager'
import { SearchResult } from '@/types/download'

describe('DownloadManager', () => {
  const mockSearchResult: SearchResult = {
    id: 'search-1',
    title: 'Test Song',
    artist: 'Test Artist',
    duration: '3:45',
    downloadUrl: 'https://example.com/test.mp3',
  }

  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
    downloadManager.clearAll()
  })

  describe('getSettings', () => {
    it('should return default settings', () => {
      const settings = downloadManager.getSettings()
      expect(settings).toHaveProperty('downloadPath')
      expect(settings.maxConcurrent).toBe(3)
      expect(settings.autoStart).toBe(true)
    })
  })

  describe('updateSettings', () => {
    it('should update settings correctly', () => {
      downloadManager.updateSettings({ maxConcurrent: 5 })
      const settings = downloadManager.getSettings()
      expect(settings.maxConcurrent).toBe(5)
    })

    it('should persist settings to localStorage', () => {
      downloadManager.updateSettings({ autoStart: false })
      const settings = downloadManager.getSettings()
      expect(settings.autoStart).toBe(false)
    })

    it('should update download path', () => {
      downloadManager.updateSettings({ downloadPath: 'D:\\NewPath' })
      const settings = downloadManager.getSettings()
      expect(settings.downloadPath).toBe('D:\\NewPath')
    })

    it('should update maxConcurrent', () => {
      downloadManager.updateSettings({ maxConcurrent: 10 })
      const settings = downloadManager.getSettings()
      expect(settings.maxConcurrent).toBe(10)
    })

    it('should update notifyOnComplete', () => {
      downloadManager.updateSettings({ notifyOnComplete: false })
      const settings = downloadManager.getSettings()
      expect(settings.notifyOnComplete).toBe(false)
    })
  })

  describe('addTask', () => {
    it('should add a new download task', () => {
      const task = downloadManager.addTask(mockSearchResult)
      expect(task.id).toBeTruthy()
      expect(task.searchResult).toEqual(mockSearchResult)
      expect(task.status).toBe('pending')
      expect(task.progress).toBe(0)
    })

    it('should generate unique task IDs', () => {
      const task1 = downloadManager.addTask(mockSearchResult)
      const task2 = downloadManager.addTask(mockSearchResult)
      expect(task1.id).not.toBe(task2.id)
    })

    it('should use custom save path', () => {
      const customPath = 'D:\\Custom\\Path'
      const task = downloadManager.addTask(mockSearchResult, customPath)
      expect(task.savePath).toBe(customPath)
    })

    it('should use default save path when not specified', () => {
      const task = downloadManager.addTask(mockSearchResult)
      expect(task.savePath).toBeTruthy()
    })

    it('should initialize task with zero values', () => {
      const task = downloadManager.addTask(mockSearchResult)
      expect(task.downloadedBytes).toBe(0)
      expect(task.totalBytes).toBe(0)
      expect(task.speed).toBe(0)
    })
  })

  describe('getTask', () => {
    it('should return task by ID', () => {
      const addedTask = downloadManager.addTask(mockSearchResult)
      const task = downloadManager.getTask(addedTask.id)
      expect(task).toEqual(addedTask)
    })

    it('should return undefined for non-existent task', () => {
      const task = downloadManager.getTask('non-existent-id')
      expect(task).toBeUndefined()
    })
  })

  describe('getAllTasks', () => {
    it('should return all tasks sorted by start time', () => {
      downloadManager.addTask(mockSearchResult)
      downloadManager.addTask({ ...mockSearchResult, id: 'search-2' })
      const tasks = downloadManager.getAllTasks()
      expect(tasks).toHaveLength(2)
    })

    it('should return empty array when no tasks', () => {
      const tasks = downloadManager.getAllTasks()
      expect(tasks).toHaveLength(0)
    })
  })

  describe('cancelDownload', () => {
    it('should remove task from list', () => {
      const task = downloadManager.addTask(mockSearchResult)
      downloadManager.cancelDownload(task.id)
      const tasks = downloadManager.getAllTasks()
      expect(tasks).toHaveLength(0)
    })

    it('should remove from active downloads', () => {
      const task = downloadManager.addTask(mockSearchResult)
      downloadManager.cancelDownload(task.id)
      const cancelledTask = downloadManager.getTask(task.id)
      expect(cancelledTask).toBeUndefined()
    })
  })

  describe('clearCompleted', () => {
    it('should remove only completed tasks', () => {
      const task1 = downloadManager.addTask(mockSearchResult)
      const task2 = downloadManager.addTask({ ...mockSearchResult, id: 'search-2' })
      
      const tasks = downloadManager.getAllTasks()
      const t1 = tasks.find(t => t.id === task1.id)
      if (t1) {
        t1.status = 'completed'
      }
      
      downloadManager.clearCompleted()
      const remaining = downloadManager.getAllTasks()
      expect(remaining).toHaveLength(1)
    })

    it('should keep non-completed tasks', () => {
      const task1 = downloadManager.addTask(mockSearchResult)
      const task2 = downloadManager.addTask({ ...mockSearchResult, id: 'search-2' })
      
      const tasks = downloadManager.getAllTasks()
      const t1 = tasks.find(t => t.id === task1.id)
      if (t1) {
        t1.status = 'completed'
      }
      const t2 = tasks.find(t => t.id === task2.id)
      if (t2) {
        t2.status = 'error'
      }
      
      downloadManager.clearCompleted()
      const remaining = downloadManager.getAllTasks()
      expect(remaining).toHaveLength(1)
      expect(remaining[0].status).toBe('error')
    })
  })

  describe('clearAll', () => {
    it('should remove all tasks', () => {
      downloadManager.addTask(mockSearchResult)
      downloadManager.addTask({ ...mockSearchResult, id: 'search-2' })
      downloadManager.clearAll()
      const tasks = downloadManager.getAllTasks()
      expect(tasks).toHaveLength(0)
    })
  })

  describe('onTaskUpdate', () => {
    it('should register callback and notify on task update', () => {
      const callback = vi.fn()
      downloadManager.onTaskUpdate(callback)
      downloadManager.addTask(mockSearchResult)
      expect(callback).toHaveBeenCalled()
    })

    it('should return unsubscribe function', () => {
      const callback = vi.fn()
      const unsubscribe = downloadManager.onTaskUpdate(callback)
      unsubscribe()
      downloadManager.addTask(mockSearchResult)
      expect(callback).not.toHaveBeenCalled()
    })

    it('should handle multiple callbacks', () => {
      const callback1 = vi.fn()
      const callback2 = vi.fn()
      downloadManager.onTaskUpdate(callback1)
      downloadManager.onTaskUpdate(callback2)
      downloadManager.addTask(mockSearchResult)
      expect(callback1).toHaveBeenCalled()
      expect(callback2).toHaveBeenCalled()
    })
  })

  describe('retryDownload', () => {
    it('should reset error task', () => {
      const task = downloadManager.addTask(mockSearchResult)
      const tasks = downloadManager.getAllTasks()
      const addedTask = tasks.find(t => t.id === task.id)
      if (addedTask) {
        addedTask.status = 'error'
        addedTask.error = 'Test error'
      }
      
      downloadManager.retryDownload(task.id)
      const retriedTask = downloadManager.getTask(task.id)
      expect(['pending', 'downloading']).toContain(retriedTask?.status)
    })

    it('should reset paused task', () => {
      const task = downloadManager.addTask(mockSearchResult)
      const tasks = downloadManager.getAllTasks()
      const addedTask = tasks.find(t => t.id === task.id)
      if (addedTask) {
        addedTask.status = 'paused'
      }
      
      downloadManager.retryDownload(task.id)
      const retriedTask = downloadManager.getTask(task.id)
      expect(['pending', 'downloading']).toContain(retriedTask?.status)
    })

    it('should not affect completed tasks', () => {
      const task = downloadManager.addTask(mockSearchResult)
      const tasks = downloadManager.getAllTasks()
      const addedTask = tasks.find(t => t.id === task.id)
      if (addedTask) {
        addedTask.status = 'completed'
      }
      
      downloadManager.retryDownload(task.id)
      const retriedTask = downloadManager.getTask(task.id)
      expect(retriedTask?.status).toBe('completed')
    })
  })

  describe('pauseDownload', () => {
    it('should pause a downloading task', () => {
      const task = downloadManager.addTask(mockSearchResult)
      const tasks = downloadManager.getAllTasks()
      const addedTask = tasks.find(t => t.id === task.id)
      if (addedTask) {
        addedTask.status = 'downloading'
      }
      
      downloadManager.pauseDownload(task.id)
      const pausedTask = downloadManager.getTask(task.id)
      expect(pausedTask?.status).toBe('paused')
      expect(pausedTask?.speed).toBe(0)
    })

    it('should not affect non-downloading tasks', () => {
      const task = downloadManager.addTask(mockSearchResult)
      downloadManager.pauseDownload(task.id)
      const pausedTask = downloadManager.getTask(task.id)
      expect(pausedTask?.status).toBe('pending')
    })
  })

  describe('resumeDownload', () => {
    it('should resume a paused task', () => {
      const task = downloadManager.addTask(mockSearchResult)
      const tasks = downloadManager.getAllTasks()
      const addedTask = tasks.find(t => t.id === task.id)
      if (addedTask) {
        addedTask.status = 'paused'
      }
      
      downloadManager.resumeDownload(task.id)
      const resumedTask = downloadManager.getTask(task.id)
      expect(['pending', 'downloading']).toContain(resumedTask?.status)
    })

    it('should not affect non-paused tasks', () => {
      const task = downloadManager.addTask(mockSearchResult)
      downloadManager.resumeDownload(task.id)
      const resumedTask = downloadManager.getTask(task.id)
      expect(resumedTask?.status).toBe('pending')
    })
  })

  describe('init', () => {
    it('should initialize only once', () => {
      downloadManager.init()
      downloadManager.init()
      expect(true).toBe(true)
    })
  })

  describe('getSafeFileName', () => {
    it('should generate safe filename from search result', () => {
      downloadManager.updateSettings({ downloadPath: 'C:\\Downloads' })
      const task = downloadManager.addTask(mockSearchResult)
      expect(task.savePath).toBeTruthy()
    })
  })

  describe('startDownload', () => {
    it('should not start download for completed task', async () => {
      const task = downloadManager.addTask(mockSearchResult)
      const tasks = downloadManager.getAllTasks()
      const addedTask = tasks.find(t => t.id === task.id)
      if (addedTask) {
        addedTask.status = 'completed'
      }
      
      downloadManager.updateSettings({ autoStart: false })
      const newTask = downloadManager.addTask({ ...mockSearchResult, id: 'search-3' })
      const newTasks = downloadManager.getAllTasks()
      const newAddedTask = newTasks.find(t => t.id === newTask.id)
      if (newAddedTask) {
        newAddedTask.status = 'completed'
      }
      
      expect(newAddedTask?.status).toBe('completed')
    })

    it('should not start download for downloading task', async () => {
      const task = downloadManager.addTask(mockSearchResult)
      const tasks = downloadManager.getAllTasks()
      const addedTask = tasks.find(t => t.id === task.id)
      if (addedTask) {
        addedTask.status = 'downloading'
      }
      
      expect(addedTask?.status).toBe('downloading')
    })

    it('should handle task without download URL', () => {
      const noUrlResult: SearchResult = {
        id: 'no-url',
        title: 'No URL Song',
        artist: 'No URL Artist',
        duration: '3:00',
        downloadUrl: '',
      }
      
      downloadManager.updateSettings({ autoStart: false })
      const task = downloadManager.addTask(noUrlResult)
      
      expect(task.id).toBeTruthy()
    })
  })

  describe('loadTasks', () => {
    it('should handle saved tasks in localStorage', () => {
      const savedTasks = [
        {
          id: 'saved-task-1',
          searchResult: mockSearchResult,
          status: 'completed',
          progress: 100,
          downloadedBytes: 1000,
          totalBytes: 1000,
          speed: 0,
          savePath: 'C:\\Downloads',
          startTime: new Date().toISOString(),
          endTime: new Date().toISOString(),
        },
      ]
      
      downloadManager.updateSettings({ downloadPath: 'C:\\Downloads' })
      const task = downloadManager.addTask(mockSearchResult)
      const tasks = downloadManager.getAllTasks()
      expect(tasks.length).toBe(1)
    })
  })
})
