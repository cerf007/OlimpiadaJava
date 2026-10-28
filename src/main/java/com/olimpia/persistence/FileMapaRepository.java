package com.olimpia.persistence;

import com.olimpia.domain.Conexion;
import com.olimpia.domain.Mapa;
import com.olimpia.domain.Sede;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public class FileMapaRepository implements MapaRepository {

    private final Path file;

    public FileMapaRepository(Path file) {
        this.file = file;
    }

    @Override
    public void guardar(Mapa mapa) throws IOException {
        if (mapa == null) {
            throw new IllegalArgumentException("El mapa no puede ser nulo.");
        }

        Path directory = file.getParent();
        if (directory != null) {
            Files.createDirectories(directory);
        }

        Properties properties = new Properties();
        List<Sede> sedes = mapa.getSedes();

        properties.setProperty("sede.count", String.valueOf(sedes.size()));

        for (int i = 0; i < sedes.size(); i++) {
            Sede sede = sedes.get(i);
            properties.setProperty("sede." + i + ".id", sede.getId());
            properties.setProperty("sede." + i + ".nombre", sede.getNombre());
        }

        List<Conexion> conexiones = mapa.getTodasLasConexiones();
        properties.setProperty("conexion.count", String.valueOf(conexiones.size()));

        for (int i = 0; i < conexiones.size(); i++) {
            Conexion conexion = conexiones.get(i);
            properties.setProperty("conexion." + i + ".origen", conexion.getOrigen());
            properties.setProperty("conexion." + i + ".destino", conexion.getDestino());
            properties.setProperty("conexion." + i + ".costo", String.valueOf(conexion.getCosto()));
        }

        try (var writer = Files.newBufferedWriter(file)) {
            properties.store(writer, "Mapa de sedes");
        }
    }

    @Override
    public Mapa cargar() throws IOException {
        if (!Files.exists(file)) {
            return new Mapa();
        }

        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(file)) {
            properties.load(reader);
        }

        Mapa mapa = new Mapa();
        int sedesCount = Integer.parseInt(properties.getProperty("sede.count", "0"));

        for (int i = 0; i < sedesCount; i++) {
            String id = properties.getProperty("sede." + i + ".id");
            String nombre = properties.getProperty("sede." + i + ".nombre");
            if (id != null && !id.isBlank()) {
                mapa.addSede(new Sede(id, nombre));
            }
        }

        int conexionesCount = Integer.parseInt(properties.getProperty("conexion.count", "0"));
        for (int i = 0; i < conexionesCount; i++) {
            String origen = properties.getProperty("conexion." + i + ".origen");
            String destino = properties.getProperty("conexion." + i + ".destino");
            String costo = properties.getProperty("conexion." + i + ".costo");

            if (origen != null && destino != null && costo != null) {
                mapa.addConexion(origen, destino, Long.parseLong(costo));
            }
        }

        return mapa;
    }
}