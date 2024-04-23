module AES {

    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens edu.crypto to javafx.fxml, javafx.graphics;

}