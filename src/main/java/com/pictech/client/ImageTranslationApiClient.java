package com.pictech.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PicTech API 客户端 (图片翻译与抠图)
 * 负责与远程 API 服务进行通信，包括构建请求、生成签名、发送 HTTP 请求，
 * 并处理图片翻译和背景移除任务。
 */
@Component
public class ImageTranslationApiClient {

    // --- 日志配置 ---
    private static final Logger LOGGER = Logger.getLogger(ImageTranslationApiClient.class.getName());

    static {
        ConsoleHandler handler = new ConsoleHandler();
        // 设置日志格式，方便阅读
        handler.setFormatter(new java.util.logging.SimpleFormatter() {
            @Override
            public String format(java.util.logging.LogRecord record) {
                return String.format("%1$tF %1$tT - %2$s - %3$s%n",
                        record.getMillis(), record.getLevel(), record.getMessage());
            }
        });
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.INFO); // 设置日志级别为 INFO，可以根据需要调整为 Level.FINE 进行更详细的调试
    }

    // --- 从 application.properties 注入配置 ---
    private final String apiBaseUrl;
    private final String apiKey;
    private final String secretKey;

    // --- HTTP 和 JSON 工具 ---
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // --- API 端点常量 (清晰分离不同功能) ---
    private static final String TRANSLATION_SUBMIT_ENDPOINT = "/submit_task";
    private static final String TRANSLATION_QUERY_ENDPOINT = "/query_result";
    private static final String BG_REMOVAL_SUBMIT_ENDPOINT = "/submit_remove_background_task";
    private static final String BG_REMOVAL_QUERY_ENDPOINT = "/query_remove_background_result";
    private static final String INPAINT_SYNC_ENDPOINT = "/inpaint_image_sync";

    /**
     * 构造函数，通过 Spring 依赖注入初始化配置和工具。
     *
     * @param apiBaseUrl API 基础 URL
     * @param apiKey     API Key
     * @param secretKey  API Secret
     */
    public ImageTranslationApiClient(
            @Value("${pictech.api.base-url}") String apiBaseUrl,
            @Value("${pictech.api.key}") String apiKey,
            @Value("${pictech.api.secret}") String secretKey) {
        this.apiBaseUrl = apiBaseUrl;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // ===================================================================================
    // =                            图片翻译功能 (Image Translation)                       =
    // ===================================================================================

    /**
     * 提交基于图片 URL 的翻译任务。
     *
     * @param imageUrl       图片的公开可访问 URL
     * @param sourceLanguage 源语言代码 (例如 "en", "zh")
     * @param targetLanguage 目标语言代码 (例如 "es", "ja")
     * @return API 返回的响应体
     */
    public Map<String, Object> submitTranslationTaskWithUrl(String imageUrl, String sourceLanguage, String targetLanguage) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ImageUrl", imageUrl);
        payload.put("SourceLanguage", sourceLanguage);
        payload.put("TargetLanguage", targetLanguage);
        return executePostRequest(TRANSLATION_SUBMIT_ENDPOINT, payload);
    }

    /**
     * 提交基于 Base64 编码图片的翻译任务。
     *
     * @param imageBase64    图片的 Base64 编码字符串 (不包含 data:image/... 前缀)
     * @param sourceLanguage 源语言代码
     * @param targetLanguage 目标语言代码
     * @return API 返回的响应体
     */
    public Map<String, Object> submitTranslationTaskWithBase64(String imageBase64, String sourceLanguage, String targetLanguage) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ImageBase64", imageBase64);
        payload.put("SourceLanguage", sourceLanguage);
        payload.put("TargetLanguage", targetLanguage);
        // 根据文档，翻译任务可能需要指定 OutputType，这里假设为1
