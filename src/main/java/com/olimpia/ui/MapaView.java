package com.olimpia.ui;

import com.olimpia.controller.MapaController;
import com.olimpia.model.Conexion;
import com.olimpia.model.EstadoRuta;
import com.olimpia.model.RutaResultado;
import com.olimpia.model.Sede;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;

public class MapaView {

    private final MapaController controller;
    private final Stage stage;

    private final TextField sedeIdField = new TextField();
    private final TextField sedeNombreField = new TextField();
    private final ComboBox<Sede> conexionOrigen = new ComboBox<>();
    private final ComboBox<Sede> conexionDestino = new ComboBox<>();
    private final TextField costoField = new TextField();
    private final ComboBox<Sede> rutaOrigen = new ComboBox<>();
    private final ComboBox<Sede> rutaDestino = new ComboBox<>();
    private final Label resultadoLabel = new Label("Selecciona origen y destino para calcular una ruta.");
    private final Label estadoLabel = new Label("Listo");
    private final TextArea logArea = new TextArea();

    public MapaView(MapaController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public BorderPane build() {
        configurarCampos();

        VBox izquierda = new VBox(14, panelSede(), panelConexion(), panelRuta(), panelArchivo());
        izquierda.setPadding(new Insets(0, 14, 0, 0));
        izquierda.setPrefWidth(400);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setLeft(izquierda);
        root.setCenter(panelDerecho());
        root.setBottom(estadoLabel);
        BorderPane.setMargin(estadoLabel, new Insets(10, 0, 0, 0));
        estadoLabel.getStyleClass().add("estado");
        return root;
    }

    private void configurarCampos() {
        sedeIdField.setPromptText("Ej: A1");
        sedeIdField.setTextFormatter(new TextFormatter<String>(filtro("[A-Za-z0-9_-]{0,10}")));
        sedeNombreField.setPromptText("Opcional");
        sedeNombreField.setTextFormatter(new TextFormatter<String>(filtro(".{0,40}")));

        costoField.setPromptText("Entero >= 0");
        costoField.setTextFormatter(new TextFormatter<String>(filtro("[0-9]{0,12}")));

        for (ComboBox<Sede> combo : java.util.List.of(conexionOrigen, conexionDestino, rutaOrigen, rutaDestino)) {
            combo.setItems(controller.getSedes());
            combo.setConverter(new StringConverter<>() {
                @Override
                public String toString(Sede s) {
                    return s == null ? "" : s.toString();
                }

                @Override
                public Sede fromString(String text) {
                    return null;
                }
            });
            combo.setPromptText("Seleccionar sede");
            combo.setMaxWidth(Double.MAX_VALUE);
        }

        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setFocusTraversable(false);
        resultadoLabel.setWrapText(true);
        resultadoLabel.getStyleClass().add("resultado");
    }

    private VBox panelSede() {
        Button agregar = new Button("Añadir sede");
        agregar.disableProperty().bind(Bindings.createBooleanBinding(
                () -> sedeIdField.getText().isBlank(), sedeIdField.textProperty()));
        agregar.setDefaultButton(false);
        agregar.setOnAction(e -> {
            try {
                String id = sedeIdField.getText().trim();
                controller.agregarSede(id, sedeNombreField.getText());
                log("Sede añadida: " + id);
                sedeIdField.clear();
                sedeNombreField.clear();
                sedeIdField.requestFocus();
            } catch (Exception ex) {
                error(ex);
            }
        });
        return seccion("Nueva sede", fila("ID", sedeIdField), fila("Nombre", sedeNombreField), agregar);
    }

    private VBox panelConexion() {
        BooleanBinding incompleta = conexionOrigen.valueProperty().isNull()
                .or(conexionDestino.valueProperty().isNull())
                .or(costoField.textProperty().isEmpty());
        BooleanBinding mismaSede = Bindings.createBooleanBinding(
                () -> conexionOrigen.getValue() != null && conexionOrigen.getValue().equals(conexionDestino.getValue()),
                conexionOrigen.valueProperty(), conexionDestino.valueProperty());

        Button agregar = new Button("Añadir conexión");
        agregar.disableProperty().bind(incompleta.or(mismaSede));
        agregar.setTooltip(new Tooltip("Requiere origen, destino distintos y un costo."));
        agregar.setOnAction(e -> {
            try {
                long costo = Long.parseLong(costoField.getText());
                controller.agregarConexion(conexionOrigen.getValue(), conexionDestino.getValue(), costo);
                log("Conexión añadida: " + conexionOrigen.getValue().getId() + " -> "
                        + conexionDestino.getValue().getId() + " (" + costo + ")");
                costoField.clear();
            } catch (Exception ex) {
                error(ex);
            }
        });
        return seccion("Nueva conexión", fila("Origen", conexionOrigen), fila("Destino", conexionDestino),
                fila("Costo", costoField), agregar);
    }

    private VBox panelRuta() {
        BooleanBinding incompleta = rutaOrigen.valueProperty().isNull().or(rutaDestino.valueProperty().isNull());

        Button calcular = new Button("Calcular ruta");
        calcular.disableProperty().bind(incompleta);
        calcular.setOnAction(e -> calcular());

        Button intercambiar = new Button("Intercambiar");
        intercambiar.getStyleClass().add("secundario");
        intercambiar.disableProperty().bind(incompleta);
        intercambiar.setOnAction(e -> {
            Sede tmp = rutaOrigen.getValue();
            rutaOrigen.setValue(rutaDestino.getValue());
            rutaDestino.setValue(tmp);
        });

        return seccion("Ruta de menor costo", fila("Origen", rutaOrigen), fila("Destino", rutaDestino),
                new HBox(8, calcular, intercambiar), resultadoLabel);
    }

    private VBox panelArchivo() {
        Button guardar = new Button("Guardar");
        guardar.setOnAction(e -> ejecutarIO("Mapa guardado.", controller::guardar));

        Button recargar = new Button("Recargar");
        recargar.setOnAction(e -> {
            if (confirmar("Se descartarán los cambios no guardados. ¿Continuar?")) {
                ejecutarIO("Mapa recargado desde el archivo local.", controller::recargar);
            }
        });

        Button importar = new Button("Importar...");
        importar.getStyleClass().add("secundario");
        importar.setOnAction(e -> {
            File f = selector("Importar mapa").showOpenDialog(stage);
            if (f != null && confirmar("El mapa actual será reemplazado. ¿Continuar?")) {
                ejecutarIO("Mapa importado desde " + f.getName(), () -> controller.importar(f.toPath()));
            }
        });

        Button exportar = new Button("Exportar...");
        exportar.getStyleClass().add("secundario");
        exportar.setOnAction(e -> {
            File f = selector("Exportar mapa").showSaveDialog(stage);
            if (f != null) {
                ejecutarIO("Mapa exportado a " + f.getName(), () -> controller.exportar(f.toPath()));
            }
        });

        return seccion("Archivo", new HBox(8, guardar, recargar), new HBox(8, importar, exportar));
    }

    private VBox panelDerecho() {
        TableView<Sede> tablaSedes = new TableView<>(controller.getSedes());
        tablaSedes.getColumns().add(columna("ID", 90, Sede::getId));
        tablaSedes.getColumns().add(columna("Nombre", 200, Sede::getNombre));
        tablaSedes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaSedes.setPlaceholder(new Label("Aún no hay sedes."));

        TableView<Conexion> tablaConexiones = new TableView<>(controller.getConexiones());
        tablaConexiones.getColumns().add(columna("Origen", 90, Conexion::getOrigen));
        tablaConexiones.getColumns().add(columna("Destino", 90, Conexion::getDestino));
        tablaConexiones.getColumns().add(columna("Costo", 90, c -> String.valueOf(c.getCosto())));
        tablaConexiones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaConexiones.setPlaceholder(new Label("Aún no hay conexiones."));

        VBox sedesBox = new VBox(6, new Label("Sedes"), tablaSedes);
        VBox conexionesBox = new VBox(6, new Label("Conexiones"), tablaConexiones);
        VBox logBox = new VBox(6, new Label("Registro"), logArea);
        VBox.setVgrow(tablaSedes, Priority.ALWAYS);
        VBox.setVgrow(tablaConexiones, Priority.ALWAYS);
        VBox.setVgrow(logArea, Priority.ALWAYS);
        VBox.setVgrow(sedesBox, Priority.ALWAYS);
        VBox.setVgrow(conexionesBox, Priority.ALWAYS);
        VBox.setVgrow(logBox, Priority.ALWAYS);

        VBox derecha = new VBox(12, sedesBox, conexionesBox, logBox);
        HBox.setHgrow(derecha, Priority.ALWAYS);
        return derecha;
    }

    private void calcular() {
        try {
            RutaResultado r = controller.calcularRuta(rutaOrigen.getValue(), rutaDestino.getValue());
            resultadoLabel.getStyleClass().removeAll("ok", "sin-ruta");
            if (r.getEstado() == EstadoRuta.OK) {
                String texto = "Ruta: " + String.join(" -> ", r.getRuta()) + "\nCosto total: " + r.getCostoTotal();
                resultadoLabel.setText(texto);
                resultadoLabel.getStyleClass().add("ok");
                log(texto.replace("\n", " | "));
            } else {
                resultadoLabel.setText("SIN_RUTA: no hay camino entre las sedes seleccionadas.");
                resultadoLabel.getStyleClass().add("sin-ruta");
                log("SIN_RUTA entre " + rutaOrigen.getValue().getId() + " y " + rutaDestino.getValue().getId());
            }
        } catch (Exception ex) {
            error(ex);
        }
    }

    private interface AccionIO {
        void ejecutar() throws IOException;
    }

    private void ejecutarIO(String exito, AccionIO accion) {
        try {
            accion.ejecutar();
            log(exito);
        } catch (Exception ex) {
            error(ex);
        }
    }

    private boolean confirmar(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText(null);
        alert.initOwner(stage);
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    private FileChooser selector(String titulo) {
        FileChooser fc = new FileChooser();
        fc.setTitle(titulo);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mapa (*.properties)", "*.properties"));
        return fc;
    }

    private void log(String mensaje) {
        logArea.appendText(mensaje + "\n");
        estadoLabel.setText(mensaje);
    }

    private void error(Exception ex) {
        String msg = ex.getMessage() == null ? "Error inesperado." : ex.getMessage();
        logArea.appendText("ERROR: " + msg + "\n");
        estadoLabel.setText("Error: " + msg);
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.initOwner(stage);
        alert.show();
    }

    private static UnaryOperator<TextFormatter.Change> filtro(String regex) {
        return change -> change.getControlNewText().matches(regex) ? change : null;
    }

    private static <T> TableColumn<T, String> columna(String titulo, double ancho, java.util.function.Function<T, String> f) {
        TableColumn<T, String> col = new TableColumn<>(titulo);
        col.setPrefWidth(ancho);
        col.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(f.apply(c.getValue())));
        return col;
    }

    private static HBox fila(String etiqueta, javafx.scene.Node campo) {
        Label l = new Label(etiqueta);
        l.setMinWidth(60);
        HBox box = new HBox(8, l, campo);
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(campo, Priority.ALWAYS);
        return box;
    }

    private static VBox seccion(String titulo, javafx.scene.Node... hijos) {
        Label t = new Label(titulo);
        t.getStyleClass().add("titulo-seccion");
        VBox box = new VBox(8, t);
        box.getChildren().addAll(hijos);
        box.getStyleClass().add("seccion");
        box.setPadding(new Insets(12));
        return box;
    }
}
