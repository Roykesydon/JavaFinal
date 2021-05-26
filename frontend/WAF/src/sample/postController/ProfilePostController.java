package sample.postController;

import javafx.scene.control.Label;
import sample.global.GlobalVariable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfilePostController {
    public Label ownerIDLabel;
    public Label categoryLabel;
    public Label priceLabel;
    public Label joinPeopleLabel;
    public Label secondaryJoinLabel;
    public Label secondaryPriceLabel;
    public Label secondaryCateLabel;
    public Label secondaryIDLabel;

    public void setData(String ownerID,String category,String price,String joinPeople){
        secondaryCateLabel.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor);
        secondaryJoinLabel.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor);
        secondaryPriceLabel.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor);
        secondaryIDLabel.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor);
        ownerIDLabel.setText(ownerID);
        categoryLabel.setText(category);
        priceLabel.setText("NT$ "+price);
        joinPeopleLabel.setText(joinPeople+"/10");
    }

}
