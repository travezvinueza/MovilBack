package com.ricardo.movilApp.service.impl;

import com.ricardo.movilApp.dto.ImageDTO;
import com.ricardo.movilApp.dto.ProductoDTO;
import com.ricardo.movilApp.entity.Image;
import com.ricardo.movilApp.entity.Producto;
import com.ricardo.movilApp.repository.ProductoRepository;
import com.ricardo.movilApp.service.FileUploadService;
import com.ricardo.movilApp.service.ProductoService;
import com.ricardo.movilApp.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final FileUploadService fileUploadService;

    @Override
    public GenericResponse<ProductoDTO> addProduct(ProductoDTO productoDTO, List<MultipartFile> imagenesProducto) throws IOException {
        Producto producto = new Producto();
        producto.setNombre(productoDTO.getNombre());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setEstado(productoDTO.isEstado());

        if (imagenesProducto != null && !imagenesProducto.isEmpty()) {
            if (producto.getImages() == null) {
                producto.setImages(new ArrayList<>());
            }

            for (MultipartFile imagenProducto : imagenesProducto) {
                if (!imagenProducto.isEmpty()) {
                    String urlImagen = fileUploadService.uploadFile(imagenProducto);
                    Image image = new Image();
                    image.setName(imagenProducto.getOriginalFilename());
                    image.setUrl(urlImagen);
                    image.setProducto(producto);

                    producto.getImages().add(image);
                }
            }
        }

        producto = productoRepository.save(producto);
        return new GenericResponse<>("EXITO", 1, "Producto creado exitosamente", toDTO(producto));
    }

    @Override
    public ProductoDTO findByIdProduct(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        return toDTO(producto);
    }

    @Override
    public List<ProductoDTO> getAllProduct() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> findByIdImage(Long  idImage) {
        List<Producto> productos = productoRepository.findByImagesId(idImage);
        return productos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public GenericResponse<ProductoDTO> updateProduct(Long id, ProductoDTO productoDTO, List<MultipartFile> nuevasImagenes) throws IOException {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        producto.setNombre(productoDTO.getNombre());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setEstado(productoDTO.isEstado());

        if (nuevasImagenes != null && !nuevasImagenes.isEmpty()) {
            // Eliminar la imagen anterior si existe
            if (producto.getImages() != null && !producto.getImages().isEmpty()) {
                for (Image image : producto.getImages()) {
                    if (image.getUrl() != null) {
                        String imageId = getImageIdFromUrl(image.getUrl());
                        fileUploadService.delete(imageId);
                    }
                }
                producto.getImages().clear();
            }

            // Subir las nuevas imágenes
            List<Image> images = new ArrayList<>();
            for (MultipartFile nuevaImagen : nuevasImagenes) {
                String urlNuevaImagen = fileUploadService.uploadFile(nuevaImagen);
                Image image = new Image();
                image.setName(nuevaImagen.getOriginalFilename());
                image.setUrl(urlNuevaImagen);
                image.setProducto(producto);
                images.add(image);
            }
            producto.setImages(images);
        }

        producto = productoRepository.save(producto);
        return new GenericResponse<>("EXITO", 1, "Producto actualizado correctamente", toDTO(producto));
    }

    @Override
    public String deleteProduct(Long id) throws IOException {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // Eliminar todas las imágenes asociadas al producto
        if (producto.getImages() != null && !producto.getImages().isEmpty()) {
            for (Image image : producto.getImages()) {
                if (image.getUrl() != null) {
                    String imageId = getImageIdFromUrl(image.getUrl());
                    fileUploadService.delete(imageId);
                }
            }
        }

        productoRepository.deleteById(id);
        return "Producto eliminado exitosamente";
    }

    private ProductoDTO toDTO(Producto producto) {
        List<ImageDTO> imageDTOs = producto.getImages().stream()
                .map(image -> ImageDTO.builder()
                        .id(image.getId())
                        .name(image.getName())
                        .url(image.getUrl())
                        .build())
                .collect(Collectors.toList());

        return ProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .descripcion(producto.getDescripcion())
                .estado(producto.isEstado())
                .images(imageDTOs)
                .build();
    }

    private String getImageIdFromUrl(String imageUrl) {
        int startIndex = imageUrl.lastIndexOf('/') + 1;
        int endIndex = imageUrl.lastIndexOf('.');
        return imageUrl.substring(startIndex, endIndex);
    }
}
