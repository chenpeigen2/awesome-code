import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'

class MockAudioElement {
  src: string = ''
  crossOrigin: string = ''
  currentTime: number = 0
  duration: number = 180
  volume: number = 1
  paused: boolean = true
  error: MediaError | null = null
  
  private eventListeners: Map<string, EventListener[]> = new Map()
  
  addEventListener(type: string, listener: EventListener) {
    if (!this.eventListeners.has(type)) {
      this.eventListeners.set(type, [])
    }
    this.eventListeners.get(type)!.push(listener)
  }
  
  removeEventListener(type: string, listener: EventListener) {
    const listeners = this.eventListeners.get(type)
    if (listeners) {
      const index = listeners.indexOf(listener)
      if (index > -1) listeners.splice(index, 1)
    }
  }
  
  dispatchEvent(event: Event): boolean {
    const listeners = this.eventListeners.get(event.type)
    if (listeners) {
      listeners.forEach(listener => listener(event))
    }
    return true
  }
  
  async play(): Promise<void> {
    this.paused = false
    this.dispatchEvent(new Event('play'))
    return Promise.resolve()
  }
  
  pause(): void {
    this.paused = true
    this.dispatchEvent(new Event('pause'))
  }
  
  load(): void {
    setTimeout(() => {
      this.dispatchEvent(new Event('loadedmetadata'))
    }, 0)
  }
  
  simulateError(code: number) {
    this.error = { code, message: 'Error' } as MediaError
    this.dispatchEvent(new Event('error'))
  }
  
  simulateTimeUpdate(time: number) {
    this.currentTime = time
    this.dispatchEvent(new Event('timeupdate'))
  }
  
  simulateEnded() {
    this.dispatchEvent(new Event('ended'))
  }
  
  simulateCanPlay() {
    this.dispatchEvent(new Event('canplay'))
  }
}

