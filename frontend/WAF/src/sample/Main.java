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
        //create a new scene (use button to switch scene reference in HomPageController.java's switchToSignup method)
        primaryStage.setTitle("We Are Family");
        Parent root = FXMLLoader.load(getClass().getResource("fxml/forgotPassWord.fxml"));//load fxml to scene
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
