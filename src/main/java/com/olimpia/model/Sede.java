package com.olimpia.model;

import java.util.Objects;

public class Sede {
    private final String id;
    private final String nombre;

    public Sede(String id, String nombre) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("La sede requiere un id válido.");
        }
        this.id = id.trim();
        this.nombre = nombre == null || nombre.isBlank() ? this.id : nombre.trim();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sede sede = (Sede) o;
        return id.equals(sede.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre + " (" + id + ")";
    }
}
