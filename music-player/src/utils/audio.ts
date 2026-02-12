import { Track } from '@/types'

type PlayState = 'idle' | 'loading' | 'playing' | 'paused' | 'ended'

class AudioManager {
  private audio: HTMLAudioElement | null = null
  private currentTrack: Track | null = null
  private state: PlayState = 'idle'
  
  private onPlayCallback: (() => void) | null = null
  private onPauseCallback: (() => void) | null = null
  private onEndCallback: (() => void) | null = null
  private onTimeUpdateCallback: ((time: number) => void) | null = null
  private onLoadCallback: ((duration: number) => void) | null = null
  private onErrorCallback: ((error: string) => void) | null = null
  private onStateChangeCallback: ((state: PlayState) => void) | null = null

  constructor() {
    this.audio = new Audio()
    this.audio.crossOrigin = 'anonymous'
    this.setupAudioEvents()
  }

  private setupAudioEvents() {
    if (!this.audio) return

    this.audio.addEventListener('loadedmetadata', () => {
      console.log('Audio loaded, duration:', this.audio!.duration)
      this.state = 'paused'
      this.onLoadCallback?.(this.audio!.duration)
      this.onStateChangeCallback?.(this.state)
    })

    this.audio.addEventListener('canplay', () => {
      console.log('Audio can play')
    })

    this.audio.addEventListener('play', () => {
      console.log('Audio play event')
      this.state = 'playing'
      this.onPlayCallback?.()
      this.onStateChangeCallback?.(this.state)
    })

    this.audio.addEventListener('pause', () => {
      console.log('Audio pause event')
      this.state = 'paused'
      this.onPauseCallback?.()
      this.onStateChangeCallback?.(this.state)
    })

    this.audio.addEventListener('ended', () => {
      console.log('Audio ended event')
      this.state = 'ended'
      this.onEndCallback?.()
      this.onStateChangeCallback?.(this.state)
    })

    this.audio.addEventListener('timeupdate', () => {
      this.onTimeUpdateCallback?.(this.audio!.currentTime)
    })

    this.audio.addEventListener('error', () => {
      console.error('Audio error')
      const error = this.audio!.error
      let errorMsg = 'Unknown error'
      if (error) {
        switch (error.code) {
          case MediaError.MEDIA_ERR_ABORTED:
            errorMsg = '播放被中止'
            break
          case MediaError.MEDIA_ERR_NETWORK:
            errorMsg = '网络错误，无法加载音频'
            break
          case MediaError.MEDIA_ERR_DECODE:
            errorMsg = '音频解码错误'
            break
          case MediaError.MEDIA_ERR_SRC_NOT_SUPPORTED:
            errorMsg = '不支持的音频格式'
            break
        }
      }
      this.state = 'idle'
      this.onErrorCallback?.(errorMsg)
      this.onStateChangeCallback?.(this.state)
    })
  }

  private isOnlineUrl(path: string): boolean {
    return path.startsWith('http://') || path.startsWith('https://')
  }

  private formatFilePath(filePath: string): string {
    if (this.isOnlineUrl(filePath)) {
      return filePath
    }
    if (filePath.startsWith('local://') || filePath.startsWith('file://')) {
      return filePath
    }
    const normalizedPath = filePath.replace(/\\/g, '/')
    return `local://${normalizedPath}`
  }

  async load(track: Track): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!this.audio) {
        reject(new Error('Audio not initialized'))
        return
      }

      this.stop()
      this.currentTrack = track
      this.state = 'loading'

      const audioSrc = this.formatFilePath(track.path)
      console.log('Loading audio:', audioSrc)

      if (this.isOnlineUrl(audioSrc)) {
        this.audio.crossOrigin = 'anonymous'
      } else {
        this.audio.crossOrigin = ''
      }

      this.audio.src = audioSrc
      
      const onLoadedMetadata = () => {
        this.audio!.removeEventListener('loadedmetadata', onLoadedMetadata)
        this.audio!.removeEventListener('error', onError)
        resolve()
      }

      const onError = () => {
        this.audio!.removeEventListener('loadedmetadata', onLoadedMetadata)
        this.audio!.removeEventListener('error', onError)
        reject(new Error('Failed to load audio'))
      }

      this.audio.addEventListener('loadedmetadata', onLoadedMetadata)
      this.audio.addEventListener('error', onError)
      
      this.audio.load()
    })
  }

  play(): void {
    if (this.audio && this.currentTrack) {
      console.log('Calling audio.play()')
      this.audio.play().catch(err => {
        console.error('Play error:', err)
        this.onErrorCallback?.(err.message)
      })
    }
  }

  pause(): void {
    if (this.audio) {
      console.log('Calling audio.pause()')
      this.audio.pause()
    }
  }

  stop(): void {
    if (this.audio) {
      this.audio.pause()
      this.audio.currentTime = 0
      this.audio.src = ''
    }
    this.currentTrack = null
    this.state = 'idle'
  }

  seek(time: number): void {
    if (this.audio && !isNaN(time) && time >= 0) {
      this.audio.currentTime = Math.min(time, this.audio.duration || 0)
    }
  }

  getCurrentTime(): number {
    return this.audio?.currentTime || 0
  }

  getDuration(): number {
    return this.audio?.duration || 0
  }

  setVolume(volume: number): void {
    if (this.audio && volume >= 0 && volume <= 1) {
      this.audio.volume = volume
    }
  }

  getVolume(): number {
    return this.audio?.volume || 1
  }

  isPlaying(): boolean {
    return this.state === 'playing'
  }

  getState(): PlayState {
    return this.state
  }

  getCurrentTrack(): Track | null {
    return this.currentTrack
  }

  onPlay(callback: () => void): void {
    this.onPlayCallback = callback
  }

  onPause(callback: () => void): void {
    this.onPauseCallback = callback
  }

  onEnd(callback: () => void): void {
    this.onEndCallback = callback
  }

  onTimeUpdate(callback: (time: number) => void): void {
    this.onTimeUpdateCallback = callback
  }

  onLoad(callback: (duration: number) => void): void {
    this.onLoadCallback = callback
  }

  onError(callback: (error: string) => void): void {
    this.onErrorCallback = callback
  }

  onStateChange(callback: (state: PlayState) => void): void {
    this.onStateChangeCallback = callback
  }
}

export const audioManager = new AudioManager()
