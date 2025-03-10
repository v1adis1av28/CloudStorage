package com.storage.model;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class File {

    private String folderPath;
    private String fileName;
    private String contentType;
    private InputStream inputStream;
    public File(String folderPath, String fileName, String contentType, InputStream inputStream) {
        this.folderPath = folderPath;
        this.fileName = fileName;
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
