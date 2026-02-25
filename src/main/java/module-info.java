module com.cybermath {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;

    // --- ESTA ES LA LÍNEA QUE FALTABA PARA QUE JLINK EMPAQUE LA BD ---
    requires com.h2database;

    exports com.cybermath;
}