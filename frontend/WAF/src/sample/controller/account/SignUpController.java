package sample.controller.account;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import sample.tool.RequestController;
import sample.tool.ToastCaller;
import sample.global.GlobalVariable;
import sample.tool.response.account.RegisterResponse;


public class SignUpController implements Initializable
{
    //public TextField userID,userPWConfirm,userMail,userPassword,userName;
    public Label test,ckName,ckPassword,ckMail,ckID,ckPWConfirm,registerResult;
    public Label primarySignLabel;
    public Button submit;
    public Button backBtn;
    public JFXTextField userName;
    public JFXTextField userID;
    public JFXPasswordField userPassword;
    public JFXPasswordField userPWConfirm;
    public JFXTextField userMail;

    public void initialize(URL url, ResourceBundle rb){
        userID.setFocusColor(Paint.valueOf(GlobalVariable.primaryColor));
        userName.setFocusColor(Paint.valueOf(GlobalVariable.primaryColor));
        userPassword.setFocusColor(Paint.valueOf(GlobalVariable.primaryColor));
        userPWConfirm.setFocusColor(Paint.valueOf(GlobalVariable.primaryColor));
        userMail.setFocusColor(Paint.valueOf(GlobalVariable.primaryColor));
        primarySignLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:50;");
        submit.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-border-color: "+GlobalVariable.primaryColor+";-fx-font-size:23;");
        backBtn.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-border-color: "+GlobalVariable.secondaryColor+";-fx-font-size:23;");
    }

    public void checkInput(ActionEvent actionEvent) throws IOException
    {
        String password = userPassword.getText();
        String mail = userMail.getText();
        String id = userID.getText();
        String name = userName.getText();
        boolean success = true;

        CheckSignUp checkUser = new CheckSignUp(password,mail,id,name);

        //verify submit form
        if(checkUser.checkPassWord()) {
            ckPassword.setText("OK");
            ckPassword.setStyle("-fx-text-fill: #00dd77;");
        }
        else {
            success = false;
            ckPassword.setText("請輸入至少個別一大、小寫英文與數字");
            ckPassword.setStyle("-fx-text-fill: #ea4141;");
        }
        if(password.equals(userPWConfirm.getText())) {
            ckPWConfirm.setText("OK");
            ckPWConfirm.setStyle("-fx-text-fill: #00dd77;");
            if(userPWConfirm.getText().equals(""))
                ckPWConfirm.setText("");
        }
        else {
            success = false;
            ckPWConfirm.setText("與原密碼不符");
            ckPWConfirm.setStyle("-fx-text-fill: #ea4141;");
        }
        if(checkUser.checkMail()) {
            ckMail.setText("OK");
            ckMail.setStyle("-fx-text-fill: #00dd77;");
        }
        else {
            success = false;
            ckMail.setText("請輸入正確的信箱格式");
            ckMail.setStyle("-fx-text-fill: #ea4141;");
        }
        if(checkUser.checkID()) {
            ckID.setText("OK");
            ckID.setStyle("-fx-text-fill: #00dd77;");
        }
        else {
            success = false;
            ckID.setText("只能有數字和英文，限於5~30字");
            ckID.setStyle("-fx-text-fill: #ea4141;");
        }
        if(checkUser.checkName()) {
            ckName.setText("OK");
            ckName.setStyle("-fx-text-fill: #00dd77;");
        }
        else {
            success = false;
            ckName.setText("只能有數字和英文，限於5~50字");
            ckName.setStyle("-fx-text-fill: #ea4141;");
        }

        //表單格式皆合法
        if(success){
            try {
                HttpResponse response = RequestController.post("http://127.0.0.1:13261/user/register",
                        new String[]{"name", userName.getText()},
                        new String[]{"userid", userID.getText()},
                        new String[]{"passwd", userPassword.getText()},
                        new String[]{"passwdConfirm", userPWConfirm.getText()},
                        new String[]{"email", userMail.getText()}
                );
                String responseString = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    RegisterResponse jsonResponse = gson.fromJson(responseString, RegisterResponse.class);


                    for(String error:jsonResponse.errors){
                        System.out.print(',' + error);
                        ToastCaller toast;
                        if(error.equals("ID has been registered"))
                            toast = new ToastCaller("ID已被註冊過", GlobalVariable.mainStage,ToastCaller.ERROR);
                        if(error.equals("email has been registered"))
                            toast = new ToastCaller("信箱已被註冊過", GlobalVariable.mainStage,ToastCaller.ERROR);
                        if(error.equals("register fail"))
                            toast = new ToastCaller("伺服器錯誤", GlobalVariable.mainStage,ToastCaller.ERROR);
                    }

                    System.out.println();
                    if(jsonResponse.errors.length==0){
                        backHomePage(actionEvent);
                        ToastCaller toast = new ToastCaller("Register Success!", GlobalVariable.mainStage,ToastCaller.SUCCESS);
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

    public void backHomePage(ActionEvent actionEvent) throws IOException {
        Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/HomePage.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }
}

