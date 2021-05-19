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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.response.posts.getProfileAndOwnPostResponse;
import sample.response.setIdentityCodeResponse;

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
    private getProfileAndOwnPostResponse classJsonResponse;
    private Label deleteStatusLabel;
    private Label choosePeopleStatusLabel;
    private List<String> postData = new ArrayList<>();


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
                    getProfileAndOwnPostResponse jsonResponse = gson.fromJson(responseString, getProfileAndOwnPostResponse.class);
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

    public Button makeButton(String name, String id, String postID)  {
        Button button = new Button(name);
        button.setId(id);
        if(name.equals("我要選這些人！"))
        {
            button.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    e -> choosePeopleFunction(postID));
        }
        else if(name.equals("刪除此貼文！"))
        {
            button.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    e -> deletePeopleFunction(postID));
        }
        else
            System.out.println("字串比對錯誤(ManagePostController makeButton method)");
        return button;
    }

    public JFXCheckBox makeCheckBox(String joinPeopleID,String postID)  {
        JFXCheckBox checkBox = new JFXCheckBox(joinPeopleID);
        checkBox.setId(postID);
        return checkBox;
    }

    public void deletePeopleFunction(String postID)
    {
        try {
            HttpResponse response=RequestController.post("http://localhost:13261/posts/deletePost",
                    new String[]{"accessKey",GlobalVariable.accessKey},
                    new String[]{"postID",postID}
            );
            String responseString= EntityUtils.toString(response.getEntity());
            if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                Gson gson =new Gson();
                setIdentityCodeResponse gsonResponse = gson.fromJson(responseString,setIdentityCodeResponse.class);
                if(Arrays.toString(gsonResponse.errors)=="[]"){
                    managePostVBox.getChildren().clear();
                    getOwnAndJoinPost();
                }
                else{
                    deleteStatusLabel.setText("失敗!");
                    System.out.println("failed");
                }
            }
            else{
                System.out.println(response.getStatusLine().getStatusCode());
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void choosePeopleFunction(String postID)
    {
        String tmpList = "";
        String chooseList = "";
        /* manageBox have many postVBox. PostVBox has many checkBox */
        for( Node element : managePostVBox.getChildren())
        {
            if(element instanceof VBox)
                for( Node postVBox : ((VBox) element).getChildren())
                {
                    if(postVBox instanceof VBox)
                        for(Node checkBox : ((VBox) postVBox).getChildren())
                        {
                            if(checkBox instanceof JFXCheckBox)
                                if(checkBox.getId().equals(postID))
                                    if(((JFXCheckBox) checkBox).isSelected())
                                    {
                                        tmpList += ((JFXCheckBox) checkBox).getText();
                                        tmpList += ",";
                                    }
                        }
                }
        }
        if(!tmpList.equals(""))
        {
            /* remove last ',' */
            if(tmpList.charAt(tmpList.length() - 1) == ',')
                chooseList = tmpList.substring(0,tmpList.length() - 1);
            try {
                HttpResponse response=RequestController.post("http://localhost:13261/posts/completePost",
                        new String[]{"accessKey",GlobalVariable.accessKey},
                        new String[]{"postID",postID},
                        new String[]{"chooseList",chooseList}
                );
                String responseString= EntityUtils.toString(response.getEntity());
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                    Gson gson =new Gson();
                    setIdentityCodeResponse gsonResponse = gson.fromJson(responseString,setIdentityCodeResponse.class);
                    if(Arrays.toString(gsonResponse.errors)=="[]"){
                        managePostVBox.getChildren().clear();
                        getOwnAndJoinPost();
                    }
                    else{
                        deleteStatusLabel.setText("失敗!");
                        System.out.println("failed");
                    }
                }
                else{
                    System.out.println(response.getStatusLine().getStatusCode());
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void renderAllPost(List<String> postData,int postsQuantity)
    {
        //creator,category,price,postID,joinPeopleCount,joinPeopleName1,....
        /* WARNING!!!! postVBox just can have one VBox inside!!! */
        /* collect information */
        String creator = "",category = "",price = "",postID = "",joinPeopleCount = "";
        for(String tmp:postData)
        {
            VBox postVBox = new VBox();//put one post in this VBox
            VBox checkBoxVBox = new VBox();
            deleteStatusLabel = new Label("");
            choosePeopleStatusLabel = new Label("");
            String tmpData = "";
            String[] dataArr = tmp.split(",");
            for(int i = 0;i<5;i++)
            {
                if(i==0)
                    creator = dataArr[i];
                else if(i==1)
                    category = dataArr[i];
                else if(i==2)
                    price = dataArr[i];
                else if(i==3)
                    postID = dataArr[i];
                else if(i==4)
                    joinPeopleCount = dataArr[i];
            }

            /* add node to scene */
            tmpData += "發文者:" + creator + " 商品種類:" + category+ " 價錢(尚未平分):" + price + " 已加入人數:" + joinPeopleCount;
            if(dataArr.length > 5)
            {
                tmpData +="\n";
                tmpData += "已加入的人:";
                for(int i = 5;i < dataArr.length;i++)
                {
                    JFXCheckBox tmpCheckBox = makeCheckBox(dataArr[i],postID);
                    checkBoxVBox.getChildren().add(tmpCheckBox);
                    tmpData += dataArr[i] + " ";
                }
            }
            Label dataLabel = new Label(tmpData);
            dataLabel.setStyle("-fx-background-color: rgba(70,230,140,255);-fx-font-size: 30");
            //managePostVBox.getChildren().add(dataLabel);
            postVBox.getChildren().add(dataLabel);
            //managePostVBox.getChildren().add(checkBoxVBox);
            postVBox.getChildren().add(checkBoxVBox);
            System.out.println();
            if(creator.equals(GlobalVariable.userID))
            {
                Button choosePeopleButton = makeButton("我要選這些人！",postID + "chooseButton",postID);
                Button deletePeopleButton = makeButton("刪除此貼文！",postID + "deleteButton",postID);
                postVBox.getChildren().addAll(choosePeopleButton,deletePeopleButton,deleteStatusLabel,choosePeopleStatusLabel);
            }
            managePostVBox.getChildren().add(postVBox);
        }
    }
}