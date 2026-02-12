import { useState, useEffect } from 'react'
import { usePlayerStore, useLibraryStore } from '@/stores'
import { audioManager } from '@/utils/audio'
import './TestPage.css'

export default function TestPage() {
  const { currentTrack, isPlaying, setCurrentTrack, setIsPlaying } = usePlayerStore()
  const { setTracks } = useLibraryStore()
  const [logs, setLogs] = useState<string[]>([])
  const [testFilePath, setTestFilePath] = useState('')

  const addLog = (message: string) => {
    const timestamp = new Date().toLocaleTimeString()
    setLogs(prev => [...prev, `[${timestamp}] ${message}`])
    console.log(message)
  }

  useEffect(() => {
    audioManager.onPlay(() => addLog('✅ Audio started playing'))
    audioManager.onPause(() => addLog('⏸️ Audio paused'))
    audioManager.onEnd(() => addLog('⏹️ Audio ended'))
    audioManager.onLoad((duration) => addLog(`✅ Audio loaded, duration: ${duration.toFixed(2)}s`))
    audioManager.onError((error) => addLog(`❌ Error: ${error}`))
  }, [])

  const testFileDialog = async () => {
    addLog('Opening file dialog...')
    try {
      if (window.electronAPI) {
        const files = await window.electronAPI.openFiles()
        addLog(`Selected files: ${JSON.stringify(files)}`)
        if (files.length > 0) {
          setTestFilePath(files[0])
          
          const newTrack = {
            id: `test-${Date.now()}`,
            path: files[0],
            title: files[0].split(/[/\\]/).pop()?.replace(/\.[^/.]+$/, '') || 'Test',
            artist: 'Test Artist',
            album: 'Test Album',
            duration: 0,
          }
          
          setTracks([newTrack])
          setCurrentTrack(newTrack)
          addLog(`Track created: ${JSON.stringify(newTrack)}`)
        }
      } else {
        addLog('❌ electronAPI not available')
      }
    } catch (error) {
      addLog(`❌ Error: ${JSON.stringify(error)}`)
    }
  }

  const testPlayWithHowler = async () => {
    if (!currentTrack) {
      addLog('❌ No track selected')
      return
    }

    addLog(`Loading track with Howler: ${currentTrack.path}`)
    addLog(`Audio URL: audio://${currentTrack.path}`)
    
    try {
      await audioManager.load(currentTrack)
      addLog('Howler load successful, attempting to play...')
      audioManager.play()
      setIsPlaying(true)
    } catch (error) {
      addLog(`❌ Howler error: ${JSON.stringify(error)}`)
    }
  }

  const testPlayWithAudioElement = () => {
    if (!currentTrack) {
      addLog('❌ No track selected')
      return
    }

    addLog(`Testing with native Audio element: audio://${currentTrack.path}`)
    
    const audio = new Audio()
    audio.src = `audio://${currentTrack.path}`
    
    audio.onloadedmetadata = () => {
      addLog(`✅ Audio element loaded, duration: ${audio.duration.toFixed(2)}s`)
    }
    
    audio.onerror = () => {
      addLog(`❌ Audio element error code: ${audio.error?.code}, message: ${audio.error?.message}`)
    }
    
    audio.oncanplay = () => {
      addLog('✅ Audio element can play')
      audio.play().then(() => {
        addLog('✅ Audio element playing!')
      }).catch(err => {
        addLog(`❌ Audio element play error: ${err}`)
      })
    }
  }

  const testPause = () => {
    audioManager.pause()
    setIsPlaying(false)
    addLog('Pause called')
  }

  const testResume = () => {
    audioManager.play()
    setIsPlaying(true)
    addLog('Resume called')
  }

  const clearLogs = () => {
    setLogs([])
  }

  return (
    <div className="test-page">
      <h1>🎵 Audio Test Page</h1>
      
      <div className="test-section">
        <h2>1. Check Environment</h2>
        <div className="info-box">
          <p><strong>electronAPI available:</strong> {window.electronAPI ? '✅ Yes' : '❌ No'}</p>
          <p><strong>Current track:</strong> {currentTrack ? currentTrack.title : 'None'}</p>
          <p><strong>Track path:</strong> {currentTrack ? currentTrack.path : 'N/A'}</p>
          <p><strong>Is playing:</strong> {isPlaying ? 'Yes' : 'No'}</p>
        </div>
      </div>

      <div className="test-section">
        <h2>2. Select Audio File</h2>
        <button className="test-btn" onClick={testFileDialog}>
          📁 Select Audio File
        </button>
        {testFilePath && (
          <p className="file-path">Selected: {testFilePath}</p>
        )}
      </div>

      <div className="test-section">
        <h2>3. Test Playback</h2>
        <div className="btn-group">
          <button className="test-btn" onClick={testPlayWithHowler}>
            🎧 Play with Howler
          </button>
          <button className="test-btn" onClick={testPlayWithAudioElement}>
            🔊 Play with Audio Element
          </button>
        </div>
        <div className="btn-group" style={{ marginTop: '12px' }}>
          <button className="test-btn secondary" onClick={testPause}>
            ⏸️ Pause
          </button>
          <button className="test-btn secondary" onClick={testResume}>
            ▶️ Resume
          </button>
        </div>
      </div>

      <div className="test-section">
        <h2>4. Logs</h2>
        <button className="test-btn small" onClick={clearLogs}>Clear Logs</button>
        <div className="log-box">
          {logs.length === 0 ? (
            <p className="no-logs">No logs yet...</p>
          ) : (
            logs.map((log, index) => (
              <p key={index} className="log-entry">{log}</p>
            ))
          )}
        </div>
      </div>
    </div>
  )
}
