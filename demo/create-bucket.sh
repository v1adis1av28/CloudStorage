#!/bin/bash

# Ожидаем, пока MinIO станет доступным
until curl -s http://minio:9000/minio/health/ready; do
    echo "Waiting for MinIO to start..."
    sleep 5
done

# Создаем псевдоним для MinIO
mc alias set myminio http://minio:9000 minioadmin minioadmin --api s3v4

# Создаем бакет user-files
if ! mc mb myminio/user-files; then
    echo "Bucket 'user-files' already exists."
else
    echo "Bucket 'user-files' created successfully."
fi