package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.global.GlobalVariable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SidePanelController {

    public JFXButton spaceBut;

    @FXML
    public void initialize() {
        spaceBut.setDisable(false);
        spaceBut.setMaxHeight(0.1);
    }

    public void EnterProfile(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("fxml/ProfilePage.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void EnterPublicPost(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("fxml/PublicPostPage.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void EnterManagePost(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("fxml/ManagePost.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void EnterMakeNewPost(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("fxml/MakeNewPost.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void EnterNotification(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("fxml/renderNotification.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void EnterAdmin(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("fxml/Admin.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

}
