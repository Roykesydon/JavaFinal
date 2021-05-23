package sample;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.response.notice.getNewestTenNoticeResponse;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RenderNotificationPageController implements Initializable {
    public Label notification;
    public   VBox  notificationVbox;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HamburgerBackArrowBasicTransition burgerTask2 = new HamburgerBackArrowBasicTransition(hamburger);
        try {
        VBox box = FXMLLoader.load(getClass().getResource("fxml/SidePanel.fxml"));
        if(GlobalVariable.isAdmin != false) {
            box = FXMLLoader.load(getClass().getResource("fxml/AdminSidePanel.fxml"));
            System.out.println("admin");
        }

        drawer.setSidePane(box);
        if(GlobalVariable.userEnterFirstTime) {
            drawer.close();
            GlobalVariable.userEnterFirstTime = false;
            burgerTask2.setRate(-1);
        }
        else {
            burgerTask2.setRate(1);
            burgerTask2.play();
            drawer.open();
        }
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            burgerTask2.setRate(burgerTask2.getRate() * -1);
            burgerTask2.play();

            if (drawer.isOpened())
                drawer.close();
            else
                drawer.open();
        });
    }catch (IOException ex){
        Logger.getLogger(PublicPageController.class.getName()).log(Level.SEVERE,null,ex);
    }

        try {
            HttpResponse response = RequestController.post("http://localhost:13261/notifications/getNewestTenNotice",
                new String[]{"accessKey", GlobalVariable.accessKey}
            );
            String responseString = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Gson gson = new Gson();
                getNewestTenNoticeResponse gsonResponse = gson.fromJson(responseString, getNewestTenNoticeResponse.class);
                if (Arrays.toString(gsonResponse.errors).equals("[]")) {
                    for (String text: gsonResponse.Notices) {
                        System.out.println(text);
                        notification=new Label(text);
                        notification.setVisible(true);
                        notification.setStyle("-fx-background-color: blue");
                        notificationVbox.getChildren().add(notification);
                    }
                }
                else {
                    System.out.println(Arrays.toString(gsonResponse.errors));
                }
            }
            else {
                System.out.println(response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
