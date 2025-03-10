package sample.controller.account;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.skin.ProgressIndicatorSkin;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.tool.RequestController;
import sample.tool.ToastCaller;
import sample.tool.response.account.SetIdentityCodeResponse;
import sample.tool.response.account.CheckIdentityResponse;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.global.GlobalVariable;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.io.IOException;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable{
    public TextField userIdentityCode, userid;
    public Label userIdResponse;
    public int tryCount=0;
    public Label primaryIdentifyLabel;
    public Label primaryForgetLabel;
    public Button submit;
    public Label primaryIDLabel;
    public Button backBtn;
    public Button send;
    public ProgressIndicator loading;

    public void initialize(URL url, ResourceBundle rb){
        loading.setVisible(false);
        backBtn.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-border-color: "+GlobalVariable.secondaryColor+";-fx-font-size:36;");
        primaryForgetLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:58;");
        primaryIDLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:34;");
        primaryIdentifyLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:34;");
        submit.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-border-color: "+GlobalVariable.primaryColor+";-fx-font-size:36;");
        send.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-border-color: "+GlobalVariable.primaryColor+";-fx-font-size:25;");
    }

    public void setIdentityCodeButtonListener(ActionEvent actionEvent){
        setIdentityCode(0);
    }
    public void setIdentityCode(int status)  {
        if(!userid.getText().isEmpty()) {
            loading.setVisible(true);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        HttpResponse response= RequestController.post(GlobalVariable.server+"user/setIdentityCode",
                                new String[]{"userid",userid.getText()}
                        );
                        String responseString= EntityUtils.toString(response.getEntity());
                        if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                            Gson gson =new Gson();
                            SetIdentityCodeResponse gsonResponse = gson.fromJson(responseString, SetIdentityCodeResponse.class);
                            if(Arrays.toString(gsonResponse.errors).equals("[]")){
                                if(status==0)sendIdentityCode(status);
                                else tryCount=0;
                            }
                            else{
                                for(String error : gsonResponse.errors){
                                    ToastCaller toast;
                                    if(error.equals("ID has not found"))
                                        toast = new ToastCaller("User ID不存在",GlobalVariable.mainStage,ToastCaller.ERROR);
                                    if(error.equals("today has set Identify code"))
                                        toast = new ToastCaller("今天已設過驗證碼",GlobalVariable.mainStage,ToastCaller.ERROR);
                                }
                            }
                        }
                        else{
                            System.out.println(response.getStatusLine().getStatusCode());
                        }
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
            loading.setVisible(false);
        }
        else{
            ToastCaller toast;
            toast = new ToastCaller("請輸入帳號",GlobalVariable.mainStage,ToastCaller.ERROR);
        }

    }
    public void sendIdentityCode(int status){
        if(!userid.getText().isEmpty()) {
            loading.setVisible(true);
            try {
                HttpResponse response=RequestController.post(GlobalVariable.server+"Email/sendEmail",
                    new String[]{"userid",userid.getText()}
                );
                ToastCaller toast;
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                    if(status==0)
                        toast = new ToastCaller("已送出Email",GlobalVariable.mainStage,ToastCaller.SUCCESS);;
                }
                else{
                    if(status==0)
                        toast = new ToastCaller("回應錯誤",GlobalVariable.mainStage,ToastCaller.ERROR);;
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            loading.setVisible(false);
        }
        else{
            ToastCaller toast;
            if(status==0)
                toast = new ToastCaller("請輸入帳號",GlobalVariable.mainStage,ToastCaller.ERROR);;
        }
    }
    public void switchToResetPassWord(ActionEvent actionEvent) {
        if(!userid.getText().isEmpty()) {
            new Thread(new Runnable() {
                public void run() {
                    ForgotPasswordController.this.loading.setVisible(true);
                    try {

                        HttpResponse response = RequestController.post(GlobalVariable.server+"user/checkIdentityCode",
                                new String[]{"userid", userid.getText()},
                                new String[]{"IdentityCode", userIdentityCode.getText()}
                        );
                        String responseString = EntityUtils.toString(response.getEntity());
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            Gson gson = new Gson();
                            CheckIdentityResponse gsonResponse = gson.fromJson(responseString, CheckIdentityResponse.class);
                            if (Arrays.toString(gsonResponse.errors).equals("[]")) {
                                GlobalVariable.userID = userid.getText();
                                GlobalVariable.accessKey = gsonResponse.accessKey;
                                tryCount = 0;
                                //URL url = new File("/sample/view/fxml/account/ResetPassWord.fxml").toURI().toURL();
                                Parent root = FXMLLoader.load(getClass().getResource("/sample/view/fxml/account/ResetPassWord.fxml"));
                                Scene tmp = new Scene(root);
                                Platform.runLater(new Runnable() {
                                    @Override public void run() {
                                        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                                        stage.hide();
                                        stage.setScene(tmp);
                                        stage.show();
                                    }
                                });
                            } else {
                                tryCount++;
                                ToastCaller toast;
                                for(String error: gsonResponse.errors) {
                                    if (error.equals("IdentityCode error"))
                                        toast = new ToastCaller("驗證碼錯誤", GlobalVariable.mainStage, ToastCaller.ERROR);
                                    if (error.equals("already try 5 times"))
                                        toast = new ToastCaller("本日已嘗試五次", GlobalVariable.mainStage, ToastCaller.ERROR);
                                }
                            }
                        } else {
                            tryCount++;
                            ToastCaller toast;
                            toast = new ToastCaller("網路錯誤",GlobalVariable.mainStage,ToastCaller.ERROR);;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ForgotPasswordController.this.loading.setVisible(false);
                }
            }).start();
            loading.setVisible(false);
        }
        else{
            ToastCaller toast;
            toast = new ToastCaller("請輸入帳號",GlobalVariable.mainStage,ToastCaller.ERROR);;
        }
    }
    public void backHomePage(ActionEvent actionEvent) throws IOException {
        Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/HomePage.fxml"));
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