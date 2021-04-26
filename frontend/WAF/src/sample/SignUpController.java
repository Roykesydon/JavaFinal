package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class SignUpController
{
    public TextField userID,userPWConfirm,userMail,userPassword,userName;
    public Label ckName,ckPassword,ckMail,ckID,ckPWConfirm;

    public void checkInput(ActionEvent actionEvent)
    {
        String password = userPassword.getText();
        String mail = userMail.getText();
        String ID = userID.getText();
        String name = userName.getText();
        CheckSignUp checkUser = new CheckSignUp(password,mail,ID,name);
        //check illegal input
        if(checkUser.checkName())
            ckName.setText("OK");
        else
            ckName.setText("WRONG");
        if(checkUser.checkID())
            ckID.setText("OK");
        else
            ckID.setText("WRONG");
        if(checkUser.checkPassWord())
            ckPassword.setText("OK");
        else
            ckPassword.setText("WRONG");
        if(password.equals(userPWConfirm.getText()))
            ckPWConfirm.setText("OK");
        else
            ckPWConfirm.setText("WRONG");
        if(checkUser.checkMail())
            ckMail.setText("OK");
        else
            ckMail.setText("WRONG");
    }

    public void BackHomePage(ActionEvent actionEvent) throws IOException {
        Parent page = FXMLLoader.load(getClass().getResource("fxml/HomePage.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();//switch smoothly
        stage.setScene(tmp);
        stage.show();
    }
}
