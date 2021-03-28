package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("We Are Family");
        //Parent root = FXMLLoader.load(getClass().getResource("fxml/HomePage.fxml"));
        this.root = setRoot("fxml/HomePage.fxml");
        primaryStage.setScene(new Scene(getRoot(), 1280, 800));
        primaryStage.show();
    }

    public Parent setRoot(String root)
    {
        try{
            Parent page = FXMLLoader.load(getClass().getResource(root));
            return page;
        }catch (java.io.IOException e){
            System.out.println("root is wrong");
            return null;
        }
    }

    public Parent getRoot()
    {
        return this.root;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
