package sample;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.response.loginResponse;
import sample.response.registerResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomePageController extends Main {

    public TextField userID,userPassword;
    public Label loginResult;

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
                    loginResponse jsonResponse = gson.fromJson(responseString,loginResponse.class);

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
                        loginResult.setText("Login success! switching to MainApp...");
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
                    loginResult.setText(errorsResult);
                }
            }
            catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }

    public void switchToForgotPassWord(ActionEvent e) throws IOException {
        Parent page = FXMLLoader.load(this.getClass().getResource("fxml/forgotPassWord.fxml"));
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