package sample.controller.posts;


import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.tool.response.detailCard.GetProfileAndOwnPostResponse;
import sample.tool.RequestController;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagePostController implements Initializable {

    public VBox managePostVBox;
    public Label primaryLabel;
    public VBox box;
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;
    private GetProfileAndOwnPostResponse classJsonResponse;
    private Label deleteStatusLabel;
    private Label choosePeopleStatusLabel;
    private List<String> postData = new ArrayList<>();
    private AnchorPane[] postArr;


    public void initialize(URL url, ResourceBundle rb) {
        primaryLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:53;");
        try {
            box.getChildren().add(FXMLLoader.load(getClass().getResource("/sample/view/fxml/sidePanel/SidePanel.fxml")));
        }catch (IOException ex){
            Logger.getLogger(ManagePostController.class.getName()).log(Level.SEVERE,null,ex);
        }
        /* catch relative post from method */
        managePostVBox.setPadding(new Insets(20, 20, 20, 20));
        managePostVBox.setSpacing(20);

        try {
            getOwnAndJoinPost();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getOwnAndJoinPost() throws IOException
    {
        postData.clear();
        boolean success = true;

        //表單格式皆合法
        if(success){
            try {
                HttpResponse response = RequestController.post("http://127.0.0.1:13261/posts/getOwnAndJoinPost",
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

                    if(jsonResponse.errors.length==0){
                        String ownPosts = "";
                        for(String postInfo:jsonResponse.ownPost){
                            postData.add(postInfo);
                        }
                        renderAllPost(postData,classJsonResponse.ownPost.length);
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



    public void renderAllPost(List<String> postData,int postsQuantity) throws IOException {
        //creator,category,price,postID,joinPeopleCount,joinPeopleName1,....
        /* WARNING!!!! postVBox just can have one VBox inside!!! */
        /* collect information */

        postArr = new AnchorPane[postsQuantity];

        for(String tmp:postData)
        {
            deleteStatusLabel = new Label("");
            choosePeopleStatusLabel = new Label("");
            String[] dataArr = tmp.split(",");


            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/sample/view/fxml/detailCard/ManagePost.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();


            ArrayList<String> joinList = new ArrayList<String>();
            for(int i = 5;i < dataArr.length;i++)
                joinList.add(dataArr[i]);

            sample.view.detailCardController.ManagePostController itemController = fxmlLoader.getController();
            itemController.setData(dataArr[0],dataArr[1],dataArr[2],dataArr[3],dataArr[4],joinList,managePostVBox);

            managePostVBox.getChildren().add(anchorPane);
        }
    }
}