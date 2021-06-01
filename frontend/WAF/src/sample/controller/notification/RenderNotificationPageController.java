package sample.controller.notification;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.controller.posts.PublicPageController;
import sample.global.GlobalVariable;
import sample.view.detailCardController.NotificationController;
import sample.tool.response.notice.getNewestTenNoticeResponse;
import sample.tool.RequestController;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RenderNotificationPageController implements Initializable {
    public Label notification;
    public   VBox  notificationVbox;
    public Label primaryNotificationLabel;
    public VBox box;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        primaryNotificationLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:56;");
        try {
            box.getChildren().add(FXMLLoader.load(getClass().getResource("/sample/view/fxml/sidePanel/SidePanel.fxml")));

        notificationVbox.setPadding(new Insets(20, 20, 20, 20));
        notificationVbox.setSpacing(20);
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
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/sample/view/fxml/detailCard/Notification.fxml"));
                        AnchorPane anchorPane = fxmlLoader.load();

                        NotificationController itemController = fxmlLoader.getController();
                        itemController.setData(text);
                        notificationVbox.getChildren().add(anchorPane);
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
