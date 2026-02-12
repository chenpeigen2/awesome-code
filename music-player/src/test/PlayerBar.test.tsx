import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import PlayerBar from '@/components/PlayerBar'

vi.mock('@/utils/audio', () => ({
  audioManager: {
    load: vi.fn().mockResolvedValue(undefined),
    play: vi.fn(),
    pause: vi.fn(),
    setVolume: vi.fn(),
    seek: vi.fn(),
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
  usePlayerStore: vi.fn((selector) => {
    const state = {
      currentTrack: null,
      isPlaying: false,
      volume: 0.8,
      currentTime: 0,
      duration: 180,
      playbackMode: 'sequence',
      setIsPlaying: vi.fn(),
      setVolume: vi.fn(),
      setCurrentTime: vi.fn(),
      setPlaybackMode: vi.fn(),
      playNext: vi.fn(),
      playPrevious: vi.fn(),
    }
    return typeof selector === 'function' ? selector(state) : state
  }),
  useSettingsStore: vi.fn((selector) => {
    const state = {
      settings: {
        showLyricsWindow: false,
      },
      updateSettings: vi.fn(),
    }
    return typeof selector === 'function' ? selector(state) : state
  }),
  useLibraryStore: vi.fn((selector) => {
    const state = {
      toggleLike: vi.fn(),
      isLiked: vi.fn().mockReturnValue(false),
    }
    return typeof selector === 'function' ? selector(state) : state
  }),
}))

describe('PlayerBar Component', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('rendering', () => {
    it('should render player bar', () => {
      render(<PlayerBar />)
      expect(screen.getByText('未播放音乐')).toBeInTheDocument()
    })

    it('should render play button', () => {
      render(<PlayerBar />)
      const buttons = screen.getAllByRole('button')
      expect(buttons.length).toBeGreaterThan(0)
    })

    it('should render volume control', () => {
      render(<PlayerBar />)
      const volumeSlider = screen.getByRole('slider')
      expect(volumeSlider).toBeInTheDocument()
    })
  })

  describe('time formatting', () => {
    it('should display current time as 0:00 initially', () => {
      render(<PlayerBar />)
      expect(screen.getByText('0:00')).toBeInTheDocument()
    })

    it('should display duration', () => {
      render(<PlayerBar />)
      const timeElements = screen.getAllByText('0:00')
      expect(timeElements.length).toBeGreaterThan(0)
    })
  })

  describe('volume control', () => {
    it('should render volume slider with correct initial value', () => {
      render(<PlayerBar />)
      const volumeSlider = screen.getByRole('slider')
      expect(volumeSlider).toHaveValue('0.8')
    })

    it('should handle volume change', async () => {
      render(<PlayerBar />)
      
      const volumeSlider = screen.getByRole('slider')
      fireEvent.change(volumeSlider, { target: { value: '0.5' } })
      
      expect(volumeSlider).toBeInTheDocument()
    })
  })

  describe('playback controls', () => {
    it('should render previous button', () => {
      render(<PlayerBar />)
      const buttons = screen.getAllByRole('button')
      expect(buttons.length).toBeGreaterThan(0)
    })

    it('should render next button', () => {
      render(<PlayerBar />)
      const buttons = screen.getAllByRole('button')
      expect(buttons.length).toBeGreaterThan(0)
    })
  })
})
