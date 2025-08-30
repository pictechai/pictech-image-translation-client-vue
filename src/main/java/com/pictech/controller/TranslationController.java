package com.pictech.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.pictech.dto.*;
import com.pictech.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 提供图片翻译的 RESTful API 接口
 */
@CrossOrigin(origins = "*") // 或指定具体地址
@RestController
@RequestMapping("/api/translate")
public class TranslationController {

    private final TranslationService translationService;
    // 【新增】通过 @Value 注解从 application.properties 文件中读取上传路径
    // 这样配置更灵活，避免硬编码
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    /**
     * 接口1: 通过图片 URL 提交翻译任务
     */
    @PostMapping("/url")
    public ResponseEntity<Object> submitFromUrl(@RequestBody UrlTranslationRequest request) {
        try {
            Map<String, Object> result = translationService.submitTaskFromUrl(
                    request.getImageUrl(),
                    request.getSourceLanguage(),
                    request.getTargetLanguage()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 使用 Collections.singletonMap() 替换 Map.of()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 接口2: 通过 Base64 字符串提交翻译任务
     */
    @PostMapping("/base64")
    public ResponseEntity<Object> submitFromBase64(@RequestBody Base64TranslationRequest request) {
        try {
            Map<String, Object> result = translationService.submitTaskFromBase64(
                    request.getImageBase64(),
                    request.getSourceLanguage(),
                    request.getTargetLanguage()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 使用 Collections.singletonMap() 替换 Map.of()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 接口3: 通过文件上传方式提交翻译任务 (推荐)
     */
    @PostMapping("/upload")
    public ResponseEntity<Object> submitFromFileUpload(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("sourceLanguage") String sourceLanguage,
                                                       @RequestParam("targetLanguage") String targetLanguage) {
        if (file.isEmpty()) {
            // 使用 Collections.singletonMap() 替换 Map.of()
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "上传文件不能为空"));
        }
        try {
            Map<String, Object> result = translationService.submitTaskFromFile(file, sourceLanguage, targetLanguage);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 使用 Collections.singletonMap() 替换 Map.of()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 【新增】接口2: 保存编辑器画布状态
     *
     * @param request 包含任务ID和最新画布状态的请求对象
     * @return 成功时返回成功信息，失败时返回错误信息
     */
    @PostMapping("/save")
    public ResponseEntity<Object> saveState(@RequestBody SaveStateRequest request) {
        try {
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("Code", 200);
            mockResponse.put("Message", "状态保存成功");
            mockResponse.put("RequestId", UUID.randomUUID().toString());
            // 返回成功的响应
            if (request == null || request.getData() == null) {
                return ResponseEntity.ok(mockResponse);
            }
            // 调用 Service 层处理保存逻辑
//          Map<String, Object> result = translationService.saveTaskState(request);
            System.out.println(request.getData().getFinalImageUrl());
            // 返回成功的响应
            return ResponseEntity.ok(mockResponse);
        } catch (IllegalArgumentException e) {
            // 处理无效参数的异常
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "请求参数错误: " + e.getMessage()));
        } catch (Exception e) {
            // 处理其他所有服务器内部异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 接口4: 查询翻译任务的结果
     */
    @GetMapping("/result/{requestId}")
    public ResponseEntity<Object> queryResult(@PathVariable String requestId) {
        try {
            Map<String, Object> result = translationService.queryTaskResult(requestId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 使用 Collections.singletonMap() 替换 Map.of()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 【全新接口】接收前端导出的 Base64 图片并保存到服务器
     *
     * @param request 包含 Base64 图片数据、任务ID和文件名的请求对象
     * @return 成功时返回文件的访问路径，失败时返回错误信息
     */
    @PostMapping("/uploadExportedImage")
    public ResponseEntity<Object> uploadExportedImage(@RequestBody UploadedImageRequest request) {
        // 1. 参数校验
        if (request == null || !StringUtils.hasText(request.getImageBase64())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "图片数据不能为空"));
        }

        try {
            // 2. 解码 Base64 字符串为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(request.getImageBase64());

            // 3. 生成文件名和路径
            // 为了避免文件名冲突和更好地组织文件，我们创建一个按日期分类的子目录
            String dateFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Path directoryPath = Paths.get(uploadDir+"/export", dateFolder);

            // 确保目录存在，如果不存在则创建
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // 生成一个唯一的文件名来防止覆盖，同时保留原始文件的扩展名
            String originalFilename = StringUtils.hasText(request.getFilename()) ? request.getFilename() : "exported.png";
            String fileExtension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFilename.substring(dotIndex);
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // 最终文件的完整路径
            Path filePath = directoryPath.resolve(uniqueFileName);
            File fileToSave = filePath.toFile();

            // 4. 将字节数组写入文件
            try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                fos.write(imageBytes);
            }

            System.out.println("成功保存导出图片，路径: " + fileToSave.getAbsolutePath());

            // 5. 返回成功的响应，包含文件的相对访问路径
            // 注意：这里的路径是给前端访问用的，需要根据你的静态资源服务器配置来调整
            String accessiblePath = "/" + dateFolder + "/" + uniqueFileName;
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "文件上传成功");
            responseBody.put("filePath", accessiblePath);

            return ResponseEntity.ok(responseBody);

        } catch (IOException e) {
            System.err.println("文件写入时发生IO异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "文件保存失败，服务器IO错误"));
        } catch (IllegalArgumentException e) {
            // Base64 解码失败时会抛出这个异常
            System.err.println("Base64 解码失败: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "无效的Base64数据"));
        }
    }


    /**
     * 【新增接口】
     * 接口2: 代理图像擦除（Inpainting）请求
     * 这个接口接收前端的擦除请求，然后由Java后端调用Python的IOPaint服务，
     * 从而绕过浏览器的跨域限制。
     *
     * @param request 包含源图和蒙版图Base64的请求体
     * @return 包含处理后新图片Base64的响应体
     */
    @PostMapping("/iopaint")
    public ResponseEntity<Object> performInpainting(@RequestBody IopaintRequest request) {
        try {
            // 1. 生成一个唯一的文件名，避免冲突
            String uniqueImageName = UUID.randomUUID().toString();

            // 3. 生成文件名和路径
            // 为了避免文件名冲突和更好地组织文件，我们创建一个按日期分类的子目录
            String dateFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Path directoryPath = Paths.get(uploadDir+"/iopaint", dateFolder);

            // 确保目录存在，如果不存在则创建
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // 2. 调用您现有的 Facade 逻辑来处理擦除，并将结果保存为文件
            // 注意：这里的 task 参数我们传入 null，如果您的业务需要，请创建或获取一个真实的 PctcBusinessTask
            String newImageNameWithExt = translationService.iopaint(
                    request.getImage(),
                    request.getMask(),
                    directoryPath.toString(),
                    uniqueImageName
            );

            // 3. 读取刚刚保存的新图片文件
            Path imagePath = directoryPath.resolve(newImageNameWithExt);
            if (!Files.exists(imagePath)) {
                throw new RuntimeException("处理后的图片文件未找到: " + imagePath);
            }
            byte[] imageBytes = Files.readAllBytes(imagePath);

            // 4. 将图片文件内容编码为 Base64 字符串
            String newImageBase64 = Base64.getEncoder().encodeToString(imageBytes);

            // 5. 将新的 Base64 字符串返回给前端
            //    使用 Collections.singletonMap() 创建一个只包含一个键值对的 Map
            return ResponseEntity.ok(Collections.singletonMap("newImageBase64", newImageBase64));

        } catch (Exception e) {
            // 记录详细错误日志
            // log.error("Inpainting process failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "图像擦除处理失败: " + e.getMessage()));
        }
    }


    /**
     * 【重构接口】接收 Inpaint 后的 Base64 图片，保存到项目静态资源目录并返回可访问 URL
     *
     * @param request 包含 Base64 图片数据的请求对象
     * @return 包含完整 URL 的标准响应体
     */
    @PostMapping("/uploadIoInpaintImage") // 【修改】接口名称已更改
    public ResponseEntity<Object> uploadIoInpaintImage(@RequestBody Map<String, String> request) {
        // 1. 参数校验
        String imageBase64 = request.get("imageData");
        if (!StringUtils.hasText(imageBase64)) {
            return ResponseEntity.badRequest().body(createErrorResponse("图片数据(imageData)不能为空"));
        }

        try {
            // 2. 解码 Base64
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);

            String savePath = "iopaint_front";

            // 3. 【核心改造】确定保存在项目内的静态资源路径
            // 我们将在 src/main/resources/static 目录下创建一个 'inpainted-images' 文件夹来存放这些图片
            // 3. 生成文件名和路径
            // 为了避免文件名冲突和更好地组织文件，我们创建一个按日期分类的子目录
            String dateFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            // 【保持不变】文件物理存储路径，使用 Paths 确保跨平台兼容性
            Path directoryPath = Paths.get(uploadDir, savePath, dateFolder);

            // 确保目录存在，如果不存在则创建
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            String uniqueFileName = UUID.randomUUID().toString() + ".png";
            Path physicalFilePath = directoryPath.resolve(uniqueFileName);
            File fileToSave = physicalFilePath.toFile();

            // 4. 写入文件
            try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                fos.write(imageBytes);
            }

            String finalUrl = "/" + String.join("/", savePath, dateFolder, uniqueFileName);

            System.out.println("返回给前端的 URL: " + finalUrl);

            // 6. 返回标准格式的成功响应
            return ResponseEntity.ok(createSuccessResponse(finalUrl));

        } catch (IOException e) {
            System.err.println("文件写入或路径查找时发生IO异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("文件保存失败，服务器IO错误"));
        } catch (IllegalArgumentException e) {
            System.err.println("Base64 解码失败: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("无效的Base64数据"));
        }
    }

    // --- 辅助方法，用于创建标准响应体 ---

    /**
     * 创建一个标准的成功响应体
     * @param url 上传成功后的图片URL
     * @return 一个 Map 对象，可被序列化为 JSON
     */
    private Map<String, Object> createSuccessResponse(String url) {
        Map<String, Object> response = new HashMap<>();
        response.put("Code", 200);
        response.put("Message", "上传成功");

        Map<String, String> data = new HashMap<>();
        data.put("Url", url);
        response.put("Data", data);

        return response;
    }

    /**
     * 创建一个标准的错误响应体
     * @param errorMessage 错误信息
     * @return 一个 Map 对象
     */
    private Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("Code", 500);
        response.put("Message", errorMessage);
        response.put("Data", null);
        return response;
    }
}