<script setup>
import { ref } from "vue";
import axios from "axios";
import QrScanner from "qr-scanner";

const apiBase = "http://192.168.0.182:3000";

const uuid = ref("");
const ticket = ref(null);
const msg = ref("");

const videoElem = ref(null);
let scanner = null;

// ===== 啟動相機 =====
async function startScan() {


    console.log("videoElem =", videoElem.value);
    msg.value = "📷 相機啟動...";
    ticket.value = null;

    scanner = new QrScanner(
        videoElem.value,
        
        
        qrText => {
            uuid.value = qrText.data;
            msg.value = "掃描成功 → 驗票中...";
            scanner.stop();
            if (scanner.$overlay) scanner.$overlay.style.display = "none";
            if (scanner.$canvas) scanner.$canvas.style.display = "none";
            verify();
        },{
    preferredCamera: 'environment',
    highlightScanRegion: true,
    returnDetailedScanResult: true
});
    scanner._onDecodeError = err => {
   msg.value = "Decode Error!"
};

    try {
        await scanner.start();
        if (!videoElem.value.srcObject) {
            msg.value = "⚠️ 相機已啟動，但影片沒有掛上！"
        }else{
             msg.value = " 相機已啟動影片有掛上！"
        }
    } catch (err) {
        msg.value = "❌ 相機無法啟動：" + err.message;
    }
    
}

// ===== 驗票 =====
async function verify() {
    if (!uuid.value) {
        msg.value = "請輸入 UUID";
        return;
    }

    try {

        const payload = { qrcodeText: uuid.value };

        // ★★★ 正確應該是 POST /api/verify
        const res = await axios.post(`${apiBase}/api/verify`, payload);

        msg.value = res.data.message;
        ticket.value = res.data.ticket || null;

    } catch (err) {
        console.error(err);
        msg.value = "❌ 後端錯誤";
    }
}
</script>

<template>
<div style="padding:20px;color:white">

    <h2>驗票系統</h2>

    <input v-model="uuid"
           placeholder="輸入或掃描 QRCode"
           style="width:100%;padding:10px;margin:10px 0;" />

    <button @click="verify"
            style="padding:10px 20px;background:#ffcc00;border:none;border-radius:999px;">
        手動驗票
    </button>

    <br><br>

    <button @click="startScan"
            style="padding:10px 20px;background:#0099ff;border:none;border-radius:999px;">
        📷 開啟相機掃描
    </button>

    <br><br>

    <video ref="videoElem" style="width:100%;border-radius:12px"></video>

    <p style="margin-top:10px">{{ msg }}</p>

    <div v-if="ticket"
         style="margin-top:20px;padding:20px;border:1px solid #fff3;border-radius:12px">

        <h3>{{ ticket.movieTitle }}</h3>

        <p>影廳：{{ ticket.screen }}</p>
        <p>座位：{{ ticket.seat }}</p>

        <p>時間：{{ new Date(ticket.showTime).toLocaleString() }}</p>

        <p>狀態：{{ msg }}</p>

        <small style="opacity:0.6;">UUID: {{ uuid }}</small>
    </div>

</div>
</template>