package com.pictech.demo;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

public class TemplateJson {
    public String version;

    @JSONField(name = "fabritor_schema_version")
    public int fabritorSchemaVersion;

    public ClipPath clipPath;

    public List<FabricObject> objects;

    public String getVersion() {
        return version;
    }

    public TemplateJson setVersion(String version) {
        this.version = version;
        return this;
    }

    public int getFabritorSchemaVersion() {
        return fabritorSchemaVersion;
    }

    public TemplateJson setFabritorSchemaVersion(int fabritorSchemaVersion) {
        this.fabritorSchemaVersion = fabritorSchemaVersion;
        return this;
    }

    public ClipPath getClipPath() {
        return clipPath;
    }

    public TemplateJson setClipPath(ClipPath clipPath) {
        this.clipPath = clipPath;
        return this;
    }

    public List<FabricObject> getObjects() {
        return objects;
    }

    public TemplateJson setObjects(List<FabricObject> objects) {
        this.objects = objects;
        return this;
    }
}

class ClipPath {
    public String type;
    public String version;
    public int left;
    public int top;
    public int width;
    public int height;
    public String fill;
    public boolean selectable;
    public boolean hasControls;

    public String getType() {
        return type;
    }

    public ClipPath setType(String type) {
        this.type = type;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ClipPath setVersion(String version) {
        this.version = version;
        return this;
    }

    public int getLeft() {
        return left;
    }

    public ClipPath setLeft(int left) {
        this.left = left;
        return this;
    }

    public int getTop() {
        return top;
    }

    public ClipPath setTop(int top) {
        this.top = top;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public ClipPath setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public ClipPath setHeight(int height) {
        this.height = height;
        return this;
    }

    public String getFill() {
        return fill;
    }

    public ClipPath setFill(String fill) {
        this.fill = fill;
        return this;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public ClipPath setSelectable(boolean selectable) {
        this.selectable = selectable;
        return this;
    }

    public boolean isHasControls() {
        return hasControls;
    }

    public ClipPath setHasControls(boolean hasControls) {
        this.hasControls = hasControls;
        return this;
    }
}

class FabricObject {
    public String type;
    public String version;
    public String id;
    public int left;
    public int top;
    public int width;
    public int height;
    public String fill;
    public double angle;
    public double scaleX;
    public double scaleY;
    public String fontFamily;
    public String fontWeight;
    public int fontSize;
    public String text;

    public List<FabricObject> objects;  // 嵌套对象支持
    public ClipPath clipPath;           // 如果存在 clipPath 字段
    public String src;                  // 图片对象中的 URL

    public String getType() {
        return type;
    }

    public FabricObject setType(String type) {
        this.type = type;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public FabricObject setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getId() {
        return id;
    }

    public FabricObject setId(String id) {
        this.id = id;
        return this;
    }

    public int getLeft() {
        return left;
    }

    public FabricObject setLeft(int left) {
        this.left = left;
        return this;
    }

    public int getTop() {
        return top;
    }

    public FabricObject setTop(int top) {
        this.top = top;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public FabricObject setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public FabricObject setHeight(int height) {
        this.height = height;
        return this;
    }

    public String getFill() {
        return fill;
    }

    public FabricObject setFill(String fill) {
        this.fill = fill;
        return this;
    }

    public double getAngle() {
        return angle;
    }

    public FabricObject setAngle(double angle) {
        this.angle = angle;
        return this;
    }

    public double getScaleX() {
        return scaleX;
    }

    public FabricObject setScaleX(double scaleX) {
        this.scaleX = scaleX;
        return this;
    }

    public double getScaleY() {
        return scaleY;
    }

    public FabricObject setScaleY(double scaleY) {
        this.scaleY = scaleY;
        return this;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public FabricObject setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public FabricObject setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
        return this;
    }

    public int getFontSize() {
        return fontSize;
    }

    public FabricObject setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public String getText() {
        return text;
    }

    public FabricObject setText(String text) {
        this.text = text;
        return this;
    }

    public List<FabricObject> getObjects() {
        return objects;
    }

    public FabricObject setObjects(List<FabricObject> objects) {
        this.objects = objects;
        return this;
    }

    public ClipPath getClipPath() {
        return clipPath;
    }

    public FabricObject setClipPath(ClipPath clipPath) {
        this.clipPath = clipPath;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public FabricObject setSrc(String src) {
        this.src = src;
        return this;
    }
}

