package sample;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.response.SetIdentityCodeResponse;
import sample.response.CheckIdentityResponse;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.global.GlobalVariable;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.io.IOException;

public class ForgotPasswordController {
    public TextField userIdentityCode, userid;
    public Label userIdResponse;
    public int tryCount=0;
    public void setIdentityCodeButtonListener(ActionEvent actionEvent){
        setIdentityCode(0);
    }
    public void setIdentityCode(int status)  {
        if(!userid.getText().isEmpty()) {
            try {
                HttpResponse response=RequestController.post("http://localhost:13261/user/setIdentityCode",
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
    public void sendIdentityCode(int status){
        if(!userid.getText().isEmpty()) {
            try {
                HttpResponse response=RequestController.post("http://localhost:13261/Email/sendEmail",
                    new String[]{"userid",userid.getText()}
                );
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                    if(status==0)userIdResponse.setText("Email寄送成功");
                }
                else{
                    if(status==0)userIdResponse.setText("Email回應錯誤");
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            if(status==0)userIdResponse.setText("請輸入帳號");
        }
    }
    public void switchToResetPassWord(ActionEvent actionEvent) {
        if(!userid.getText().isEmpty()) {
            if(tryCount==4){
                userIdResponse.setText("失敗次數過多驗證碼已改變\n請按重寄驗證碼");
                setIdentityCode(1);
            }
            else {
                try {
                    HttpResponse response = RequestController.post("http://localhost:13261/user/checkIdentityCode",
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
                            URL url = new File("src/sample/fxml/ResetPassWord.fxml").toURI().toURL();
                            Parent root = FXMLLoader.load(url);
                            Scene tmp = new Scene(root);
                            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                            stage.hide();
                            stage.setScene(tmp);
                            stage.show();
                        } else {
                            tryCount++;
                            userIdResponse.setText(Arrays.toString(gsonResponse.errors));
                        }
                    } else {
                        tryCount++;
                        userIdResponse.setText("驗證碼錯誤");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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