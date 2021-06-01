package sample.controller.account;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.tool.RequestController;
import sample.tool.ToastCaller;
import sample.tool.response.account.ResetPassWordResponse;
import sample.global.GlobalVariable;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;


public class ResetPassWordController implements Initializable {
    public TextField newPassWord, confirmPassWord;
    public Label userId,resetResponse;
    public Label primaryUserIDLabel;
    public Label primaryResetLabel;
    public Label primaryConfirmLabel;
    public Label primaryNewLabel;
    public Button resetBtn;
    public Button backBtn;
    public Label secondaryUserId;
    // public JFXPasswordField newPassWord;
    // public JFXPasswordField confirmPassWord;

    public void initialize(URL url, ResourceBundle rb) {
        secondaryUserId.setText(GlobalVariable.userID);
        primaryConfirmLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:36;");
        primaryUserIDLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:36;");
        primaryResetLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:48;");
        primaryNewLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:36;");
        resetBtn.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-border-color: "+GlobalVariable.primaryColor+";-fx-font-size:36;");
        secondaryUserId.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-font-size:36;");
        newPassWord.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor);
        confirmPassWord.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor);
        backBtn.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-border-color: "+GlobalVariable.secondaryColor+";-fx-font-size:36;");
    }
    public void resetPassword(ActionEvent actionEvent){
        if(!newPassWord.getText().isEmpty()&&!confirmPassWord.getText().isEmpty()) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        HttpResponse response= RequestController.post(GlobalVariable.server+"user/resetPassword",
                                new String[]{"accessKey",GlobalVariable.accessKey},
                                new String[]{"passwd",newPassWord.getText()},
                                new String[]{"passwdConfirm",confirmPassWord.getText()}
                        );
                        String responseString= EntityUtils.toString(response.getEntity());
                        if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                            Gson gson =new Gson();
                            ResetPassWordResponse gsonResponse = gson.fromJson(responseString, ResetPassWordResponse.class);
                            CheckSignUp checkPassWord = new CheckSignUp(newPassWord.getText());
                            if(Arrays.toString(gsonResponse.errors)=="[]"&&checkPassWord.checkPassWord()==true){
                                ToastCaller toast = new ToastCaller("修改成功",GlobalVariable.mainStage,ToastCaller.SUCCESS);
                                Parent page = FXMLLoader.load(getClass().getResource("/sample/view/fxml/HomePage.fxml"));
                                Scene tmp = new Scene(page);
                                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                                stage.hide();//switch smoothly
                                stage.setScene(tmp);
                                stage.show();
                            }
                            else{
                                if(!checkPassWord.checkPassWord()){
                                    ToastCaller toast = new ToastCaller("需有大小寫英文以及數字",GlobalVariable.mainStage,ToastCaller.ERROR);
                                }
                                if(!newPassWord.getText().equals(confirmPassWord.getText())){
                                    ToastCaller toast = new ToastCaller("確認密碼不同",GlobalVariable.mainStage,ToastCaller.ERROR);
                                }
                                System.out.println(Arrays.toString(gsonResponse.errors));
                            }
                        }
                        else{
                            System.out.println(response.getStatusLine().getStatusCode());
                            ToastCaller toast = new ToastCaller("回應錯誤",GlobalVariable.mainStage,ToastCaller.ERROR);
                        }
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else{
            ToastCaller toast = new ToastCaller("輸入不可為空",GlobalVariable.mainStage,ToastCaller.ERROR);
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