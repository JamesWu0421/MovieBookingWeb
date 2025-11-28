package tw.com.ispan.util;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

public class EcpayUtil {

    public static String generateCheckMacValue(Map<String, String> params, String hashKey, String hashIv) {

        // 1️⃣ 排序參數（照 A→Z 排）
        SortedMap<String, String> sortedMap = new TreeMap<>(params);

        // 2️⃣ 組成查核字串 QueryString
        StringBuilder checkValue = new StringBuilder("HashKey=" + hashKey);

        sortedMap.forEach((key, value) -> {
            if (value != null && !value.isEmpty())
                checkValue.append("&").append(key).append("=").append(value);
        });

        checkValue.append("&HashIV=").append(hashIv);

        // 3️⃣ URL Encode → 做兩次（綠界規範）
        String encoded = urlEncode(checkValue.toString()).toLowerCase();

        // 4️⃣ SHA256 加密 → 全大寫回傳
        return sha256(encoded).toUpperCase();
    }

    private static String urlEncode(String raw) {
        try {
            return URLEncoder.encode(raw, "UTF-8")
                    .replaceAll("%21", "!")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%2A", "*")
                    .replaceAll("%2D", "-")
                    .replaceAll("%2E", ".")
                    .replaceAll("%5F", "_")
                    .replaceAll("%7E", "~");
        } catch (Exception e) {
            return raw;
        }
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder out = new StringBuilder();
            for (byte b : hash) {
                out.append(String.format("%02x", b));
            }
            return out.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
