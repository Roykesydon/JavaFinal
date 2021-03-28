package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class SignUpController
{
    public TextField userID,userPWConfirm,userMail,userPassword,userName;
    public Label test,ckName,ckPassword,ckMail,ckID,ckPWConfirm;

    public void checkInput(ActionEvent actionEvent)
    {
        String password = userPassword.getText();
        String mail = userMail.getText();
        CheckSignUp checkUser = new CheckSignUp(password,mail);
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
}
