package ni.edu.uam.olimpiadas.olimpiadasreto.service;

import com.olimpia.domain.EstadoRuta;
import com.olimpia.domain.Mapa;
import com.olimpia.domain.MapaException;
import com.olimpia.domain.RutaResultado;
import com.olimpia.domain.Sede;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DijkstraServiceTest {

    @Test
    void casoNormalDebeDevolverRutaMinima() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));
        mapa.addSede(new Sede("B", "B"));
        mapa.addSede(new Sede("C", "C"));

        mapa.addConexion("A", "B", 4);
        mapa.addConexion("A", "C", 1);
        mapa.addConexion("C", "B", 2);

        DijkstraService service = new DijkstraService();
        RutaResultado resultado = service.calcularRuta(mapa, "A", "B");

        assertEquals(EstadoRuta.OK, resultado.getEstado());
        assertEquals(3L, resultado.getCostoTotal());
        assertEquals(java.util.List.of("A", "C", "B"), resultado.getRuta());
    }

    @Test
    void sedeAisladaDebeDarSinRuta() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));
        mapa.addSede(new Sede("B", "B"));

        DijkstraService service = new DijkstraService();
        RutaResultado resultado = service.calcularRuta(mapa, "A", "B");

        assertEquals(EstadoRuta.SIN_RUTA, resultado.getEstado());
        assertTrue(resultado.getRuta().isEmpty());
    }

    @Test
    void pesoNegativoDebeRechazarse() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));
        mapa.addSede(new Sede("B", "B"));

        MapaException ex = assertThrows(MapaException.class, () -> mapa.addConexion("A", "B", -1));
        assertTrue(ex.getMessage().contains("negativos"));
    }

    @Test
    void origenIgualDestinoDebeDarCostoCero() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));
        mapa.addSede(new Sede("B", "B"));
        mapa.addConexion("A", "B", 10);

        DijkstraService service = new DijkstraService();
        RutaResultado resultado = service.calcularRuta(mapa, "A", "A");

        assertEquals(EstadoRuta.OK, resultado.getEstado());
        assertEquals(0L, resultado.getCostoTotal());
        assertEquals(java.util.List.of("A"), resultado.getRuta());
    }

    @Test
    void destinoInexistenteDebeLanzarExcepcion() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));

        DijkstraService service = new DijkstraService();
        MapaException ex = assertThrows(MapaException.class, () -> service.calcularRuta(mapa, "A", "Z"));
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void rutaConMasNodosDebeElegirLaMasBarata() {
        Mapa mapa = new Mapa();
        mapa.addSede(new Sede("A", "A"));
        mapa.addSede(new Sede("B", "B"));
        mapa.addSede(new Sede("C", "C"));
        mapa.addSede(new Sede("D", "D"));

        mapa.addConexion("A", "B", 10);
        mapa.addConexion("A", "C", 1);
        mapa.addConexion("C", "B", 1);
        mapa.addConexion("C", "D", 2);
        mapa.addConexion("D", "B", 1);

        DijkstraService service = new DijkstraService();
        RutaResultado resultado = service.calcularRuta(mapa, "A", "B");

        assertEquals(EstadoRuta.OK, resultado.getEstado());
        assertEquals(2L, resultado.getCostoTotal());
    }
}