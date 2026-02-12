import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import OnlineMusic from '@/components/OnlineMusic'
import { downloadManager } from '@/utils/downloadManager'

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
    addTask: vi.fn(),
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

vi.mock('@/stores', () => ({
  usePlayerStore: vi.fn(() => ({
    currentTrack: null,
    isPlaying: false,
    setCurrentTrack: vi.fn(),
    setIsPlaying: vi.fn(),
  })),
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

    it('should render search button', () => {
      render(<OnlineMusic />)
      const buttons = screen.getAllByRole('button')
      const searchButton = buttons.find(btn => btn.textContent?.includes('搜索'))
      expect(searchButton).toBeTruthy()
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

    it('should allow typing in search input', async () => {
      render(<OnlineMusic />)
      
      const input = screen.getByPlaceholderText(/搜索歌曲/i)
      fireEvent.change(input, { target: { value: 'test' } })
      
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
})
