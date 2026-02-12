import React from 'react'
import ReactDOM from 'react-dom/client'
import Lyrics from './components/Lyrics'
import './App.css'

function LyricsApp() {
  return (
    <div style={{
      height: '100%',
      background: 'rgba(0, 0, 0, 0.7)',
      borderRadius: '8px',
      overflow: 'hidden',
    }}>
      <Lyrics isWindow />
    </div>
  )
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <LyricsApp />
  </React.StrictMode>,
)
