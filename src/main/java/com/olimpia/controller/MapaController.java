package com.olimpia.controller;

import com.olimpia.model.Conexion;
import com.olimpia.model.Mapa;
import com.olimpia.model.RutaResultado;
import com.olimpia.model.Sede;
import com.olimpia.persistence.FileMapaRepository;
import com.olimpia.persistence.MapaImporterExporter;
import com.olimpia.service.DijkstraService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Coordina el modelo, el servicio de rutas y la persistencia. No conoce widgets:
 * la vista le pide acciones y observa las listas que expone.
 */
public class MapaController {

    private final Mapa mapa;
    private final DijkstraService dijkstraService;
    private final FileMapaRepository repository;

    private final ObservableList<Sede> sedes = FXCollections.observableArrayList();
    private final ObservableList<Conexion> conexiones = FXCollections.observableArrayList();

    public MapaController(Mapa mapa, DijkstraService dijkstraService, FileMapaRepository repository) {
        this.mapa = mapa;
        this.dijkstraService = dijkstraService;
        this.repository = repository;
        recuperarEstadoPrevio();
    }

    public ObservableList<Sede> getSedes() {
        return sedes;
    }

    public ObservableList<Conexion> getConexiones() {
        return conexiones;
    }

    public void agregarSede(String id, String nombre) {
        mapa.addSede(new Sede(id, nombre));
        refrescar();
    }

    public void agregarConexion(Sede origen, Sede destino, long costo) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Selecciona origen y destino.");
        }
        mapa.addConexion(origen.getId(), destino.getId(), costo);
        refrescar();
    }

    public RutaResultado calcularRuta(Sede origen, Sede destino) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Selecciona origen y destino.");
        }
        return dijkstraService.calcularRuta(mapa, origen.getId(), destino.getId());
    }

    public void guardar() throws IOException {
        repository.guardar(mapa);
    }

    public void recargar() throws IOException {
        reemplazarMapa(repository.cargar());
    }

    public void exportar(Path destino) throws IOException {
        MapaImporterExporter.exportar(mapa, destino);
    }

    public void importar(Path origen) throws IOException {
        reemplazarMapa(MapaImporterExporter.importar(origen));
    }

    public void guardarAlCerrar() {
        try {
            repository.guardar(mapa);
        } catch (IOException e) {
            System.err.println("No se pudo guardar el mapa al cerrar: " + e.getMessage());
        }
    }

    private void reemplazarMapa(Mapa nuevo) {
        mapa.limpiar();
        nuevo.getSedes().forEach(mapa::addSede);
        nuevo.getTodasLasConexiones().forEach(c -> mapa.addConexion(c.getOrigen(), c.getDestino(), c.getCosto()));
        refrescar();
    }

    private void recuperarEstadoPrevio() {
        try {
            reemplazarMapa(repository.cargar());
        } catch (Exception e) {
            System.err.println("No se pudo recuperar el mapa previo: " + e.getMessage());
        }
        if (mapa.size() == 0) {
            cargarDatosEjemplo();
        }
    }

    private void cargarDatosEjemplo() {
        mapa.addSede(new Sede("A", "Sede A"));
        mapa.addSede(new Sede("B", "Sede B"));
        mapa.addSede(new Sede("C", "Sede C"));
        mapa.addSede(new Sede("D", "Sede D"));
        mapa.addConexion("A", "B", 4);
        mapa.addConexion("A", "C", 1);
        mapa.addConexion("C", "B", 2);
        mapa.addConexion("B", "D", 3);
        mapa.addConexion("C", "D", 2);
        refrescar();
    }

    private void refrescar() {
        sedes.setAll(mapa.getSedes());
        conexiones.setAll(mapa.getTodasLasConexiones());
    }
}
