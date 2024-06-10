package com.ricardo.movilApp.service.impl;

import com.ricardo.movilApp.dto.ImageDTO;
import com.ricardo.movilApp.entity.Image;
import com.ricardo.movilApp.repository.ImageRepository;
import com.ricardo.movilApp.service.FileUploadService;
import com.ricardo.movilApp.service.ImageService;
import com.ricardo.movilApp.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final FileUploadService fileUploadService;

    @Override
    public GenericResponse<ImageDTO> add(ImageDTO imageDTO, MultipartFile imagen) throws IOException {
        Image image = new Image();
        image.setId(imageDTO.getId());

        // Extrae el nombre del archivo del MultipartFile y lo establece en el campo name
        if (imagen != null && !imagen.isEmpty()) {
            String originalFilename = imagen.getOriginalFilename();
            image.setName(originalFilename);
            String urlImagen = fileUploadService.uploadFile(imagen);
            image.setUrl(urlImagen);
        } else {
            image.setName(imageDTO.getName());
            image.setUrl(imageDTO.getUrl());
        }

        imageRepository.save(image);

        // Actualiza imageDTO para reflejar el nombre correcto
        imageDTO.setName(image.getName());
        imageDTO.setUrl(image.getUrl());

        return new GenericResponse<>("EXITO", 1, "Imagen agregada exitosamente", imageDTO);
    }

    @Override
    public GenericResponse<ImageDTO> update(ImageDTO imageDTO, MultipartFile nuevaImagen) throws IOException {
        Optional<Image> imageOptional = imageRepository.findById(imageDTO.getId());
        if (imageOptional.isEmpty()) {
            throw new IllegalArgumentException("Error, producto no encontrado");
        }
        Image image = imageOptional.get();
        image.setId(imageDTO.getId());

        // Si hay una nueva imagen, actualizar el nombre y la URL
        if (nuevaImagen != null && !nuevaImagen.isEmpty()) {
            if (image.getUrl() != null) {
                String imageId = getImageIdFromUrl(image.getUrl());
                try {
                    fileUploadService.delete(imageId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String originalFilename = nuevaImagen.getOriginalFilename();
            image.setName(originalFilename);
            String urlNuevaImagen = fileUploadService.uploadFile(nuevaImagen);
            image.setUrl(urlNuevaImagen);
        } else {
            // Si no hay nueva imagen, mantener el nombre actual o establecer el nombre del DTO
            image.setName(imageDTO.getName());
            image.setUrl(imageDTO.getUrl());
        }

        imageRepository.save(image);

        // Actualiza imageDTO para reflejar el nombre correcto
        imageDTO.setName(image.getName());
        imageDTO.setUrl(image.getUrl());

//        return new GenericResponse<>("ERROR", 0, "Image not found", null);
        return new GenericResponse<>("EXITO", 1, "Imagen actualizada correctamente", imageDTO);
    }

    @Override
    public ImageDTO findById(Long id) {
        Optional<Image> optionalImage = imageRepository.findById(id);
        if (!optionalImage.isPresent()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        Image image = optionalImage.get();
        ImageDTO imageDTO = ImageDTO.builder()
                .id(image.getId())
                .name(image.getName())
                .url(image.getUrl())
                .build();

        return imageDTO;
    }

    @Override
    public List<ImageDTO> list() {
        return imageRepository.findAll().stream().map(image -> {
            ImageDTO imageDTO = ImageDTO.builder()
                    .id(image.getId())
                    .name(image.getName())
                    .url(image.getUrl())
                    .build();
            return imageDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if(imageOptional.isPresent()) {
            Image image = imageOptional.get();

            if (image.getUrl() != null) {
                String imageId = getImageIdFromUrl(image.getUrl());
                try {
                    fileUploadService.delete(imageId);
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
            // Después eliminar las asociaciones con Cliente y Producto si existen
            if (image.getCliente() != null) {
                image.setCliente(null);
            }
            if (image.getProducto() != null) {
                image.setProducto(null);
            }

            imageRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Error: No se puede eliminar la imagen");
        }
    }

    private String getImageIdFromUrl(String imageUrl) {
        int startIndex = imageUrl.lastIndexOf('/') + 1;
        int endIndex = imageUrl.lastIndexOf('.');
        return imageUrl.substring(startIndex, endIndex);
    }
}
