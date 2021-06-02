package sample.controller;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXPasswordField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.Main;
import sample.global.GlobalVariable;
import sample.tool.response.account.LoginResponse;
import sample.tool.RequestController;
import sample.tool.ToastCaller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class HomePageController extends Main implements Initializable {

    public Button btnSignIn;
    public Button buttonForgot;
    public Button btnSignUp;
    public Label primaryWAFLabel;
    public TextField userID;
    public JFXPasswordField userPassword;
    public ProgressIndicator loading;

    public void initialize(URL url, ResourceBundle rb){
        primaryWAFLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:12em;");
        btnSignUp.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-border-color: "+GlobalVariable.secondaryColor+";-fx-font-size:22;");
        btnSignIn.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-border-color: "+GlobalVariable.primaryColor+";-fx-font-size:25;");
        buttonForgot.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-font-size:18;");
        userPassword.setFocusColor(Paint.valueOf(GlobalVariable.primaryColor));
        loading.setVisible(false);
    }

    public void switchToSignup(ActionEvent actionEvent) throws IOException
    {
        Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/account/SignUp.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(tmp);
        stage.show();
    }


    public void loginRequest(ActionEvent actionEvent) throws IOException
    {

        String id = userID.getText();
        String password = userPassword.getText();
        boolean success = true;

        //verify submit form


        //表單格式皆合法
        if(success){
            new Thread(new Runnable() {
                public void run() {
                    HomePageController.this.loading.setVisible(true);
                    try {
                        HttpResponse response = RequestController.post(GlobalVariable.server+"user/login",
                                new String[]{"userid", userID.getText()},
                                new String[]{"passwd", userPassword.getText()}
                        );
                        String responseString = EntityUtils.toString(response.getEntity());

                        String errorsResult = "";
                        int errorsResultCount = 0;

                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            Gson gson = new Gson();
                            LoginResponse jsonResponse = gson.fromJson(responseString, LoginResponse.class);

                            errorsResult = "";
                            errorsResultCount=0;
                            for(String error:jsonResponse.errors){
                                if(errorsResultCount != 0)
                                    errorsResult += " , ";

                                errorsResult += error;
                                errorsResultCount++;

                                ToastCaller toast;
                                if(error.equals("password error"))
                                    toast = new ToastCaller("密碼輸入錯誤", GlobalVariable.mainStage,ToastCaller.ERROR);
                                if(error.equals("userID doesn't exist"))
                                    toast = new ToastCaller("User ID不存在", GlobalVariable.mainStage,ToastCaller.ERROR);
                                System.out.print(',' + error);
                            }

                            System.out.println();

                            if(jsonResponse.errors.length==0){
                                GlobalVariable.accessKey = jsonResponse.accessKey;
                                GlobalVariable.userID = jsonResponse.userID;
                                System.out.println(jsonResponse.isAdmin);
                                if( ! jsonResponse.isAdmin.equals("0"))
                                    GlobalVariable.isAdmin = true;
                                switchToMainApp(actionEvent);
                            }
                        } else {
                            System.out.println(response.getStatusLine());
                        }
                    }
                    catch (IOException  e) {
                        e.printStackTrace();
                    }
                    HomePageController.this.loading.setVisible(false);
                }
            }).start();
            loading.setVisible(false);
        }
    }

    public void switchToForgotPassWord(ActionEvent e) throws IOException {
        Parent page = FXMLLoader.load(this.getClass().getResource("/sample/view/fxml/account/ForgotPassword.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(tmp);
        stage.show();
    }

    public void switchToMainApp(ActionEvent actionEvent) throws IOException {
        Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/posts/PublicPostPage.fxml"));
        Scene tmp = new Scene(page);
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.hide();//switch smoothly
                stage.setScene(tmp);
                stage.show();
            }
        });

    }
}