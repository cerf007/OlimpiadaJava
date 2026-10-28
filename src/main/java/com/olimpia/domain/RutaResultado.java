package com.olimpia.domain;

import java.util.Collections;
import java.util.List;

public class RutaResultado {
    private final EstadoRuta estado;
    private final long costoTotal;
    private final List<String> ruta;

    public RutaResultado(EstadoRuta estado, long costoTotal, List<String> ruta) {
        this.estado = estado;
        this.costoTotal = costoTotal;
        this.ruta = ruta == null ? Collections.emptyList() : List.copyOf(ruta);
    }

    public EstadoRuta getEstado() {
        return estado;
    }

    public long getCostoTotal() {
        return costoTotal;
    }

    public List<String> getRuta() {
        return ruta;
    }

    public boolean hayRuta() {
        return estado == EstadoRuta.OK;
    }
}
