package sample;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.response.resetPassWordResponse;
import sample.global.GlobalVariable;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class resetPassWordController implements Initializable {
    public TextField newPassWord, confirmPassWord;
    public Label userId,resetResponse;

    public void initialize(URL url, ResourceBundle rb) {
        userId.setText(GlobalVariable.userID);
    }
    public void resetPassword(ActionEvent actionEvent){
        if(!newPassWord.getText().isEmpty()&&!confirmPassWord.getText().isEmpty()) {
            try {
                HttpResponse response=RequestController.post("http://localhost:13261/user/resetPassword",
                        new String[]{"userid",userId.getText()},
                        new String[]{"passwd",newPassWord.getText()},
                        new String[]{"passwdConfirm",confirmPassWord.getText()}
                );
                String responseString= EntityUtils.toString(response.getEntity());
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                    Gson gson =new Gson();
                    resetPassWordResponse gsonResponse = gson.fromJson(responseString,resetPassWordResponse.class);
                    if(Arrays.toString(gsonResponse.errors)=="[]"){
                        resetResponse.setText("修改成功");
                        Parent page = FXMLLoader.load(getClass().getResource("fxml/HomePage.fxml"));
                        Scene tmp = new Scene(page);
                        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                        stage.hide();//switch smoothly
                        stage.setScene(tmp);
                        stage.show();
                    }
                    else{
                        if(Arrays.toString(gsonResponse.errors)=="[password length error]"){
                            resetResponse.setText("長度錯誤");
                        }
                        else if(Arrays.toString(gsonResponse.errors)=="[passwd has illegal characters]"){
                            resetResponse.setText("密碼不可含非法字元");
                        }
                        else if(Arrays.toString(gsonResponse.errors)=="[password diffrent from passwordConfirm]"){
                            resetResponse.setText("確認密碼與密碼不符");
                        }
                        else{
                            resetResponse.setText(Arrays.toString(gsonResponse.errors));
                            System.out.println(Arrays.toString(gsonResponse.errors));
                        }
                    }
                }
                else{
                    System.out.println(response.getStatusLine().getStatusCode());
                    resetResponse.setText("回應錯誤");
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            resetResponse.setText("輸入不可為空");
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