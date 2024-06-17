package com.ricardo.movilApp.controller;

import com.ricardo.movilApp.dto.ImageDTO;
import com.ricardo.movilApp.service.ImageService;
import com.ricardo.movilApp.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {
    private final ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse<ImageDTO>> addImage(@ModelAttribute ImageDTO imageDTO,
                                                              @RequestParam("imagen") MultipartFile imagenProducto) {
        try {
            GenericResponse<ImageDTO> response = imageService.add(imageDTO, imagenProducto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse<>("ERROR", 0, "Error al agregar la imagen", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse<ImageDTO>> updateImage(@ModelAttribute ImageDTO imageDTO,
                                                                 @RequestParam(value = "nuevaImagen", required = false) MultipartFile nuevaImagen) {
        try {
            GenericResponse<ImageDTO> response = imageService.update(imageDTO, nuevaImagen);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse<>("ERROR", 0, "Error al actualizar la imagen", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<ImageDTO> getImageById(@PathVariable Long id) {
        ImageDTO imageDTO = imageService.findById(id);
        if (imageDTO != null) {
            return new ResponseEntity<>(imageDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<ImageDTO>> listImages() {
        List<ImageDTO> images = imageService.list();
        return new ResponseEntity<>(images, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        try {
            imageService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

