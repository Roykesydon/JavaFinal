package sample;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sample.response.registerResponse;
import sample.RequestController;


public class SignUpController
{
    public TextField userID,userPWConfirm,userMail,userPassword,userName;
    public Label test,ckName,ckPassword,ckMail,ckID,ckPWConfirm,registerResult;

    public void checkInput(ActionEvent actionEvent) throws IOException
    {
        String password = userPassword.getText();
        String mail = userMail.getText();
        boolean success = true;

        CheckSignUp checkUser = new CheckSignUp(password,mail);

        //verify submit form
        if(checkUser.checkPassWord())
            ckPassword.setText("OK");
        else {
            success = false;
            ckPassword.setText("WRONG");
        }
        if(password.equals(userPWConfirm.getText()))
            ckPWConfirm.setText("OK");
        else {
            success = false;
            ckPWConfirm.setText("WRONG");
        }
        if(checkUser.checkMail())
            ckMail.setText("OK");
        else {
            success = false;
            ckMail.setText("WRONG");
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
                    registerResponse jsonResponse = gson.fromJson(responseString,registerResponse.class);


                    for(String error:jsonResponse.errors){
                        System.out.print(',' + error);
                    }

                    System.out.println();
                    if(jsonResponse.errors.length==0){
                        registerResult.setText("Register success! switching to HomePage...");
                        backHomePage(actionEvent);
                    }
                    else{
                        registerResult.setText("Register fail");
                    }
                } else {
                    System.out.println(response.getStatusLine());
                    registerResult.setText("Register fail");
                }
            }
            catch (IOException  e) {
                e.printStackTrace();
            }
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

