package com.sky.utils;

import com.sky.properties.MinioProperties;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Component
public class MinioUtil {

    @Resource
    private MinioProperties properties;

    @Resource
    private MinioClient minioClient;

    /**
     * 获取桶名
     */
    public String getBucketName(){
        return properties.getBucketName();
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @return Boolean
     */
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException("上传文件文件名为空");
        }
        String preName = originalFilename.substring(0,originalFilename.lastIndexOf("."));
        String pastName = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName =  preName + UUID.randomUUID() + pastName;
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(properties.getBucketName()).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return objectName;
    }

    /**
     * 获取预览的路径
     */
    public String preview(String objectName) {
        // 查看文件地址
        GetPresignedObjectUrlArgs build = new GetPresignedObjectUrlArgs().builder().bucket(properties.getBucketName()).object(objectName).method(Method.GET).build();
        try {
            return minioClient.getPresignedObjectUrl(build);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     *
     */
    public InputStream download(String objectName) {
        try {
            GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(properties.getBucketName()).object(objectName).build();
            return minioClient.getObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据对象名删除对象
     * @param objectName
     * @return
     */
    public boolean remove(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(properties.getBucketName()).object(objectName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据预览路径删除对象
     * @param url
     * @return
     */
    public boolean removeByPreUrl(String url){
        int start = url.indexOf(properties.getBucketName());
        int end = url.lastIndexOf('?');
        String objectName = url.substring(start, end);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(properties.getBucketName()).object(objectName).build());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 复制对象
     * @param src 复制源对象名
     * @return
     */
    public String copy(String src){
        // 处理对象名
        String target;
        String pastName = src.substring(src.lastIndexOf('.'));
        String preName = src.substring(0, src.lastIndexOf('.')-36);// 减去36个uuid的位置
        target = preName + UUID.randomUUID() + pastName;
        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .source(CopySource.builder().bucket(properties.getBucketName()).object(src).build())
                    .bucket(properties.getBucketName())
                    .object(target)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("复制对象失败...");
        }
        return target;
    }


    /**
     * 查看存储bucket是否存在
     * @return boolean
     */
    public Boolean bucketExist() {
        Boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return found;
    }

    /**
     * 创建存储bucket
     *
     * @return Boolean
     */
    public Boolean makeBucket() {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(properties.getBucketName())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除存储bucket
     *
     * @return Boolean
     */
    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取全部bucket
     */
    public List<Bucket> getAllHotBuckets() {
        try {
            List<Bucket> buckets = minioClient.listBuckets();
            return buckets;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
