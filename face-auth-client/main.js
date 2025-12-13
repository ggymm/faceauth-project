const { app, BrowserWindow } = require('electron');

async function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1600,
    height: 900,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false
    }
  });

  await mainWindow.loadFile('index.html');

  // 窗口最大化（全屏显示）
  // mainWindow.maximize();

  // 打开开发者工具（可选）
  mainWindow.webContents.openDevTools({
    mode: "right"
  });
}

app.whenReady().then(async () => {
  await createWindow();

  app.on('activate', function () {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit();
});
