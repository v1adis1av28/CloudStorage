package com.storage;


import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileUploadTest {
    public static void main(String[] args) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient client = MinioClient.builder().endpoint("http://127.0.0.1:9000")
                .credentials("minioadmin","minioadmin").build(); // создали клиента mini0


        return;
    }
}


//Проверка наличия бакета с определенным именем + создание
// boolean checkBucketExist = client.bucketExists(BucketExistsArgs.builder().bucket("test").build());
//        if(checkBucketExist) {
//            System.out.println("такой бакет уже существует переименую или хз выруби комп");
//        }
//        else
//        {
//client.makeBucket(MakeBucketArgs.builder().bucket("test").build()); // создание бакета
//}
//Выгрузка файла
//        client.uploadObject(UploadObjectArgs.builder().bucket("user-files").object("zapuskTxt.txt").filename("G:\\CloudStorage\\demo\\src\\main\\resources\\minioLaunch.txt").build());
//        System.out.println("файл был загружен");



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
