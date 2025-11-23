<script setup>
import { ref } from "vue";
import axios from "axios";
import QrcodeVue from "qrcode.vue";

const apiBase = "http://192.168.0.182:3000";

const form = ref({
    movie_title: "",
    screen: "",
    seat: "",
    show_time: "",
});

const ticket = ref(null);
const msg = ref("");

function toSqlDateFormat(localTime) {
    const date = new Date(localTime);
    if (isNaN(date.getTime())) return null;

    const YYYY = date.getFullYear();
    const MM = String(date.getMonth() + 1).padStart(2, "0");
    const DD = String(date.getDate()).padStart(2, "0");
    const hh = String(date.getHours()).padStart(2, "0");
    const mm = String(date.getMinutes()).padStart(2, "0");

    return `${YYYY}-${MM}-${DD} ${hh}:${mm}:00`;
}

async function createTicket() {
    try {
        const payload = {
            movieTitle: form.value.movie_title,
            screen: form.value.screen,
            seat: form.value.seat,
            showTime: toSqlDateFormat(form.value.show_time),
        };

        const res = await axios.post(`${apiBase}/api/tickets`, payload);

        msg.value = "票券建立成功！";

        ticket.value = res.data;

    } catch (err) {
        console.error(err);
        msg.value = "建立票券失敗（後端未啟動或 SQL 錯誤）";
    }
}
</script>


<template>
    <div style="padding:20px; color:white;">

        <h2>電子票券</h2>

        <input v-model="form.movie_title" placeholder="電影名稱" />
        <input v-model="form.screen" placeholder="影廳" />
        <input v-model="form.seat" placeholder="座位" />
        <input type="datetime-local" v-model="form.show_time" />

        <button @click="createTicket">建立票券</button>

        <p>{{ msg }}</p>

        <div v-if="ticket" style="margin-top:20px;">

            <h3>{{ ticket.movieTitle }}</h3>
            <p>{{ ticket.screen }} 座位：{{ ticket.seat }}</p>
            <p>開演：{{ ticket.showTime }}</p>

            <qrcode-vue :value="ticket.qrcodeText" :size="240" />

            <p style="margin-top:10px;">{{ ticket.qrcodeText }}</p>

        </div>

    </div>
</template>