package com.logistics.controller;


import com.logistics.entity.Result;
import com.logistics.utils.AliOssUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
public class    FileUploadController {
    @PostMapping(value = "/upload")
    public Result<String> fileUpload(MultipartFile multipartFile , HttpServletRequest request) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        //最终文件名
        String fileName= UUID.randomUUID().toString().replaceAll("-","")+originalFilename.substring(originalFilename.lastIndexOf("."));
        //调用工具类
        String upload = AliOssUtil.upload(fileName, multipartFile.getInputStream());

        return Result.success(upload);
    }
}
