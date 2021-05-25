package sample.postController;

import com.google.gson.Gson;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.RequestController;
import sample.ToastCaller;
import sample.global.GlobalVariable;
import sample.response.SetIdentityCodeResponse;

import java.io.IOException;
import java.util.Arrays;

public class CommentController {
    public Label senderIDLabel;
    public Label contentLabel;
    public AnchorPane anchorPane;

    public void setData(String sender, String detail){
        senderIDLabel.setText(sender+" :");
        contentLabel.setText(detail);
        contentLabel.setMinHeight(Region.USE_PREF_SIZE);
        anchorPane.setPadding(new Insets(10, 10, 1, 10));
    }
}
