package WeAreFamily;

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
<<<<<<< HEAD
        Parent root = FXMLLoader.load(getClass().getResource("fxml/forgotPassWord.fxml"));//load fxml to scene
        primaryStage.setScene(new Scene(root, 1280, 800));
=======
        //Parent root = FXMLLoader.load(getClass().getResource("fxml/HomePage.fxml"));
        this.root = setRoot("fxml/HomePage.fxml");
        primaryStage.setScene(new Scene(getRoot(), 1280, 800));
>>>>>>> db3b2de85de5e6f299b4d16b3d52ee85650804ce
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
