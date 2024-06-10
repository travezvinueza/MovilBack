package com.ricardo.movilApp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ricardo.movilApp.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    private Cloudinary cloudinary;

    @Autowired
    public void CloudinaryService() {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cloud_name", "duzogl1l3");
        valuesMap.put("api_key", "183368993578433");
        valuesMap.put("api_secret", "eW-hXi2NfoIpMQF38VT9fD__Ihg");
        cloudinary = new Cloudinary(valuesMap);
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        File fileUpload = convert(file);
        Map<String, String> result = cloudinary.uploader().upload(fileUpload, ObjectUtils.emptyMap());
        if (!Files.deleteIfExists(fileUpload.toPath())) {
            throw new IOException("No se pudo eliminar el archivo temporal: " + fileUpload.getAbsolutePath());
        }
        return result.get("url");
    }

    @Override
    public Map<String, String> delete(String id) throws IOException {
        return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
    }

    @Override
    public File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(multipartFile.getBytes());
        fo.close();
        return file;
    }
}

