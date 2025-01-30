package com.storage.services.minio;


import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
                        .bucket(ROOT_BUCKET)//TODO что-то придумать с валидацией и составления пути к загружаемому файлу
                        .object(folderPath)// здесь задаем путь где он будет храниться после последнего слеша будет задаваться имя файла
                        .filename(localFilePath)//путь к файлу на локальном устройстве
                        .contentType(contentType)
                        .build()
        );
    }

}

//TODO 3) Переименовывание файлов( нет такой операции, переименование папки по сути представляет собой создание папки под новым именем и перенос туда файлов, см. CopyObject)
//TODO 4) Удаление файлов




//Проверка наличия бакета с определенным именем + создание
//         boolean checkBucketExist = client.bucketExists(BucketExistsArgs.builder().bucket("test").build());
//                if(checkBucketExist) {
//                    System.out.println("такой бакет уже существует переименую или хз выруби комп");
//                }
//                else
//                {
//client.makeBucket(MakeBucketArgs.builder().bucket("test").bucket("inner").build()); // создание бакета
//        }
//        MinioClient client = MinioClient.builder().endpoint("http://127.0.0.1:9000")
//                .credentials("minioadmin","minioadmin").build();
//        String bucketName = "user-1-bucket"; // Имя бакета
//        String userFolder = "user-1-files/"; // Папка пользователя
//        String userSubFolder = userFolder + "myfolder/"; // Подпапка пользователя
//        String filePath = "G:\\CloudStorage\\demo\\src\\main\\resources\\minioLaunch.txt"; // Путь к файлу на локальной машине
//        String objectName = userSubFolder + "myfile.txt"; // Имя объекта в MinIO
//
//        // Проверяем, существует ли бакет, и создаем его, если нет
//        boolean isExist = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//        if (!isExist) {
//            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//        }
//
//        // Загружаем файл в папку пользователя
//        client.uploadObject(
//                UploadObjectArgs.builder()
//                        .bucket(bucketName)
//                        .object(objectName)
//                        .filename(filePath)
//                        .build());
//
//        System.out.println("File uploaded successfully to: " + objectName);
//        return;
//    }

//полуение инфы о всех бакетах
//public List<Bucket> listBuckets() [Javadoc]
//Lists bucket information of all buckets.

//Список объектов в бакете
//Iterable<Result<Item>> results = client.listObjects(
//        ListObjectsArgs.builder().bucket("test").build());
//Item item = results.iterator().next().get();
//        System.out.println(item.objectName());


//Копирование объекта сперва указываем куда и как назоваем -> потом откуда и что
// client.copyObject(CopyObjectArgs.builder().bucket("user-files").object("zapuskTxtcop.txt")
//                .source(CopySource.builder().bucket("test").object("zapuskTxt.txt").build()).build());
//


//Загрузка файлов закинул файл в корневую папку прроекта наврное можно задать путь для установки
//client.downloadObject(DownloadObjectArgs.builder().bucket("user-files").object("zapuskTxt.txt").filename("testDownload").build());

//Удаление файла из бакета
//client.removeObject(RemoveObjectArgs.builder().bucket("test").object("zapuskTxt.txt").build());
