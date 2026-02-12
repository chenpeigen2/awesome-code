import { app, BrowserWindow, ipcMain, dialog, screen, protocol } from 'electron'
import * as path from 'path'
import * as fs from 'fs'

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

ipcMain.handle('lyrics:show', () => {
  createLyricsWindow()
})

ipcMain.handle('lyrics:hide', () => {
  lyricsWindow?.close()
})

ipcMain.handle('lyrics:update', (_event, lyrics: string, currentTime: number) => {
  lyricsWindow?.webContents.send('lyrics:update', lyrics, currentTime)
})
