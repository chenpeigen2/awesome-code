import { LyricsLine } from '@/types'

export type { LyricsLine } from '@/types'

export function parseLrc(lrcContent: string): LyricsLine[] {
  const lines = lrcContent.split('\n')
  const result: LyricsLine[] = []

  for (const line of lines) {
    const match = line.match(/\[(\d{2}):(\d{2})\.(\d{2,3})\](.*)/)
    if (match) {
      const minutes = parseInt(match[1], 10)
      const seconds = parseInt(match[2], 10)
      const milliseconds = parseInt(match[3].padEnd(3, '0'), 10)
      const time = minutes * 60 + seconds + milliseconds / 1000
      const text = match[4].trim()
      if (text) {
        result.push({ time, text })
      }
    }
  }

  return result.sort((a, b) => a.time - b.time)
}

export function formatLrcTime(seconds: number): string {
  const minutes = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  const ms = Math.floor((seconds % 1) * 100)
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}.${ms.toString().padStart(2, '0')}`
}

export function getCurrentLyric(lyrics: LyricsLine[], currentTime: number): LyricsLine | null {
  if (lyrics.length === 0) return null

  for (let i = lyrics.length - 1; i >= 0; i--) {
    if (currentTime >= lyrics[i].time) {
      return lyrics[i]
    }
  }

  return null
}

export function generateLrcContent(lyrics: LyricsLine[]): string {
  return lyrics
    .map(line => `[${formatLrcTime(line.time)}]${line.text}`)
    .join('\n')
}
