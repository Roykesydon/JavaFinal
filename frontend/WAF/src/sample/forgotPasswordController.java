package sample;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.response.setIdentityCodeResponse;
import sample.response.checkIdentityResponse;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.global.GlobalVariable;
import java.util.Arrays;
import java.io.IOException;

public class forgotPasswordController {
    public TextField userIdentityCode, userid;
    public Label userIdResponse;
    public void setIdentityCode(ActionEvent actionEvent)  {
        if(!userid.getText().isEmpty()) {
            try {
                HttpResponse response=RequestController.post("http://localhost:13261/user/setIdentityCode",
                        new String[]{"userid",userid.getText()}
                );
                String responseString= EntityUtils.toString(response.getEntity());
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                    Gson gson =new Gson();
                    setIdentityCodeResponse gsonResponse = gson.fromJson(responseString,setIdentityCodeResponse.class);
                    if(Arrays.toString(gsonResponse.errors)=="[]"){
                        sendIdentityCode();
                    }
                    else{
                        userIdResponse.setText((Arrays.toString(gsonResponse.errors)));
                    }
                }
                else{
                    System.out.println(response.getStatusLine().getStatusCode());
                    userIdResponse.setText("回應錯誤");
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            userIdResponse.setText("請輸入帳號");
        }

    }
    public void sendIdentityCode(){
        if(!userid.getText().isEmpty()) {
            try {
                HttpResponse response=RequestController.post("http://localhost:13261/Email/sendEmail",
                    new String[]{"userid",userid.getText()}
                );
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                    userIdResponse.setText("Email寄送成功");
                }
                else{
                    userIdResponse.setText("Email回應錯誤");
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            userIdResponse.setText("請輸入帳號");
        }
    }
    public void switchToResetPassWord(ActionEvent actionEvent){
        if(!userid.getText().isEmpty()) {
            try {
                HttpResponse response=RequestController.post("http://localhost:13261/user/checkIdentityCode",
                        new String[]{"userid",userid.getText()},
                        new String[]{"IdentityCode",userIdentityCode.getText()}
                );
                String responseString= EntityUtils.toString(response.getEntity());
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                    Gson gson =new Gson();
                    checkIdentityResponse gsonResponse = gson.fromJson(responseString,checkIdentityResponse.class);
                    if(Arrays.toString(gsonResponse.errors)=="[]"){
                        GlobalVariable.userID=userid.getText();
                        Parent page =FXMLLoader.load(this.getClass().getResource("fxml/resetPassWord.fxml"));
                        Scene tmp = new Scene(page);
                        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                        stage.hide();
                        stage.setScene(tmp);
                        stage.show();
                    }
                    else{
                        userIdResponse.setText(Arrays.toString(gsonResponse.errors));
                    }
                }
                else{
                    userIdResponse.setText("驗證碼錯誤");
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            userIdResponse.setText("請輸入帳號");
        }
    }
    public void backHomePage(ActionEvent actionEvent) throws IOException {
        Parent page = FXMLLoader.load(getClass().getResource("fxml/HomePage.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }
}