import { SearchResult } from '@/types/download'

const BASE_URL = 'https://mp3.pm'
const SEARCH_TIMEOUT = 30000

interface ParsedTrack {
  id: string
  title: string
  artist: string
  duration: string
  downloadUrl: string
}

async function fetchHtml(url: string): Promise<string> {
  if (window.electronAPI && window.electronAPI.httpFetch) {
    const response = await window.electronAPI.httpFetch(url, {
      timeout: SEARCH_TIMEOUT,
      followRedirects: true,
    })
    if (response.error) {
      throw new Error(response.error)
    }
    if (response.status && response.status >= 400) {
      throw new Error(`服务器返回错误: HTTP ${response.status}`)
    }
    if (!response.data) {
      throw new Error('服务器返回空数据')
    }
    return response.data
  } else {
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), SEARCH_TIMEOUT)
    
    try {
      const response = await fetch(url, {
        method: 'GET',
        signal: controller.signal,
        redirect: 'follow',
        headers: {
          'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
          'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
          'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        },
      })
      clearTimeout(timeoutId)
      
      if (!response.ok) {
        throw new Error(`服务器返回错误: HTTP ${response.status}`)
      }
      
      const text = await response.text()
      if (!text) {
        throw new Error('服务器返回空数据')
      }
      return text
    } catch (error: any) {
      clearTimeout(timeoutId)
      if (error.name === 'AbortError') {
        throw new Error('请求超时，请检查网络连接')
      }
      throw error
    }
  }
}

function parseHtmlTracks(html: string): ParsedTrack[] {
  const tracks: ParsedTrack[] = []
  
  const trackRegex = /<li[^>]*class="[^"]*cplayer-sound-item[^"]*"[^>]*data-sound-id="([^"]*)"[^>]*data-sound-url="([^"]*)"[^>]*>([\s\S]*?)<\/li>/gi
  
  let match
  while ((match = trackRegex.exec(html)) !== null) {
    const id = match[1]
    const downloadUrl = match[2]
    const content = match[3]
    
    if (!downloadUrl) {
      continue
    }
    
    const titleMatch = content.match(/<b[^>]*>([^<]*)<\/b>/i)
    const artistMatch = content.match(/<i[^>]*>([^<]*)<\/i>/i)
    const durationMatch = content.match(/<em[^>]*>([^<]*)<\/em>/i)
    
    try {
      const decodedUrl = decodeURIComponent(downloadUrl)
      tracks.push({
        id: id || `track-${Date.now()}-${Math.random()}`,
        title: titleMatch ? titleMatch[1].trim() : 'Unknown Title',
        artist: artistMatch ? artistMatch[1].trim() : 'Unknown Artist',
        duration: durationMatch ? durationMatch[1].trim() : '0:00',
        downloadUrl: decodedUrl,
      })
    } catch {
      console.warn('Failed to parse track:', downloadUrl)
    }
  }
  
  if (tracks.length === 0) {
    const divRegex = /<div[^>]*class="[^"]*cplayer-sound-item[^"]*"[^>]*data-sound-id="([^"]*)"[^>]*data-sound-url="([^"]*)"[^>]*>([\s\S]*?)<\/div>/gi
    while ((match = divRegex.exec(html)) !== null) {
      const id = match[1]
      const downloadUrl = match[2]
      const content = match[3]
      
      if (!downloadUrl) continue
      
      const titleMatch = content.match(/<b[^>]*>([^<]*)<\/b>/i)
      const artistMatch = content.match(/<i[^>]*>([^<]*)<\/i>/i)
      const durationMatch = content.match(/<em[^>]*>([^<]*)<\/em>/i)
      
      try {
        const decodedUrl = decodeURIComponent(downloadUrl)
        tracks.push({
          id: id || `track-${Date.now()}-${Math.random()}`,
          title: titleMatch ? titleMatch[1].trim() : 'Unknown Title',
          artist: artistMatch ? artistMatch[1].trim() : 'Unknown Artist',
          duration: durationMatch ? durationMatch[1].trim() : '0:00',
          downloadUrl: decodedUrl,
        })
      } catch {
        console.warn('Failed to parse track:', downloadUrl)
      }
    }
  }
  
  return tracks
}

function formatSeconds(seconds: number): string {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

export async function searchTracks(query: string): Promise<SearchResult[]> {
  if (!query.trim()) {
    return []
  }

  try {
    const searchUrl = `${BASE_URL}/?a=redirect&q=${encodeURIComponent(query)}`
    
    const html = await fetchHtml(searchUrl)
    const tracks = parseHtmlTracks(html)
    
    if (tracks.length === 0) {
      console.log('No tracks found, HTML preview:', html.substring(0, 500))
    }
    
    return tracks.map(track => ({
      id: track.id,
      title: track.title,
      artist: track.artist,
      duration: track.duration,
      downloadUrl: track.downloadUrl,
    }))
  } catch (error: any) {
    console.error('Search error:', error)
    throw new Error(`搜索失败: ${error.message || '网络错误'}`)
  }
}

export async function getPopularTracks(): Promise<SearchResult[]> {
  try {
    const html = await fetchHtml(BASE_URL)
    const tracks = parseHtmlTracks(html)
    
    return tracks.map(track => ({
      id: track.id,
      title: track.title,
      artist: track.artist,
      duration: track.duration,
      downloadUrl: track.downloadUrl,
    }))
  } catch (error: any) {
    console.error('Get popular tracks error:', error)
    throw new Error(`获取热门歌曲失败: ${error.message || '网络错误'}`)
  }
}

export function formatDuration(duration: string): number {
  const parts = duration.split(':')
  if (parts.length === 2) {
    const minutes = parseInt(parts[0], 10)
    const seconds = parseInt(parts[1], 10)
    return minutes * 60 + seconds
  }
  return 0
}

export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

export function formatSpeed(bytesPerSecond: number): string {
  return formatFileSize(bytesPerSecond) + '/s'
}

export function getDownloadFileName(result: SearchResult): string {
  const sanitize = (str: string) => str.replace(/[<>:"/\\|?*]/g, '_').trim()
  return `${sanitize(result.artist)} - ${sanitize(result.title)}.mp3`
}
