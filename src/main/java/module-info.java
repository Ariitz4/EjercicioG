module es.aritzherrero.ejerciciog {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens es.aritzherrero.ejerciciog to javafx.fxml;
    exports es.aritzherrero.ejerciciog;
    opens es.aritzherrero.ejerciciog.controlador to javafx.fxml;
    opens es.aritzherrero.ejerciciog.modelo to javafx.fxml, javafx.base;
    opens es.aritzherrero.ejerciciog.dao to javafx.fxml, javafx.base;
    opens es.aritzherrero.ejerciciog.Conexion to javafx.fxml, javafx.base;
}