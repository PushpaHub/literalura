package com.ananda.desafioliteralura.service;

public interface IConversor {
    <T> T convierte (String json, Class<T> clase);
}
