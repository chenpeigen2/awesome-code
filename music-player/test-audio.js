const { app, BrowserWindow, protocol } = require('electron')
const path = require('path')
const fs = require('fs')

const testFilePath = 'C:\\Users\\chan_\\Downloads\\Charle_Puth_feat._Selena_Gomez_-_We_Don_t_Talk_Anymore_(mp3.pm).mp3'

function createWindow() {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      webSecurity: false,
    }
  })

  const html = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Audio Test</title>
  <style>
    body { 
      font-family: Arial, sans-serif; 
      padding: 20px; 
      background: #1a1a2e; 
      color: white; 
    }
    .section { margin: 20px 0; padding: 20px; background: #16213e; border-radius: 8px; }
    button { padding: 10px 20px; margin: 5px; cursor: pointer; }
    audio { width: 100%; margin-top: 10px; }
    #log { background: #000; padding: 10px; border-radius: 4px; height: 200px; overflow-y: auto; font-family: monospace; font-size: 12px; }
    .success { color: #0f0; }
    .error { color: #f00; }
  </style>
</head>
<body>
  <h1>🎵 Audio Test</h1>
  
  <div class="section">
    <h2>Test 1: Direct file:// path</h2>
    <audio id="audio1" controls></audio>
    <button onclick="testFile()">Load file://</button>
    <button onclick="play1()">Play</button>
    <div id="result1"></div>
  </div>

  <div class="section">
    <h2>Test 2: audio:// protocol</h2>
    <audio id="audio2" controls></audio>
    <button onclick="testAudio()">Load audio://</button>
    <button onclick="play2()">Play</button>
    <div id="result2"></div>
  </div>

  <div class="section">
    <h2>Test 3: HTML5 Audio Object</h2>
    <button onclick="testAudioObject()">Test with Audio Object</button>
    <div id="result3"></div>
  </div>

  <div class="section">
    <h2>Log</h2>
    <div id="log"></div>
  </div>

  <script>
    const filePath = ${JSON.stringify(testFilePath)};
    const fileUrl = 'file:///' + filePath.replace(/\\\\/g, '/');
    const audioUrl = 'audio://' + filePath;
    
    log('File path: ' + filePath);
    log('File URL: ' + fileUrl);
    log('Audio URL: ' + audioUrl);
    
    function log(msg, isError = false) {
      const logDiv = document.getElementById('log');
      const p = document.createElement('p');
      p.className = isError ? 'error' : 'success';
      p.textContent = new Date().toLocaleTimeString() + ' - ' + msg;
      logDiv.appendChild(p);
      console.log(msg);
    }
    
    function testFile() {
      log('Testing file:// protocol...');
      const audio = document.getElementById('audio1');
      audio.src = fileUrl;
      audio.onloadedmetadata = () => log('file:// loaded, duration: ' + audio.duration);
      audio.onerror = () => log('file:// error: ' + audio.error?.message, true);
      audio.oncanplay = () => log('file:// can play');
      document.getElementById('result1').textContent = 'Loaded: ' + fileUrl;
    }
    
    function play1() {
      document.getElementById('audio1').play().then(() => log('Playing!')).catch(e => log('Play error: ' + e, true));
    }
    
    function testAudio() {
      log('Testing audio:// protocol...');
      const audio = document.getElementById('audio2');
      audio.src = audioUrl;
      audio.onloadedmetadata = () => log('audio:// loaded, duration: ' + audio.duration);
      audio.onerror = () => log('audio:// error: ' + audio.error?.message, true);
      audio.oncanplay = () => log('audio:// can play');
      document.getElementById('result2').textContent = 'Loaded: ' + audioUrl;
    }
    
    function play2() {
      document.getElementById('audio2').play().then(() => log('Playing!')).catch(e => log('Play error: ' + e, true));
    }
    
    function testAudioObject() {
      log('Testing with Audio object...');
      const audio = new Audio();
      audio.src = fileUrl;
      audio.onloadedmetadata = () => {
        log('Audio object loaded, duration: ' + audio.duration);
        audio.play().then(() => log('Audio object playing!')).catch(e => log('Play error: ' + e, true));
      };
      audio.onerror = () => log('Audio object error: ' + audio.error?.message, true);
    }
  </script>
</body>
</html>
  `

  win.loadURL('data:text/html;charset=utf-8,' + encodeURIComponent(html))
  win.webContents.openDevTools()
}

app.whenReady().then(() => {
  protocol.registerFileProtocol('audio', (request, callback) => {
    const url = request.url.substr(8)
    const decodedPath = decodeURIComponent(url)
    console.log('Audio protocol request:', decodedPath)
    callback(decodedPath)
  })
  
  createWindow()
})

app.on('window-all-closed', () => app.quit())
