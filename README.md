# 🍳 廚師 AI 工具箱 · Chef AI Toolbox

> 由 20 年經驗的真廚師打造的 AI 料理助理
> 你的金鑰，你的隱私，你的料理

![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B-brightgreen)
![Language](https://img.shields.io/badge/Language-Kotlin-purple)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)
![AI](https://img.shields.io/badge/AI-OpenAI%20API-orange)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![Version](https://img.shields.io/badge/Version-v0.6.2-cyan)

---

## 📖 這個 App 是什麼？

**廚師 AI 工具箱** 是一款 Android 原生料理 App，三個核心功能：

| 功能 | 一句話介紹 |
|------|------|
| 🥬 **冰箱食譜生成器** | 告訴 App 你冰箱有什麼食材，AI 主廚立刻出 1-3 道家常食譜，附廚師級眉角 |
| 🔥 **熱量雷達** | 拍一張料理照，估算熱量、營養成分、給降熱量的具體建議 |
| 📚 **我的紀錄** | 自動保存每次生成的食譜與熱量分析，隨時翻舊不用再扣 AI 費 |

跟坊間 AI 食譜 App 最大的差異 —— 食譜不是「機器丟給你」，而是經過真廚師調教過的 AI 出的，每一步都有「為什麼這樣做」的眉角提示，**像真人廚師站在你旁邊邊做邊講**。

### 核心設計原則

| 特色 | 說明 |
|------|------|
| 🔐 **零後端** | App 不經過任何開發者伺服器，API 請求直送 OpenAI |
| 💰 **BYO Key** | 使用者用自己的 OpenAI API Key，費用直接從自己帳戶扣 |
| 🛡️ **隱私優先** | API Key 只存在手機本地，不會上傳到任何伺服器 |
| ⚡ **即時串流** | 食譜逐字生成（SSE），像真的在看廚師思考 |
| 📚 **自動紀錄** | 每次生成自動保存，翻舊食譜不用再花 API 費 |

---

## 🚀 下載安裝

**📦 [點此下載最新版 APK（v0.6.2）](https://drive.google.com/file/d/10HYPLadG7iBR5fSXlc0SOmQnvA_RsxeH/view?usp=sharing)**

1. 點上方連結，在 Google Drive 頁面點右上角「⬇ 下載」
2. 在 Android 手機上點開 APK 安裝（首次安裝需開啟「允許未知來源」）
3. 安裝後輸入你的 OpenAI API Key 即可開始使用

> **支援版本：** Android 8.0（API 26）以上

---

## 🔑 取得 OpenAI API Key

這個 App **不收任何訂閱費**，但你需要自己的 OpenAI API Key。

| 步驟 | 動作 |
|------|------|
| 1️⃣ | 前往 **platform.openai.com/api-keys**（沒帳號先註冊） |
| 2️⃣ | Billing → Add payment method 綁信用卡並儲值（建議先 5 美金試水溫）|
| 3️⃣ | Create new secret key → 建立後立刻複製（只顯示一次！）|
| 4️⃣ | 打開 App → 貼上 Key → 驗證 → 開始使用 |

鑰匙格式：`sk-proj-xxxxxxxxxxxxxxxxxx`（以 `sk-` 開頭）
**請勿將 Key 傳給任何人。**

---

## 🍽️ 功能說明

### 冰箱食譜生成器

- 從 6 大分類（蔬菜 / 肉類 / 海鮮 / 蛋奶豆 / 五穀雜糧 / 調味料）各 30 個食材中選取
- 支援手動輸入自訂食材
- 選擇料理風格（家常 / 日式 / 西式 / 低卡 / 快手）
- 選擇**幾人份**（1-7 人 / 8 人以上）— AI 自動調整食材份量
- 選擇生成道數（1-3 道）
- 即時 SSE 串流輸出，附精確份量與廚師眉角

### 熱量雷達

- 拍照或從相簿選圖
- 可加備註幫助 AI 估算更準確
- 輸出：菜名 / 熱量 / 蛋白質・碳水・脂肪 / 可執行的降卡建議
- 誤差 ±15%，僅供參考

### 我的紀錄

- 食譜與熱量分析自動儲存（各最多 50 筆）
- 支援長按或按鈕進入多選模式，批次刪除
- 所有刪除動作都有確認對話框防止誤觸
- 紀錄含人份標示，方便日後查閱

---

## 🛠️ 技術規格

```
語言：Kotlin 2.0.21
UI：Jetpack Compose + Material 3 (BOM 2024.12.01)
網路：OkHttp + okhttp-sse（SSE 串流）
AI：OpenAI Chat Completions API + Vision API
架構：手動 DI（AppContainer）+ ViewModel + StateFlow
本地儲存：SharedPreferences + org.json（無 Room）
minSdk：26（Android 8.0）
targetSdk：35（Android 15）
AGP：8.7.3 / Gradle：8.11.1
```

### 專案結構

```
app/src/main/java/com/chefai/toolbox/
├── ai/                  # OpenAI client, prompt builder, model constants
├── data/                # AppSettings, repositories, history stores
├── ui/
│   ├── about/           # 關於頁
│   ├── calories/        # 熱量雷達
│   ├── common/          # 共用元件（HUD components, ConfirmDialog...）
│   ├── fridge/          # 冰箱食譜生成器
│   ├── history/         # 我的紀錄（料理 + 熱量 Tab）
│   ├── home/            # 首頁
│   ├── navigation/      # Nav graph
│   ├── settings/        # 設定頁
│   ├── setup/           # API Key 設定
│   └── theme/           # Cosmic + Iron Man HUD 色系
└── AppContainer.kt      # 手動 DI root
```

---

## 💰 費用參考

| 動作 | 約略費用（台幣）|
|------|------|
| 一次食譜生成（3 道）| NT$ 0.05 - 0.15 |
| 一次熱量雷達分析 | NT$ 0.30 - 0.50 |
| 翻看我的紀錄 | 0 元（本地資料）|

一般家庭使用：一個月約 **NT$ 30-150**。

---

## 🔒 隱私

- 食材清單、照片 → 直接送 OpenAI，**不經過開發者任何伺服器**
- API Key → 只存在手機 SharedPreferences，**永不上傳**
- 食譜與熱量紀錄 → 存在手機 internal storage，開發者看不到
- **零後端架構，開發者無法取得任何使用者資料**

---

*v0.6.2 · 2026-04-27 · Built with Kotlin + Jetpack Compose + OpenAI API*
