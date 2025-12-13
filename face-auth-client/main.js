const { app, BrowserWindow } = require('electron');

// 从命令行参数或环境变量获取要加载的页面
const getPageToLoad = () => {
  // 支持环境变量：PAGE=auto-verify npm start
  if (process.env.PAGE === 'auto-verify') {
    return 'auto-verify.html';
  }

  // 支持命令行参数：electron . --page=auto-verify
  const pageArg = process.argv.find(arg => arg.startsWith('--page='));
  if (pageArg) {
    const pageName = pageArg.split('=')[1];
    return pageName === 'auto-verify' ? 'auto-verify.html' : 'app.html';
  }

  // 默认加载手动认证页面
  return 'app.html';
};

async function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1600,
    height: 900,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false
    }
  });

  const pageToLoad = getPageToLoad();
  console.log(`Loading page: ${pageToLoad}`);
  await mainWindow.loadFile(pageToLoad);

  // 窗口最大化（全屏显示）
  // mainWindow.maximize();

  // 打开开发者工具（可选）
  // mainWindow.webContents.openDevTools({
  //   mode: "bottom"
  // });
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
