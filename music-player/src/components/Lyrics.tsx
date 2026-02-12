import { useState, useEffect, useRef } from 'react'
import { parseLrc, getCurrentLyric, LyricsLine } from '@/utils/lyrics'
import { usePlayerStore } from '@/stores'
import './Lyrics.css'

interface LyricsProps {
  lyricsContent?: string
  currentTime?: number
  isWindow?: boolean
}

export default function Lyrics({ lyricsContent, currentTime, isWindow = false }: LyricsProps) {
  const { currentTrack, currentTime: playerTime } = usePlayerStore()
  const [lyrics, setLyrics] = useState<LyricsLine[]>([])
  const [currentLineIndex, setCurrentLineIndex] = useState(-1)
  const containerRef = useRef<HTMLDivElement>(null)

  const time = currentTime ?? playerTime

  useEffect(() => {
    if (lyricsContent) {
      const parsed = parseLrc(lyricsContent)
      setLyrics(parsed)
    } else if (currentTrack) {
      const lrcPath = currentTrack.path.replace(/\.[^/.]+$/, '.lrc')
      fetch(`file://${lrcPath}`)
        .then(res => res.text())
        .then(text => {
          const parsed = parseLrc(text)
          setLyrics(parsed)
        })
        .catch(() => {
          setLyrics([])
        })
    }
  }, [lyricsContent, currentTrack])

  useEffect(() => {
    if (lyrics.length === 0) return

    const current = getCurrentLyric(lyrics, time)
    if (current) {
      const index = lyrics.findIndex(l => l.time === current?.time)
      if (index !== currentLineIndex) {
        setCurrentLineIndex(index)
      }
    }
  }, [time, lyrics])

  useEffect(() => {
    if (containerRef.current && currentLineIndex >= 0) {
      const container = containerRef.current
      const activeElement = container.querySelector('.lyrics-line.active')
      if (activeElement) {
        const containerHeight = container.clientHeight
        const elementTop = activeElement.getBoundingClientRect().top - container.getBoundingClientRect().top
        const scrollTop = container.scrollTop + elementTop - containerHeight / 2 + activeElement.clientHeight / 2
        container.scrollTo({
          top: scrollTop,
          behavior: 'smooth'
        })
      }
    }
  }, [currentLineIndex])

  if (lyrics.length === 0) {
    return (
      <div className={`lyrics-container ${isWindow ? 'window-mode' : ''}`}>
        <div className="no-lyrics">
          <p>暂无歌词</p>
        </div>
      </div>
    )
  }

  return (
    <div className={`lyrics-container ${isWindow ? 'window-mode' : ''}`} ref={containerRef}>
      <div className="lyrics-content">
        {lyrics.map((line, index) => (
          <p
            key={`${line.time}-${index}`}
            className={`lyrics-line ${index === currentLineIndex ? 'active' : ''}`}
          >
            {line.text}
          </p>
        ))}
      </div>
    </div>
  )
}
