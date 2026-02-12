import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Sidebar from '@/components/Sidebar'

vi.mock('@/stores', () => ({
  useLibraryStore: vi.fn((selector) => {
    const state = {
      playlists: [],
      setCurrentView: vi.fn(),
      addPlaylist: vi.fn(),
      tracks: new Map(),
      likedTrackIds: new Set(),
    }
    return typeof selector === 'function' ? selector(state) : state
  }),
  usePlayerStore: vi.fn((selector) => {
    const state = {
      setCurrentTrack: vi.fn(),
      setIsPlaying: vi.fn(),
      setQueue: vi.fn(),
    }
    return typeof selector === 'function' ? selector(state) : state
  }),
}))

describe('Sidebar Component', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('rendering', () => {
    it('should render logo', () => {
      render(<Sidebar />)
      expect(screen.getByText('Music Player')).toBeInTheDocument()
    })

    it('should render all navigation items', () => {
      render(<Sidebar />)
      expect(screen.getByText('全部音乐')).toBeInTheDocument()
      expect(screen.getByText('艺术家')).toBeInTheDocument()
      expect(screen.getByText('专辑')).toBeInTheDocument()
      expect(screen.getByText('流派')).toBeInTheDocument()
      expect(screen.getByText('在线音乐')).toBeInTheDocument()
    })

    it('should render favorites section', () => {
      render(<Sidebar />)
      expect(screen.getByText('我喜欢')).toBeInTheDocument()
    })

    it('should render play all button', () => {
      render(<Sidebar />)
      expect(screen.getByText('播放全部')).toBeInTheDocument()
    })

    it('should render settings button', () => {
      render(<Sidebar />)
      expect(screen.getByText('设置')).toBeInTheDocument()
    })

    it('should show empty playlist message', () => {
      render(<Sidebar />)
      expect(screen.getByText('暂无播放列表')).toBeInTheDocument()
    })
  })

  describe('navigation', () => {
    it('should highlight active menu item', async () => {
      const user = userEvent.setup()
      render(<Sidebar />)
      
      const allMusicBtn = screen.getByText('全部音乐').closest('button')
      expect(allMusicBtn).toHaveClass('active')
    })

    it('should change active item on click', async () => {
      const user = userEvent.setup()
      render(<Sidebar />)
      
      const artistsBtn = screen.getByText('艺术家').closest('button')
      await user.click(artistsBtn!)
      
      expect(artistsBtn).toHaveClass('active')
    })
  })

  describe('playlist creation', () => {
    it('should render add playlist button', () => {
      render(<Sidebar />)
      const addButtons = screen.getAllByRole('button')
      const addButton = addButtons.find(btn => btn.querySelector('svg'))
      expect(addButton).toBeTruthy()
    })
  })
})
