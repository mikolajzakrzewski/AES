package edu.crypto;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class MyFXApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainScene.fxml"));
        Parent root = loader.load();


        Controller controller = loader.getController();
        Scene scene = new Scene(root);


        stage.setScene(scene);


        stage.setTitle("AES");


        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
