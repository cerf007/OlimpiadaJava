package com.olimpia;

import com.olimpia.controller.MapaController;
import com.olimpia.model.Mapa;
import com.olimpia.persistence.FileMapaRepository;
import com.olimpia.service.DijkstraService;
import com.olimpia.ui.MapaView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

public class App extends Application {

    private MapaController controller;

    @Override
    public void start(Stage stage) {
        FileMapaRepository repository = new FileMapaRepository(Path.of("data", "mapa.properties"));
        controller = new MapaController(new Mapa(), new DijkstraService(), repository);

        Scene scene = new Scene(new MapaView(controller, stage).build(), 1080, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        stage.setTitle("Trayectos entre sedes");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        controller.guardarAlCerrar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
