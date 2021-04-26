package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;


public class HomePageController extends Main {

    public void switchToSignup(ActionEvent actionEvent) throws IOException
    {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("fxml/SignUp.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void royTest(ActionEvent actionEvent) throws IOException
    {
        Parent page = FXMLLoader.load(getClass().getResource("fxml/RoyTest.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }
    public void switchToForgotPassWord(MouseEvent e) throws IOException {
        Parent page = FXMLLoader.load(this.getClass().getResource("fxml/forgotPassWord.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(tmp);
        stage.show();
    }

}