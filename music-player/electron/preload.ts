import { contextBridge, ipcRenderer } from 'electron'

contextBridge.exposeInMainWorld('electronAPI', {
  minimizeWindow: () => ipcRenderer.invoke('window:minimize'),
  maximizeWindow: () => ipcRenderer.invoke('window:maximize'),
  closeWindow: () => ipcRenderer.invoke('window:close'),
  
  openFiles: () => ipcRenderer.invoke('dialog:openFiles'),
  openFolder: () => ipcRenderer.invoke('dialog:openFolder'),
  readMetadata: (filePath: string) => ipcRenderer.invoke('file:readMetadata', filePath),
  saveFile: (filePath: string, buffer: ArrayBuffer) => ipcRenderer.invoke('file:save', filePath, buffer),
  
  showLyrics: () => ipcRenderer.invoke('lyrics:show'),
  hideLyrics: () => ipcRenderer.invoke('lyrics:hide'),
  updateLyrics: (lyrics: string, currentTime: number) => 
    ipcRenderer.invoke('lyrics:update', lyrics, currentTime),
  
  onLyricsUpdate: (callback: (lyrics: string, currentTime: number) => void) => {
    ipcRenderer.on('lyrics:update', (_event, lyrics, currentTime) => callback(lyrics, currentTime))
  },
})
