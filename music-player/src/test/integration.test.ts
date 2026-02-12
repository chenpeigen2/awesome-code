import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { act, waitFor } from '@testing-library/react'

describe('Integration Tests', () => {
  describe('Player and Library Integration', () => {
    beforeEach(async () => {
      vi.resetModules()
    })

    afterEach(() => {
      vi.clearAllMocks()
    })

    it('should integrate player store with track management', async () => {
      const { usePlayerStore, useLibraryStore } = await import('@/stores')
      
      const mockTrack = {
        id: 'integration-track-1',
        path: '/path/to/song.mp3',
        title: 'Integration Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }

      act(() => {
        useLibraryStore.getState().setTracks([mockTrack])
        usePlayerStore.getState().setCurrentTrack(mockTrack)
        usePlayerStore.getState().setIsPlaying(true)
      })

      expect(usePlayerStore.getState().currentTrack).toEqual(mockTrack)
      expect(usePlayerStore.getState().isPlaying).toBe(true)
      expect(useLibraryStore.getState().tracks.size).toBe(1)
    })

    it('should handle queue management across stores', async () => {
      const { usePlayerStore, useLibraryStore } = await import('@/stores')
      
      const tracks = [
        { id: 'queue-1', path: '/path/1.mp3', title: 'Song 1', artist: 'Artist 1', duration: 180 },
        { id: 'queue-2', path: '/path/2.mp3', title: 'Song 2', artist: 'Artist 2', duration: 200 },
        { id: 'queue-3', path: '/path/3.mp3', title: 'Song 3', artist: 'Artist 3', duration: 220 },
      ]

      act(() => {
        useLibraryStore.getState().setTracks(tracks)
        usePlayerStore.getState().setQueue(tracks)
        usePlayerStore.getState().setQueueIndex(0)
      })

      expect(usePlayerStore.getState().queue).toHaveLength(3)
      expect(usePlayerStore.getState().queueIndex).toBe(0)

      act(() => {
        usePlayerStore.getState().playNext()
      })

      expect(usePlayerStore.getState().queueIndex).toBe(1)
    })

    it('should handle playback mode changes', async () => {
      const { usePlayerStore } = await import('@/stores')
      
      const modes: Array<'sequence' | 'random' | 'single' | 'loop'> = ['sequence', 'random', 'single', 'loop']
      
      for (const mode of modes) {
        act(() => {
          usePlayerStore.getState().setPlaybackMode(mode)
        })
        expect(usePlayerStore.getState().playbackMode).toBe(mode)
      }
    })

    it('should handle like functionality integration', async () => {
      const { useLibraryStore } = await import('@/stores')
      
      const track = {
        id: 'like-test-1',
        path: '/path/to/liked.mp3',
        title: 'Liked Song',
        artist: 'Artist',
        duration: 180,
      }

      act(() => {
        useLibraryStore.getState().setTracks([track])
        useLibraryStore.getState().toggleLike('like-test-1')
      })

      expect(useLibraryStore.getState().isLiked('like-test-1')).toBe(true)
      expect(useLibraryStore.getState().likedTrackIds.size).toBe(1)

      act(() => {
        useLibraryStore.getState().toggleLike('like-test-1')
      })

      expect(useLibraryStore.getState().isLiked('like-test-1')).toBe(false)
    })

    it('should handle playlist management', async () => {
      const { useLibraryStore } = await import('@/stores')
      
      const playlist = {
        id: 'playlist-integration-1',
        name: 'Integration Playlist',
        tracks: ['track-1', 'track-2'],
        createdAt: new Date(),
        updatedAt: new Date(),
      }

      act(() => {
        useLibraryStore.getState().addPlaylist(playlist)
      })

      expect(useLibraryStore.getState().playlists).toHaveLength(1)

      act(() => {
        useLibraryStore.getState().updatePlaylist('playlist-integration-1', { name: 'Updated Playlist' })
      })

      expect(useLibraryStore.getState().playlists[0].name).toBe('Updated Playlist')

      act(() => {
        useLibraryStore.getState().removePlaylist('playlist-integration-1')
      })

      expect(useLibraryStore.getState().playlists).toHaveLength(0)
    })
  })

  describe('Settings Integration', () => {
    beforeEach(async () => {
      vi.resetModules()
    })

    it('should handle settings persistence', async () => {
      const { useSettingsStore } = await import('@/stores')
      
      act(() => {
        useSettingsStore.getState().updateSettings({
          theme: 'light',
          language: 'en-US',
          volume: 0.5,
        })
      })

      const settings = useSettingsStore.getState().settings
      expect(settings.theme).toBe('light')
      expect(settings.language).toBe('en-US')
      expect(settings.volume).toBe(0.5)
    })

    it('should handle lyrics window settings', async () => {
      const { useSettingsStore } = await import('@/stores')
      
      act(() => {
        useSettingsStore.getState().updateLyricsWindowSettings({
          fontSize: 32,
          opacity: 0.9,
          color: '#ff0000',
        })
      })

      const lyricsSettings = useSettingsStore.getState().settings.lyricsWindowSettings
      expect(lyricsSettings.fontSize).toBe(32)
      expect(lyricsSettings.opacity).toBe(0.9)
      expect(lyricsSettings.color).toBe('#ff0000')
    })
  })

  describe('Download Manager Integration', () => {
    beforeEach(async () => {
      vi.resetModules()
    })

    it('should handle download task lifecycle', async () => {
      const { downloadManager } = await import('@/utils/downloadManager')
      
      const searchResult = {
        id: 'dl-test-1',
        title: 'Download Test',
        artist: 'Test Artist',
        duration: '3:00',
        downloadUrl: 'https://example.com/test.mp3',
      }

      downloadManager.updateSettings({ autoStart: false })
      
      const task = downloadManager.addTask(searchResult)
      expect(task.status).toBe('pending')

      const allTasks = downloadManager.getAllTasks()
      expect(allTasks.length).toBe(1)

      downloadManager.cancelDownload(task.id)
      expect(downloadManager.getAllTasks().length).toBe(0)
    })

    it('should handle multiple concurrent downloads', async () => {
      const { downloadManager } = await import('@/utils/downloadManager')
      
      downloadManager.updateSettings({ autoStart: false, maxConcurrent: 3 })
      
      const tasks = [
        { id: 'multi-1', title: 'Song 1', artist: 'Artist 1', duration: '3:00', downloadUrl: 'https://example.com/1.mp3' },
        { id: 'multi-2', title: 'Song 2', artist: 'Artist 2', duration: '3:30', downloadUrl: 'https://example.com/2.mp3' },
        { id: 'multi-3', title: 'Song 3', artist: 'Artist 3', duration: '4:00', downloadUrl: 'https://example.com/3.mp3' },
      ]

      tasks.forEach(t => downloadManager.addTask(t))
      
      expect(downloadManager.getAllTasks().length).toBe(3)
      
      downloadManager.clearAll()
      expect(downloadManager.getAllTasks().length).toBe(0)
    })
  })

  describe('Lyrics Integration', () => {
    beforeEach(async () => {
      vi.resetModules()
    })

    it('should parse and format lyrics correctly', async () => {
      const { parseLrc, formatLrcTime, getCurrentLyric } = await import('@/utils/lyrics')
      
      const lrcContent = `[00:00.00]First line
[00:05.00]Second line
[00:10.00]Third line`

      const parsed = parseLrc(lrcContent)
      expect(parsed).toHaveLength(3)

      const currentLyric = getCurrentLyric(parsed, 7)
      expect(currentLyric?.text).toBe('Second line')

      const formattedTime = formatLrcTime(125.5)
      expect(formattedTime).toBe('02:05.50')
    })

    it('should handle empty lyrics gracefully', async () => {
      const { parseLrc, getCurrentLyric } = await import('@/utils/lyrics')
      
      const parsed = parseLrc('')
      expect(parsed).toHaveLength(0)

      const currentLyric = getCurrentLyric([], 10)
      expect(currentLyric).toBeNull()
    })
  })

  describe('Audio Manager Integration', () => {
    it('should handle audio state transitions', async () => {
      vi.resetModules()
      
      class MockAudio {
        src: string = ''
        crossOrigin: string = ''
        currentTime: number = 0
        duration: number = 180
        volume: number = 1
        paused: boolean = true
        
        private listeners: Map<string, EventListener[]> = new Map()
        
        addEventListener(type: string, listener: EventListener) {
          if (!this.listeners.has(type)) this.listeners.set(type, [])
          this.listeners.get(type)!.push(listener)
        }
        
        removeEventListener(type: string, listener: EventListener) {
          const arr = this.listeners.get(type)
          if (arr) {
            const idx = arr.indexOf(listener)
            if (idx > -1) arr.splice(idx, 1)
          }
        }
        
        dispatchEvent(event: Event): boolean {
          const arr = this.listeners.get(event.type)
          if (arr) arr.forEach(l => l(event))
          return true
        }
        
        async play(): Promise<void> {
          this.paused = false
          this.dispatchEvent(new Event('play'))
        }
        
        pause(): void {
          this.paused = true
          this.dispatchEvent(new Event('pause'))
        }
        
        load(): void {
          setTimeout(() => this.dispatchEvent(new Event('loadedmetadata')), 0)
        }
      }

      vi.stubGlobal('Audio', MockAudio)

      const { audioManager } = await import('@/utils/audio')
      
      const track = {
        id: 'audio-integration-1',
        path: '/path/to/audio.mp3',
        title: 'Audio Test',
        artist: 'Test Artist',
        duration: 180,
      }

      const loadPromise = audioManager.load(track)
      
      await waitFor(() => {
        expect(audioManager.getCurrentTrack()).toEqual(track)
      })

      expect(audioManager.getState()).toBe('paused')
    })
  })
})
