package com.pictech.dto;

/**
 * 封装前端发起的图像擦除（Inpainting）请求的数据。
 * 只包含前端需要动态传递的核心数据。
 */
public class IopaintRequest {

    // 对应前端生成的、去除了文本的源图 Base64
    private String image;

    // 对应前端绘制的、黑白蒙版图 Base64
    private String mask;

    // --- Standard Getters and Setters ---

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }
}