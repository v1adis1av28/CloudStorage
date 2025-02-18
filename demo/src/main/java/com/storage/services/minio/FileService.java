package com.storage.services.minio;


import com.storage.dto.FileInfoDto;
import com.storage.model.File;
import com.storage.services.DTOService;
import com.storage.services.StringOperation;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class FileService {

    private String ROOT_BUCKET = "user-files";
    private final MinioClient minioClient;
    private final StringOperation stringOperation;
    private final DTOService dtoService;
    @Autowired
    public FileService(MinioClient minioClient, StringOperation stringOperation, DTOService dtoService) {
        this.minioClient = minioClient;
        this.stringOperation = stringOperation;
        this.dtoService = dtoService;
    }
    
    public void createInitialUserFolder(int userId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean checkBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(ROOT_BUCKET).build());
        String userFolder = "user-" + userId + "-files/";
        if (checkBucketExist) {
            System.out.println("Bucket with that name already exists!");
        } else {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(ROOT_BUCKET).build());
        }
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(ROOT_BUCKET).object(userFolder)
                .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                .build());
    }


    public void uploadFile(File file)
            throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        if (file.getInputStream() == null) {
            throw new IllegalArgumentException("InputStream не может быть null.");
        }

        if (file == null || file.getFileName().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым.");
        }
        Path tempFile = Files.createTempFile(
                file.getFileName().substring(0, file.getFileName().lastIndexOf('.')),
                file.getFileName().substring(file.getFileName().lastIndexOf('.'))
        );

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        String objectPath = file.getFolderPath() + file.getFileName();

        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .object(objectPath)
                        .filename(tempFile.toString())
                        .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                        .build()
        );

        Files.deleteIfExists(tempFile);
    }


    public void uploadFolder(String folderPath, MultipartFile[] files) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String objectPath = folderPath + filename;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(ROOT_BUCKET)
                            .object(objectPath)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
    }

    @SneakyThrows
    public void renameFile(String newFileName, String oldFilePath) {
        String pathToFile = oldFilePath.substring(0, oldFilePath.lastIndexOf('/') + 1);
        minioClient.copyObject(CopyObjectArgs.builder()
                .bucket(ROOT_BUCKET)
                .object(pathToFile + newFileName)
                .source(CopySource.builder()
                        .bucket(ROOT_BUCKET).object(oldFilePath).build()).build());
        removeFile(oldFilePath);
    }

    @SneakyThrows
    public void removeFile(String filePath) {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(ROOT_BUCKET).object(filePath).build());
    }


    @SneakyThrows
    public InputStream downloadFile(String filePath) {
        if (filePath.endsWith("/")) {
            throw new IllegalArgumentException("Вы не можете скачать папку");
        }
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .object(filePath)
                        .build()
        );
    }


    @SneakyThrows
    public void removeFolder(String folderPath) {
        if (!folderPath.endsWith("/")) {
            throw new IllegalArgumentException("Folder path must end with '/'");
        }

        String parentFolderPath = folderPath.substring(0, folderPath.substring(0, folderPath.length() - 1).lastIndexOf('/') + 1);

        ArrayList<String> itemsToDelete = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .prefix(folderPath)
                        .recursive(true)
                        .build());

        for (Result<Item> result : results) {
            itemsToDelete.add(result.get().objectName());
        }

        for (String item : itemsToDelete) {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(ROOT_BUCKET).object(item).build());
        }

        if (!parentFolderPath.equals("/")) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(ROOT_BUCKET)
                            .object(parentFolderPath)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build());
        }
    }


    @SneakyThrows
    public void createEmptyFolder(int userId, String folderPath) {
        if (!folderPath.endsWith("/")) {
            throw new IllegalArgumentException("Folder path must end with '/'");
        }

        String userFolder = "user-" + userId + "-files/";
        String fullFolderPath = userFolder + folderPath;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .object(fullFolderPath)
                        .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                        .build()
        );
    }


    @SneakyThrows
    public void renameFolder(String folderPath, String newFolderName) {
        if (!folderPath.endsWith("/")) {
            throw new IllegalArgumentException("Folder path must end with '/'");
        }

        String parentFolderPath = stringOperation.trimFolderPath(folderPath);

        String newFolderPath = parentFolderPath + newFolderName + "/";

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .prefix(folderPath)
                        .recursive(true)
                        .build());

        for (Result<Item> result : results) {
            Item item = result.get();
            String itemName = item.objectName();

            String newObjectPath = newFolderPath + itemName.substring(folderPath.length());

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(ROOT_BUCKET)
                            .object(newObjectPath)
                            .source(CopySource.builder()
                                    .bucket(ROOT_BUCKET)
                                    .object(itemName)
                                    .build())
                            .build());

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(ROOT_BUCKET)
                            .object(itemName)
                            .build());
        }

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .object(newFolderPath)
                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build());
    }

    public ArrayList<FileInfoDto> getFolderObjects(String userRootFolder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(ROOT_BUCKET).recursive(true).prefix(userRootFolder).build());
        ArrayList<String> arr = new ArrayList<>();
        for (Result<Item> result : results) {
            arr.add(result.get().objectName());
        }
        HashSet<String> tmp = stringOperation.getFileAndFolders(userRootFolder,arr);
        return dtoService.convertPathToFileInfoDto(tmp);
    }

    public ArrayList<FileInfoDto> getObjectsByQuery(String query) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(ROOT_BUCKET).recursive(true).build());
        ArrayList<String> arr = new ArrayList<>();
        System.out.println("getObjectsByQuery");
        for (Result<Item> result : results) {
            arr.add(result.get().objectName());
            System.out.println(result.get().objectName());
        }
        List<String> objects = stringOperation.findObjectsByQuery(query,arr);
        return dtoService.convertPathToFileInfoDto(objects);
    }
}
