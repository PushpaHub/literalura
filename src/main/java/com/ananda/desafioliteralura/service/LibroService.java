package com.ananda.desafioliteralura.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ananda.desafioliteralura.models.Autor;
import com.ananda.desafioliteralura.models.DatosDeLibro;
import com.ananda.desafioliteralura.models.Idioma;
import com.ananda.desafioliteralura.models.Libro;
import com.ananda.desafioliteralura.repository.LibroRepository;
import com.ananda.desafioliteralura.repository.AutorRepository;
import com.ananda.desafioliteralura.repository.IdiomaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final IdiomaRepository idiomaRepository;

    @Autowired
    public LibroService(LibroRepository libroRepository,
                        AutorRepository autorRepository, IdiomaRepository idiomaRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.idiomaRepository = idiomaRepository;
    }

    @Transactional
    public Libro saveLibro(DatosDeLibro datosDeLibro) {
        Libro libro = new Libro();
        libro.setTitulo(datosDeLibro.titulo());
        libro.setDescargas(datosDeLibro.descargas());

        List<Autor> autores = datosDeLibro.autores().stream()
                .map(datosDeAutor -> {
                    Optional<Autor> existingAutor = autorRepository.findByNombre(datosDeAutor.nombre());
                    if (existingAutor.isPresent()) {
                        return existingAutor.get();
                    } else {
                        Autor nuevoAutor = new Autor();
                        nuevoAutor.setNombre(datosDeAutor.nombre());
                        nuevoAutor.setNacimiento(datosDeAutor.nacimiento());
                        nuevoAutor.setFallecimiento(datosDeAutor.fallecimiento());
                        return autorRepository.save(nuevoAutor);
                    }
                })
                .collect(Collectors.toList());
        libro.setAutores(autores);

        List<Idioma> idiomas = datosDeLibro.idiomas().stream()
                .map(idiomaStr -> {
                    Optional<Idioma> existingIdioma = idiomaRepository.findByNombre(idiomaStr);
                    if (existingIdioma.isPresent()) {
                        return existingIdioma.get();
                    } else {
                        Idioma nuevoIdioma = new Idioma();
                        nuevoIdioma.setNombre(idiomaStr);
                        return idiomaRepository.save(nuevoIdioma);
                    }
                })
                .collect(Collectors.toList());
        libro.setIdiomas(idiomas);

        return libroRepository.save(libro);
    }
}
