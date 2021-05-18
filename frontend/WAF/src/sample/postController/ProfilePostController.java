package sample.postController;

import javafx.scene.control.Label;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfilePostController {
    public Label ownerIDLabel;
    public Label categoryLabel;
    public Label priceLabel;
    public Label joinPeopleLabel;

    public void setData(String ownerID,String category,String price,String joinPeople){
        ownerIDLabel.setText(ownerID);
        categoryLabel.setText(category);
        priceLabel.setText("NT$ "+price);
        joinPeopleLabel.setText(joinPeople+"/10");
    }

}
