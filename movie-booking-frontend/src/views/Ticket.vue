    <template>
    <div class="ticket-page">

        <h2 class="title">🎟 您的電影票</h2>

        <div v-if="tickets.length === 0" class="empty">
        <p>尚未產生票券</p>
        </div>

        <div v-else class="ticket-list">

        <div class="ticket-card" v-for="t in tickets" :key="t.id">

            <div class="left">
            <h3 class="movie">{{ t.movieName }}</h3>
            <p><strong>場次時間：</strong>{{ formatDate(t.showTime) }}</p>
            <p><strong>座位：</strong>{{ t.seatId }}</p>
            <p><strong>票種：</strong>{{ t.ticketType }}</p>
            <p><strong>價格：</strong>{{ t.price }} 元</p>
            </div>

            <div class="qr">
            <qrcode-vue :value="qrValue(t)" :size="120" />
            </div>

        </div>
        </div>

    </div>
    </template>

    <script setup>
    import axios from "axios";
    import { ref, onMounted } from "vue";
    import { useRoute } from "vue-router";
    //import QrcodeVue from "qrcode.vue";

    const route = useRoute();
    const orderId = Number(route.params.id);

    const tickets = ref([]);

    onMounted(async () => {
    const res = await axios.get(
        import.meta.env.VITE_API_BASE_URL+`tickets/order/${orderId}`||`http://localhost:8080/api/tickets/order/${orderId}`
    );
    tickets.value = res.data;
    });

    function formatDate(t) {
    return t
        ? new Date(t).toLocaleString("zh-TW", { hour12: false })
        : "";
    }

    // QRCode 內容（後端可驗票用）
    function qrValue(t) {
    return `ticket=${t.id};order=${t.orderId};seat=${t.seatId}`;
    }
    </script>

    <style scoped>
    .ticket-page {
    max-width: 600px;
    margin: 40px auto;
    padding: 10px;
    }

    .title {
    text-align: center;
    font-size: 28px;
    margin-bottom: 20px;
    font-weight: 800;
    }

    .ticket-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
    }

    .ticket-card {
    display: flex;
    justify-content: space-between;
    background: white;
    border-radius: 12px;
    padding: 16px;
    border: 1px solid #eee;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    }

    .ticket-card .left {
    width: 65%;
    }

    .movie {
    font-size: 20px;
    margin-bottom: 8px;
    font-weight: 700;
    }

    .qr {
    display: flex;
    align-items: center;
    justify-content: center;
    }
    </style>
