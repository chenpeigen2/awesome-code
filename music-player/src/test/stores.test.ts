import { describe, it, expect, beforeEach, vi } from 'vitest'
import { usePlayerStore, useLibraryStore, useSettingsStore } from '@/stores'
import { Track, Playlist } from '@/types'
import { act } from '@testing-library/react'

describe('PlayerStore', () => {
  const mockTrack: Track = {
    id: 'test-track-1',
    path: '/path/to/track.mp3',
    title: 'Test Track',
    artist: 'Test Artist',
    album: 'Test Album',
    duration: 180,
  }

  const mockTrack2: Track = {
    id: 'test-track-2',
    path: '/path/to/track2.mp3',
    title: 'Test Track 2',
    artist: 'Test Artist 2',
    album: 'Test Album 2',
    duration: 200,
  }

  beforeEach(() => {
    usePlayerStore.setState({
      currentTrack: null,
      isPlaying: false,
      volume: 0.8,
      currentTime: 0,
      duration: 0,
      playbackMode: 'sequence',
      queue: [],
      queueIndex: -1,
    })
  })

  describe('setCurrentTrack', () => {
    it('should set current track correctly', () => {
      const { setCurrentTrack, currentTrack } = usePlayerStore.getState()
      act(() => setCurrentTrack(mockTrack))
      expect(usePlayerStore.getState().currentTrack).toEqual(mockTrack)
    })

    it('should set current track to null', () => {
      const { setCurrentTrack } = usePlayerStore.getState()
      act(() => setCurrentTrack(mockTrack))
      act(() => setCurrentTrack(null))
      expect(usePlayerStore.getState().currentTrack).toBeNull()
    })
  })

  describe('setIsPlaying', () => {
    it('should set playing state to true', () => {
      const { setIsPlaying } = usePlayerStore.getState()
      act(() => setIsPlaying(true))
      expect(usePlayerStore.getState().isPlaying).toBe(true)
    })

    it('should set playing state to false', () => {
      const { setIsPlaying } = usePlayerStore.getState()
      act(() => setIsPlaying(true))
      act(() => setIsPlaying(false))
      expect(usePlayerStore.getState().isPlaying).toBe(false)
    })
  })

  describe('setVolume', () => {
    it('should set volume correctly', () => {
      const { setVolume } = usePlayerStore.getState()
      act(() => setVolume(0.5))
      expect(usePlayerStore.getState().volume).toBe(0.5)
    })

    it('should handle volume boundaries', () => {
      const { setVolume } = usePlayerStore.getState()
      act(() => setVolume(0))
      expect(usePlayerStore.getState().volume).toBe(0)
      act(() => setVolume(1))
      expect(usePlayerStore.getState().volume).toBe(1)
    })
  })

  describe('setPlaybackMode', () => {
    it('should set playback mode to sequence', () => {
      const { setPlaybackMode } = usePlayerStore.getState()
      act(() => setPlaybackMode('sequence'))
      expect(usePlayerStore.getState().playbackMode).toBe('sequence')
    })

    it('should set playback mode to random', () => {
      const { setPlaybackMode } = usePlayerStore.getState()
      act(() => setPlaybackMode('random'))
      expect(usePlayerStore.getState().playbackMode).toBe('random')
    })

    it('should set playback mode to single', () => {
      const { setPlaybackMode } = usePlayerStore.getState()
      act(() => setPlaybackMode('single'))
      expect(usePlayerStore.getState().playbackMode).toBe('single')
    })

    it('should set playback mode to loop', () => {
      const { setPlaybackMode } = usePlayerStore.getState()
      act(() => setPlaybackMode('loop'))
      expect(usePlayerStore.getState().playbackMode).toBe('loop')
    })
  })

  describe('queue management', () => {
    it('should set queue correctly', () => {
      const { setQueue } = usePlayerStore.getState()
      act(() => setQueue([mockTrack, mockTrack2]))
      expect(usePlayerStore.getState().queue).toHaveLength(2)
    })

    it('should add track to queue', () => {
      const { setQueue, addToQueue } = usePlayerStore.getState()
      act(() => setQueue([mockTrack]))
      act(() => addToQueue(mockTrack2))
      expect(usePlayerStore.getState().queue).toHaveLength(2)
    })

    it('should remove track from queue', () => {
      const { setQueue, removeFromQueue } = usePlayerStore.getState()
      act(() => setQueue([mockTrack, mockTrack2]))
      act(() => removeFromQueue('test-track-1'))
      expect(usePlayerStore.getState().queue).toHaveLength(1)
      expect(usePlayerStore.getState().queue[0].id).toBe('test-track-2')
    })

    it('should clear queue', () => {
      const { setQueue, clearQueue } = usePlayerStore.getState()
      act(() => setQueue([mockTrack, mockTrack2]))
      act(() => clearQueue())
      expect(usePlayerStore.getState().queue).toHaveLength(0)
      expect(usePlayerStore.getState().queueIndex).toBe(-1)
    })
  })

  describe('playNext', () => {
    it('should not change track when queue is empty', () => {
      const { playNext } = usePlayerStore.getState()
      act(() => playNext())
      expect(usePlayerStore.getState().currentTrack).toBeNull()
    })

    it('should play next track in sequence mode', () => {
      const { setQueue, setQueueIndex, playNext } = usePlayerStore.getState()
      act(() => setQueue([mockTrack, mockTrack2]))
      act(() => setQueueIndex(0))
      act(() => playNext())
      expect(usePlayerStore.getState().queueIndex).toBe(1)
      expect(usePlayerStore.getState().currentTrack?.id).toBe('test-track-2')
    })

    it('should wrap around to first track in sequence mode', () => {
      const { setQueue, setQueueIndex, playNext } = usePlayerStore.getState()
      act(() => setQueue([mockTrack, mockTrack2]))
      act(() => setQueueIndex(1))
      act(() => playNext())
      expect(usePlayerStore.getState().queueIndex).toBe(0)
      expect(usePlayerStore.getState().currentTrack?.id).toBe('test-track-1')
    })

    it('should stay on same track in single mode', () => {
      const { setQueue, setQueueIndex, setPlaybackMode, playNext } = usePlayerStore.getState()
      act(() => setQueue([mockTrack, mockTrack2]))
      act(() => setQueueIndex(0))
      act(() => setPlaybackMode('single'))
      act(() => playNext())
      expect(usePlayerStore.getState().queueIndex).toBe(0)
    })
  })

  describe('playPrevious', () => {
    it('should not change track when queue is empty', () => {
      const { playPrevious } = usePlayerStore.getState()
      act(() => playPrevious())
      expect(usePlayerStore.getState().currentTrack).toBeNull()
    })

    it('should play previous track in sequence mode', () => {
      const { setQueue, setQueueIndex, playPrevious } = usePlayerStore.getState()
      act(() => setQueue([mockTrack, mockTrack2]))
      act(() => setQueueIndex(1))
      act(() => playPrevious())
      expect(usePlayerStore.getState().queueIndex).toBe(0)
      expect(usePlayerStore.getState().currentTrack?.id).toBe('test-track-1')
    })

    it('should wrap around to last track', () => {
      const { setQueue, setQueueIndex, playPrevious } = usePlayerStore.getState()
      act(() => setQueue([mockTrack, mockTrack2]))
      act(() => setQueueIndex(0))
      act(() => playPrevious())
      expect(usePlayerStore.getState().queueIndex).toBe(1)
      expect(usePlayerStore.getState().currentTrack?.id).toBe('test-track-2')
    })
  })
})

