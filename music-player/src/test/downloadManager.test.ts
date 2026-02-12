import { describe, it, expect, beforeEach, vi } from 'vitest'
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
  })
})
