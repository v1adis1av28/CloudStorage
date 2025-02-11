package com.storage.services;

import com.storage.dto.FileInfoDto;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;

@Service
public class StringOperation {

    //user-20-files/test/some/
    //Method get folder name -> trim -> return folder path for create new folder name 
    public String trimFolderPath(String str)
    {
        if(!str.endsWith("/"))
        {
            return "your path doesn't end with /";
        }
        return str.substring(0,str.lastIndexOf("/", str.lastIndexOf("/")-1)+1);
    }

//   Функция для получения путей до файлов и папок в передаваемой директории(rootPath), arrayList() все файлы по ссылке переданной в fileService checkPath
    public HashSet<String> getFileAndFolders(String rootPath, ArrayList<String> arrayList)
    {
        HashSet<String> results = new HashSet<>();
        for(String path : arrayList)
        {
            if(results.contains(path) || path.equals(rootPath))
            {
                continue;
            }
            boolean check = path.substring(rootPath.length()).contains("/");
            if(!check)
            {
                //System.out.println(path);
                results.add(path);
            }
            else
            {
                String folderPath = path.substring(0, path.indexOf("/",rootPath.length()+1)+1);
                if(!folderPath.isEmpty())
                {
                    results.add(folderPath);
                }
            }
        }
        for(String res : results)
        {
            System.out.println(res);
        }
        return results;
    }

    //user-20-files/test/some/
    //user-20-files/test/some.txt
    public String getShortFileName(String str)
    {

        if(!str.endsWith("/"))
        {
            return str.substring(str.lastIndexOf("/")+1,str.length());
        }
        else
        {
            return str.split("/")[str.split("/").length-1] + "/";
        }
    }

    public String getParentPath(String str) {

        if(!str.endsWith("/"))
            return str.substring(0,str.lastIndexOf("/")+1);
        else
        {
            return str.substring(0,str.substring(0,str.lastIndexOf("/")).lastIndexOf("/")+1);
        }
    }

    public String encodeFilenameForDownload(String filename) {
        if (StringUtils.hasText(filename)) {
            try {
                String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.name())
                        .replace("+", "%20");
                return "filename=\"" + sanitizeAsciiFilename(filename) + "\"; filename*=UTF-8''" + encodedFilename;
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Failed to encode filename", e);
            }
        }
        return "filename=\"unknown\"";
    }

    private String sanitizeAsciiFilename(String filename) {
        return filename.replaceAll("[^\\x20-\\x7e]", "_");
    }

    public boolean rightsVerification(String currentPath, String userRootFolder) {

        if(userRootFolder.equals(currentPath.split("/")[0]))
        {
            return true;
        }
        return false;
    }
}
