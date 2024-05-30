package com.ananda.desafioliteralura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Conversor implements IConversor{

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T convierte(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json, clase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
