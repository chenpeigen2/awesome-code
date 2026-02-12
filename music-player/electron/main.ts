import { app, BrowserWindow, ipcMain, dialog, screen, protocol } from 'electron'
import * as path from 'path'
import * as fs from 'fs'
import * as https from 'https'
import * as http from 'http'

let mainWindow: BrowserWindow | null = null
let lyricsWindow: BrowserWindow | null = null

const isDev = !app.isPackaged

function createMainWindow() {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    minWidth: 900,
    minHeight: 600,
    frame: false,
    backgroundColor: '#1a1a2e',
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js'),
      webSecurity: false,
    },
  })

  if (isDev) {
    mainWindow.loadURL('http://localhost:5173')
    mainWindow.webContents.openDevTools()
  } else {
    mainWindow.loadFile(path.join(__dirname, '../index.html'))
  }

  mainWindow.on('closed', () => {
    mainWindow = null
    if (lyricsWindow) {
      lyricsWindow.close()
    }
  })
}

function createLyricsWindow() {
  if (lyricsWindow) {
    lyricsWindow.focus()
    return
  }

  const { width } = screen.getPrimaryDisplay().workAreaSize

  lyricsWindow = new BrowserWindow({
    width: 600,
    height: 80,
    x: (width - 600) / 2,
    y: 50,
    frame: false,
    transparent: true,
    alwaysOnTop: true,
    resizable: true,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js'),
    },
  })

  if (isDev) {
    lyricsWindow.loadURL('http://localhost:5173/lyrics.html')
  } else {
    lyricsWindow.loadFile(path.join(__dirname, '../lyrics.html'))
  }

  lyricsWindow.on('closed', () => {
    lyricsWindow = null
  })
}

app.whenReady().then(() => {
  protocol.registerFileProtocol('local', (request, callback) => {
    const url = request.url.substr(8)
    try {
      const decodedPath = decodeURIComponent(url)
      console.log('Local protocol request:', decodedPath)
      callback({ path: decodedPath })
    } catch (err) {
      console.error('Local protocol error:', err)
      callback({ error: -2 })
    }
  })

  createMainWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createMainWindow()
    }
  })
})

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

ipcMain.handle('window:minimize', () => {
  mainWindow?.minimize()
})

ipcMain.handle('window:maximize', () => {
  if (mainWindow?.isMaximized()) {
    mainWindow.unmaximize()
  } else {
    mainWindow?.maximize()
  }
})

ipcMain.handle('window:close', () => {
  mainWindow?.close()
})

ipcMain.handle('dialog:openFiles', async () => {
  const result = await dialog.showOpenDialog(mainWindow!, {
    properties: ['openFile', 'multiSelections'],
    filters: [
      { name: 'Audio Files', extensions: ['mp3', 'flac', 'wav', 'aac', 'ogg', 'ape'] },
    ],
  })
  return result.filePaths
})

ipcMain.handle('dialog:openFolder', async () => {
  const result = await dialog.showOpenDialog(mainWindow!, {
    properties: ['openDirectory'],
  })
  return result.filePaths[0]
})

ipcMain.handle('file:readMetadata', async (_event, filePath: string) => {
  try {
    const stats = fs.statSync(filePath)
    return {
      path: filePath,
      size: stats.size,
      modified: stats.mtime,
    }
  } catch {
    return null
  }
})

ipcMain.handle('file:save', async (_event, filePath: string, buffer: ArrayBuffer) => {
  try {
    const dir = path.dirname(filePath)
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true })
    }
    const nodeBuffer = Buffer.from(buffer)
    fs.writeFileSync(filePath, nodeBuffer)
    return { success: true, path: filePath }
  } catch (error: any) {
    console.error('File save error:', error)
    return { success: false, error: error.message }
  }
})

ipcMain.handle('lyrics:show', () => {
  createLyricsWindow()
})

ipcMain.handle('lyrics:hide', () => {
  lyricsWindow?.close()
})

ipcMain.handle('lyrics:update', (_event, lyrics: string, currentTime: number) => {
  lyricsWindow?.webContents.send('lyrics:update', lyrics, currentTime)
})

ipcMain.handle('http:fetch', async (_event, url: string, options: any = {}) => {
  return new Promise((resolve) => {
    try {
      const urlObj = new URL(url)
      const isHttps = urlObj.protocol === 'https:'
      const httpModule = isHttps ? https : http
      
      const requestOptions = {
        hostname: urlObj.hostname,
        port: urlObj.port || (isHttps ? 443 : 80),
        path: urlObj.pathname + urlObj.search,
        method: options.method || 'GET',
        headers: {
          'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
          'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
          'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
          ...options.headers,
        },
        timeout: options.timeout || 15000,
      }

      const req = httpModule.request(requestOptions, (res) => {
        let data = ''
        res.setEncoding('utf8')
        res.on('data', (chunk) => {
          data += chunk
        })
        res.on('end', () => {
          resolve({
            status: res.statusCode,
            headers: res.headers,
            data: data,
          })
        })
      })

      req.on('error', (error) => {
        console.error('HTTP fetch error:', error)
        resolve({
          status: 0,
          headers: {},
          data: '',
          error: error.message || '网络请求失败',
        })
      })

      req.on('timeout', () => {
        req.destroy()
        resolve({
          status: 0,
          headers: {},
          data: '',
          error: '请求超时，请检查网络连接',
        })
      })

      if (options.body) {
        req.write(options.body)
      }

      req.end()
    } catch (error: any) {
      console.error('HTTP fetch setup error:', error)
      resolve({
        status: 0,
        headers: {},
        data: '',
        error: error.message || '请求配置失败',
      })
    }
  })
})

ipcMain.handle('http:download', async (_event, url: string, savePath: string) => {
  return new Promise((resolve, reject) => {
    const urlObj = new URL(url)
    const isHttps = urlObj.protocol === 'https:'
    const httpModule = isHttps ? https : http

    const file = fs.createWriteStream(savePath)

    const req = httpModule.get(url, {
      headers: {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
        'Accept': '*/*',
      },
    }, (res) => {
      if (res.statusCode !== 200) {
        fs.unlinkSync(savePath)
        reject({ error: `HTTP ${res.statusCode}` })
        return
      }

      const totalSize = parseInt(res.headers['content-length'] || '0', 10)
      let downloadedSize = 0

      res.on('data', (chunk) => {
        downloadedSize += chunk.length
        mainWindow?.webContents.send('download:progress', {
          url,
          downloaded: downloadedSize,
          total: totalSize,
        })
      })

      res.pipe(file)

      file.on('finish', () => {
        file.close()
        resolve({ success: true, path: savePath, size: downloadedSize })
      })
    })

    req.on('error', (error) => {
      fs.unlinkSync(savePath)
      reject({ error: error.message })
    })

    req.setTimeout(60000, () => {
      req.destroy()
      fs.unlinkSync(savePath)
      reject({ error: 'Download timeout' })
    })
  })
})
