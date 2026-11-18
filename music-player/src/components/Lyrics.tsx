import { useState, useEffect, useMemo, useRef } from 'react'
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
  // 直接传入歌词内容时在渲染期解析；只有异步读取 .lrc 文件的结果才需要存 state
  const [fetchedLyrics, setFetchedLyrics] = useState<LyricsLine[]>([])
  const containerRef = useRef<HTMLDivElement>(null)

  const time = currentTime ?? playerTime

  const lyrics = useMemo(
    () => (lyricsContent ? parseLrc(lyricsContent) : fetchedLyrics),
    [lyricsContent, fetchedLyrics],
  )

  useEffect(() => {
    if (lyricsContent || !currentTrack) return

    const lrcPath = currentTrack.path.replace(/\.[^/.]+$/, '.lrc')
    fetch(`file://${lrcPath}`)
      .then(res => res.text())
      .then(text => {
        setFetchedLyrics(parseLrc(text))
      })
      .catch(() => {
        setFetchedLyrics([])
      })
  }, [lyricsContent, currentTrack])

  // 当前高亮行由播放时间直接派生，避免在 effect 里同步 setState
  const currentLineIndex = useMemo(() => {
    if (lyrics.length === 0) return -1

    const current = getCurrentLyric(lyrics, time)
    if (!current) return -1

    return lyrics.findIndex(l => l.time === current.time)
  }, [lyrics, time])

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
