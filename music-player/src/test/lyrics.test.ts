import { describe, it, expect } from 'vitest'
import { 
  parseLrc, 
  formatLrcTime, 
  getCurrentLyric, 
  generateLrcContent 
} from '@/utils/lyrics'
import { LyricsLine } from '@/types'

describe('lyrics utils', () => {
  describe('parseLrc', () => {
    it('should parse standard LRC format', () => {
      const lrc = `[00:01.00]First line
[00:05.00]Second line
[00:10.00]Third line`
      
      const result = parseLrc(lrc)
      
      expect(result).toHaveLength(3)
      expect(result[0]).toEqual({ time: 1, text: 'First line' })
      expect(result[1]).toEqual({ time: 5, text: 'Second line' })
      expect(result[2]).toEqual({ time: 10, text: 'Third line' })
    })

    it('should parse LRC with milliseconds', () => {
      const lrc = '[00:01.500]Half second'
      const result = parseLrc(lrc)
      
      expect(result).toHaveLength(1)
      expect(result[0]).toEqual({ time: 1.5, text: 'Half second' })
    })

    it('should parse LRC with 3-digit milliseconds', () => {
      const lrc = '[00:01.123]Three digits'
      const result = parseLrc(lrc)
      
      expect(result).toHaveLength(1)
      expect(result[0]).toEqual({ time: 1.123, text: 'Three digits' })
    })

    it('should ignore empty lines', () => {
      const lrc = `[00:01.00]First line

[00:05.00]Second line`
      
      const result = parseLrc(lrc)
      expect(result).toHaveLength(2)
    })

    it('should ignore lines without text', () => {
      const lrc = `[00:01.00]First line
[00:05.00]
[00:10.00]Third line`
      
      const result = parseLrc(lrc)
      expect(result).toHaveLength(2)
    })

    it('should sort lyrics by time', () => {
      const lrc = `[00:10.00]Third
[00:01.00]First
[00:05.00]Second`
      
      const result = parseLrc(lrc)
      expect(result[0].text).toBe('First')
      expect(result[1].text).toBe('Second')
      expect(result[2].text).toBe('Third')
    })

    it('should handle empty content', () => {
      const result = parseLrc('')
      expect(result).toHaveLength(0)
    })

    it('should handle content without valid LRC tags', () => {
      const lrc = `This is plain text
Without any LRC tags`
      
      const result = parseLrc(lrc)
      expect(result).toHaveLength(0)
    })
  })

  describe('formatLrcTime', () => {
    it('should format time correctly', () => {
      expect(formatLrcTime(0)).toBe('00:00.00')
      expect(formatLrcTime(61.5)).toBe('01:01.50')
      expect(formatLrcTime(125.75)).toBe('02:05.75')
    })

    it('should handle edge cases', () => {
      expect(formatLrcTime(0.01)).toBe('00:00.01')
      expect(formatLrcTime(3599.99)).toBe('59:59.99')
    })
  })

  describe('getCurrentLyric', () => {
    const lyrics: LyricsLine[] = [
      { time: 0, text: 'Line 0' },
      { time: 5, text: 'Line 5' },
      { time: 10, text: 'Line 10' },
      { time: 15, text: 'Line 15' },
    ]

    it('should return null for empty lyrics', () => {
      expect(getCurrentLyric([], 5)).toBeNull()
    })

    it('should return correct lyric for exact time', () => {
      const result = getCurrentLyric(lyrics, 10)
      expect(result?.text).toBe('Line 10')
    })

    it('should return correct lyric for time between lines', () => {
      const result = getCurrentLyric(lyrics, 7)
      expect(result?.text).toBe('Line 5')
    })

    it('should return first lyric for time before first line', () => {
      const result = getCurrentLyric(lyrics, 0)
      expect(result?.text).toBe('Line 0')
    })

    it('should return last lyric for time after all lines', () => {
      const result = getCurrentLyric(lyrics, 100)
      expect(result?.text).toBe('Line 15')
    })

    it('should return null for time before first line when first line has non-zero time', () => {
      const lateLyrics: LyricsLine[] = [
        { time: 5, text: 'Line 5' },
        { time: 10, text: 'Line 10' },
      ]
      const result = getCurrentLyric(lateLyrics, 2)
      expect(result).toBeNull()
    })
  })

  describe('generateLrcContent', () => {
    it('should generate correct LRC content', () => {
      const lyrics: LyricsLine[] = [
        { time: 1, text: 'First line' },
        { time: 5, text: 'Second line' },
      ]
      
      const result = generateLrcContent(lyrics)
      
      expect(result).toContain('[00:01.00]First line')
      expect(result).toContain('[00:05.00]Second line')
    })

    it('should handle empty lyrics', () => {
      const result = generateLrcContent([])
      expect(result).toBe('')
    })

    it('should be reversible with parseLrc', () => {
      const originalLyrics: LyricsLine[] = [
        { time: 1, text: 'First line' },
        { time: 5.5, text: 'Second line' },
        { time: 10.25, text: 'Third line' },
      ]
      
      const generated = generateLrcContent(originalLyrics)
      const parsed = parseLrc(generated)
      
      expect(parsed[0].time).toBeCloseTo(originalLyrics[0].time, 1)
      expect(parsed[0].text).toBe(originalLyrics[0].text)
      expect(parsed[1].time).toBeCloseTo(originalLyrics[1].time, 1)
      expect(parsed[1].text).toBe(originalLyrics[1].text)
    })
  })
})
