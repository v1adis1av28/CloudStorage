package com.storage.services;


import com.storage.dto.FileInfoDto;
import com.storage.model.FileInfo;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

//Класс в котором будет проходить работа с DTO
@Service
public class DTOService {

    private final StringOperation stringOperation;

    @Autowired
    public DTOService(StringOperation stringOperation) {
        this.stringOperation = stringOperation;
    }


    public ArrayList<FileInfoDto> convertPathToFileInfoDto(HashSet<String> paths) {

        ArrayList<FileInfoDto> fileInfoDtos = new ArrayList<>();

        for (String path : paths) {
            FileInfoDto fileInfoDto = new FileInfoDto();
            fileInfoDto.setParentPath(stringOperation.getParentPath(path));
            fileInfoDto.setFullPath(path);
            fileInfoDto.setShortName(stringOperation.getShortFileName(path));
            fileInfoDtos.add(fileInfoDto);
        }
        return fileInfoDtos;
    }

}
