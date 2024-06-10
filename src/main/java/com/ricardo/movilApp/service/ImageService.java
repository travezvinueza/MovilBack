package com.ricardo.movilApp.service;

import com.ricardo.movilApp.dto.ImageDTO;
import com.ricardo.movilApp.util.GenericResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    GenericResponse<ImageDTO> add(ImageDTO imageDTO, MultipartFile imagen) throws IOException;
    GenericResponse<ImageDTO> update(ImageDTO imageDTO, MultipartFile nuevaImagen) throws IOException;
    ImageDTO findById(Long id);
    List<ImageDTO> list();
    void delete(Long id);
}
