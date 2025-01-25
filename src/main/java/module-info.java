module springbootapp.teachersnote {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.net.http;
    requires com.google.gson;
    requires java.desktop;

    // Öffnet `springbootapp.gui` für JavaFX FXML
    opens springbootapp.gui to javafx.fxml;

    // Öffnet `springbootapp.model` für JavaFX Base
    opens springbootapp.model to javafx.base;

    // Exportiert das GUI-Paket
    exports springbootapp.gui;
}
