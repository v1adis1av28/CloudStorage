package com.storage.services;


import com.storage.dto.FileInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class DTOService {

    private final StringOperation stringOperation;

    @Autowired
    public DTOService(StringOperation stringOperation) {
        this.stringOperation = stringOperation;
    }


    public ArrayList<FileInfoDto> convertPathToFileInfoDto(Collection<String> paths) {

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
