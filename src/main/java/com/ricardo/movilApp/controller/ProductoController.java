package com.ricardo.movilApp.controller;

import com.ricardo.movilApp.dto.ProductoDTO;
import com.ricardo.movilApp.service.ProductoService;
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
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse<ProductoDTO>> addProduct(@ModelAttribute ProductoDTO productoDTO,
                                                                   @RequestParam("imagenesProducto") List<MultipartFile> imagenesProducto) {
        try {
            GenericResponse<ProductoDTO> savedProducto = productoService.addProduct(productoDTO, imagenesProducto);
            return new ResponseEntity<>(savedProducto, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse<>("ERROR", 0, "Error al crear el producto", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GenericResponse<ProductoDTO>> updateProduct(@PathVariable Long id,
                                                                      @ModelAttribute ProductoDTO productoDTO,
                                                                      @RequestParam(value = "nuevasImagenes", required = false) List<MultipartFile> nuevasImagenes) {
        try {
            GenericResponse<ProductoDTO> response = productoService.updateProduct(id, productoDTO, nuevasImagenes);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("ERROR", 0, "Error al procesar la solicitud", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductById(@PathVariable Long id) {
        ProductoDTO productoDTO = productoService.findByIdProduct(id);
        return ResponseEntity.ok(productoDTO);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductoDTO>> getAllProducts() {
        List<ProductoDTO> productos = productoService.getAllProduct();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/get-image/{idImage}")
    public ResponseEntity<List<ProductoDTO>> getProductsByImageId(@PathVariable Long idImage) {
        List<ProductoDTO> productos = productoService.findByIdImage(idImage);
        return ResponseEntity.ok(productos);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            String message = productoService.deleteProduct(id);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el producto");
        }
    }
}
