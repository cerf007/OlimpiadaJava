package com.olimpia.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MapaTest {

    @Test
    void debeCrearSede() {
        Sede sede = new Sede("A", "Sede A");
        assertEquals("A", sede.getId());
        assertEquals("Sede A", sede.getNombre());
    }

    @Test
    void debeRechazarSedeSinId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Sede("", "X"));
        assertTrue(ex.getMessage().contains("id"));
    }

    @Test
    void debeAnadirConexionValida() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "Sede A"));
        mapa.addSede(new Sede("B", "Sede B"));
        mapa.addConexion("A", "B", 5);

        assertEquals(1, mapa.getConexionesDesde("A").size());
    }

    @Test
    void debeRechazarConexionConPesoNegativo() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));
        mapa.addSede(new Sede("B", "B"));

        MapaException ex = assertThrows(MapaException.class, () -> mapa.addConexion("A", "B", -1));
        assertTrue(ex.getMessage().contains("negativos"));
    }

    @Test
    void debeRechazarConexionConSedeInexistente() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));

        MapaException ex = assertThrows(MapaException.class, () -> mapa.addConexion("A", "Z", 3));
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void pesoNegativoNoDebeAnadirLaConexion() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));
        mapa.addSede(new Sede("B", "B"));

        assertThrows(MapaException.class, () -> mapa.addConexion("A", "B", -1));
        assertTrue(mapa.getTodasLasConexiones().isEmpty());
    }

    @Test
    void debeRechazarSedeConIdRepetido() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));

        MapaException ex = assertThrows(MapaException.class, () -> mapa.addSede(new Sede("A", "Otra")));
        assertTrue(ex.getMessage().contains("ya existe"));
        assertEquals(1, mapa.size());
    }
}
