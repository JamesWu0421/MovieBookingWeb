package tw.com.ispan.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;

import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import tw.com.ispan.service.OrderService;
import tw.com.ispan.utils.EcpayUtil;

@Controller
public class EcpayController {

    @Autowired
    private OrderService orderService;

    private final String MERCHANT_ID = "3002607";
    private final String HASH_KEY = "pwFHCqoQZGmho4w6";
    private final String HASH_IV = "EkRm7iFT261dpevs";
    @Value("${backend.url}")
    private String backendUrl;
    @Value("${frontend.url}")
    private String frontendUrl;

    // ⭐ 產生綠界付款
    @GetMapping("/ecpay/pay")
    public void pay(@RequestParam int amount,
            @RequestParam String orderId,
            HttpServletResponse response) throws IOException {

        System.out.println("🔵 產生綠界付款, OrderID = " + orderId);

        String tradeNo = "OD" + System.currentTimeMillis();
        orderService.updateTradeNo(Integer.valueOf(orderId), tradeNo);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", MERCHANT_ID);
        params.put("MerchantTradeNo", tradeNo);
        params.put("MerchantTradeDate",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(amount));
        params.put("TradeDesc", "Movie Ticket");
        params.put("ItemName", "電影票");
        params.put("ReturnURL", backendUrl + "/ecpay/return");
        params.put("ClientBackURL", frontendUrl + "/payment/success/" + orderId);
        params.put("ChoosePayment", "Credit");
        params.put("EncryptType", "1");

        // ⭐ 產生正確 CheckMacValue
        params.put("CheckMacValue", EcpayUtil.generateCheckMacValue(params, HASH_KEY, HASH_IV));

        // ⭐ 自動送出 form
        StringBuilder form = new StringBuilder();
        form.append("<html><body onload='document.forms[0].submit()'>");
        form.append("<form method='POST' action='https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5'>");

        params.forEach((k, v) -> form.append("<input type='hidden' name='" + k + "' value='" + v + "'/>"));

        form.append("</form></body></html>");

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(form.toString());
    }
}
