import { SearchResult } from '@/types/download'

const BASE_URL = 'https://mp3.pm'
const SEARCH_TIMEOUT = 15000

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
    })
    if (response.error) {
      throw new Error(response.error)
    }
    return response.data
  } else {
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), SEARCH_TIMEOUT)
    
    try {
      const response = await fetch(url, {
        method: 'GET',
        signal: controller.signal,
        headers: {
          'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
          'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
          'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        },
      })
      clearTimeout(timeoutId)
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }
      
      return await response.text()
    } catch (error: any) {
      clearTimeout(timeoutId)
      throw error
    }
  }
}

function parseHtmlTracks(html: string): ParsedTrack[] {
  const tracks: ParsedTrack[] = []
  
  const patterns = [
    /<li[^>]*class="[^"]*cplayer-sound-item[^"]*"[^>]*data-sound-id="([^"]*)"[^>]*data-sound-url="([^"]*)"[^>]*>([\s\S]*?)<\/li>/gi,
    /<li[^>]*data-sound-id="([^"]*)"[^>]*data-sound-url="([^"]*)"[^>]*class="[^"]*cplayer-sound-item[^"]*"[^>]*>([\s\S]*?)<\/li>/gi,
    /<li[^>]*data-sound-url="([^"]*)"[^>]*data-sound-id="([^"]*)"[^>]*>([\s\S]*?)<\/li>/gi,
    /<li[^>]*data-url="([^"]*)"[^>]*data-id="([^"]*)"[^>]*>([\s\S]*?)<\/li>/gi,
  ]
  
  for (const trackRegex of patterns) {
    let match
    while ((match = trackRegex.exec(html)) !== null) {
      let id = match[1]
      let downloadUrl = match[2]
      const content = match[3]
      
      if (downloadUrl && downloadUrl.includes('data-sound-id')) {
        const temp = id
        id = downloadUrl
        downloadUrl = temp
      }
      
      if (!downloadUrl || downloadUrl.includes('data-')) {
        continue
      }
      
      const titleMatch = content.match(/<b[^>]*>([^<]*)<\/b>/i) || 
                         content.match(/class="[^"]*title[^"]*"[^>]*>([^<]*)</i)
      const artistMatch = content.match(/<i[^>]*>([^<]*)<\/i>/i) ||
                          content.match(/class="[^"]*artist[^"]*"[^>]*>([^<]*)</i) ||
                          content.match(/class="[^"]*author[^"]*"[^>]*>([^<]*)</i)
      const durationMatch = content.match(/<em[^>]*>([^<]*)<\/em>/i) ||
                            content.match(/class="[^"]*duration[^"]*"[^>]*>([^<]*)</i) ||
                            content.match(/class="[^"]*time[^"]*"[^>]*>([^<]*)</i) ||
                            content.match(/(\d+:\d+)/)
      
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
    if (tracks.length > 0) break
  }
  
  const jsonMatch = html.match(/window\.__INITIAL_STATE__\s*=\s*({[\s\S]*?});/)
  if (jsonMatch && tracks.length === 0) {
    try {
      const data = JSON.parse(jsonMatch[1])
      if (data.tracks && Array.isArray(data.tracks)) {
        return data.tracks.map((t: any) => ({
          id: t.id || t.soundId || `track-${Date.now()}`,
          title: t.title || t.name || 'Unknown Title',
          artist: t.artist || t.author || 'Unknown Artist',
          duration: t.duration ? formatSeconds(t.duration) : '0:00',
          downloadUrl: t.url || t.downloadUrl || t.streamUrl || '',
        }))
      }
    } catch {
      console.warn('Failed to parse JSON data')
    }
  }
  
  const urlPattern = /https?:\/\/[^\s"'<>]+\.mp3[^\s"'<>]*/gi
  const mp3Urls = html.match(urlPattern) || []
  
  if (tracks.length === 0 && mp3Urls.length > 0) {
    mp3Urls.forEach((url, index) => {
      const cleanUrl = url.split('"')[0].split("'")[0]
      tracks.push({
        id: `mp3-${Date.now()}-${index}`,
        title: `Track ${index + 1}`,
        artist: 'Unknown Artist',
        duration: '0:00',
        downloadUrl: cleanUrl,
      })
    })
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
    const searchUrl = `${BASE_URL}/search/${encodeURIComponent(query)}/`
    
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
