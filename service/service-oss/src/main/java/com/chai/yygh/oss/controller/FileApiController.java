package com.chai.yygh.oss.controller;

import com.chai.yygh.common.result.Result;
import com.chai.yygh.oss.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: yygh
 * @author:
 * @create: 2024-04-23 21:37
 */
@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {
    @Autowired
    private FileService fileService;

    /**
     * 上传文件到阿里云oss
     *
     * @param file 文件
     * @return {@link Result}
     */
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) {
        //获取上传文件
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
