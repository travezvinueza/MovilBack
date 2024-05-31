package com.ricardo.movilApp.repository;

import com.ricardo.movilApp.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByNumDoc(String numDoc);
    @Query(value = "(SELECT EXISTS(SELECT id FROM clientes WHERE num_doc=:dni))", nativeQuery = true)
    int existByDoc(String dni);
    @Query(value = "SELECT EXISTS(SELECT C.* FROM clientes C WHERE C.num_doc=:dni AND NOT (C.id=:id))", nativeQuery = true)
    int existByDocForUpdate(@Param("dni") String dni, @Param("id") Long id);
}
