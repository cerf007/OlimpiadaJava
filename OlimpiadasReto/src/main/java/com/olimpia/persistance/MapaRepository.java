package com.olimpia.persistence;

import com.olimpia.domain.Mapa;

import java.io.IOException;

public interface MapaRepository {
    void guardar(Mapa mapa) throws IOException;
    Mapa cargar() throws IOException;
}