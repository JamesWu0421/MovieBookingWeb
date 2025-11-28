package tw.com.ispan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")

public class UploadController {

    // 圖片儲存路徑（可以在 application.properties 設定）
    @Value("${upload.path:${user.dir}/uploads}")
    private String uploadPath;

    // 圖片訪問的基礎 URL
    @Value("${upload.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * 上傳圖片
     * 
     * @param file 圖片檔案
     * @return 圖片 URL
     */
    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 檢查檔案是否為空
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "檔案不能為空");
                return ResponseEntity.badRequest().body(response);
            }

            // 檢查檔案類型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "只能上傳圖片檔案");
                return ResponseEntity.badRequest().body(response);
            }

            // 檢查檔案大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "圖片大小不能超過 2MB");
                return ResponseEntity.badRequest().body(response);
            }

            // 建立上傳目錄
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成唯一檔案名稱
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            String filename = timestamp + "_" + uuid + extension;

            // 儲存檔案
            Path filePath = uploadDir.resolve(filename);
            file.transferTo(filePath.toFile());

            // 生成訪問 URL
            String imageUrl = baseUrl + "/uploads/" + filename;

            response.put("success", true);
            response.put("message", "上傳成功");
            response.put("url", imageUrl);
            response.put("filename", filename);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "上傳失敗：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 刪除圖片
     * 
     * @param filename 檔案名稱
     * @return 刪除結果
     */
    @DeleteMapping("/image/{filename}")
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable String filename) {
        Map<String, Object> response = new HashMap<>();

        try {
            Path filePath = Paths.get(uploadPath).resolve(filename);
            Files.deleteIfExists(filePath);

            response.put("success", true);
            response.put("message", "刪除成功");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "刪除失敗：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
