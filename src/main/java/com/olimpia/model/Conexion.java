package com.olimpia.model;

import java.util.Objects;

public class Conexion {
    private final String origen;
    private final String destino;
    private final long costo;

    public Conexion(String origen, String destino, long costo) {
        if (origen == null || origen.isBlank()) {
            throw new IllegalArgumentException("Origen inválido.");
        }
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("Destino inválido.");
        }
        if (costo < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo.");
        }

        this.origen = origen.trim();
        this.destino = destino.trim();
        this.costo = costo;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public long getCosto() {
        return costo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conexion conexion = (Conexion) o;
        return costo == conexion.costo &&
                origen.equals(conexion.origen) &&
                destino.equals(conexion.destino);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origen, destino, costo);
    }

    @Override
    public String toString() {
        return origen + " -> " + destino + " (" + costo + ")";
    }
}
