package com.ananda.desafioliteralura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosDeListaDeLibros(@JsonAlias("results") List<DatosDeLibro> datosDeLibros) {
}
