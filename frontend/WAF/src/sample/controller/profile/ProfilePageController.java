package sample.controller.profile;


import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.view.detailCardController.ProfilePostController;
import sample.tool.response.detailCard.GetProfileAndOwnPostResponse;
import sample.tool.RequestController;
import sample.tool.ToastCaller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfilePageController implements Initializable {

    public VBox postVBox;
    public Button searchButton;
    public Label primaryID;
    public Label primaryLoginTime;
    public Label primaryName;
    public Label primaryEmail;
    public Label primaryProfile;
    public VBox box;
    private AnchorPane[] postArr;

    @FXML
    public Label useridLabel,nameLabel,emailLabel,lastAccessTimeLabel,ownPostsLabel;
    public TextField searchTextField;

    public ScrollPane postsScroll;

    public void initialize(URL url, ResourceBundle rb) {
        primaryEmail.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:31;");
        primaryID.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:31;");
        primaryLoginTime.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:31;");
        primaryName.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:31;");
        primaryProfile.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:53;");
        searchButton.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor + ";-fx-border-color: " + GlobalVariable.secondaryColor+";-fx-font-size:19;");
        try {
            box.getChildren().add(FXMLLoader.load(getClass().getResource("/sample/view/fxml/sidePanel/SidePanel.fxml")));
        }catch (IOException ex){
            Logger.getLogger(ProfilePageController.class.getName()).log(Level.SEVERE,null,ex);
        }

        try {
            getProfileAndOwnPost(GlobalVariable.userID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getProfileAndOwnPost(String userID) throws IOException
    {

        boolean success = true;
        postVBox.setPadding(new Insets(20, 50, 20, 8));
        postVBox.setSpacing(20);
        //表單格式皆合法
        if(success){
            try {
                HttpResponse response = RequestController.post(GlobalVariable.server+"posts/getProfileAndOwnPost",
                        new String[]{"userID", userID}
                );
                String responseString = EntityUtils.toString(response.getEntity());

                String errorsResult = "";
                int errorsResultCount = 0;

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    GetProfileAndOwnPostResponse jsonResponse = gson.fromJson(responseString, GetProfileAndOwnPostResponse.class);

                    errorsResult = "";
                    errorsResultCount=0;
                    for(String error:jsonResponse.errors){
                        if(errorsResultCount != 0)
                            errorsResult += " , ";
                        ToastCaller toast;
                        if(error.equals("userID doesn't exist!"))
                            toast = new ToastCaller("UserID不存在", GlobalVariable.mainStage,ToastCaller.ERROR);
                        errorsResult += error;
                        errorsResultCount++;
                        System.out.print(',' + error);
                    }

                    System.out.println();

                    if(jsonResponse.errors.length==0){
                        useridLabel.setText(jsonResponse.userID);
                        nameLabel.setText(jsonResponse.name);
                        emailLabel.setText(jsonResponse.email);
                        lastAccessTimeLabel.setText(jsonResponse.lastAccessTime);

                        String ownPosts = "";
                        for(String postInfo:jsonResponse.ownPost){
                            ownPosts += postInfo;
                            ownPosts += "=";
                        }
                        renderAllPost(ownPosts,jsonResponse.ownPost.length);
                    }

                } else {
                    System.out.println(response.getStatusLine());
                }
            }
            catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }
    public void renderAllPost(String posts,int postsQuantity) throws IOException {
        try {
            System.out.println("posts: "+posts);
            postVBox.getChildren().clear();
            if(postsQuantity!=0){
                postArr = new AnchorPane[postsQuantity];
                int postCount = 0;
                for (String retval: posts.split("="))
                {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/sample/view/fxml/detailCard/ProfilePost.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    System.out.println("retval: "+retval);
                    String[] postInfo = retval.split(",");

                    ProfilePostController itemController = fxmlLoader.getController();
                    itemController.setData(postInfo[0],postInfo[1],postInfo[2],postInfo[4]);

                    postArr[postCount++] = anchorPane;
                }
                for(AnchorPane aaa:postArr){
                    postVBox.getChildren().add(aaa);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void searchUser(){
        try {
            getProfileAndOwnPost(searchTextField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

