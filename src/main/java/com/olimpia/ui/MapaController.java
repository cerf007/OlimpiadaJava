package com.olimpia.ui;

import com.olimpia.domain.Conexion;
import com.olimpia.domain.EstadoRuta;
import com.olimpia.domain.Mapa;
import com.olimpia.domain.RutaResultado;
import com.olimpia.domain.Sede;
import com.olimpia.persistence.FileMapaRepository;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ni.edu.uam.olimpiadas.olimpiadasreto.service.DijkstraService;

import java.io.IOException;

public class MapaController {

    private final Mapa mapa;
    private final DijkstraService dijkstraService;
    private final FileMapaRepository repository;

    private final TextField sedeIdField = new TextField();
    private final TextField sedeNombreField = new TextField();
    private final TextField conexionOrigenField = new TextField();
    private final TextField conexionDestinoField = new TextField();
    private final TextField conexionCostoField = new TextField();
    private final TextField origenRutaField = new TextField();
    private final TextField destinoRutaField = new TextField();
    private final TextArea logArea = new TextArea();
    private final Label estadoLabel = new Label("Sistema listo");

    public MapaController(Mapa mapa, DijkstraService dijkstraService, FileMapaRepository repository) {
        this.mapa = mapa;
        this.dijkstraService = dijkstraService;
        this.repository = repository;
        cargarDatosEjemplo();
    }

    public BorderPane createView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        GridPane panel = new GridPane();
        panel.setHgap(12);
        panel.setVgap(10);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: #f3f6fb; -fx-border-color: #dfe8f5; -fx-border-radius: 8;");

        panel.add(new Label("ID sede:"), 0, 0);
        panel.add(sedeIdField, 1, 0);
        panel.add(new Label("Nombre:"), 2, 0);
        panel.add(sedeNombreField, 3, 0);

        Button addSedeBtn = new Button("Añadir sede");
        addSedeBtn.setOnAction(e -> addSede());
        panel.add(addSedeBtn, 0, 1, 2, 1);

        panel.add(new Label("Origen:"), 0, 2);
        panel.add(conexionOrigenField, 1, 2);
        panel.add(new Label("Destino:"), 2, 2);
        panel.add(conexionDestinoField, 3, 2);

        panel.add(new Label("Costo:"), 0, 3);
        panel.add(conexionCostoField, 1, 3);

        Button addConexionBtn = new Button("Añadir conexión");
        addConexionBtn.setOnAction(e -> addConexion());
        panel.add(addConexionBtn, 0, 4, 2, 1);

        panel.add(new Label("Origen ruta:"), 0, 5);
        panel.add(origenRutaField, 1, 5);
        panel.add(new Label("Destino ruta:"), 2, 5);
        panel.add(destinoRutaField, 3, 5);

        Button calcularBtn = new Button("Calcular ruta");
        calcularBtn.setOnAction(e -> calcularRuta());
        panel.add(calcularBtn, 0, 6, 2, 1);

        Button guardarBtn = new Button("Guardar mapa");
        guardarBtn.setOnAction(e -> guardarMapa());
        panel.add(guardarBtn, 2, 6, 1, 1);

        Button cargarBtn = new Button("Cargar mapa");
        cargarBtn.setOnAction(e -> cargarMapa());
        panel.add(cargarBtn, 3, 6, 1, 1);

        panel.add(estadoLabel, 0, 7, 4, 1);

        logArea.setEditable(false);
        logArea.setWrapText(true);

        VBox logBox = new VBox();
        logBox.setPadding(new Insets(12, 0, 0, 12));
        logBox.setSpacing(8);
        logBox.getChildren().add(new Label("Log del sistema"));
        logBox.getChildren().add(logArea);
        VBox.setVgrow(logArea, javafx.scene.layout.Priority.ALWAYS);

        root.setLeft(panel);
        root.setCenter(logBox);

        return root;
    }

    private void addSede() {
        try {
            String id = sedeIdField.getText().trim();
            String nombre = sedeNombreField.getText().trim();

            mapa.addSede(new Sede(id, nombre));
            logArea.appendText("Sede añadida: " + id + "\n");
            estadoLabel.setText("Sede creada correctamente.");
            sedeIdField.clear();
            sedeNombreField.clear();
        } catch (Exception ex) {
            reportarError(ex);
        }
    }

    private void addConexion() {
        try {
            String origen = conexionOrigenField.getText().trim();
            String destino = conexionDestinoField.getText().trim();
            long costo = Long.parseLong(conexionCostoField.getText().trim());

            mapa.addConexion(origen, destino, costo);
            logArea.appendText("Conexión añadida: " + origen + " -> " + destino + " (" + costo + ")\n");
            estadoLabel.setText("Conexión creada.");
            conexionOrigenField.clear();
            conexionDestinoField.clear();
            conexionCostoField.clear();
        } catch (NumberFormatException ex) {
            reportarError(new IllegalArgumentException("El costo debe ser un entero."));
        } catch (Exception ex) {
            reportarError(ex);
        }
    }

    private void calcularRuta() {
        try {
            String origen = origenRutaField.getText().trim();
            String destino = destinoRutaField.getText().trim();

            RutaResultado resultado = dijkstraService.calcularRuta(mapa, origen, destino);

            if (resultado.getEstado() == EstadoRuta.OK) {
                logArea.appendText("Ruta: " + String.join(" -> ", resultado.getRuta()) + "\n");
                logArea.appendText("Costo total: " + resultado.getCostoTotal() + "\n");
                estadoLabel.setText("Ruta encontrada: " + resultado.getCostoTotal());
            } else {
                logArea.appendText("SIN_RUTA\n");
                estadoLabel.setText("SIN_RUTA");
            }
        } catch (Exception ex) {
            reportarError(ex);
        }
    }

    private void guardarMapa() {
        try {
            repository.guardar(mapa);
            logArea.appendText("Mapa guardado en data/mapa.properties\n");
            estadoLabel.setText("Mapa guardado.");
        } catch (IOException e) {
            logArea.appendText("ERROR al guardar: " + e.getMessage() + "\n");
            estadoLabel.setText("No se pudo guardar.");
        }
    }

    private void cargarMapa() {
        try {
            Mapa cargado = repository.cargar();
            mapa.limpiar();
            for (Sede sede : cargado.getSedes()) {
                mapa.addSede(sede);
            }
            for (Conexion conexion : cargado.getTodasLasConexiones()) {
                mapa.addConexion(conexion.getOrigen(), conexion.getDestino(), conexion.getCosto());
            }
            logArea.appendText("Mapa cargado correctamente desde archivo.\n");
            estadoLabel.setText("Mapa recargado.");
        } catch (Exception e) {
            logArea.appendText("ERROR al cargar: " + e.getMessage() + "\n");
            estadoLabel.setText("No se pudo cargar.");
        }
    }

    private void reportarError(Exception ex) {
        estadoLabel.setText(ex.getMessage());
        logArea.appendText("ERROR: " + ex.getMessage() + "\n");
    }

    private void cargarDatosEjemplo() {
        if (!mapa.getSedes().isEmpty()) {
            return;
        }
        mapa.addSede(new Sede("A", "Sede A"));
        mapa.addSede(new Sede("B", "Sede B"));
        mapa.addSede(new Sede("C", "Sede C"));
        mapa.addSede(new Sede("D", "Sede D"));

        mapa.addConexion("A", "B", 4);
        mapa.addConexion("A", "C", 1);
        mapa.addConexion("C", "B", 2);
        mapa.addConexion("B", "D", 3);
        mapa.addConexion("C", "D", 2);
    }
}
