package com.ricardo.movilApp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface FileUploadService {
    String uploadFile(MultipartFile file) throws IOException;
    Map<String, String> delete(String id) throws IOException;
    File convert(MultipartFile multipartFile) throws IOException;
}
