package com.ricardo.movilApp.service;

import com.ricardo.movilApp.dto.ProductoDTO;
import com.ricardo.movilApp.util.GenericResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductoService {
    GenericResponse<ProductoDTO> addProduct(ProductoDTO productoDTO, List<MultipartFile> imagenProducto) throws IOException;
    GenericResponse<ProductoDTO> updateProduct(Long id, ProductoDTO productoDTO, List<MultipartFile> nuevasImagenes) throws IOException;
    ProductoDTO findByIdProduct(Long id);
    List<ProductoDTO> getAllProduct();
    List<ProductoDTO> findByIdImage(Long  idImage);
    String deleteProduct(Long id) throws IOException;
}
