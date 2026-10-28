package com.olimpia;

import com.olimpia.domain.Mapa;
import com.olimpia.persistence.FileMapaRepository;
import com.olimpia.ui.MapaController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ni.edu.uam.olimpiadas.olimpiadasreto.service.DijkstraService;

import java.io.IOException;
import java.nio.file.Path;

public class App extends Application {

    private final Mapa mapa = new Mapa();
    private final FileMapaRepository repository = new FileMapaRepository(Path.of("data", "mapa.properties"));

    @Override
    public void start(Stage stage) {
        recuperarEstadoPrevio();

        MapaController controller = new MapaController(mapa, new DijkstraService(), repository);
        Scene scene = new Scene(controller.createView(), 980, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        stage.setTitle("Institución - Trayectos entre sedes");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        try {
            repository.guardar(mapa);
        } catch (IOException e) {
            System.err.println("No se pudo guardar el mapa al cerrar: " + e.getMessage());
        }
    }

    private void recuperarEstadoPrevio() {
        try {
            Mapa previo = repository.cargar();
            previo.getSedes().forEach(mapa::addSede);
            previo.getTodasLasConexiones().forEach(c -> mapa.addConexion(c.getOrigen(), c.getDestino(), c.getCosto()));
        } catch (Exception e) {
            System.err.println("No se pudo recuperar el mapa previo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
