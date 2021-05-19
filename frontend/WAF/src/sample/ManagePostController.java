package sample;


import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.postController.PublicPostController;
import sample.response.posts.GetProfileAndOwnPostResponse;
import sample.response.SetIdentityCodeResponse;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagePostController implements Initializable {

    public VBox managePostVBox;
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
        HamburgerBackArrowBasicTransition burgerTask2 = new HamburgerBackArrowBasicTransition(hamburger);
        try {
            VBox box = FXMLLoader.load(getClass().getResource("fxml/SidePanel.fxml"));
            if(GlobalVariable.isAdmin)
                box = FXMLLoader.load(getClass().getResource("fxml/AdminSidePanel.fxml"));
            drawer.setSidePane(box);
            if(GlobalVariable.userEnterFirstTime) {
                drawer.close();
                GlobalVariable.userEnterFirstTime = false;
                burgerTask2.setRate(-1);
            }
            else {
                burgerTask2.setRate(1);
                burgerTask2.play();
                drawer.open();
            }
            hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
                burgerTask2.setRate(burgerTask2.getRate() * -1);
                burgerTask2.play();

                if (drawer.isOpened())
                    drawer.close();
                else
                    drawer.open();
            });
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
            VBox postVBox = new VBox();//put one post in this VBox
            VBox checkBoxVBox = new VBox();
            deleteStatusLabel = new Label("");
            choosePeopleStatusLabel = new Label("");
            String tmpData = "";
            String[] dataArr = tmp.split(",");


            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/sample/fxml/posts/ManagePost.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();


            ArrayList<String> joinList = new ArrayList<String>();
            for(int i = 5;i < dataArr.length;i++)
                joinList.add(dataArr[i]);

            sample.postController.ManagePostController itemController = fxmlLoader.getController();
            itemController.setData(dataArr[0],dataArr[1],dataArr[2],dataArr[3],dataArr[4],joinList,managePostVBox);

            managePostVBox.getChildren().add(anchorPane);
        }
    }
}