package sample;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.response.posts.getAllPostResponse;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PublicPageController implements Initializable {

    public ScrollPane postsScroll;
    public AnchorPane anchorpane;
    public VBox postVBox;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    public Label getAllPostResult;
    private Label[] postLabelArr;

    public void initialize(URL url, ResourceBundle rb) {
        HamburgerBackArrowBasicTransition burgerTask2 = new HamburgerBackArrowBasicTransition(hamburger);
        try {
            VBox box = FXMLLoader.load(getClass().getResource("fxml/SidePanel.fxml"));
            if(GlobalVariable.isAdmin != false) {
                box = FXMLLoader.load(getClass().getResource("fxml/AdminSidePanel.fxml"));
                System.out.println("admin");
            }
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
            Logger.getLogger(PublicPageController.class.getName()).log(Level.SEVERE,null,ex);
        }

        try {
            getAllPost();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getAllPost() throws IOException
    {
        try {
            HttpResponse response = RequestController.post("http://127.0.0.1:13261/posts/getAllPost",null
            );
            String responseString = EntityUtils.toString(response.getEntity());

            String errorsResult = "";
            int errorsResultCount = 0;

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Gson gson = new Gson();
                getAllPostResponse jsonResponse = gson.fromJson(responseString, getAllPostResponse.class);
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
                        posts += postInfo;
                        posts += ',';
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

    public Button makeButton(String name, String id)  {
        Button button = new Button(name);
        button.setId(id);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> buttonFunction(button.getId()));
        return button;
    }

    public void buttonFunction(String tmp)
    {
        //Write joinButton's function here
        System.out.println("You Clicked " + tmp + " from PublicPage");
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
            Button tmpBut = makeButton("我要跟這團！",aaa.getId() + "Button");
            postVBox.getChildren().addAll(aaa,tmpBut);
        }
    }
}
