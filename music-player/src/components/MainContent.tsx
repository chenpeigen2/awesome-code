import { useState, useEffect, useMemo } from 'react'
import { 
  Play, 
  Pause, 
  MoreHorizontal, 
  Clock, 
  Music,
  FolderOpen,
  Search,
  Mic2,
  Disc,
  Radio,
  Heart
} from 'lucide-react'
import { useLibraryStore, usePlayerStore } from '@/stores'
import { Track } from '@/types'
import OnlineMusic from './OnlineMusic'
import './MainContent.css'

function formatDuration(seconds: number): string {
  if (!seconds || isNaN(seconds)) return '0:00'
  const minutes = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

export default function MainContent() {
  const { tracks, currentView, searchQuery, setSearchQuery, setTracks, toggleLike, isLiked, getLikedTracks, likedTrackIds } = useLibraryStore()
  const { currentTrack, isPlaying, setCurrentTrack, setIsPlaying, setQueue, setQueueIndex } = usePlayerStore()
  const [tracksArray, setTracksArray] = useState<Track[]>([])

  useEffect(() => {
    let arr: Track[]
    
    if (currentView === 'favorites') {
      arr = getLikedTracks()
    } else {
      arr = Array.from(tracks.values())
    }
    
    if (searchQuery) {
      const filtered = arr.filter(track => 
        track.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        track.artist.toLowerCase().includes(searchQuery.toLowerCase()) ||
        track.album.toLowerCase().includes(searchQuery.toLowerCase())
      )
      setTracksArray(filtered)
    } else {
      setTracksArray(arr)
    }
  }, [tracks, searchQuery, currentView, likedTrackIds])

  const groupedByArtist = useMemo(() => {
    const groups: Record<string, Track[]> = {}
    tracksArray.forEach(track => {
      const artist = track.artist || 'Unknown Artist'
      if (!groups[artist]) groups[artist] = []
      groups[artist].push(track)
    })
    return groups
  }, [tracksArray])

  const groupedByAlbum = useMemo(() => {
    const groups: Record<string, Track[]> = {}
    tracksArray.forEach(track => {
      const album = track.album || 'Unknown Album'
      if (!groups[album]) groups[album] = []
      groups[album].push(track)
    })
    return groups
  }, [tracksArray])

  const groupedByGenre = useMemo(() => {
    const groups: Record<string, Track[]> = {}
    tracksArray.forEach(track => {
      const genre = track.genre || 'Unknown Genre'
      if (!groups[genre]) groups[genre] = []
      groups[genre].push(track)
    })
    return groups
  }, [tracksArray])

  const handlePlayTrack = (track: Track, index: number) => {
    if (currentTrack?.id === track.id) {
      setIsPlaying(!isPlaying)
    } else {
      setQueue(tracksArray)
      setQueueIndex(index)
      setCurrentTrack(track)
      setIsPlaying(true)
    }
  }

  const handleAddFolder = async () => {
    if (window.electronAPI) {
      const folder = await window.electronAPI.openFolder()
      if (folder) {
        console.log('Selected folder:', folder)
      }
    }
  }

  const handleAddFiles = async () => {
    if (window.electronAPI) {
      const files = await window.electronAPI.openFiles()
      if (files.length > 0) {
        const newTracks: Track[] = files.map((filePath, index) => ({
          id: `${Date.now()}-${index}`,
          path: filePath,
          title: filePath.split(/[/\\]/).pop()?.replace(/\.[^/.]+$/, '') || 'Unknown',
          artist: 'Unknown Artist',
          album: 'Unknown Album',
          duration: 0,
        }))
        const existingTracks = Array.from(tracks.values())
        setTracks([...existingTracks, ...newTracks])
      }
    }
  }

  const handlePlayAll = () => {
    if (tracksArray.length > 0) {
      setQueue(tracksArray)
      setQueueIndex(0)
      setCurrentTrack(tracksArray[0])
      setIsPlaying(true)
    }
  }

  const handleToggleLike = (e: React.MouseEvent, trackId: string) => {
    e.stopPropagation()
    toggleLike(trackId)
  }

  const getViewTitle = () => {
    switch (currentView) {
      case 'favorites':
        return '我喜欢'
      case 'artists':
        return '艺术家'
      case 'albums':
        return '专辑'
      case 'genres':
        return '流派'
      case 'online':
        return '在线音乐'
      default:
        return '全部音乐'
    }
  }

  const renderTrackList = () => (
    <div className="track-list">
      <div className="track-list-header">
        <div className="col-index">#</div>
        <div className="col-title">标题</div>
        <div className="col-album">专辑</div>
        <div className="col-duration">
          <Clock size={14} />
        </div>
        <div className="col-actions"></div>
      </div>
      <div className="track-list-body">
        {tracksArray.length === 0 ? (
          <div className="empty-list">
            {currentView === 'favorites' ? (
              <>
                <Heart size={48} strokeWidth={1} />
                <p>还没有喜欢的歌曲</p>
                <p className="hint">点击歌曲旁的心形图标添加到我喜欢</p>
              </>
            ) : (
              <p>暂无歌曲</p>
            )}
          </div>
        ) : (
          tracksArray.map((track, index) => (
            <div 
              key={track.id} 
              className={`track-item ${currentTrack?.id === track.id ? 'active' : ''}`}
              onDoubleClick={() => handlePlayTrack(track, index)}
            >
              <div className="col-index">
                <span className="index-number">{index + 1}</span>
                <button 
                  className="play-btn"
                  onClick={() => handlePlayTrack(track, index)}
                >
                  {currentTrack?.id === track.id && isPlaying ? (
                    <Pause size={14} />
                  ) : (
                    <Play size={14} />
                  )}
                </button>
              </div>
              <div className="col-title">
                <div className="track-cover">
                  {track.cover ? (
                    <img src={track.cover} alt={track.title} />
                  ) : (
                    <Music size={16} />
                  )}
                </div>
                <div className="track-info">
                  <span className="track-name">{track.title}</span>
                  <span className="track-artist">{track.artist}</span>
                </div>
              </div>
              <div className="col-album">{track.album}</div>
              <div className="col-duration">
                {formatDuration(track.duration)}
              </div>
              <div className="col-actions">
                <button 
                  className={`like-track-btn ${isLiked(track.id) ? 'liked' : ''}`}
                  onClick={(e) => handleToggleLike(e, track.id)}
                  title={isLiked(track.id) ? '取消喜欢' : '添加到我喜欢'}
                >
                  <Heart size={14} fill={isLiked(track.id) ? 'currentColor' : 'none'} />
                </button>
                <button className="more-btn">
                  <MoreHorizontal size={16} />
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )

  const renderGroupedView = (groups: Record<string, Track[]>, icon: React.ReactNode, _type: string) => (
    <div className="grouped-view">
      {Object.entries(groups).map(([name, groupTracks]) => (
        <div key={name} className="group-section">
          <div className="group-header">
            <div className="group-icon">{icon}</div>
            <div className="group-info">
              <h3>{name}</h3>
              <span>{groupTracks.length} 首歌曲</span>
            </div>
            <button 
              className="play-group-btn"
              onClick={() => {
                setQueue(groupTracks)
                setQueueIndex(0)
                setCurrentTrack(groupTracks[0])
                setIsPlaying(true)
              }}
            >
              <Play size={16} />
              播放
            </button>
          </div>
          <div className="group-tracks">
            {groupTracks.slice(0, 5).map((track, idx) => (
              <div 
                key={track.id} 
                className={`group-track-item ${currentTrack?.id === track.id ? 'active' : ''}`}
                onClick={() => handlePlayTrack(track, tracksArray.indexOf(track))}
              >
                <span className="track-num">{idx + 1}</span>
                <span className="track-name">{track.title}</span>
                <span className="track-duration">{formatDuration(track.duration)}</span>
                <button 
                  className={`like-track-btn small ${isLiked(track.id) ? 'liked' : ''}`}
                  onClick={(e) => handleToggleLike(e, track.id)}
                >
                  <Heart size={12} fill={isLiked(track.id) ? 'currentColor' : 'none'} />
                </button>
              </div>
            ))}
            {groupTracks.length > 5 && (
              <div className="more-tracks">还有 {groupTracks.length - 5} 首...</div>
            )}
          </div>
        </div>
      ))}
    </div>
  )

  const renderContent = () => {
    switch (currentView) {
      case 'artists':
        return renderGroupedView(groupedByArtist, <Mic2 size={24} />, 'artist')
      case 'albums':
        return renderGroupedView(groupedByAlbum, <Disc size={24} />, 'album')
      case 'genres':
        return renderGroupedView(groupedByGenre, <Radio size={24} />, 'genre')
      default:
        return renderTrackList()
    }
  }

  return (
    <main className="main-content">
      <header className="content-header draggable">
        <div className="header-controls non-draggable">
          <button className="control-btn" onClick={() => window.electronAPI?.minimizeWindow()}>
            <span>─</span>
          </button>
          <button className="control-btn" onClick={() => window.electronAPI?.maximizeWindow()}>
            <span>□</span>
          </button>
          <button className="control-btn close" onClick={() => window.electronAPI?.closeWindow()}>
            <span>✕</span>
          </button>
        </div>
        <div className="search-bar non-draggable">
          <Search size={16} />
          <input
            type="text"
            placeholder="搜索音乐..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </header>

      <div className="content-body">
        {currentView === 'online' ? (
          <OnlineMusic />
        ) : tracks.size === 0 && currentView !== 'favorites' ? (
          <div className="empty-state">
            <Music size={64} strokeWidth={1} />
            <h2>开始添加音乐</h2>
            <p>添加本地音乐文件或文件夹来开始播放</p>
            <div className="empty-actions">
              <button className="action-btn primary" onClick={handleAddFiles}>
                <FolderOpen size={18} />
                添加文件
              </button>
              <button className="action-btn" onClick={handleAddFolder}>
                <FolderOpen size={18} />
                添加文件夹
              </button>
            </div>
          </div>
        ) : (
          <>
            <div className="content-toolbar">
              <h2 className="view-title">{getViewTitle()}</h2>
              <div className="toolbar-actions">
                {tracksArray.length > 0 && (
                  <button className="action-btn primary" onClick={handlePlayAll}>
                    <Play size={16} />
                    播放全部
                  </button>
                )}
                {currentView !== 'favorites' && (
                  <button className="action-btn" onClick={handleAddFiles}>
                    <FolderOpen size={16} />
                    添加音乐
                  </button>
                )}
              </div>
            </div>

            {renderContent()}
          </>
        )}
      </div>
    </main>
  )
}
