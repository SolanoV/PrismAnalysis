module com.prismx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.prismx to javafx.fxml;
    exports com.prismx;
}