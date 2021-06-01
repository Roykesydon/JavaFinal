package sample.view.detailCardController;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import sample.global.GlobalVariable;

public class CommentController {
    public Label senderIDLabel;
    public Label contentLabel;
    public AnchorPane anchorPane;
    public Label secondaryFrom;

    public void setData(String sender, String detail){
        secondaryFrom.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-font-size:26;");
        senderIDLabel.setText(sender+" :");
        contentLabel.setText(detail);
        contentLabel.setMinHeight(Region.USE_PREF_SIZE);
        anchorPane.setPadding(new Insets(10, 10, 1, 10));
    }
}
