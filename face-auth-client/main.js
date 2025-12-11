const { app, BrowserWindow, ipcMain, dialog } = require('electron');
const fs = require('fs');

async function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1200,
    height: 900,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false
    }
  });

  await mainWindow.loadFile('index.html');

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

// 处理保存照片到本地
ipcMain.handle('save-image', async (event, imageData) => {
  try {
    // 生成默认文件名（使用时间戳）
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const defaultFileName = `face-${timestamp}.jpg`;

    // 弹出保存对话框让用户选择保存路径
    const { filePath, canceled } = await dialog.showSaveDialog({
      title: '保存照片',
      defaultPath: defaultFileName,
      filters: [
        { name: 'JPEG 图片', extensions: ['jpg', 'jpeg'] },
        { name: 'PNG 图片', extensions: ['png'] },
        { name: '所有文件', extensions: ['*'] }
      ]
    });

    if (canceled || !filePath) {
      return { success: false, message: '已取消保存', canceled: true };
    }

    // 将 base64 数据转换为 Buffer
    const base64Data = imageData.replace(/^data:image\/\w+;base64,/, '');
    const buffer = Buffer.from(base64Data, 'base64');

    // 保存文件
    fs.writeFileSync(filePath, buffer);

    return { success: true, message: '照片保存成功', path: filePath };
  } catch (error) {
    console.error('保存照片失败:', error);
    return { success: false, message: '保存失败: ' + error.message };
  }
});

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit();
});
