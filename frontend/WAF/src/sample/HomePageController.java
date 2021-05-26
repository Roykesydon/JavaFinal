package sample;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.response.LoginResponse;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class HomePageController extends Main implements Initializable {

    public Button btnSignIn;
    public Button labelForgot;
    public Button btnSignUp;
    public Label primaryWAFLabel;
    public JFXTextField userID;
    public JFXPasswordField userPassword;

    public void initialize(URL url, ResourceBundle rb){
        primaryWAFLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor);
        btnSignUp.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-border-color: "+GlobalVariable.secondaryColor);
        btnSignIn.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-border-color: "+GlobalVariable.primaryColor);
        labelForgot.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-border-color: "+GlobalVariable.secondaryColor);
        userID.setFocusColor(Paint.valueOf(GlobalVariable.primaryColor));
        userPassword.setFocusColor(Paint.valueOf(GlobalVariable.primaryColor));
    }

    public void switchToSignup(ActionEvent actionEvent) throws IOException
    {
        Parent page = FXMLLoader.load(getClass().getResource("fxml/SignUp.fxml"));
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
            try {
                HttpResponse response = RequestController.post("http://127.0.0.1:13261/user/login",
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
                    else{
//                      loginResult.setText(errorsResult);
                    }
                } else {
                    System.out.println(response.getStatusLine());
//                    loginResult.setText(errorsResult);
                }
            }
            catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }

    public void switchToForgotPassWord(ActionEvent e) throws IOException {
        Parent page = FXMLLoader.load(this.getClass().getResource("fxml/ForgotPassword.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(tmp);
        stage.show();
    }

    public void switchToMainApp(ActionEvent actionEvent) throws IOException {
        Parent page = FXMLLoader.load(getClass().getResource("fxml/PublicPostPage.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }
}