describe('LibraryStore', () => {
  const mockTrack: Track = {
    id: 'lib-track-1',
    path: '/path/to/libtrack.mp3',
    title: 'Library Track',
    artist: 'Library Artist',
    album: 'Library Album',
    duration: 200,
  }

  const mockPlaylist: Playlist = {
    id: 'playlist-1',
    name: 'Test Playlist',
    tracks: ['lib-track-1'],
    createdAt: new Date(),
    updatedAt: new Date(),
  }

  beforeEach(() => {
    useLibraryStore.setState({
      tracks: new Map(),
      playlists: [],
      currentView: 'all',
      searchQuery: '',
      likedTrackIds: new Set<string>(),
    })
  })

  describe('track management', () => {
    it('should set tracks correctly', () => {
      const { setTracks } = useLibraryStore.getState()
      act(() => setTracks([mockTrack]))
      const tracks = useLibraryStore.getState().tracks
      expect(tracks.size).toBe(1)
      expect(tracks.get('lib-track-1')).toEqual(mockTrack)
    })

    it('should add track correctly', () => {
      const { addTrack } = useLibraryStore.getState()
      act(() => addTrack(mockTrack))
      expect(useLibraryStore.getState().tracks.size).toBe(1)
    })

    it('should remove track correctly', () => {
      const { setTracks, removeTrack } = useLibraryStore.getState()
      act(() => setTracks([mockTrack]))
      act(() => removeTrack('lib-track-1'))
      expect(useLibraryStore.getState().tracks.size).toBe(0)
    })
  })

  describe('playlist management', () => {
    it('should add playlist correctly', () => {
      const { addPlaylist } = useLibraryStore.getState()
      act(() => addPlaylist(mockPlaylist))
      expect(useLibraryStore.getState().playlists).toHaveLength(1)
    })

    it('should remove playlist correctly', () => {
      const { addPlaylist, removePlaylist } = useLibraryStore.getState()
      act(() => addPlaylist(mockPlaylist))
      act(() => removePlaylist('playlist-1'))
      expect(useLibraryStore.getState().playlists).toHaveLength(0)
    })

    it('should update playlist correctly', () => {
      const { addPlaylist, updatePlaylist } = useLibraryStore.getState()
      act(() => addPlaylist(mockPlaylist))
      act(() => updatePlaylist('playlist-1', { name: 'Updated Playlist' }))
      expect(useLibraryStore.getState().playlists[0].name).toBe('Updated Playlist')
    })
  })

  describe('like functionality', () => {
    it('should toggle like on track', () => {
      const { setTracks, toggleLike } = useLibraryStore.getState()
      act(() => setTracks([mockTrack]))
      act(() => toggleLike('lib-track-1'))
      expect(useLibraryStore.getState().likedTrackIds.has('lib-track-1')).toBe(true)
      act(() => toggleLike('lib-track-1'))
      expect(useLibraryStore.getState().likedTrackIds.has('lib-track-1')).toBe(false)
    })

    it('should check if track is liked', () => {
      const { toggleLike, isLiked } = useLibraryStore.getState()
      act(() => toggleLike('lib-track-1'))
      expect(isLiked('lib-track-1')).toBe(true)
    })

    it('should get liked tracks', () => {
      const { setTracks, toggleLike, getLikedTracks } = useLibraryStore.getState()
      act(() => setTracks([mockTrack]))
      act(() => toggleLike('lib-track-1'))
      const liked = getLikedTracks()
      expect(liked).toHaveLength(1)
      expect(liked[0].id).toBe('lib-track-1')
    })
  })

  describe('view and search', () => {
    it('should set current view', () => {
      const { setCurrentView } = useLibraryStore.getState()
      act(() => setCurrentView('artists'))
      expect(useLibraryStore.getState().currentView).toBe('artists')
    })

    it('should set search query', () => {
      const { setSearchQuery } = useLibraryStore.getState()
      act(() => setSearchQuery('test query'))
      expect(useLibraryStore.getState().searchQuery).toBe('test query')
    })
  })
})

