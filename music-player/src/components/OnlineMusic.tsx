import { useState, useEffect, useCallback } from 'react'
import { 
  Search, 
  Download, 
  Play, 
  Pause, 
  X, 
  Loader2,
  CheckCircle,
  AlertCircle,
  Music,
  Trash2,
  RefreshCw,
  Settings,
  FolderOpen
} from 'lucide-react'
import { searchTracks, getPopularTracks, formatFileSize, formatSpeed } from '@/utils/mp3pm'
import { downloadManager } from '@/utils/downloadManager'
import { SearchResult, DownloadTask, DownloadStatus } from '@/types/download'
import { usePlayerStore } from '@/stores'
import { Track } from '@/types'
import { audioManager } from '@/utils/audio'
import './OnlineMusic.css'

export default function OnlineMusic() {
  const [searchQuery, setSearchQuery] = useState('')
  const [searchResults, setSearchResults] = useState<SearchResult[]>([])
  const [isSearching, setIsSearching] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [downloadTasks, setDownloadTasks] = useState<DownloadTask[]>([])
  const [activeTab, setActiveTab] = useState<'search' | 'downloads'>('search')
  const [showSettings, setShowSettings] = useState(false)
  const [downloadPath, setDownloadPath] = useState('')
  
  const { currentTrack, isPlaying, setCurrentTrack, setIsPlaying } = usePlayerStore()

  const updateTasks = useCallback(() => {
    setDownloadTasks([...downloadManager.getAllTasks()])
  }, [])

  useEffect(() => {
    const settings = downloadManager.getSettings()
    setDownloadPath(settings.downloadPath || '')
    
    downloadManager.init()
    updateTasks()
    
    const unsubscribe = downloadManager.onTaskUpdate(updateTasks)
    
    audioManager.onPlay(() => {
      console.log('Audio started playing')
    })
    
    audioManager.onError((err) => {
      console.error('Audio error:', err)
      setError(`播放失败: ${err}`)
    })
    
    return () => {
      unsubscribe()
    }
  }, [updateTasks])

  const loadPopularTracks = async () => {
    setIsSearching(true)
    setError(null)
    try {
      const results = await getPopularTracks()
      setSearchResults(results)
      if (results.length === 0) {
        setError('未能获取热门歌曲，请检查网络连接')
      }
    } catch (err: any) {
      setError(err.message || '加载失败')
      setSearchResults([])
    } finally {
      setIsSearching(false)
    }
  }

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!searchQuery.trim()) {
      loadPopularTracks()
      return
    }

    setIsSearching(true)
    setError(null)
    
    try {
      const results = await searchTracks(searchQuery)
      setSearchResults(results)
      if (results.length === 0) {
        setError('未找到相关歌曲，请尝试其他关键词')
      }
    } catch (err: any) {
      setError(err.message || '搜索失败')
      setSearchResults([])
    } finally {
      setIsSearching(false)
    }
  }

  const handleDownload = (result: SearchResult) => {
    if (!result.downloadUrl) {
      setError('该歌曲暂无下载链接')
      return
    }
    downloadManager.addTask(result)
    setActiveTab('downloads')
  }

  const handlePlayPreview = async (result: SearchResult) => {
    const trackId = `online-${result.id}`
    const isCurrentTrack = currentTrack?.id === trackId
    
    if (isCurrentTrack) {
      if (isPlaying) {
        audioManager.pause()
        setIsPlaying(false)
      } else {
        audioManager.play()
        setIsPlaying(true)
      }
    } else {
      try {
        const track: Track = {
          id: trackId,
          path: result.downloadUrl,
          title: result.title,
          artist: result.artist,
          album: '在线音乐',
          duration: 0,
        }
        
        setCurrentTrack(track)
        
        await audioManager.load(track)
        audioManager.setVolume(0.8)
        audioManager.play()
        setIsPlaying(true)
      } catch (err: any) {
        console.error('Failed to play:', err)
        setError(`播放失败: ${err.message || '无法加载音频'}`)
        setIsPlaying(false)
      }
    }
  }

  const handlePauseResume = (taskId: string, status: DownloadStatus) => {
    if (status === 'downloading') {
      downloadManager.pauseDownload(taskId)
    } else if (status === 'paused') {
      downloadManager.resumeDownload(taskId)
    }
  }

  const handleCancelDownload = (taskId: string) => {
    downloadManager.cancelDownload(taskId)
  }

  const handleRetryDownload = (taskId: string) => {
    downloadManager.retryDownload(taskId)
  }

  const handleClearCompleted = () => {
    downloadManager.clearCompleted()
  }

  const handleClearAll = () => {
    downloadManager.clearAll()
  }

  const handleSelectDownloadPath = async () => {
    if (window.electronAPI && window.electronAPI.openFolder) {
      const folder = await window.electronAPI.openFolder()
      if (folder) {
        setDownloadPath(folder)
        downloadManager.updateSettings({ downloadPath: folder })
      }
    }
  }

  const getStatusIcon = (status: DownloadStatus) => {
    switch (status) {
      case 'completed':
        return <CheckCircle size={16} className="status-icon completed" />
      case 'error':
        return <AlertCircle size={16} className="status-icon error" />
      case 'downloading':
        return <Loader2 size={16} className="status-icon spinning" />
      case 'paused':
        return <Pause size={16} className="status-icon paused" />
      default:
        return <Download size={16} className="status-icon" />
    }
  }

  const getStatusText = (task: DownloadTask) => {
    switch (task.status) {
      case 'pending':
        return '等待下载...'
      case 'downloading':
        return `${task.progress.toFixed(1)}% - ${formatSpeed(task.speed)}`
      case 'paused':
        return `已暂停 - ${task.progress.toFixed(1)}%`
      case 'completed':
        return `已完成 - ${formatFileSize(task.downloadedBytes)}`
      case 'error':
        return `失败: ${task.error || '未知错误'}`
      default:
        return ''
    }
  }

  const pendingCount = downloadTasks.filter(t => t.status !== 'completed').length
  const completedCount = downloadTasks.filter(t => t.status === 'completed').length

  const isTrackPlaying = (resultId: string) => {
    return currentTrack?.id === `online-${resultId}` && isPlaying
  }

  return (
    <div className="online-music">
      <div className="online-header">
        <h2><Music size={24} /> 在线音乐</h2>
        <div className="header-actions">
          <button 
            className="settings-btn"
            onClick={() => setShowSettings(!showSettings)}
            title="设置"
          >
            <Settings size={18} />
          </button>
          <div className="tab-buttons">
            <button 
              className={`tab-btn ${activeTab === 'search' ? 'active' : ''}`}
              onClick={() => setActiveTab('search')}
            >
              搜索
            </button>
            <button 
              className={`tab-btn ${activeTab === 'downloads' ? 'active' : ''}`}
              onClick={() => setActiveTab('downloads')}
            >
              下载 {pendingCount > 0 && `(${pendingCount})`}
            </button>
          </div>
        </div>
      </div>

      {showSettings && (
        <div className="settings-panel">
          <div className="setting-item">
            <label>下载路径:</label>
            <div className="path-input">
              <input 
                type="text" 
                value={downloadPath} 
                onChange={(e) => setDownloadPath(e.target.value)}
                placeholder="选择下载目录"
              />
              <button onClick={handleSelectDownloadPath} title="选择文件夹">
                <FolderOpen size={16} />
              </button>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'search' ? (
        <>
          <form className="search-form" onSubmit={handleSearch}>
            <div className="search-input-wrapper">
              <Search size={18} />
              <input
                type="text"
                placeholder="搜索歌曲、艺术家或专辑..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                disabled={isSearching}
              />
              {searchQuery && (
                <button 
                  type="button" 
                  className="clear-search"
                  onClick={() => {
                    setSearchQuery('')
                    loadPopularTracks()
                  }}
                >
                  <X size={16} />
                </button>
              )}
            </div>
            <button type="submit" className="search-btn" disabled={isSearching}>
              {isSearching ? <Loader2 size={18} className="spinning" /> : '搜索'}
            </button>
          </form>

          {error && (
            <div className="error-message">
              <AlertCircle size={16} />
              <span>{error}</span>
              <button onClick={() => setError(null)}>
                <X size={14} />
              </button>
            </div>
          )}

          {isSearching ? (
            <div className="loading-state">
              <Loader2 size={32} className="spinning" />
              <p>正在加载...</p>
            </div>
          ) : (
            <div className="search-results">
              {searchResults.length > 0 ? (
                searchResults.map((result) => (
                  <div key={result.id} className="result-item">
                    <div className="result-info">
                      <div className="result-cover">
                        <Music size={20} />
                      </div>
                      <div className="result-details">
                        <span className="result-title">{result.title}</span>
                        <span className="result-artist">{result.artist}</span>
                      </div>
                      <span className="result-duration">{result.duration}</span>
                    </div>
                    <div className="result-actions">
                      <button 
                        className={`action-btn with-label preview ${isTrackPlaying(result.id) ? 'active' : ''}`}
                        onClick={() => handlePlayPreview(result)}
                        title={isTrackPlaying(result.id) ? '暂停播放' : '播放试听'}
                      >
                        {isTrackPlaying(result.id) ? (
                          <Pause size={16} />
                        ) : (
                          <Play size={16} />
                        )}
                        <span className="btn-label">{isTrackPlaying(result.id) ? '暂停' : '播放'}</span>
                      </button>
                      <button 
                        className="action-btn with-label download"
                        onClick={() => handleDownload(result)}
                        title="下载到本地"
                      >
                        <Download size={16} />
                        <span className="btn-label">下载</span>
                      </button>
                    </div>
                  </div>
                ))
              ) : !error && (
                <div className="empty-results">
                  <Music size={48} strokeWidth={1} />
                  <p>输入关键词搜索歌曲</p>
                </div>
              )}
            </div>
          )}
        </>
      ) : (
        <div className="downloads-view">
          <div className="downloads-header">
            <div className="downloads-stats">
              <span>共 {downloadTasks.length} 个任务</span>
              {completedCount > 0 && <span>已完成 {completedCount} 个</span>}
            </div>
            <div className="downloads-actions">
              {completedCount > 0 && (
                <button className="action-text-btn" onClick={handleClearCompleted}>
                  <Trash2 size={14} />
                  清除已完成
                </button>
              )}
              {downloadTasks.length > 0 && (
                <button className="action-text-btn danger" onClick={handleClearAll}>
                  <X size={14} />
                  清空全部
                </button>
              )}
            </div>
          </div>

          <div className="downloads-list">
            {downloadTasks.length === 0 ? (
              <div className="empty-downloads">
                <Download size={48} strokeWidth={1} />
                <p>暂无下载任务</p>
                <p className="hint">搜索歌曲后点击下载按钮添加任务</p>
              </div>
            ) : (
              downloadTasks.map((task) => (
                <div key={task.id} className={`download-item ${task.status}`}>
                  <div className="download-info">
                    <div className="download-cover">
                      <Music size={20} />
                    </div>
                    <div className="download-details">
                      <span className="download-title">{task.searchResult.title}</span>
                      <span className="download-artist">{task.searchResult.artist}</span>
                      <div className="download-progress">
                        <div className="progress-bar">
                          <div 
                            className="progress-fill"
                            style={{ width: `${Math.min(100, task.progress)}%` }}
                          />
                        </div>
                        <span className="progress-text">{getStatusText(task)}</span>
                      </div>
                    </div>
                  </div>
                  <div className="download-actions">
                    {getStatusIcon(task.status)}
                    {task.status === 'downloading' && (
                      <button 
                        className="action-btn small"
                        onClick={() => handlePauseResume(task.id, task.status)}
                        title="暂停下载"
                      >
                        <Pause size={14} />
                      </button>
                    )}
                    {task.status === 'paused' && (
                      <button 
                        className="action-btn small"
                        onClick={() => handlePauseResume(task.id, task.status)}
                        title="继续下载"
                      >
                        <Play size={14} />
                      </button>
                    )}
                    {task.status === 'error' && (
                      <button 
                        className="action-btn small"
                        onClick={() => handleRetryDownload(task.id)}
                        title="重新下载"
                      >
                        <RefreshCw size={14} />
                      </button>
                    )}
                    {task.status !== 'completed' && (
                      <button 
                        className="action-btn small cancel"
                        onClick={() => handleCancelDownload(task.id)}
                        title="取消下载"
                      >
                        <X size={14} />
                      </button>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  )
}
