package com.ricardo.movilApp.repository;

import com.ricardo.movilApp.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>   {
    List<Producto> findByImagesId(Long imageId);
}
