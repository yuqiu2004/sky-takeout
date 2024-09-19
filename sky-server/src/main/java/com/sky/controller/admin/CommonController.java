package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.MinioUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 通用接口
 */
@Slf4j
@Api("通用接口")
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Resource
    private MinioUtil minioUtil;

    /**
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result upload(MultipartFile file){
        try {
            String objectName = minioUtil.upload(file);
            String path = minioUtil.preview(objectName);
            return Result.success(path);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("上传文件失败!");
        }
    }


}
