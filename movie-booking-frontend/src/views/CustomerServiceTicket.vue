        <template>
        <div class="issue-wrapper">
            <div class="issue-card">
            <h2 class="title">問題回報表單</h2>

            <!-- 問題類型 -->
            <div class="form-group">
                <label>問題類型 <span class="required">*</span></label>
                <select v-model="issueType" class="form-control">
                <option value="">請選擇問題類型</option>
                <option value="order">訂單</option>
                <option value="payment">付款</option>
                <option value="refund">退款</option>
                </select>
            </div>

            <!-- 訂單編號 -->
            <div class="form-group">
                <label>訂單編號 <span class="required">*</span></label>
                <input
                v-model="orderId"
                type="text"
                class="form-control"
                placeholder="請輸入訂單編號，例如：12345"/>
            </div>

            <!-- 標題 -->
            <div class="form-group">
                <label>問題描述標題 <span class="required">*</span></label>
                <input
                v-model="title"
                type="text"
                class="form-control"
                placeholder="例：付款後未顯示訂單、退款失敗…"
                />
            </div>

            <!-- 內容 -->
            <div class="form-group">
                <label>問題描述內容 <span class="required">*</span></label>
                <textarea
                v-model="description"
                class="form-control textarea"
                rows="5"
                placeholder="請詳細描述問題發生的狀況與操作步驟…"
                ></textarea>
            </div>

            <button class="submit-btn" @click="submitForm">送出</button>
            </div>
        </div>
        </template>

            <script setup>
    import { ref } from "vue";
    import axios from "axios";

    // 所有欄位都要定義
    const issueType = ref("");
    const orderId = ref("");
    const title = ref("");
    const description = ref("");

    // 送出表單
    const submitForm = async () => {
    if (!issueType.value || !orderId.value || !title.value.trim() || !description.value.trim()) {
        alert("請將所有必填欄位填寫完整！");
        return;
    }

    // 正確 payload （符合後端 TicketRequest）
    const payload = {
        orderId: Number(orderId.value),
        issueType: issueType.value,
        title: title.value,
        description: description.value,
        handledBy: 0   // 未指派
    };

    try {
        const res = await axios.post(import.meta.env.VITE_API_BASE_URL + "customer-service" || "http://localhost:8080/api/customer-service",
        payload,
        { headers: { "Content-Type": "application/json" } }
        );

        console.log("後端回傳：", res.data);
        alert("工單已成功送出！");
    } catch (error) {
        console.error(error);
        alert("送出失敗，請稍後再試！");
    }
    };
    </script>


        <style scoped>

        .issue-wrapper {
    display: flex;
    justify-content: center;
    padding-top: 60px;
    padding-bottom: 80px;
    }

    .issue-card {
    width: 550px;
    background: white;
    padding: 35px 40px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
    }

    .title {
    text-align: center;
    margin-bottom: 30px;
    font-size: 26px;
    font-weight: 700;
    }

    label {
    font-size: 15px;
    font-weight: 600;
    margin-bottom: 6px;
    display: block;
    }

    .required {
    color: red;
    font-size: 16px;
    }

    /* 統一欄位寬度 + 圓角 */
    .form-control {
    width: 100%;
    padding: 12px 14px;
    font-size: 15px;
    border: 1px solid #ccc;
    border-radius: 8px;
    box-sizing: border-box;
    }

    /* textarea 禁止拉動 */
    .textarea {
    resize: none !important;
    }

    /* 顯示間距 */
    .form-group {
    margin-bottom: 22px;
    }

    /* 按鈕樣式 */
    .submit-btn {
    width: 100%;
    background: #1a73e8;
    color: white;
    padding: 12px;
    border: none;
    border-radius: 8px;
    font-size: 17px;
    font-weight: bold;
    cursor: pointer;
    transition: 0.2s;
    }

    .submit-btn:hover {
    background: #0c5dc8;
    }
    </style>