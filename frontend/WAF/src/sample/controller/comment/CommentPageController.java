package sample.controller.comment;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.controller.posts.ManagePostController;
import sample.global.GlobalVariable;
import sample.view.detailCardController.CommentController;
import sample.tool.response.detailCard.GetCommentResponse;
import sample.tool.RequestController;
import sample.tool.ToastCaller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentPageController implements Initializable {

    public TextArea commentTextBox;
    public Label sendStatusLabel;
    public Button leaveCommentButton;
    public Label primarySendTo;
    public Label primaryCommentLabel;
    public VBox box;
    private GetCommentResponse classJsonResponse;
    public TextField toSendUserIDTextBox;
    public JFXDrawer drawer;
    public JFXHamburger hamburger;
    public VBox commentVBox;
    private String message = "";
    private String toSendUserID = "";
    private List<String> messageData = new ArrayList<>();
    private AnchorPane[] messageArr;
    public  AnchorPane anchorpane;
    public ProgressIndicator loading;

    public void initialize(URL url, ResourceBundle rb) {
        loading.setVisible(false);
        leaveCommentButton.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-border-color: "+GlobalVariable.primaryColor+";-fx-font-size:19;");
        primarySendTo.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:27;");
        primaryCommentLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:49;");
        try {
            box.getChildren().add(FXMLLoader.load(getClass().getResource("/sample/view/fxml/sidePanel/SidePanel.fxml")));
        } catch (IOException ex) {
            Logger.getLogger(ManagePostController.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* catch relative post from method */
        commentVBox.setPadding(new Insets(20, 20, 20, 5));
        commentVBox.setSpacing(5);

        new Thread(new Runnable() {
            public void run() {
                try {
                    getComments();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getComments() throws IOException {
        boolean success = true;
        final ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(50, 50);
        //表單格式皆合法
        loading.setVisible(true);
        if (success) {
            try {
                HttpResponse response = RequestController.post(GlobalVariable.server+"comments/getComments",
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
                    messageData = new ArrayList<String>();
                    if (jsonResponse.errors.length == 0) {
                        for (String message : jsonResponse.Notices){
                            String[] dataArr = message.split("=");

                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/sample/view/fxml/detailCard/Comment.fxml"));
                            AnchorPane anchorPane = fxmlLoader.load();

                            CommentController itemController = fxmlLoader.getController();
                            itemController.setData(dataArr[0],dataArr[1]);

                            Platform.runLater(new Runnable() {
                                @Override public void run() {
                                    commentVBox.getChildren().add(anchorPane);
                                }
                            });
                        }
                    }

                } else {
                    System.out.println(response.getStatusLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loading.setVisible(false);
    }

    public void leaveComment(ActionEvent actionEvent) {
        message = commentTextBox.getText();
        toSendUserID = toSendUserIDTextBox.getText();
        boolean success = true;
        //表單格式皆合法
        loading.setVisible(true);
        if (success) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        HttpResponse response = RequestController.post(GlobalVariable.server+"comments/createComment",
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
                                ToastCaller toast;
                                if (error.equals("message too long"))
                                    toast = new ToastCaller("訊息過長",GlobalVariable.mainStage,ToastCaller.ERROR);
                                if (error.equals("userID doesn't exist!"))
                                    toast = new ToastCaller("user ID 不存在",GlobalVariable.mainStage,ToastCaller.ERROR);
                                if (error.equals("don't send yourself"))
                                    toast = new ToastCaller("請勿發給自己",GlobalVariable.mainStage,ToastCaller.ERROR);
                                if (error.equals("createNotice fail"))
                                    toast = new ToastCaller("伺服器錯誤",GlobalVariable.mainStage,ToastCaller.ERROR);
                            }

                            System.out.println();
                            ToastCaller toast;
                            if (jsonResponse.errors.length == 0 && checkMessageLegal(message)) {
                                toast = new ToastCaller("發送成功",GlobalVariable.mainStage,ToastCaller.SUCCESS);
                            }
                            else if(!checkMessageLegal(message)){
                                toast = new ToastCaller("訊息不可超過六行!",GlobalVariable.mainStage,ToastCaller.ERROR);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        loading.setVisible(false);
    }

    public boolean checkMessageLegal(String message)
    {
        if(!message.contains("\n"))
            return true;
        else
        {
            String[] checkReturn;
            checkReturn = message.split("\n");
            if(checkReturn.length <= 6)
                return true;
            else
                return false;
        }
    }
}
