package com.ananda.desafioliteralura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosDeLibro(@JsonAlias("title") String titulo,
                           @JsonAlias("authors") List<DatosDeAutor> autores,
                           @JsonAlias("languages") List<String> idiomas,
                           @JsonAlias("download_count") Integer descargas) {

}
