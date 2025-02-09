package com.storage.model;
import lombok.Data;

@Data
public class FileInfo {

    private String shortName;//или само название файла или папка в виде(название/)
    private String fullPath;//Полный путь до файла пример(user-22-files/test/text.txt)
    private String parentPath;//Путь до файла не включая сам файла пример(user-22-files/test/text.txt -> полный путь, user-22-files/test/ -> parentPath)
    private long size;//Размер файла
    boolean isFile; //булевое значение, которое будет проверять по полному пути, файл это или нет

    FileInfo(String shortName, String fullPath, String parentPath, long size) {
        this.shortName = shortName;
        this.fullPath = fullPath;
        this.parentPath = parentPath;
        this.size = size;
        this.isFile = !fullPath.endsWith("/");
    }
}
