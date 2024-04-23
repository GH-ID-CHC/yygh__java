package com.chai.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @program: yygh
 * @author:
 * @create: 2024-04-23 21:38
 */
public interface FileService {
    String upload(MultipartFile file);
}
