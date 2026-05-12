package com.empresa.catalogo.repository;

import com.empresa.catalogo.entity.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // list
    List<Producto> findByActivoTrue();
}
