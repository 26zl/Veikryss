module com.g15.veikryss {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.g15.veikryss to javafx.fxml;
    exports com.g15.veikryss;
}