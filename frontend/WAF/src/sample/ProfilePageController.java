package sample;


import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.response.posts.getProfileAndOwnPostResponse;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfilePageController implements Initializable {

    public VBox postVBox;
    private Label[] postLabelArr;
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    public Label useridLabel,nameLabel,emailLabel,lastAccessTimeLabel,ownPostsLabel;

    public void initialize(URL url, ResourceBundle rb) {
        try {
            VBox box = FXMLLoader.load(getClass().getResource("fxml/SidePanel.fxml"));
            if(GlobalVariable.isAdmin)
                box = FXMLLoader.load(getClass().getResource("fxml/AdminSidePanel.fxml"));
            drawer.setSidePane(box);
            if(GlobalVariable.userEnterFirstTime) {
                drawer.close();
                GlobalVariable.userEnterFirstTime = false;
            }
            else
                drawer.open();

            HamburgerBackArrowBasicTransition burgerTask2 = new HamburgerBackArrowBasicTransition(hamburger);
            burgerTask2.setRate(-1);
            hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
                burgerTask2.setRate(burgerTask2.getRate() * -1);
                burgerTask2.play();

                if (drawer.isOpened())
                    drawer.close();
                else
                    drawer.open();
            });
        }catch (IOException ex){
            Logger.getLogger(ProfilePageController.class.getName()).log(Level.SEVERE,null,ex);
        }

        try {
            getProfileAndOwnPost();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getProfileAndOwnPost() throws IOException
    {

        boolean success = true;

        //表單格式皆合法
        if(success){
            try {
                HttpResponse response = RequestController.post("http://127.0.0.1:13261/posts/getProfileAndOwnPost",
                        new String[]{"accessKey", GlobalVariable.accessKey}
                );
                String responseString = EntityUtils.toString(response.getEntity());

                String errorsResult = "";
                int errorsResultCount = 0;

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    getProfileAndOwnPostResponse jsonResponse = gson.fromJson(responseString, getProfileAndOwnPostResponse.class);

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
                        useridLabel.setText(jsonResponse.userID);
                        nameLabel.setText(jsonResponse.name);
                        emailLabel.setText(jsonResponse.email);
                        lastAccessTimeLabel.setText(jsonResponse.lastAccessTime);

                        String ownPosts = "";
                        for(String postInfo:jsonResponse.ownPost){
                            ownPosts += postInfo;
                            ownPosts += ",";
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

    public Button makeButton(String name, String id)  {
        Button button = new Button(name);
        button.setId(id);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> buttonFunction(button.getId()));
        return button;
    }

    public void buttonFunction(String tmp)
    {
        //Write DeleteButton's function here
        System.out.println("You Clicked " + tmp + " from ProfilePage");
    }

    public void renderAllPost(String posts,int postsQuantity)
    {
        postLabelArr = new Label[postsQuantity];
        int postCount = 0;
        int count = 0;
        String tmp = "";
        String postID = "";
        for (String retval: posts.split(","))
        {
            count++;
            if(count == 5)
            {
                tmp += "\n";
                System.out.println(tmp);
                Label tmpLabel = new Label(tmp);
                tmpLabel.setFont(new Font(18));
                tmpLabel.setId(postID);
                postLabelArr[postCount] = tmpLabel;
                tmp = "";
                postCount++;
                count = 0;
            }
            else
            {
                if(count == 4)
                    postID = retval;
                tmp += retval + " ";
            }
        }
        for(Label aaa:postLabelArr)
        {
            Button tmpBut = makeButton("我要刪掉此團！",aaa.getId() + "Button");
            postVBox.getChildren().addAll(aaa,tmpBut);
        }
    }
}
