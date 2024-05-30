package com.ananda.desafioliteralura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosDeAutor(@JsonAlias("name") String nombre,
                           @JsonAlias("birth_year") Integer nacimiento,
                           @JsonAlias("death_year") Integer fallecimiento) {
}
