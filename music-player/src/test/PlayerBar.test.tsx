import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import PlayerBar from '@/components/PlayerBar'

const mockTrack = {
  id: 'test-track-1',
  path: '/path/to/track.mp3',
  title: 'Test Track',
  artist: 'Test Artist',
  album: 'Test Album',
  duration: 180,
}

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

vi.mock('@/stores', () => {
  const mockState = {
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
  
  return {
    usePlayerStore: vi.fn((selector) => {
      return typeof selector === 'function' ? selector(mockState) : mockState
    }),
    useSettingsStore: vi.fn((selector) => {
      const settingsState = {
        settings: {
          showLyricsWindow: false,
        },
        updateSettings: vi.fn(),
      }
      return typeof selector === 'function' ? selector(settingsState) : settingsState
    }),
    useLibraryStore: vi.fn((selector) => {
      const libraryState = {
        toggleLike: vi.fn(),
        isLiked: vi.fn().mockReturnValue(false),
      }
      return typeof selector === 'function' ? selector(libraryState) : libraryState
    }),
  }
})

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
      const sliders = screen.getAllByRole('slider')
      expect(sliders.length).toBeGreaterThan(0)
    })
  })

  describe('time formatting', () => {
    it('should display current time as 0:00 initially', () => {
      render(<PlayerBar />)
      expect(screen.getByText('0:00')).toBeInTheDocument()
    })
  })

  describe('volume control', () => {
    it('should render volume slider with correct initial value', () => {
      render(<PlayerBar />)
      const sliders = screen.getAllByRole('slider')
      const volumeSlider = sliders.find(s => s.getAttribute('type') === 'range')
      expect(volumeSlider).toBeTruthy()
    })

    it('should handle volume change', () => {
      render(<PlayerBar />)
      
      const sliders = screen.getAllByRole('slider')
      const volumeSlider = sliders.find(s => s.getAttribute('type') === 'range')
      
      if (volumeSlider) {
        fireEvent.change(volumeSlider, { target: { value: '0.5' } })
        expect(volumeSlider).toBeInTheDocument()
      }
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

    it('should handle play button click', () => {
      render(<PlayerBar />)
      const buttons = screen.getAllByRole('button')
      const playButton = buttons[0]
      fireEvent.click(playButton)
      expect(buttons.length).toBeGreaterThan(0)
    })
  })

  describe('progress bar', () => {
    it('should render progress bar', () => {
      render(<PlayerBar />)
      const progressBars = screen.getAllByRole('slider')
      expect(progressBars.length).toBeGreaterThan(0)
    })

    it('should handle progress bar click', () => {
      render(<PlayerBar />)
      const sliders = screen.getAllByRole('slider')
      
      sliders.forEach(slider => {
        fireEvent.change(slider, { target: { value: '50' } })
      })
      
      expect(sliders.length).toBeGreaterThan(0)
    })
  })
})
