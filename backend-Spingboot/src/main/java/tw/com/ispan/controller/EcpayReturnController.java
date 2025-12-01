package tw.com.ispan.controller;

import tw.com.ispan.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ecpay")
public class EcpayReturnController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/return")
    public String paymentReturn(HttpServletRequest request) {

        String rtnCode = request.getParameter("RtnCode");
        String tradeNo = request.getParameter("MerchantTradeNo");

        System.out.println("🟢 綠界回傳 | tradeNo=" + tradeNo + " | RtnCode=" + rtnCode);

        if ("1".equals(rtnCode)) {
            orderService.updateStatusByTradeNo(tradeNo, "COMPLETED");
            System.out.println("✔付款成功→訂單更新為 COMPLETED");
        } else {
            orderService.updateStatusByTradeNo(tradeNo, "FAILED");
            System.out.println("❌付款失敗→訂單更新為 FAILED");
        }

        return "1|OK"; // 回傳綠界必須這樣
    }
}