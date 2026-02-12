import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { Track, PlaybackMode, Playlist, AppSettings, LyricsWindowSettings } from '@/types'

interface PlayerStore {
  currentTrack: Track | null
  isPlaying: boolean
  volume: number
  currentTime: number
  duration: number
  playbackMode: PlaybackMode
  queue: Track[]
  queueIndex: number
  
  setCurrentTrack: (track: Track | null) => void
  setIsPlaying: (isPlaying: boolean) => void
  setVolume: (volume: number) => void
  setCurrentTime: (time: number) => void
  setDuration: (duration: number) => void
  setPlaybackMode: (mode: PlaybackMode) => void
  setQueue: (tracks: Track[]) => void
  setQueueIndex: (index: number) => void
  playNext: () => void
  playPrevious: () => void
  addToQueue: (track: Track) => void
  removeFromQueue: (trackId: string) => void
  clearQueue: () => void
}

export const usePlayerStore = create<PlayerStore>((set, get) => ({
  currentTrack: null,
  isPlaying: false,
  volume: 0.8,
  currentTime: 0,
  duration: 0,
  playbackMode: 'sequence',
  queue: [],
  queueIndex: -1,

  setCurrentTrack: (track) => set({ currentTrack: track }),
  setIsPlaying: (isPlaying) => set({ isPlaying }),
  setVolume: (volume) => set({ volume }),
  setCurrentTime: (currentTime) => set({ currentTime }),
  setDuration: (duration) => set({ duration }),
  setPlaybackMode: (playbackMode) => set({ playbackMode }),
  setQueue: (queue) => set({ queue }),
  setQueueIndex: (queueIndex) => set({ queueIndex }),

  playNext: () => {
    const { queue, queueIndex, playbackMode } = get()
    if (queue.length === 0) return

    let nextIndex = queueIndex
    if (playbackMode === 'random') {
      nextIndex = Math.floor(Math.random() * queue.length)
    } else if (playbackMode === 'single') {
      nextIndex = queueIndex
    } else {
      nextIndex = (queueIndex + 1) % queue.length
    }

    set({ queueIndex: nextIndex, currentTrack: queue[nextIndex] })
  },

  playPrevious: () => {
    const { queue, queueIndex, playbackMode } = get()
    if (queue.length === 0) return

    let prevIndex = queueIndex
    if (playbackMode === 'random') {
      prevIndex = Math.floor(Math.random() * queue.length)
    } else {
      prevIndex = queueIndex - 1 < 0 ? queue.length - 1 : queueIndex - 1
    }

    set({ queueIndex: prevIndex, currentTrack: queue[prevIndex] })
  },

  addToQueue: (track) => set((state) => ({ queue: [...state.queue, track] })),
  removeFromQueue: (trackId) => set((state) => ({ 
    queue: state.queue.filter(t => t.id !== trackId) 
  })),
  clearQueue: () => set({ queue: [], queueIndex: -1 }),
}))

interface LibraryStore {
  tracks: Map<string, Track>
  playlists: Playlist[]
  currentView: 'all' | 'artists' | 'albums' | 'genres' | 'folders' | 'favorites'
  searchQuery: string
  likedTrackIds: Set<string>
  
  setTracks: (tracks: Track[]) => void
  addTrack: (track: Track) => void
  removeTrack: (trackId: string) => void
  setPlaylists: (playlists: Playlist[]) => void
  addPlaylist: (playlist: Playlist) => void
  removePlaylist: (playlistId: string) => void
  updatePlaylist: (playlistId: string, updates: Partial<Playlist>) => void
  setCurrentView: (view: 'all' | 'artists' | 'albums' | 'genres' | 'folders' | 'favorites') => void
  setSearchQuery: (query: string) => void
  toggleLike: (trackId: string) => void
  isLiked: (trackId: string) => boolean
  getLikedTracks: () => Track[]
}

export const useLibraryStore = create<LibraryStore>()(
  persist(
    (set, get) => ({
      tracks: new Map(),
      playlists: [],
      currentView: 'all',
      searchQuery: '',
      likedTrackIds: new Set<string>(),

      setTracks: (tracks) => {
        const trackMap = new Map<string, Track>()
        tracks.forEach(track => trackMap.set(track.id, track))
        set({ tracks: trackMap })
      },

      addTrack: (track) => set((state) => {
        const newTracks = new Map(state.tracks)
        newTracks.set(track.id, track)
        return { tracks: newTracks }
      }),

      removeTrack: (trackId) => set((state) => {
        const newTracks = new Map(state.tracks)
        newTracks.delete(trackId)
        const newLikedIds = new Set(state.likedTrackIds)
        newLikedIds.delete(trackId)
        return { tracks: newTracks, likedTrackIds: newLikedIds }
      }),

      setPlaylists: (playlists) => set({ playlists }),
      
      addPlaylist: (playlist) => set((state) => ({
        playlists: [...state.playlists, playlist]
      })),

      removePlaylist: (playlistId) => set((state) => ({
        playlists: state.playlists.filter(p => p.id !== playlistId)
      })),

      updatePlaylist: (playlistId, updates) => set((state) => ({
        playlists: state.playlists.map(p => 
          p.id === playlistId ? { ...p, ...updates, updatedAt: new Date() } : p
        )
      })),

      setCurrentView: (currentView) => set({ currentView }),
      setSearchQuery: (searchQuery) => set({ searchQuery }),

      toggleLike: (trackId) => set((state) => {
        const newLikedIds = new Set(state.likedTrackIds)
        if (newLikedIds.has(trackId)) {
          newLikedIds.delete(trackId)
        } else {
          newLikedIds.add(trackId)
        }
        return { likedTrackIds: newLikedIds }
      }),

      isLiked: (trackId) => {
        return get().likedTrackIds.has(trackId)
      },

      getLikedTracks: () => {
        const { tracks, likedTrackIds } = get()
        const likedTracks: Track[] = []
        likedTrackIds.forEach(id => {
          const track = tracks.get(id)
          if (track) likedTracks.push(track)
        })
        return likedTracks
      },
    }),
    {
      name: 'music-library',
      serialize: (state) => {
        const tracksArray = Array.from(state.tracks.entries())
        const likedArray = Array.from(state.likedTrackIds)
        return JSON.stringify({ ...state, tracks: tracksArray, likedTrackIds: likedArray })
      },
      deserialize: (str) => {
        const data = JSON.parse(str)
        return { 
          ...data, 
          tracks: new Map(data.tracks || []),
          likedTrackIds: new Set(data.likedTrackIds || [])
        }
      },
    }
  )
)

interface SettingsStore {
  settings: AppSettings
  updateSettings: (updates: Partial<AppSettings>) => void
  updateLyricsWindowSettings: (updates: Partial<LyricsWindowSettings>) => void
}

const defaultSettings: AppSettings = {
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
}

export const useSettingsStore = create<SettingsStore>()(
  persist(
    (set) => ({
      settings: defaultSettings,

      updateSettings: (updates) => set((state) => ({
        settings: { ...state.settings, ...updates }
      })),

      updateLyricsWindowSettings: (updates) => set((state) => ({
        settings: {
          ...state.settings,
          lyricsWindowSettings: { ...state.settings.lyricsWindowSettings, ...updates }
        }
      })),
    }),
    { name: 'app-settings' }
  )
)
