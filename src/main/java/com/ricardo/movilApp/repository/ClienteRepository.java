package com.ricardo.movilApp.repository;

import com.ricardo.movilApp.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByNumDoc(String numDoc);
}
