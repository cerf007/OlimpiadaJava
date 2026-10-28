package com.olimpia.persistence;

import com.olimpia.model.Mapa;
import com.olimpia.model.Sede;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MapaRepositoryTest {

    @Test
    void debeGuardarYRecargarMapa() throws IOException {
        Path file = Files.createTempFile("mapa", ".properties");

        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));
        mapa.addSede(new Sede("B", "B"));
        mapa.addSede(new Sede("C", "C"));
        mapa.addConexion("A", "C", 1);
        mapa.addConexion("C", "B", 2);

        FileMapaRepository repo = new FileMapaRepository(file);
        repo.guardar(mapa);

        Mapa cargado = repo.cargar();

        assertEquals(3, cargado.getSedes().size());
        assertEquals(2, cargado.getTodasLasConexiones().size());
    }

    @Test
    void debeImportarMapaDesdeArchivo() throws IOException {
        Path file = Files.createTempFile("mapa-import", ".properties");

        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));
        mapa.addSede(new Sede("B", "B"));
        mapa.addConexion("A", "B", 10);

        new FileMapaRepository(file).guardar(mapa);

        Mapa importado = MapaImporterExporter.importar(file);

        assertTrue(importado.existeSede("A"));
        assertTrue(importado.existeSede("B"));
        assertEquals(1, importado.getTodasLasConexiones().size());
    }
}