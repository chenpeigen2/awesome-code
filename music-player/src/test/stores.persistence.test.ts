import { describe, it, expect, beforeEach } from 'vitest'
import { Track } from '@/types'

function createMemoryStorage() {
  const data = new Map<string, string>()
  return {
    data,
    getItem: (key: string) => data.get(key) ?? null,
    setItem: (key: string, value: string) => {
      data.set(key, value)
    },
    removeItem: (key: string) => {
      data.delete(key)
    },
    clear: () => data.clear(),
    key: (index: number) => Array.from(data.keys())[index] ?? null,
    get length() {
      return data.size
    },
  }
}

const storage = createMemoryStorage()
Object.defineProperty(window, 'localStorage', { value: storage, writable: true })

const { useLibraryStore } = await import('@/stores')

const mockTrack: Track = {
  id: 'track-1',
  path: '/path/to/track.mp3',
  title: 'Persisted Song',
  artist: 'Persisted Artist',
  album: 'Persisted Album',
  duration: 123,
}

describe('LibraryStore 持久化', () => {
  beforeEach(() => {
    storage.clear()
    useLibraryStore.setState({ tracks: new Map(), likedTrackIds: new Set() })
  })

  it('把 Map 形态的曲目与 Set 形态的收藏写入 localStorage', () => {
    useLibraryStore.getState().setTracks([mockTrack])
    useLibraryStore.getState().toggleLike(mockTrack.id)

    const raw = storage.getItem('music-library')
    expect(raw).not.toBeNull()

    const persisted = JSON.parse(raw as string)
    expect(JSON.stringify(persisted)).toContain(mockTrack.id)
  })

  it('重新水合后恢复为 Map 与 Set', async () => {
    useLibraryStore.getState().setTracks([mockTrack])
    useLibraryStore.getState().toggleLike(mockTrack.id)

    const snapshot = storage.getItem('music-library') as string

    // 清空当前内存状态，再把落盘数据放回，模拟应用重启
    useLibraryStore.setState({ tracks: new Map(), likedTrackIds: new Set() })
    storage.setItem('music-library', snapshot)

    await useLibraryStore.persist.rehydrate()

    const state = useLibraryStore.getState()
    expect(state.tracks).toBeInstanceOf(Map)
    expect(state.tracks.get(mockTrack.id)?.title).toBe('Persisted Song')
    expect(state.likedTrackIds).toBeInstanceOf(Set)
    expect(state.likedTrackIds.has(mockTrack.id)).toBe(true)
  })
})
