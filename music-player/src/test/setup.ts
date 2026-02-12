import '@testing-library/jest-dom'
import { vi } from 'vitest'

const localStorageMock = {
  getItem: vi.fn(() => null),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
Object.defineProperty(window, 'localStorage', { 
  value: localStorageMock,
  writable: true,
})

Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})

class MockAudio {
  src: string = ''
  crossOrigin: string = ''
  currentTime: number = 0
  duration: number = 0
  volume: number = 1
  paused: boolean = true
  
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
    return Promise.resolve()
  }
  
  pause(): void {
    this.paused = true
  }
  
  load(): void {}
}

Object.defineProperty(window, 'Audio', { 
  value: MockAudio,
  writable: true,
})

const electronAPIMock = {
  minimizeWindow: vi.fn(),
  maximizeWindow: vi.fn(),
  closeWindow: vi.fn(),
  openFiles: vi.fn().mockResolvedValue([]),
  openFolder: vi.fn().mockResolvedValue(''),
  readMetadata: vi.fn().mockResolvedValue(null),
  saveFile: vi.fn().mockResolvedValue({ success: true }),
  showLyrics: vi.fn(),
  hideLyrics: vi.fn(),
  updateLyrics: vi.fn(),
  onLyricsUpdate: vi.fn(),
  httpFetch: vi.fn().mockResolvedValue({ status: 200, headers: {}, data: '' }),
  httpDownload: vi.fn().mockResolvedValue({ success: true, size: 1000 }),
  onDownloadProgress: vi.fn(),
}

Object.defineProperty(window, 'electronAPI', { 
  value: electronAPIMock,
  writable: true,
})
