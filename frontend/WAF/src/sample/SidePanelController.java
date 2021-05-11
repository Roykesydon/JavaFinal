package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SidePanelController {

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