describe('SettingsStore', () => {
  beforeEach(() => {
    useSettingsStore.setState({
      settings: {
        theme: 'dark',
        language: 'zh-CN',
        musicFolders: [],
        lastPlayedTrack: null,
        volume: 0.8,
        playbackMode: 'sequence',
        showLyricsWindow: false,
        lyricsWindowSettings: {
          fontFamily: 'Microsoft YaHei',
          fontSize: 24,
          color: '#ffffff',
          backgroundColor: '#000000',
          opacity: 0.8,
          displayMode: 'double',
        },
      },
    })
  })

  describe('updateSettings', () => {
    it('should update theme setting', () => {
      const { updateSettings } = useSettingsStore.getState()
      act(() => updateSettings({ theme: 'light' }))
      expect(useSettingsStore.getState().settings.theme).toBe('light')
    })

    it('should update volume setting', () => {
      const { updateSettings } = useSettingsStore.getState()
      act(() => updateSettings({ volume: 0.5 }))
      expect(useSettingsStore.getState().settings.volume).toBe(0.5)
    })

    it('should update music folders', () => {
      const { updateSettings } = useSettingsStore.getState()
      act(() => updateSettings({ musicFolders: ['/path/to/music'] }))
      expect(useSettingsStore.getState().settings.musicFolders).toContain('/path/to/music')
    })
  })

  describe('updateLyricsWindowSettings', () => {
    it('should update lyrics window font size', () => {
      const { updateLyricsWindowSettings } = useSettingsStore.getState()
      act(() => updateLyricsWindowSettings({ fontSize: 32 }))
      expect(useSettingsStore.getState().settings.lyricsWindowSettings.fontSize).toBe(32)
    })

    it('should update lyrics window opacity', () => {
      const { updateLyricsWindowSettings } = useSettingsStore.getState()
      act(() => updateLyricsWindowSettings({ opacity: 0.5 }))
      expect(useSettingsStore.getState().settings.lyricsWindowSettings.opacity).toBe(0.5)
    })
  })
})
