package com.olimpia.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Mapa {
    private final Map<String, Sede> sedes = new LinkedHashMap<>();
    private final Map<String, List<Conexion>> conexiones = new LinkedHashMap<>();

    public void addSede(Sede sede) {
        if (sede == null) {
            throw new IllegalArgumentException("La sede no puede ser nula.");
        }
        if (sedes.containsKey(sede.getId())) {
            throw new MapaException("La sede ya existe: " + sede.getId());
        }
        sedes.put(sede.getId(), sede);
        conexiones.putIfAbsent(sede.getId(), new ArrayList<>());
    }

    public void addConexion(String origen, String destino, long costo) {
        if (!existeSede(origen)) {
            throw new MapaException("La sede origen no existe: " + origen);
        }
        if (!existeSede(destino)) {
            throw new MapaException("La sede destino no existe: " + destino);
        }
        if (costo < 0) {
            throw new MapaException("No se admiten pesos negativos: " + costo);
        }

        Conexion nueva = new Conexion(origen, destino, costo);
        conexiones.computeIfAbsent(origen, k -> new ArrayList<>()).add(nueva);
    }

    public boolean existeSede(String id) {
        return sedes.containsKey(id);
    }

    public Sede getSede(String id) {
        return sedes.get(id);
    }

    public List<Sede> getSedes() {
        return new ArrayList<>(sedes.values());
    }

    public List<Conexion> getConexionesDesde(String sedeId) {
        return conexiones.getOrDefault(sedeId, Collections.emptyList());
    }

    public List<Conexion> getTodasLasConexiones() {
        List<Conexion> todas = new ArrayList<>();
        for (List<Conexion> lista : conexiones.values()) {
            todas.addAll(lista);
        }
        return todas;
    }

    public int size() {
        return sedes.size();
    }

    public void limpiar() {
        sedes.clear();
        conexiones.clear();
    }
}
