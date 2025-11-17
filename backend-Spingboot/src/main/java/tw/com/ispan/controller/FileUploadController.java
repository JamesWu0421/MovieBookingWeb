package tw.com.ispan.controller;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${file.upload.path:uploads/avatars/}")
    private String uploadPath;

    private static final int AVATAR_SIZE = 300;  // 頭像尺寸

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        
        // 驗證檔案
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("請選擇檔案");
        }

        // 驗證是否為圖片
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("只允許上傳圖片檔案");
        }

        // 驗證檔案大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("檔案大小不能超過 10MB");
        }

        try {
            // 建立上傳目錄
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 產生唯一檔名
            String filename = UUID.randomUUID().toString() + ".jpg";
            Path filePath = Paths.get(uploadPath, filename);

            // ✅ 裁切為正方形（取中心部分）
            Thumbnails.of(file.getInputStream())
                    .size(AVATAR_SIZE, AVATAR_SIZE)   // 300x300 正方形
                    .crop(Positions.CENTER)           // 從中心裁切
                    .outputFormat("jpg")              // 統一輸出 JPEG
                    .outputQuality(0.85)              // 85% 品質
                    .toFile(filePath.toFile());

            // 返回檔案 URL
            String fileUrl = "/uploads/avatars/" + filename;
            
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("檔案上傳失敗: " + e.getMessage());
        }
    }
}