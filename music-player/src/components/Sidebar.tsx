import { useState } from 'react'
import { 
  Music, 
  ListMusic, 
  Mic2, 
  Settings, 
  Plus,
  Home,
  Heart,
  Disc,
  Radio
} from 'lucide-react'
import { useLibraryStore, usePlayerStore } from '@/stores'
import './Sidebar.css'

export default function Sidebar() {
  const { playlists, currentView, setCurrentView, addPlaylist, tracks, likedTrackIds } = useLibraryStore()
  const { setCurrentTrack, setIsPlaying, setQueue } = usePlayerStore()
  const [activeItem, setActiveItem] = useState('all')

  const handleMenuClick = (id: string) => {
    setActiveItem(id)
    if (['all', 'artists', 'albums', 'genres', 'favorites'].includes(id)) {
      setCurrentView(id as 'all' | 'artists' | 'albums' | 'genres' | 'favorites')
    }
  }

  const handleCreatePlaylist = () => {
    const name = `播放列表 ${playlists.length + 1}`
    addPlaylist({
      id: Date.now().toString(),
      name,
      tracks: [],
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  }

  const handlePlayAll = () => {
    const tracksArray = Array.from(tracks.values())
    if (tracksArray.length > 0) {
      setQueue(tracksArray)
      setCurrentTrack(tracksArray[0])
      setIsPlaying(true)
    }
  }

  const likedCount = likedTrackIds.size

  return (
    <aside className="sidebar">
      <div className="sidebar-header draggable">
        <div className="logo">
          <Music size={24} />
          <span>Music Player</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        <div className="nav-section">
          <ul className="nav-list">
            <li>
              <button
                className={`nav-item ${activeItem === 'all' ? 'active' : ''}`}
                onClick={() => handleMenuClick('all')}
              >
                <Music size={18} />
                <span>全部音乐</span>
              </button>
            </li>
            <li>
              <button
                className={`nav-item ${activeItem === 'artists' ? 'active' : ''}`}
                onClick={() => handleMenuClick('artists')}
              >
                <Mic2 size={18} />
                <span>艺术家</span>
              </button>
            </li>
            <li>
              <button
                className={`nav-item ${activeItem === 'albums' ? 'active' : ''}`}
                onClick={() => handleMenuClick('albums')}
              >
                <Disc size={18} />
                <span>专辑</span>
              </button>
            </li>
            <li>
              <button
                className={`nav-item ${activeItem === 'genres' ? 'active' : ''}`}
                onClick={() => handleMenuClick('genres')}
              >
                <Radio size={18} />
                <span>流派</span>
              </button>
            </li>
          </ul>
        </div>

        <div className="nav-section">
          <div className="nav-section-header">
            <span>我的音乐</span>
          </div>
          <ul className="nav-list">
            <li>
              <button 
                className={`nav-item ${activeItem === 'favorites' ? 'active' : ''}`}
                onClick={() => handleMenuClick('favorites')}
              >
                <Heart size={16} />
                <span>我喜欢</span>
                {likedCount > 0 && <span className="badge">{likedCount}</span>}
              </button>
            </li>
            <li>
              <button 
                className="nav-item"
                onClick={handlePlayAll}
              >
                <ListMusic size={16} />
                <span>播放全部</span>
              </button>
            </li>
          </ul>
        </div>

        <div className="nav-section">
          <div className="nav-section-header">
            <span>播放列表</span>
            <button className="add-playlist-btn" onClick={handleCreatePlaylist}>
              <Plus size={16} />
            </button>
          </div>
          <ul className="nav-list playlist-list">
            {playlists.map((playlist) => (
              <li key={playlist.id}>
                <button className="nav-item">
                  <ListMusic size={16} />
                  <span>{playlist.name}</span>
                </button>
              </li>
            ))}
            {playlists.length === 0 && (
              <li className="empty-playlist">
                <span>暂无播放列表</span>
              </li>
            )}
          </ul>
        </div>
      </nav>

      <div className="sidebar-footer">
        <button className="settings-btn">
          <Settings size={18} />
          <span>设置</span>
        </button>
      </div>
    </aside>
  )
}