//        payload.put("OutputType", 1);
        return executePostRequest(TRANSLATION_SUBMIT_ENDPOINT, payload);
    }

    /**
     * 查询指定任务 ID 的翻译结果。
     *
     * @param requestId 提交任务时获取的任务 ID
     * @return API 返回的响应体
     */
    public Map<String, Object> queryTranslationTaskResult(String requestId) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("RequestId", requestId);
        return executePostRequest(TRANSLATION_QUERY_ENDPOINT, payload);
    }


    // ===================================================================================
    // =                           背景移除功能 (Background Removal)                       =
    // ===================================================================================

    /**
     * 执行抠图任务的高级封装方法。
     * 自动完成提交、轮询查询、下载并保存结果图片的整个流程。
     *
     * @param imagePath      本地图片路径 (与 imageUrl 二选一)
     * @param imageUrl       图片 URL (与 imagePath 二选一)
     * @param outputDir      输出目录
     * @param outputFilename 输出文件名
     * @return 是否成功完成整个流程
     */
    public boolean removeBackground(String imagePath, String imageUrl, String outputDir, String outputFilename) throws Exception {
        long startTime = System.currentTimeMillis();

        // --- 1. 准备请求参数 (Base64 或 URL) ---
        String imageBase64 = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            imageBase64 = readImageAsBase64(imagePath);
            if (imageBase64 == null) {
                LOGGER.severe("从路径读取图片并转换为 Base64 失败: " + imagePath);
                return false;
            }
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("BgColor","white");
        if (imageBase64 != null) {
            payload.put("ImageBase64", imageBase64.substring(imageBase64.indexOf(',') + 1)); // 移除 Data URL 前缀
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            payload.put("ImageUrl", imageUrl);
        } else {
            LOGGER.severe("必须提供本地图片路径(imagePath)或图片URL(imageUrl)中的一个！");
            return false;
        }

        // --- 2. 提交抠图任务 ---
        LOGGER.info("正在提交抠图任务...");
        Map<String, Object> submitResponse = executePostRequest(BG_REMOVAL_SUBMIT_ENDPOINT, payload);
        if (submitResponse == null || !submitResponse.getOrDefault("Code", -1).equals(200)) {
            String errorMessage = submitResponse != null ? submitResponse.get("Message").toString() : "无响应";
            LOGGER.severe("抠图任务提交失败: " + errorMessage);
            return false;
        }

        String requestId = (String) submitResponse.get("RequestId");
        LOGGER.info("任务提交成功, RequestId: " + requestId);
        LOGGER.fine("提交任务响应详情: " + objectMapper.writeValueAsString(submitResponse));

        // --- 3. 轮询查询结果 ---
        int maxAttempts = 15; // 最多尝试15次
        long intervalMs = 1500; // 初始查询间隔1.5秒

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            Map<String, Object> result = queryRemoveBackgroundTaskResult(requestId);
            if (result == null) {
                LOGGER.severe("查询任务 " + requestId + " 失败: 无响应。");
                return false;
            }

            LOGGER.fine("查询响应 (第 " + attempt + " 次): " + objectMapper.writeValueAsString(result));
            int code = ((Number) result.getOrDefault("Code", -1)).intValue();

            if (code == 200) { // 任务成功
                Map<String, Object> data = (Map<String, Object>) result.get("Data");
                if (data == null || !data.containsKey("OutputUrl")) {
                    LOGGER.severe("任务成功，但响应中未找到有效的输出URL (OutputUrl)。");
                    return false;
                }

                String outputUrl = (String) data.get("OutputUrl");
                LOGGER.info("任务处理成功，结果图片URL: " + outputUrl);

                // --- 4. 下载并保存图片 ---
                try {
                    Path outputPath = Paths.get(outputDir, outputFilename);
                    Files.createDirectories(outputPath.getParent()); // 确保目录存在
                    byte[] imageBytes = restTemplate.getForObject(outputUrl, byte[].class);
                    if (imageBytes == null) {
                        LOGGER.severe("从 " + outputUrl + " 下载图片失败，得到空内容。");
                        return false;
                    }
                    Files.write(outputPath, imageBytes);
                    long endTime = System.currentTimeMillis();
                    double duration = (endTime - startTime) / 1000.0;
                    LOGGER.info("图片已成功保存到: " + outputPath);
                    LOGGER.info(String.format("任务总耗时: %.2f 秒", duration));
                    return true;
                } catch (IOException | RestClientException e) {
                    LOGGER.severe("下载或保存图片失败: " + e.getMessage());
                    return false;
                }
            } else if (code == 202) { // 任务处理中
                LOGGER.info("任务 " + requestId + " 仍在处理中，" + (intervalMs / 1000.0) + "秒后重试 (尝试 " + attempt + "/" + maxAttempts + ")");
                Thread.sleep(intervalMs);
            } else { // 任务失败
                String errorMessage = result.get("Message") + ", ErrorCode: " + result.get("ErrorCode");
                LOGGER.severe("任务 " + requestId + " 处理失败: " + errorMessage);
                return false;
            }
        }

        LOGGER.severe("任务 " + requestId + " 在 " + maxAttempts + " 次尝试后仍未完成，已超时。");
        return false;
    }

    /**
     * 查询指定任务 ID 的抠图结果。
     *
     * @param requestId 提交任务时获取的任务 ID
     * @return API 返回的响应体
     */
    public Map<String, Object> queryRemoveBackgroundTaskResult(String requestId) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("RequestId", requestId);
        return executePostRequest(BG_REMOVAL_QUERY_ENDPOINT, payload);
    }


    // ===================================================================================
    // =                          私有辅助方法 (Private Helpers)                         =
    // ===================================================================================

    /**
     * 将本地图片文件读取为 Base64 Data URL 格式的字符串。
     * 例如: "data:image/png;base64,iVBORw0KGgo..."
     */
    private String readImageAsBase64(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            LOGGER.severe("文件未找到: " + filePath);
            return null;
        }

        String mimeType = Files.probeContentType(path);
        if (mimeType == null || !mimeType.startsWith("image/")) {
            LOGGER.warning("无法识别的图片MIME类型 '" + filePath + "'，将默认使用 image/jpeg。");
            mimeType = "image/jpeg";
        }

        byte[] imageBytes = Files.readAllBytes(path);
        String base64Data = Base64.getEncoder().encodeToString(imageBytes);
        return "data:" + mimeType + ";base64," + base64Data;
    }


    /**
     * 执行 POST 请求的核心方法。
     * 负责添加公共参数、生成签名、序列化并发送请求。
     *
     * @param endpoint API 端点路径 (例如 "/submit_task")
     * @param payload  请求的业务参数
     * @return API 返回的响应体，解析为 Map
     */
    private Map<String, Object> executePostRequest(String endpoint, Map<String, Object> payload) throws Exception {
        String timestamp = String.valueOf(ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toEpochSecond());

        // 1. 添加公共参数
        payload.put("AccountId", this.apiKey);
        payload.put("Timestamp", timestamp);

        // 2. 准备用于签名的参数 (所有参数值必须为字符串)
        Map<String, String> paramsForSignature = new HashMap<>();
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            paramsForSignature.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        String signature = generateSignature(paramsForSignature);
        payload.put("Signature", signature);

        // 3. 设置 HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 4. 将最终的 payload (包含签名) 转换为 JSON 字符串
        String requestBody = objectMapper.writeValueAsString(payload);
        LOGGER.fine("请求 URL: " + this.apiBaseUrl + endpoint);
        LOGGER.fine("请求体: " + requestBody);


        // 5. 创建并发送 HTTP 请求
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String fullUrl = this.apiBaseUrl + endpoint;

        try {
            // 使用 RestTemplate 发送请求，并期望返回一个可以映射到 Map 的 JSON
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(fullUrl, requestEntity, Map.class);
            return response;
        } catch (RestClientException e) {
            LOGGER.severe("调用 PicTech API 失败: " + fullUrl + ", 错误: " + e.getMessage());
            throw new RuntimeException("调用 PicTech API 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成 API 请求签名。
     * 流程:
     * 1. 将所有非空请求参数按 key 的字母顺序排序。
     * 2. 将排序后的参数拼接成 `key1=value1&key2=value2` 的形式。
     * 3. 在拼接后的字符串末尾加上 `&SecretKey=YOUR_SECRET_KEY`。
     * 4. 对最终的字符串进行 HMAC-SHA256 哈希计算，并进行 Base64 编码。
     *
     * @param params 用于签名的参数映射
     * @return 生成的 Base64 编码的签名字符串
     */
    private String generateSignature(Map<String, String> params)
            throws NoSuchAlgorithmException, InvalidKeyException {
        // 1. 过滤空值并排序
        List<Map.Entry<String, String>> sortedEntries = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                sortedEntries.add(entry);
            }
        }
        sortedEntries.sort(Map.Entry.comparingByKey());

        // 2. 构造签名字符串
        StringBuilder toSign = new StringBuilder();
        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<String, String> entry = sortedEntries.get(i);
            toSign.append(entry.getKey()).append("=").append(entry.getValue());
            if (i < sortedEntries.size() - 1) {
                toSign.append("&");
            }
        }
        toSign.append("&SecretKey=").append(this.secretKey);
        LOGGER.fine("用于签名的源字符串: " + toSign);

        // 3. HMAC-SHA256 + Base64
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = hmacSha256.doFinal(toSign.toString().getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(hash);
    }
    /**
     * 【新增】执行同步图片修复任务，并直接将结果保存到文件。
     *
     * @param sourceImageBase64 原始图片
     * @param maskImageBase64   蒙版图片
     * @return byte[] 修复后的图片字节信息
     * @throws Exception 如果在API调用或文件操作过程中发生严重错误。
     */
    public byte[] inpaintImageSync(String sourceImageBase64, String maskImageBase64) throws Exception {
        long startTime = System.currentTimeMillis();
        LOGGER.info("正在启动同步图片修复任务...");

        // --- 4. 将字节流写入文件 ---
        try {
            if (sourceImageBase64 == null || maskImageBase64 == null) {
                LOGGER.severe("读取源图片或蒙版图片失败，请检查文件路径。");
                return null;
            }
            // --- 2. 准备请求体 ---
            Map<String, Object> payload = new HashMap<>();
            // 中文备注：根据服务端要求，传递不带 "data:" 前缀的纯 Base64 字符串
            payload.put("image", sourceImageBase64.substring(sourceImageBase64.indexOf(',') + 1));
            payload.put("mask", maskImageBase64.substring(maskImageBase64.indexOf(',') + 1));
            // --- 3. 调用API并获取图片字节流 ---
            byte[] imageBytes = executePostRequestForBytes(INPAINT_SYNC_ENDPOINT, payload);
            if (imageBytes == null || imageBytes.length == 0) {
                LOGGER.severe("API调用失败或未返回有效的图片数据。");
                return null;
            }
            return imageBytes;
        } catch (IOException e) {
            LOGGER.severe("保存修复后的图片失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 【新增】执行 POST 请求的核心方法，专门用于期望返回二进制数据 (如图片) 的场景。
     *
     * @param endpoint API 端点路径
     * @param payload  请求的业务参数
     * @return API 返回的二进制字节数组 (byte[])
     * @throws Exception API 调用失败时抛出异常
     */
    private byte[] executePostRequestForBytes(String endpoint, Map<String, Object> payload) throws Exception {
        String timestamp = String.valueOf(ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toEpochSecond());

        // 1. 添加公共参数
        payload.put("AccountId", this.apiKey);
        payload.put("Timestamp", timestamp);

        // 2. 准备签名
        Map<String, String> paramsForSignature = new HashMap<>();
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            paramsForSignature.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        String signature = generateSignature(paramsForSignature);
        payload.put("Signature", signature);

        // 3. 设置 HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 中文备注：告诉服务器客户端可以接受任何类型的响应
        headers.setAccept(Collections.singletonList(MediaType.ALL));

        // 4. 转换请求体为 JSON
        String requestBody = objectMapper.writeValueAsString(payload);
        LOGGER.fine("请求体: " + requestBody);

        // 5. 创建并发送 HTTP 请求
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String fullUrl = this.apiBaseUrl + endpoint;

        try {
            // 中文备注：使用 exchange 方法，并指定期望的响应体类型为 byte[]
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class // 关键：期望返回字节数组
            );

            // 检查响应状态码是否为 2xx
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                LOGGER.severe("API 调用成功但返回了非 2xx 状态码: " + responseEntity.getStatusCode());
                return null;
            }
        } catch (HttpClientErrorException e) {
            // 中文备注：当服务器返回 4xx 或 5xx 错误时，RestTemplate 会抛出此异常
            // 我们可以从异常中获取带错误详情的 JSON 响应体
            String errorBody = e.getResponseBodyAsString(StandardCharsets.UTF_8);
            LOGGER.severe("调用 PicTech API 失败，状态码: " + e.getStatusCode() + ", 错误响应: " + errorBody);
            throw new RuntimeException("调用 PicTech API 失败: " + errorBody, e);
        } catch (RestClientException e) {
            // 中文备注：处理其他网络层面的错误，如连接超时
            LOGGER.severe("调用 PicTech API 时发生网络错误: " + fullUrl + ", 错误: " + e.getMessage());
            throw new RuntimeException("调用 PicTech API 时发生网络错误: " + e.getMessage(), e);
        }
    }
    public static class ImageResponse {
        private final String mediaType;
        private final byte[] imageBytes;

        public ImageResponse(String mediaType, byte[] imageBytes) {
            this.mediaType = mediaType;
            this.imageBytes = imageBytes;
        }

        public String getMediaType() {
            return mediaType;
        }

        public byte[] getImageBytes() {
            return imageBytes;
        }
    }
}