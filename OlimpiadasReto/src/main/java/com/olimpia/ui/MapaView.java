package com.olimpia.ui;

import javafx.scene.layout.BorderPane;

public class MapaView {
    public BorderPane build(MapaController controller) {
        return controller.createView();
    }
}
