package com.storage.dto;

import com.storage.model.FileInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@EqualsAndHashCode
public class FileInfoDto {
    private String shortName;
    private String fullPath;
    private String parentPath;

    public FileInfoDto(FileInfo fileInfo) {
        this.shortName = fileInfo.getShortName();
        this.fullPath = fileInfo.getFullPath();
        this.parentPath = fileInfo.getParentPath();
    }

    public FileInfoDto() {}

}
