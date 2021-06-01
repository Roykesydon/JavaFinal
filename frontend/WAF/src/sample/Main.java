package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.controller.PollingController;
import sample.global.GlobalVariable;

public class Main extends Application {
    private Parent root;
    public static int connectErrorCount;
    @Override
    public void start(Stage primaryStage) throws Exception{
        GlobalVariable.mainStage = primaryStage;
        primaryStage.setTitle("We Are Family");
//        Parent root = FXMLLoader.load(getClass().getResource("fxml/HomePage.fxml"));
        this.root = setRoot("view/fxml/HomePage.fxml");
        primaryStage.setScene(new Scene(getRoot(), 1280, 800));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new javafx.scene.image.Image("/sample/resource/WAF.png"));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        GlobalVariable.polling = new PollingController();
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
