package sample;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.postController.CommentController;
import sample.postController.NotificationController;
import sample.response.posts.GetCommentResponse;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentPageController implements Initializable {

    public TextArea commentTextBox;
    public Label sendStatusLabel;
    private GetCommentResponse classJsonResponse;
    public TextField toSendUserIDTextBox;
    public JFXDrawer drawer;
    public JFXHamburger hamburger;
    public VBox commentVBox;
    private String message = "";
    private String toSendUserID = "";
    private List<String> messageData = new ArrayList<>();
    private AnchorPane[] messageArr;

    public void initialize(URL url, ResourceBundle rb) {
        try {
            VBox box = FXMLLoader.load(getClass().getResource("fxml/SidePanel.fxml"));
            drawer.setSidePane(box);
            drawer.open();
        } catch (IOException ex) {
            Logger.getLogger(ManagePostController.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* catch relative post from method */
        commentVBox.setPadding(new Insets(20, 20, 20, 5));
        commentVBox.setSpacing(5);

        try {
            getComments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getComments() throws IOException {
        boolean success = true;

        //表單格式皆合法
        if (success) {
            try {
                HttpResponse response = RequestController.post("http://127.0.0.1:13261/comments/getComments",
                        new String[]{"accessKey", GlobalVariable.accessKey}
                );
                String responseString = EntityUtils.toString(response.getEntity());
                String errorsResult = "";
                int errorsResultCount = 0;

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    GetCommentResponse jsonResponse = gson.fromJson(responseString, GetCommentResponse.class);
                    //create class variable to use renderAllPost method in update scene
                    classJsonResponse = jsonResponse;

                    errorsResult = "";
                    errorsResultCount = 0;
                    for (String error : jsonResponse.errors) {
                        if (errorsResultCount != 0)
                            errorsResult += " , ";

                        errorsResult += error;
                        errorsResultCount++;
                        System.out.print(',' + error);
                    }

                    System.out.println();

                    if (jsonResponse.errors.length == 0) {
                        for (String message : jsonResponse.Notices)
                            messageData.add(message);
                        renderAllMessage(messageData, classJsonResponse.Notices.length);
                    }

                } else {
                    System.out.println(response.getStatusLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void renderAllMessage(List<String> messageData, int messagesQuantity) throws IOException {
        messageArr = new AnchorPane[messagesQuantity];
        //"test1=I am test1=1621822282" (use = to split)
        //sender , message , timestamp
        for (String tmp : messageData) {
            String[] dataArr = tmp.split("=");
//            Long timeStamp = Long.valueOf(dataArr[2]);
//            /*
//            Timestamp ts = new Timestamp(1621824984);
//            System.out.println(ts.toLocalDateTime());
//            */
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/sample/fxml/posts/Comment.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();

            CommentController itemController = fxmlLoader.getController();
            itemController.setData(dataArr[0],dataArr[1]);

//            String comment = "Sender is : " + dataArr[0] + "\n" + "Message is : " + dataArr[1] + "\n" + "Send time is : " + timeStamp + "\n";
//            Label commentLabel = new Label(comment);
//            commentLabel.setStyle("-fx-font-size: 25;-fx-border-color: #444444;-fx-border-style: solid none none none;");
            commentVBox.getChildren().add(anchorPane);
        }
    }

    public void leaveComment(ActionEvent actionEvent) {
        message = commentTextBox.getText();
        toSendUserID = toSendUserIDTextBox.getText();
        boolean success = true;
        //表單格式皆合法
        if (success) {
            try {
                HttpResponse response = RequestController.post("http://127.0.0.1:13261/comments/createComment",
                        new String[]{"accessKey", GlobalVariable.accessKey},
                        new String[]{"userID", toSendUserID},
                        new String[]{"message", message}
                );
                String responseString = EntityUtils.toString(response.getEntity());
                String errorsResult = "";
                int errorsResultCount = 0;

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    GetCommentResponse jsonResponse = gson.fromJson(responseString, GetCommentResponse.class);
                    //create class variable to use renderAllPost method in update scene
                    classJsonResponse = jsonResponse;

                    errorsResult = "";
                    errorsResultCount = 0;
                    for (String error : jsonResponse.errors) {
                        if (errorsResultCount != 0)
                            errorsResult += " , ";

                        errorsResult += error;
                        errorsResultCount++;
                        System.out.print(',' + error);
                    }

                    System.out.println();
                    ToastCaller toast;
                    if (jsonResponse.errors.length == 0) {
                        toast = new ToastCaller("Send message success!",GlobalVariable.mainStage,ToastCaller.SUCCESS);
                    }
                    else
                        toast = new ToastCaller("Send message failed!",GlobalVariable.mainStage,ToastCaller.ERROR);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