describe('AudioManager', () => {
  let mockAudio: MockAudioElement
  let AudioManager: typeof import('@/utils/audio').audioManager
  
  beforeEach(async () => {
    vi.resetModules()
    
    mockAudio = new MockAudioElement()
    
    class MockAudio {
      constructor() {
        return mockAudio
      }
    }
    
    vi.stubGlobal('Audio', MockAudio)
    vi.stubGlobal('MediaError', {
      MEDIA_ERR_ABORTED: 1,
      MEDIA_ERR_NETWORK: 2,
      MEDIA_ERR_DECODE: 3,
      MEDIA_ERR_SRC_NOT_SUPPORTED: 4,
    })
    
    const module = await import('@/utils/audio')
    AudioManager = module.audioManager
  })
  
  afterEach(() => {
    vi.unstubAllGlobals()
    vi.clearAllMocks()
  })

  describe('initialization', () => {
    it('should initialize with default state', () => {
      expect(AudioManager.getState()).toBe('idle')
      expect(AudioManager.isPlaying()).toBe(false)
      expect(AudioManager.getCurrentTrack()).toBeNull()
      expect(AudioManager.getCurrentTime()).toBe(0)
      expect(AudioManager.getDuration()).toBe(180)
      expect(AudioManager.getVolume()).toBe(1)
    })
  })

  describe('load', () => {
    it('should load a track successfully', async () => {
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      
      await expect(loadPromise).resolves.toBeUndefined()
      expect(AudioManager.getCurrentTrack()).toEqual(track)
      expect(AudioManager.getState()).toBe('paused')
    })

    it('should load an online URL track', async () => {
      const track = {
        id: 'test-2',
        path: 'https://example.com/song.mp3',
        title: 'Online Song',
        artist: 'Online Artist',
        album: 'Online Album',
        duration: 200,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      
      await expect(loadPromise).resolves.toBeUndefined()
      expect(mockAudio.crossOrigin).toBe('anonymous')
    })

    it('should load a local:// URL track', async () => {
      const track = {
        id: 'test-3',
        path: 'local:///path/to/song.mp3',
        title: 'Local Song',
        artist: 'Local Artist',
        album: 'Local Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      
      await expect(loadPromise).resolves.toBeUndefined()
    })

    it('should load a file:// URL track', async () => {
      const track = {
        id: 'test-4',
        path: 'file:///path/to/song.mp3',
        title: 'File Song',
        artist: 'File Artist',
        album: 'File Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      
      await expect(loadPromise).resolves.toBeUndefined()
    })

    it('should reject on load error', async () => {
      const track = {
        id: 'test-5',
        path: '/invalid/path.mp3',
        title: 'Invalid',
        artist: 'Invalid',
        album: 'Invalid',
        duration: 0,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('error'))
      
      await expect(loadPromise).rejects.toThrow('Failed to load audio')
    })
  })

  describe('play', () => {
    it('should play a loaded track', async () => {
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      AudioManager.play()
      
      expect(mockAudio.paused).toBe(false)
    })

    it('should not play when no track is loaded', () => {
      AudioManager.play()
      expect(mockAudio.paused).toBe(true)
    })
  })

  describe('pause', () => {
    it('should pause a playing track', async () => {
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      AudioManager.play()
      AudioManager.pause()
      
      expect(mockAudio.paused).toBe(true)
    })
  })

  describe('stop', () => {
    it('should stop the current track', async () => {
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      AudioManager.play()
      AudioManager.stop()
      
      expect(AudioManager.getCurrentTrack()).toBeNull()
      expect(AudioManager.getState()).toBe('idle')
      expect(mockAudio.currentTime).toBe(0)
    })
  })

  describe('seek', () => {
    it('should seek to a specific time', async () => {
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      AudioManager.seek(60)
      expect(mockAudio.currentTime).toBe(60)
    })

    it('should not seek to negative time', () => {
      AudioManager.seek(-10)
      expect(mockAudio.currentTime).toBe(0)
    })

    it('should not seek beyond duration', async () => {
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      AudioManager.seek(999)
      expect(mockAudio.currentTime).toBe(180)
    })

    it('should not seek with NaN', () => {
      AudioManager.seek(NaN)
      expect(mockAudio.currentTime).toBe(0)
    })
  })

  describe('setVolume', () => {
    it('should set volume correctly', () => {
      AudioManager.setVolume(0.5)
      expect(mockAudio.volume).toBe(0.5)
    })

    it('should not set volume below 0', () => {
      AudioManager.setVolume(-0.5)
      expect(mockAudio.volume).toBe(1)
    })

    it('should not set volume above 1', () => {
      AudioManager.setVolume(1.5)
      expect(mockAudio.volume).toBe(1)
    })

    it('should set volume to 0', () => {
      AudioManager.setVolume(0)
      expect(mockAudio.volume).toBe(0)
    })

    it('should set volume to 1', () => {
      AudioManager.setVolume(1)
      expect(mockAudio.volume).toBe(1)
    })
  })

  describe('callbacks', () => {
    it('should call onPlay callback', async () => {
      const onPlayCallback = vi.fn()
      AudioManager.onPlay(onPlayCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      AudioManager.play()
      
      expect(onPlayCallback).toHaveBeenCalled()
    })

    it('should call onPause callback', async () => {
      const onPauseCallback = vi.fn()
      AudioManager.onPause(onPauseCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      AudioManager.play()
      AudioManager.pause()
      
      expect(onPauseCallback).toHaveBeenCalled()
    })

    it('should call onEnd callback', async () => {
      const onEndCallback = vi.fn()
      AudioManager.onEnd(onEndCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      mockAudio.simulateEnded()
      
      expect(onEndCallback).toHaveBeenCalled()
      expect(AudioManager.getState()).toBe('ended')
    })

    it('should call onTimeUpdate callback', async () => {
      const onTimeUpdateCallback = vi.fn()
      AudioManager.onTimeUpdate(onTimeUpdateCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      mockAudio.simulateTimeUpdate(30)
      
      expect(onTimeUpdateCallback).toHaveBeenCalledWith(30)
    })

    it('should call onLoad callback', async () => {
      const onLoadCallback = vi.fn()
      AudioManager.onLoad(onLoadCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      expect(onLoadCallback).toHaveBeenCalledWith(180)
    })

    it('should call onStateChange callback', async () => {
      const onStateChangeCallback = vi.fn()
      AudioManager.onStateChange(onStateChangeCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      expect(onStateChangeCallback).toHaveBeenCalledWith('paused')
    })
  })

  describe('error handling', () => {
    it('should handle MEDIA_ERR_ABORTED error', async () => {
      const onErrorCallback = vi.fn()
      AudioManager.onError(onErrorCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      mockAudio.simulateError(1)
      
      expect(onErrorCallback).toHaveBeenCalledWith('播放被中止')
    })

    it('should handle MEDIA_ERR_NETWORK error', async () => {
      const onErrorCallback = vi.fn()
      AudioManager.onError(onErrorCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      mockAudio.simulateError(2)
      
      expect(onErrorCallback).toHaveBeenCalledWith('网络错误，无法加载音频')
    })

    it('should handle MEDIA_ERR_DECODE error', async () => {
      const onErrorCallback = vi.fn()
      AudioManager.onError(onErrorCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      mockAudio.simulateError(3)
      
      expect(onErrorCallback).toHaveBeenCalledWith('音频解码错误')
    })

    it('should handle MEDIA_ERR_SRC_NOT_SUPPORTED error', async () => {
      const onErrorCallback = vi.fn()
      AudioManager.onError(onErrorCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      mockAudio.simulateError(4)
      
      expect(onErrorCallback).toHaveBeenCalledWith('不支持的音频格式')
    })

    it('should handle unknown error', async () => {
      const onErrorCallback = vi.fn()
      AudioManager.onError(onErrorCallback)
      
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      mockAudio.error = null
      mockAudio.dispatchEvent(new Event('error'))
      
      expect(onErrorCallback).toHaveBeenCalledWith('Unknown error')
    })
  })

  describe('isPlaying', () => {
    it('should return true when playing', async () => {
      const track = {
        id: 'test-1',
        path: '/path/to/song.mp3',
        title: 'Test Song',
        artist: 'Test Artist',
        album: 'Test Album',
        duration: 180,
      }
      
      const loadPromise = AudioManager.load(track)
      mockAudio.dispatchEvent(new Event('loadedmetadata'))
      await loadPromise
      
      AudioManager.play()
      
      expect(AudioManager.isPlaying()).toBe(true)
    })

    it('should return false when not playing', () => {
      expect(AudioManager.isPlaying()).toBe(false)
    })
  })
})
