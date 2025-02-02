package com.storage.services.minio;


import com.storage.utils.StringOperation;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.A;
import org.simpleframework.xml.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    private String ROOT_BUCKET = "user-files";
    private final String USER_FOLDER_PATH = "user-%d-files/";
    private final MinioClient minioClient;
    @Autowired
    public FileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    //функция инициализирующая папку для пользователя
    //private String userBucket = String.format("user-%d-files",);
    public void createInitialUserFolder(int userId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean checkBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(ROOT_BUCKET).build());
        if(checkBucketExist) {
             System.out.println("Bucket with that name already exists!");
        }
        else
        {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(ROOT_BUCKET).build());
        }
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(ROOT_BUCKET).object(String.format(USER_FOLDER_PATH,userId))
                .stream(new ByteArrayInputStream(new byte[]{}),0,-1)
                .build());
    }


    public void uploadFile(int userId, String folderPath, String localFilePath, String contentType)
            throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        String userFolder = "user-" + userId + "-files/";
        if (!folderPath.startsWith(userFolder)) {
            throw new IllegalArgumentException("Недопустимый путь. Файл должен находиться в " + userFolder);
        }
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .object(folderPath)// здесь задаем путь где он будет храниться после последнего слеша будет задаваться имя файла
                        .filename(localFilePath)//путь к файлу на локальном устройстве
                        .contentType(contentType)
                        .build()
        );
    }


    //Сперва нужно копировать файл,
    @SneakyThrows
    public void renameFile(int userId, String newFileName, String oldFilePath)
    {
     //user-20-files/test/minioLaunch
        String pathToFile = oldFilePath.substring(0,oldFilePath.lastIndexOf('/') + 1);
        String oldFileName = oldFilePath.substring(oldFilePath.lastIndexOf('/') + 1);
        minioClient.copyObject(CopyObjectArgs.builder()
                .bucket(ROOT_BUCKET)
                .object(pathToFile+newFileName)
                .source(CopySource.builder()
                .bucket(ROOT_BUCKET).object(oldFilePath).build()).build());
        //call remove method
        removeFile(oldFilePath);
    }

    @SneakyThrows
    public void removeFile(String filePath)
    {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(ROOT_BUCKET).object(filePath).build());
    }


    //user-20-files/test/
    @SneakyThrows
    public void removeFolder(String folderPath)
    {
        if(!folderPath.endsWith("/"))
        {
            throw new IllegalArgumentException("folder path must end with /");
        }

        ArrayList<String> itemsToDelete = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(ROOT_BUCKET).prefix(folderPath)
                .recursive(true).build());

        for(Result<Item> result : results)
        {
            itemsToDelete.add(result.get().objectName());
            System.out.println(result.get().objectName());
        }

        for(String item : itemsToDelete)
        {
            System.out.println(item);
            removeFile(item);
            System.out.println("delete file named -> " + item);
        }

    }

    @SneakyThrows
    public void renameFolder(String folderPath, String newFolderName) {
        if (!folderPath.endsWith("/")) {
            throw new IllegalArgumentException("Folder path must end with '/'");
        }

        String parentFolderPath = StringOperation.trimFolderPath(folderPath);

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

}
