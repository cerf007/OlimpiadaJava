package ni.edu.uam.olimpiadas.olimpiadasreto.service;


import com.olimpia.domain.EstadoRuta;
import com.olimpia.domain.Mapa;
import com.olimpia.domain.MapaException;
import com.olimpia.domain.RutaResultado;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class DijkstraService {

    public RutaResultado calcularRuta(Mapa mapa, String origen, String destino) {
        if (mapa == null) {
            throw new MapaException("El mapa no puede ser nulo.");
        }
        if (!mapa.existeSede(origen)) {
            throw new MapaException("La sede origen no existe: " + origen);
        }
        if (!mapa.existeSede(destino)) {
            throw new MapaException("La sede destino no existe: " + destino);
        }

        if (origen.equals(destino)) {
            return new RutaResultado(EstadoRuta.OK, 0L, List.of(origen));
        }

        Map<String, Long> distancias = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<Nodo> cola = new PriorityQueue<>(Comparator.comparingLong(n -> n.distancia));

        distancias.put(origen, 0L);
        cola.add(new Nodo(origen, 0L));

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();

            if (actual.distancia > distancias.getOrDefault(actual.id, Long.MAX_VALUE)) {
                continue;
            }

            for (Conexion conexion : mapa.getConexionesDesde(actual.id)) {
                long nuevaDistancia = actual.distancia + conexion.getCosto();

                if (nuevaDistancia < distancias.getOrDefault(conexion.getDestino(), Long.MAX_VALUE)) {
                    distancias.put(conexion.getDestino(), nuevaDistancia);
                    prev.put(conexion.getDestino(), actual.id);
                    cola.add(new Nodo(conexion.getDestino(), nuevaDistancia));
                }
            }
        }

        if (!distancias.containsKey(destino)) {
            return new RutaResultado(EstadoRuta.SIN_RUTA, -1L, List.of());
        }

        List<String> ruta = reconstruirRuta(prev, destino);
        long costoTotal = distancias.get(destino);

        return new RutaResultado(EstadoRuta.OK, costoTotal, ruta);
    }

    private List<String> reconstruirRuta(Map<String, String> prev, String destino) {
        List<String> ruta = new ArrayList<>();
        String actual = destino;

        while (actual != null) {
            ruta.add(actual);
            actual = prev.get(actual);
        }

        List<String> invertida = new ArrayList<>();
        for (int i = ruta.size() - 1; i >= 0; i--) {
            invertida.add(ruta.get(i));
        }
        return invertida;
    }

    private static class Nodo {
        private final String id;
        private final long distancia;

        private Nodo(String id, long distancia) {
            this.id = id;
            this.distancia = distancia;
        }
    }
}
