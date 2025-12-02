        <template>
        <div style="text-align:center; padding:40px;">
            <h2>綠界付款中...</h2>
            <div id="ECPayPayment" style="margin-top:30px;"></div>
        </div>
        </template>

    <script setup>
    import { useRoute } from "vue-router";
    import { onMounted } from "vue";

    const token = useRoute().query.token; // 這次是真正的 PayToken

    onMounted(()=>{
        window.ECPay.initialize("Stage",1,()=>{
            window.ECPay.createPayment(token,window.ECPay.Language.zhTW);
        });
    });
    </script>
