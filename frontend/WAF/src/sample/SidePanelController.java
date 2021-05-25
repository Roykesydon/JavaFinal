package sample;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.response.CommentNoticeResponse;
import sample.response.notice.CheckNoticeResponse;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SidePanelController {

    public JFXButton spaceBut;
    public Circle noticeCircle;
    @FXML
    public  Label noticeCircleLabel;

    @FXML
    public void initialize() {
        spaceBut.setDisable(false);
        spaceBut.setMaxHeight(0.1);

        noticeCircle.setVisible(false);
        noticeCircleLabel.setVisible(false);

        noticeCircle.setFill(Color.CRIMSON);

        Timer timer = new Timer();
        TimerTask task = new Polling();
        timer.schedule(task, 1000, 1000);
    }


    private class Polling extends TimerTask
    {
        public void run()
        {
//            System.out.println(Thread.currentThread().getName() + ":" + LocalDateTime.now().getSecond());
            try {
                HttpResponse response = RequestController.post("http://127.0.0.1:13261/comments/getUnreadCommentCount",
                        new String[]{"accessKey", GlobalVariable.accessKey}
                );
                String responseString = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    CommentNoticeResponse jsonResponse = gson.fromJson(responseString, CommentNoticeResponse.class);
                    if(jsonResponse.errors.length==0){
                        if(jsonResponse.count==0){
                            Platform.runLater(() -> {
                                noticeCircle.setVisible(false);
                                noticeCircleLabel.setVisible(false);
                            });
                        }
                        else{
                            Platform.runLater(() -> {
                                noticeCircle.setVisible(true);
                                noticeCircleLabel.setVisible(true);
                                int tmp = jsonResponse.count;
                                if(tmp>99)
                                    tmp=99;
                                SidePanelController.this.noticeCircleLabel.setText(Integer.toString(tmp));
                            });
                        }

                    }
                } else {
                    System.out.println(response.getStatusLine());
                }
            }
            catch (IOException  e) {
                e.printStackTrace();
            }
        }
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

    public void EnterCommentPage(ActionEvent actionEvent) throws IOException {
        //use button to switch scene
        Parent page = FXMLLoader.load(getClass().getResource("fxml/CommentPage.fxml"));
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
