import { useState, useRef } from 'react'
import { 
  Play, 
  Pause, 
  SkipBack, 
  SkipForward, 
  Volume2, 
  VolumeX,
  Repeat,
  Shuffle,
  ListMusic,
  Mic2,
  Heart,
  Music
} from 'lucide-react'
import { usePlayerStore, useSettingsStore } from '@/stores'
import { audioManager } from '@/utils/audio'
import './PlayerBar.css'

function formatTime(seconds: number): string {
  if (!seconds || isNaN(seconds)) return '0:00'
  const minutes = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

export default function PlayerBar() {
  const { 
    currentTrack, 
    isPlaying, 
    volume, 
    currentTime, 
    duration, 
    playbackMode,
    setIsPlaying, 
    setVolume, 
    setCurrentTime,
    setPlaybackMode,
    playNext,
    playPrevious
  } = usePlayerStore()
  
  const { settings, updateSettings } = useSettingsStore()
  const [isMuted, setIsMuted] = useState(false)
  const [previousVolume, setPreviousVolume] = useState(volume)
  const [isLiked, setIsLiked] = useState(false)
  const progressRef = useRef<HTMLDivElement>(null)
  const isDragging = useRef(false)

  const playbackModes = ['sequence', 'random', 'single', 'loop'] as const
  const playbackLabels = {
    sequence: '顺序播放',
    random: '随机播放',
    single: '单曲循环',
    loop: '列表循环',
  }

  const handleProgressMouseDown = (e: React.MouseEvent<HTMLDivElement>) => {
    isDragging.current = true
    updateProgressFromEvent(e)
  }

  const handleProgressMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (isDragging.current) {
      updateProgressFromEvent(e)
    }
  }

  const handleProgressMouseUp = () => {
    isDragging.current = false
  }

  const updateProgressFromEvent = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!progressRef.current || !currentTrack || duration <= 0) return
    const rect = progressRef.current.getBoundingClientRect()
    const percent = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width))
    const newTime = percent * duration
    setCurrentTime(newTime)
    audioManager.seek(newTime)
  }

  const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newVolume = parseFloat(e.target.value)
    setVolume(newVolume)
    setIsMuted(newVolume === 0)
  }

  const toggleMute = () => {
    if (isMuted) {
      setVolume(previousVolume || 0.5)
      setIsMuted(false)
    } else {
      setPreviousVolume(volume)
      setVolume(0)
      setIsMuted(true)
    }
  }

  const cyclePlaybackMode = () => {
    const currentIndex = playbackModes.indexOf(playbackMode)
    const nextIndex = (currentIndex + 1) % playbackModes.length
    setPlaybackMode(playbackModes[nextIndex])
  }

  const toggleLyricsWindow = () => {
    if (settings.showLyricsWindow) {
      window.electronAPI?.hideLyrics()
      updateSettings({ showLyricsWindow: false })
    } else {
      window.electronAPI?.showLyrics()
      updateSettings({ showLyricsWindow: true })
    }
  }

  const handlePlayPause = () => {
    if (currentTrack) {
      setIsPlaying(!isPlaying)
    }
  }

  const progress = duration > 0 ? (currentTime / duration) * 100 : 0

  return (
    <footer className="player-bar">
      <div className="player-track-info">
        {currentTrack ? (
          <>
            <div className="track-cover">
              {currentTrack.cover ? (
                <img src={currentTrack.cover} alt={currentTrack.title} />
              ) : (
                <Music size={20} />
              )}
            </div>
            <div className="track-details">
              <span className="track-title">{currentTrack.title}</span>
              <span className="track-artist">{currentTrack.artist}</span>
            </div>
            <button 
              className={`like-btn ${isLiked ? 'liked' : ''}`}
              onClick={() => setIsLiked(!isLiked)}
            >
              <Heart size={16} fill={isLiked ? 'currentColor' : 'none'} />
            </button>
          </>
        ) : (
          <div className="no-track">
            <span>未播放音乐</span>
          </div>
        )}
      </div>

      <div className="player-controls">
        <div className="control-buttons">
          <button 
            className={`control-btn mode-btn ${playbackMode !== 'sequence' ? 'active' : ''}`}
            onClick={cyclePlaybackMode}
            title={playbackLabels[playbackMode]}
          >
            {playbackMode === 'single' ? <Repeat size={18} className="single-mode" /> : 
             playbackMode === 'random' ? <Shuffle size={18} /> : <Repeat size={18} />}
          </button>
          <button className="control-btn" onClick={playPrevious}>
            <SkipBack size={20} />
          </button>
          <button 
            className="control-btn play-pause"
            onClick={handlePlayPause}
            disabled={!currentTrack}
          >
            {isPlaying ? <Pause size={24} /> : <Play size={24} />}
          </button>
          <button className="control-btn" onClick={playNext}>
            <SkipForward size={20} />
          </button>
          <button className="control-btn">
            <ListMusic size={18} />
          </button>
        </div>

        <div className="progress-container">
          <span className="time current">{formatTime(currentTime)}</span>
          <div 
            className="progress-bar"
            ref={progressRef}
            onMouseDown={handleProgressMouseDown}
            onMouseMove={handleProgressMouseMove}
            onMouseUp={handleProgressMouseUp}
            onMouseLeave={handleProgressMouseUp}
          >
            <div className="progress-track">
              <div 
                className="progress-fill"
                style={{ width: `${progress}%` }}
              />
              <div 
                className="progress-handle"
                style={{ left: `${progress}%` }}
              />
            </div>
          </div>
          <span className="time total">{formatTime(duration)}</span>
        </div>
      </div>

      <div className="player-extra">
        <button 
          className={`control-btn ${settings.showLyricsWindow ? 'active' : ''}`}
          onClick={toggleLyricsWindow}
        >
          <Mic2 size={18} />
        </button>
        <div className="volume-control">
          <button className="control-btn" onClick={toggleMute}>
            {isMuted || volume === 0 ? <VolumeX size={18} /> : <Volume2 size={18} />}
          </button>
          <input
            type="range"
            min="0"
            max="1"
            step="0.01"
            value={isMuted ? 0 : volume}
            onChange={handleVolumeChange}
            className="volume-slider"
          />
        </div>
      </div>
    </footer>
  )
}
