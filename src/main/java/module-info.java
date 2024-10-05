module springbootapp.teachersnote {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.net.http;
    requires com.google.gson;

    opens springbootapp.gui to javafx.fxml;
    exports springbootapp.gui;
}