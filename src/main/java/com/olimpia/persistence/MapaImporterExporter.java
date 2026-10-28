package com.olimpia.persistence;

import com.olimpia.domain.Mapa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MapaImporterExporter {

    public static void exportar(Mapa mapa, Path destino) throws IOException {
        FileMapaRepository repository = new FileMapaRepository(destino);
        repository.guardar(mapa);
    }

    public static Mapa importar(Path origen) throws IOException {
        FileMapaRepository repository = new FileMapaRepository(origen);
        return repository.cargar();
    }

    public static void crearDirectorioSiNoExiste(Path ruta) throws IOException {
        if (ruta.getParent() != null) {
            Files.createDirectories(ruta.getParent());
        }
    }
}