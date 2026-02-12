export interface ElectronAPI {
  minimizeWindow: () => Promise<void>
  maximizeWindow: () => Promise<void>
  closeWindow: () => Promise<void>
  openFiles: () => Promise<string[]>
  openFolder: () => Promise<string>
  readMetadata: (filePath: string) => Promise<FileMetadata | null>
  showLyrics: () => Promise<void>
  hideLyrics: () => Promise<void>
  updateLyrics: (lyrics: string, currentTime: number) => Promise<void>
  onLyricsUpdate: (callback: (lyrics: string, currentTime: number) => void) => void
}

declare global {
  interface Window {
    electronAPI: ElectronAPI
  }
}

export interface FileMetadata {
  path: string
  size: number
  modified: Date
}

export interface Track {
  id: string
  path: string
  title: string
  artist: string
  album: string
  duration: number
  cover?: string
  genre?: string
  year?: number
  trackNumber?: number
}

export interface Playlist {
  id: string
  name: string
  description?: string
  tracks: string[]
  createdAt: Date
  updatedAt: Date
  cover?: string
}

export interface LyricsLine {
  time: number
  text: string
}

export interface PlayerState {
  currentTrack: Track | null
  isPlaying: boolean
  volume: number
  currentTime: number
  duration: number
  playbackMode: PlaybackMode
  queue: Track[]
  queueIndex: number
}

export type PlaybackMode = 'sequence' | 'random' | 'single' | 'loop'

export type LibraryView = 'all' | 'artists' | 'albums' | 'genres' | 'folders'

export interface Artist {
  name: string
  tracks: Track[]
  albums: Album[]
}

export interface Album {
  name: string
  artist: string
  tracks: Track[]
  cover?: string
  year?: number
}

export interface MusicLibrary {
  tracks: Map<string, Track>
  artists: Map<string, Artist>
  albums: Map<string, Album>
  genres: Map<string, Track[]>
  folders: Map<string, Track[]>
}

export interface AppSettings {
  theme: 'light' | 'dark'
  language: string
  musicFolders: string[]
  lastPlayedTrack: string | null
  volume: number
  playbackMode: PlaybackMode
  showLyricsWindow: boolean
  lyricsWindowSettings: LyricsWindowSettings
}

export interface LyricsWindowSettings {
  fontFamily: string
  fontSize: number
  color: string
  backgroundColor: string
  opacity: number
  displayMode: 'single' | 'double'
}
