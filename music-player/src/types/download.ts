export interface SearchResult {
  id: string
  title: string
  artist: string
  duration: string
  downloadUrl: string
  coverUrl?: string
}

export interface DownloadTask {
  id: string
  searchResult: SearchResult
  status: DownloadStatus
  progress: number
  downloadedBytes: number
  totalBytes: number
  speed: number
  error?: string
  savePath: string
  startTime?: Date
  endTime?: Date
}

export type DownloadStatus = 'pending' | 'downloading' | 'paused' | 'completed' | 'error'

export interface DownloadQueue {
  tasks: DownloadTask[]
  activeCount: number
  maxConcurrent: number
}

export interface DownloadSettings {
  downloadPath: string
  maxConcurrent: number
  autoStart: boolean
  notifyOnComplete: boolean
}

export const defaultDownloadSettings: DownloadSettings = {
  downloadPath: '',
  maxConcurrent: 3,
  autoStart: true,
  notifyOnComplete: true,
}
