package com.storage.model;
import lombok.Data;

@Data
public class FileInfo {

    private String shortName;
    private String fullPath;
    private String parentPath;
    private long size;
    boolean isFile;

    FileInfo(String shortName, String fullPath, String parentPath, long size) {
        this.shortName = shortName;
        this.fullPath = fullPath;
        this.parentPath = parentPath;
        this.size = size;
        this.isFile = !fullPath.endsWith("/");
    }
}
