package com.anan.petadmin.controller.admin;

import com.anan.petadmin.result.Result;
import com.anan.petadmin.utils.AliOssUtil;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Resource
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    public Result<String> uplaod(MultipartFile file){
        log.info("文件上传");
        try {
            String originalFilename = file.getOriginalFilename();
            String lastName = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + lastName;
            String path = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(path);
        } catch (IOException e) {
            log.error("文件上传失败{}",e);
        }
        return Result.error("文件上传失败");
    }
}
