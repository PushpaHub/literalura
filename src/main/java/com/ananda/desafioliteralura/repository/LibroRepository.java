package com.ananda.desafioliteralura.repository;

import com.ananda.desafioliteralura.models.Autor;
import com.ananda.desafioliteralura.models.Idioma;
import com.ananda.desafioliteralura.models.Libro;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long>{

    @Query("SELECT a FROM Libro l JOIN l.autores a WHERE l.id = :libroId")
    List<Autor> encuentraAutoresDeLibro(Long libroId);

    @Query("SELECT i FROM Libro l JOIN l.idiomas i WHERE l.id = :libroId")
    List<Idioma> encuentraIdiomasDeLibro(Long libroId);

    @Query("SELECT l FROM Libro l JOIN l.autores a WHERE a.id = :autorId")
    List<Libro> encuentraLibrosDeAutor(Long autorId);

    @Query("SELECT l FROM Libro l JOIN l.idiomas i WHERE i.id = :idiomaId")
    List<Libro> encuentaLibrosPorIdioma(@Param("idiomaId") Long idiomaId);



}


