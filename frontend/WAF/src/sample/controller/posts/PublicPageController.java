package sample.controller.posts;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.tool.response.detailCard.GetProfileAndOwnPostResponse;
import sample.view.detailCardController.ProfilePostController;
import sample.view.detailCardController.PublicPostController;
import sample.tool.response.detailCard.GetAllPostResponse;
import sample.tool.RequestController;
import sample.tool.ToastCaller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PublicPageController implements Initializable {

    public ScrollPane postsScroll;
    public AnchorPane anchorpane;
    public VBox postVBox;
    public Button filterBtn;
    public Label primaryPublicLabel;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    public Label getAllPostResult;
    private GetProfileAndOwnPostResponse classJsonResponse;
    private Label[] postLabelArr;
    private AnchorPane[] postArr;
    public Label joinStatusLabel= new Label();
    public ComboBox categoryComboBox;
    public VBox box;
    private List<String> joinPostData = new ArrayList<>();

    public void initialize(URL url, ResourceBundle rb) {
        filterBtn.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor + ";-fx-border-color: " + GlobalVariable.primaryColor+";-fx-font-size:20;");
        primaryPublicLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:53;");
        try {
            box.getChildren().add(FXMLLoader.load(getClass().getResource("/sample/view/fxml/sidePanel/SidePanel.fxml")));
        }catch (IOException ex){
            Logger.getLogger(PublicPageController.class.getName()).log(Level.SEVERE,null,ex);
        }

        try {
            getAllPost();
        } catch (IOException e) {
            e.printStackTrace();
        }
        postVBox.setPadding(new Insets(20, 20, 20, 20));
        postVBox.setSpacing(20);
        categoryComboBox.getItems().addAll("Spotify","NintendoSwitchOnline","YoutubePremium","Netflix","AppleMusic");
    }

    public void getAllPost() throws IOException
    {
        try {
            HttpResponse response = RequestController.post(GlobalVariable.server+"posts/getOwnAndJoinPost",
                    new String[]{"accessKey", GlobalVariable.accessKey}
            );
            String responseString = EntityUtils.toString(response.getEntity());
            String errorsResult = "";
            int errorsResultCount = 0;

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Gson gson = new Gson();
                GetProfileAndOwnPostResponse jsonResponse = gson.fromJson(responseString, GetProfileAndOwnPostResponse.class);
                //create class variable to use renderAllPost method in update scene
                classJsonResponse = jsonResponse;

                errorsResult = "";
                errorsResultCount=0;
                for(String error:jsonResponse.errors){
                    if(errorsResultCount != 0)
                        errorsResult += " , ";

                    errorsResult += error;
                    errorsResultCount++;
                    System.out.print(',' + error);
                }

                System.out.println();
                joinPostData.clear();
                if(jsonResponse.errors.length==0){
                    String ownPosts = "";
                    for(String postInfo:jsonResponse.ownPost){
                        if((postInfo.split(","))[0]!=GlobalVariable.userID)
                            joinPostData.add((postInfo.split(","))[3]);
                    }
                }

            } else {
                System.out.println(response.getStatusLine());
            }
        }
        catch (IOException  e) {
            e.printStackTrace();
        }



        try {
            HttpResponse response = RequestController.post(GlobalVariable.server+"posts/getAllPost",null
            );
            String responseString = EntityUtils.toString(response.getEntity());

            String errorsResult = "";
            int errorsResultCount = 0;

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Gson gson = new Gson();
                GetAllPostResponse jsonResponse = gson.fromJson(responseString, GetAllPostResponse.class);
                errorsResult = "";
                errorsResultCount=0;
                for(String error:jsonResponse.errors){
                    if(errorsResultCount != 0)
                        errorsResult += " , ";

                    errorsResult += error;
                    errorsResultCount++;
                    System.out.print(',' + error);
                }
                System.out.println();
                if(jsonResponse.errors.length==0){
                    int count = 0;//to split two responseString with comma
                    String posts = "";
                    for(String postInfo:jsonResponse.posts){
                        if(count++ != 0)
                            posts += "=";
                        posts += postInfo;
                    }
                    renderAllPost(posts,jsonResponse.posts.length);
                }
            } else {
                System.out.println(response.getStatusLine());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void renderAllPost(String posts,int postsQuantity) throws IOException {
        try {
            postArr = new AnchorPane[postsQuantity];
            int postCount = 0;
            int count = 0;
            String tmp = "";
            String postID = "";
            if(posts.equals(""))
                return;
            for (String retval: posts.split("="))
            {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/sample/view/fxml/detailCard/PublicPost.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                System.out.println("retval: "+retval);
                String[] postInfo = retval.split(",");

                PublicPostController itemController = fxmlLoader.getController();
                itemController.setData(postInfo[0],postInfo[1],postInfo[2],postInfo[4],postInfo[3],joinPostData);

                postArr[postCount++] = anchorPane;
            }
            for(AnchorPane aaa:postArr){
                postVBox.getChildren().add(aaa);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filterPost() {
        try {
            if(categoryComboBox.getValue()==null){
                ToastCaller toast = new ToastCaller("請選擇分類",GlobalVariable.mainStage,ToastCaller.ERROR);
                return ;
            }
            postVBox.getChildren().clear();
            HttpResponse response = RequestController.post(GlobalVariable.server+"posts/getFilteredPost",
                    new String[]{"category",categoryComboBox.getValue().toString()}
            );
            String responseString = EntityUtils.toString(response.getEntity());

            String errorsResult = "";
            int errorsResultCount = 0;

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Gson gson = new Gson();
                GetAllPostResponse jsonResponse = gson.fromJson(responseString, GetAllPostResponse.class);
                errorsResult = "";
                errorsResultCount=0;
                for(String error:jsonResponse.errors){
                    if(errorsResultCount != 0)
                        errorsResult += " , ";
                    if(error.equals("getFilteredPost fail")){
                        ToastCaller toast = new ToastCaller("伺服器錯誤",GlobalVariable.mainStage,ToastCaller.ERROR);
                    }
                    errorsResult += error;
                    errorsResultCount++;
                    System.out.print(',' + error);
                }
                System.out.println();
                if(jsonResponse.errors.length==0){
                    int count = 0;//to split two responseString with comma
                    String posts = "";
                    for(String postInfo:jsonResponse.posts){
                        if(count++!=0)
                            posts+="=";
                        posts += postInfo;
                    }
                    renderAllPost(posts,jsonResponse.posts.length);
                }
            } else {
                System.out.println(response.getStatusLine());
            }
            getAllPostResult.setText(errorsResult);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
