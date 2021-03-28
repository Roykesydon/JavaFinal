package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Page {
    Stage primaryStage;
    Parent root;


    public Page(String input) throws Exception {
        root = FXMLLoader.load(getClass().getResource(input));
        primaryStage.setTitle("We Are Family");
        primaryStage.setScene(new Scene(root, 1280, 800));
    }
}