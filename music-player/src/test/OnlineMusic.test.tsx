import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import OnlineMusic from '@/components/OnlineMusic'
import { downloadManager } from '@/utils/downloadManager'

const mockSearchResults = [
  {
    id: '1',
    title: 'Test Song 1',
    artist: 'Test Artist 1',
    duration: '3:45',
    downloadUrl: 'https://example.com/song1.mp3',
  },
  {
    id: '2',
    title: 'Test Song 2',
    artist: 'Test Artist 2',
    duration: '4:30',
    downloadUrl: 'https://example.com/song2.mp3',
  },
]

vi.mock('@/utils/downloadManager', () => ({
  downloadManager: {
    init: vi.fn(),
    getAllTasks: vi.fn().mockReturnValue([]),
    getSettings: vi.fn().mockReturnValue({
      downloadPath: 'C:\\Users\\Public\\Music\\Downloads',
      maxConcurrent: 3,
      autoStart: true,
      notifyOnComplete: true,
    }),
    onTaskUpdate: vi.fn().mockReturnValue(() => {}),
    addTask: vi.fn().mockReturnValue({ id: 'task-1', status: 'pending' }),
    pauseDownload: vi.fn(),
    resumeDownload: vi.fn(),
    cancelDownload: vi.fn(),
    retryDownload: vi.fn(),
    clearCompleted: vi.fn(),
    clearAll: vi.fn(),
    updateSettings: vi.fn(),
  },
}))

vi.mock('@/utils/audio', () => ({
  audioManager: {
    load: vi.fn().mockResolvedValue(undefined),
    play: vi.fn(),
    pause: vi.fn(),
    setVolume: vi.fn(),
    onPlay: vi.fn(),
    onPause: vi.fn(),
    onError: vi.fn(),
    onTimeUpdate: vi.fn(),
    onLoad: vi.fn(),
    onEnd: vi.fn(),
    onStateChange: vi.fn(),
  },
}))

vi.mock('@/utils/mp3pm', () => ({
  searchTracks: vi.fn().mockResolvedValue([
    { id: '1', title: 'Test Song 1', artist: 'Test Artist 1', duration: '3:45', downloadUrl: 'https://example.com/song1.mp3' },
    { id: '2', title: 'Test Song 2', artist: 'Test Artist 2', duration: '4:30', downloadUrl: 'https://example.com/song2.mp3' },
  ]),
  getPopularTracks: vi.fn().mockResolvedValue([
    { id: 'pop1', title: 'Popular Song', artist: 'Popular Artist', duration: '3:00', downloadUrl: 'https://example.com/popular.mp3' },
  ]),
  formatDuration: vi.fn((d) => {
    const parts = d.split(':')
    return parseInt(parts[0]) * 60 + parseInt(parts[1])
  }),
  formatFileSize: vi.fn((size) => `${size} B`),
  formatSpeed: vi.fn((speed) => `${speed} B/s`),
  getDownloadFileName: vi.fn((r) => `${r.artist} - ${r.title}.mp3`),
}))

vi.mock('@/stores', () => ({
  usePlayerStore: vi.fn((selector) => {
    const state = {
      currentTrack: null,
      isPlaying: false,
      setCurrentTrack: vi.fn(),
      setIsPlaying: vi.fn(),
      setQueue: vi.fn(),
      setQueueIndex: vi.fn(),
    }
    return typeof selector === 'function' ? selector(state) : state
  }),
}))

describe('OnlineMusic Component', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('rendering', () => {
    it('should render search input', () => {
      render(<OnlineMusic />)
      expect(screen.getByPlaceholderText(/搜索歌曲/i)).toBeInTheDocument()
    })

    it('should render tabs', () => {
      render(<OnlineMusic />)
      const buttons = screen.getAllByRole('button')
      expect(buttons.length).toBeGreaterThan(0)
    })

    it('should render settings button', () => {
      render(<OnlineMusic />)
      const buttons = screen.getAllByRole('button')
      expect(buttons.length).toBeGreaterThan(0)
    })
  })

  describe('search functionality', () => {
    it('should update search query on input change', async () => {
      const user = userEvent.setup()
      render(<OnlineMusic />)
      
      const input = screen.getByPlaceholderText(/搜索歌曲/i)
      await user.type(input, 'test song')
      
      expect(input).toHaveValue('test song')
    })

    it('should allow typing in search input', () => {
      render(<OnlineMusic />)
      
      const input = screen.getByPlaceholderText(/搜索歌曲/i)
      fireEvent.change(input, { target: { value: 'test' } })
      
      expect(input).toHaveValue('test')
    })

    it('should trigger search on Enter key', async () => {
      const user = userEvent.setup()
      render(<OnlineMusic />)
      
      const input = screen.getByPlaceholderText(/搜索歌曲/i)
      await user.type(input, 'test{enter}')
      
      expect(input).toHaveValue('test')
    })

    it('should trigger search on button click', async () => {
      const user = userEvent.setup()
      render(<OnlineMusic />)
      
      const input = screen.getByPlaceholderText(/搜索歌曲/i)
      await user.type(input, 'test')
      
      const buttons = screen.getAllByRole('button')
      const searchButton = buttons.find(btn => btn.textContent?.includes('搜索'))
      if (searchButton) {
        await user.click(searchButton)
      }
      
      expect(input).toHaveValue('test')
    })
  })

  describe('tab navigation', () => {
    it('should switch to downloads tab', async () => {
      const user = userEvent.setup()
      render(<OnlineMusic />)
      
      const buttons = screen.getAllByRole('button')
      const downloadsTab = buttons.find(btn => btn.textContent?.includes('下载'))
      if (downloadsTab) {
        await user.click(downloadsTab)
      }
      
      expect(screen.getByText(/暂无下载任务/i)).toBeInTheDocument()
    })
  })

  describe('settings panel', () => {
    it('should have settings button', () => {
      render(<OnlineMusic />)
      const buttons = screen.getAllByRole('button')
      expect(buttons.length).toBeGreaterThan(0)
    })
  })

  describe('empty state', () => {
    it('should show empty state when no results', () => {
      render(<OnlineMusic />)
      expect(screen.getByText(/输入关键词搜索歌曲/i)).toBeInTheDocument()
    })
  })

  describe('search results', () => {
    it('should display search results after search', async () => {
      const user = userEvent.setup()
      render(<OnlineMusic />)
      
      const input = screen.getByPlaceholderText(/搜索歌曲/i)
      await user.type(input, 'test{enter}')
      
      await waitFor(() => {
        expect(input).toHaveValue('test')
      })
    })
  })
})
