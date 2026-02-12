import { describe, it, expect, vi, beforeEach } from 'vitest'
import { 
  searchTracks, 
  getPopularTracks, 
  formatDuration, 
  formatFileSize, 
  formatSpeed,
  getDownloadFileName
} from '@/utils/mp3pm'
import { SearchResult } from '@/types/download'

describe('mp3pm utils', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('formatDuration', () => {
    it('should format duration correctly', () => {
      expect(formatDuration('3:45')).toBe(225)
      expect(formatDuration('0:30')).toBe(30)
      expect(formatDuration('10:00')).toBe(600)
    })

    it('should handle invalid duration', () => {
      expect(formatDuration('')).toBe(0)
      expect(formatDuration('invalid')).toBe(0)
      expect(formatDuration('3')).toBe(0)
    })
  })

  describe('formatFileSize', () => {
    it('should format bytes correctly', () => {
      expect(formatFileSize(0)).toBe('0 B')
      expect(formatFileSize(500)).toBe('500 B')
      expect(formatFileSize(1024)).toBe('1 KB')
      expect(formatFileSize(1024 * 1024)).toBe('1 MB')
      expect(formatFileSize(1024 * 1024 * 1024)).toBe('1 GB')
    })

    it('should handle decimal values', () => {
      expect(formatFileSize(1536)).toBe('1.5 KB')
      expect(formatFileSize(2560)).toBe('2.5 KB')
    })
  })

  describe('formatSpeed', () => {
    it('should format speed correctly', () => {
      expect(formatSpeed(1024)).toBe('1 KB/s')
      expect(formatSpeed(1024 * 1024)).toBe('1 MB/s')
    })
  })

  describe('getDownloadFileName', () => {
    it('should generate correct filename', () => {
      const result: SearchResult = {
        id: '1',
        title: 'Test Song',
        artist: 'Test Artist',
        duration: '3:45',
        downloadUrl: 'https://example.com/test.mp3',
      }
      expect(getDownloadFileName(result)).toBe('Test Artist - Test Song.mp3')
    })

    it('should sanitize special characters', () => {
      const result: SearchResult = {
        id: '1',
        title: 'Test<>:"/\\|?*Song',
        artist: 'Test<>Artist',
        duration: '3:45',
        downloadUrl: 'https://example.com/test.mp3',
      }
      const filename = getDownloadFileName(result)
      expect(filename).not.toContain('<')
      expect(filename).not.toContain('>')
      expect(filename).not.toContain(':')
      expect(filename).not.toContain('"')
      expect(filename).not.toContain('/')
      expect(filename).not.toContain('\\')
      expect(filename).not.toContain('|')
      expect(filename).not.toContain('?')
      expect(filename).not.toContain('*')
      expect(filename).toMatch(/Test_+Artist - Test_+Song\.mp3/)
    })

    it('should handle empty strings', () => {
      const result: SearchResult = {
        id: '1',
        title: '',
        artist: '',
        duration: '3:45',
        downloadUrl: 'https://example.com/test.mp3',
      }
      expect(getDownloadFileName(result)).toBe(' - .mp3')
    })
  })

  describe('searchTracks', () => {
    it('should return empty array for empty query', async () => {
      const results = await searchTracks('')
      expect(results).toEqual([])
    })

    it('should return empty array for whitespace query', async () => {
      const results = await searchTracks('   ')
      expect(results).toEqual([])
    })

    it('should call httpFetch with correct URL', async () => {
      const mockFetch = vi.fn().mockResolvedValue({
        status: 200,
        headers: {},
        data: '<html><body>No tracks</body></html>',
      })
      window.electronAPI.httpFetch = mockFetch

      await searchTracks('test query')
      expect(mockFetch).toHaveBeenCalledWith(
        'https://mp3.pm/?a=redirect&q=test%20query',
        expect.objectContaining({ timeout: 30000, followRedirects: true })
      )
    })

    it('should throw error on fetch failure', async () => {
      const mockFetch = vi.fn().mockResolvedValue({
        status: 0,
        headers: {},
        data: '',
        error: 'Network error',
      })
      window.electronAPI.httpFetch = mockFetch

      await expect(searchTracks('test')).rejects.toThrow('搜索失败')
    })

    it('should parse tracks from HTML response', async () => {
      const htmlWithTracks = `
        <li class="cplayer-sound-item" data-sound-id="123" data-sound-url="https://example.com/song1.mp3">
          <b>Song Title 1</b>
          <i>Artist 1</i>
          <em>3:45</em>
        </li>
        <li class="cplayer-sound-item" data-sound-id="456" data-sound-url="https://example.com/song2.mp3">
          <b>Song Title 2</b>
          <i>Artist 2</i>
          <em>4:30</em>
        </li>
      `
      const mockFetch = vi.fn().mockResolvedValue({
        status: 200,
        headers: {},
        data: htmlWithTracks,
      })
      window.electronAPI.httpFetch = mockFetch

      const results = await searchTracks('test')
      expect(results.length).toBeGreaterThan(0)
    })

    it('should handle HTTP error status', async () => {
      const mockFetch = vi.fn().mockResolvedValue({
        status: 404,
        headers: {},
        data: 'Not Found',
      })
      window.electronAPI.httpFetch = mockFetch

      await expect(searchTracks('test')).rejects.toThrow()
    })

    it('should handle empty data response', async () => {
      const mockFetch = vi.fn().mockResolvedValue({
        status: 200,
        headers: {},
        data: '',
      })
      window.electronAPI.httpFetch = mockFetch

      await expect(searchTracks('test')).rejects.toThrow()
    })
  })

  describe('getPopularTracks', () => {
    it('should call httpFetch with base URL', async () => {
      const mockFetch = vi.fn().mockResolvedValue({
        status: 200,
        headers: {},
        data: '<html><body>No tracks</body></html>',
      })
      window.electronAPI.httpFetch = mockFetch

      await getPopularTracks()
      expect(mockFetch).toHaveBeenCalledWith(
        'https://mp3.pm',
        expect.objectContaining({ timeout: 30000 })
      )
    })

    it('should throw error on fetch failure', async () => {
      const mockFetch = vi.fn().mockResolvedValue({
        status: 0,
        headers: {},
        data: '',
        error: 'Network error',
      })
      window.electronAPI.httpFetch = mockFetch

      await expect(getPopularTracks()).rejects.toThrow('获取热门歌曲失败')
    })

    it('should parse popular tracks from HTML', async () => {
      const htmlWithTracks = `
        <li class="cplayer-sound-item" data-sound-id="pop1" data-sound-url="https://example.com/popular.mp3">
          <b>Popular Song</b>
          <i>Popular Artist</i>
          <em>3:00</em>
        </li>
      `
      const mockFetch = vi.fn().mockResolvedValue({
        status: 200,
        headers: {},
        data: htmlWithTracks,
      })
      window.electronAPI.httpFetch = mockFetch

      const results = await getPopularTracks()
      expect(results.length).toBeGreaterThan(0)
    })
  })

  describe('fetchHtml fallback (browser environment)', () => {
    it('should handle fetch when electronAPI.httpFetch is not available', async () => {
      const originalHttpFetch = window.electronAPI.httpFetch
      window.electronAPI.httpFetch = undefined as any

      const mockFetch = vi.fn().mockResolvedValue({
        ok: true,
        text: () => Promise.resolve('<html>test</html>'),
      })
      vi.stubGlobal('fetch', mockFetch)

      const results = await searchTracks('test')
      expect(results).toEqual([])

      vi.unstubAllGlobals()
      window.electronAPI.httpFetch = originalHttpFetch
    })
  })
})
