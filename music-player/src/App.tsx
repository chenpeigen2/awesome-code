import { useEffect } from 'react'
import { usePlayerStore } from '@/stores'
import { audioManager } from '@/utils/audio'
import Sidebar from '@/components/Sidebar'
import PlayerBar from '@/components/PlayerBar'
import MainContent from '@/components/MainContent'
import './App.css'

function App() {
  const { 
    currentTrack, 
    isPlaying, 
    volume, 
    setIsPlaying, 
    setCurrentTime, 
    setDuration,
    playNext 
  } = usePlayerStore()

  useEffect(() => {
    audioManager.onPlay(() => {
      console.log('onPlay callback')
      setIsPlaying(true)
    })
    audioManager.onPause(() => {
      console.log('onPause callback')
      setIsPlaying(false)
    })
    audioManager.onEnd(() => {
      console.log('onEnd callback')
      setIsPlaying(false)
      playNext()
    })
    audioManager.onTimeUpdate((time) => setCurrentTime(time))
    audioManager.onLoad((duration) => {
      console.log('onLoad callback, duration:', duration)
      setDuration(duration)
    })
    audioManager.onError((error) => {
      console.error('Audio error:', error)
    })
  }, [setIsPlaying, playNext, setCurrentTime, setDuration])

  useEffect(() => {
    audioManager.setVolume(volume)
  }, [volume])

  useEffect(() => {
    if (currentTrack) {
      console.log('Current track changed:', currentTrack.title)
      audioManager.load(currentTrack).then(() => {
        // 读 store 的最新状态，避免把 isPlaying 放进依赖导致切歌时重复加载
        const playing = usePlayerStore.getState().isPlaying
        console.log('Track loaded, isPlaying:', playing)
        if (playing) {
          audioManager.play()
        }
      }).catch(err => {
        console.error('Failed to load track:', err)
      })
    }
  }, [currentTrack])

  useEffect(() => {
    console.log('isPlaying changed:', isPlaying)
    if (isPlaying) {
      audioManager.play()
    } else {
      audioManager.pause()
    }
  }, [isPlaying])

  return (
    <div className="app">
      <div className="app-container">
        <Sidebar />
        <MainContent />
      </div>
      <PlayerBar />
    </div>
  )
}

export default App
