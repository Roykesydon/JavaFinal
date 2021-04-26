<<<<<<< HEAD
package sample;
=======
package WeAreFamily;
>>>>>>> db3b2de85de5e6f299b4d16b3d52ee85650804ce

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;


public class resetPassWordController {

    public void switchToForgotPassWord(MouseEvent actionEvent) throws IOException {
        Parent page = (Parent) FXMLLoader.load(this.getClass().getResource("fxml/resetPassWord.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(tmp);
        stage.show();
    }
}