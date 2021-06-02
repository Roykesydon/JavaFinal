package sample.controller.sidePanel;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import sample.controller.PollingController;
import sample.global.GlobalVariable;

import java.io.IOException;

public class SidePanelController {

    public JFXButton spaceBut;
    public Circle noticeCircle;
    @FXML
    public  Label noticeCircleLabel;
    public VBox primaryVBox;

    @FXML
    public void initialize() {
        primaryVBox.setStyle("-fx-background-color: "+GlobalVariable.primaryColor);
        spaceBut.setDisable(false);
        spaceBut.setMaxHeight(0.1);

        noticeCircle.setVisible(false);
        noticeCircleLabel.setVisible(false);

        noticeCircle.setFill(Color.CRIMSON);
        PollingController.noticeCircle = noticeCircle;
        PollingController.noticeCircleLabel = noticeCircleLabel;
    }

    public void EnterProfile(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/profile/ProfilePage.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void EnterPublicPost(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/posts/PublicPostPage.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void EnterManagePost(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/posts/ManagePost.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void EnterMakeNewPost(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/posts/MakeNewPost.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void EnterNotification(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/notification/Notification.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

    public void EnterCommentPage(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/comment/CommentPage.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }

}
