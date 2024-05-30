package com.ananda.desafioliteralura.repository;

import com.ananda.desafioliteralura.models.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE a.nacimiento <= :anio AND a.fallecimiento >= :anio")
    List<Autor> encuentraAutoresPorAnio(Integer anio);

}
