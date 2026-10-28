module ni.edu.uam.olimpiadas.olimpiadasreto {
    requires javafx.controls;
    requires javafx.fxml;

    opens ni.edu.uam.olimpiadas.olimpiadasreto to javafx.fxml;
    exports ni.edu.uam.olimpiadas.olimpiadasreto;
    exports ni.edu.uam.olimpiadas.olimpiadasreto.service;
    exports com.olimpia;
    exports com.olimpia.domain;
    exports com.olimpia.persistence;
    exports com.olimpia.ui;
}
