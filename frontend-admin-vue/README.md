# frontend-admin-full (中文介面)

包含：
- Vue 3 + Vite + Element Plus 管理後台（中文）
- （已移除登入頁，直接進入 Dashboard）
- Axios baseURL 設定（src/services/api.js）
- 儀表板：統計卡、銷售趨勢、電影類型圓餅圖、日期篩選（使用 ECharts）
- 其他頁面為佔位，可接後端 API

## 快速啟動
1. 安裝依賴
```bash
npm install
```
2. 啟動
```bash
npm run dev
```

## 說明
- 在 `src/services/api.js` 將 `baseURL` 設成你的 Spring Boot 後端，例如 `http://localhost:8080/api`
- 登入頁目前為模擬登入（會生成假的 token），你可以改成呼叫後端 `/auth/login`，並把回傳 token 存進 localStorage('admin_token')
