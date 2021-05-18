package sample;


import com.google.gson.Gson;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.response.posts.getProfileAndOwnPostResponse;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagePostController implements Initializable {

    public VBox managePostVBox;
    public GridPane checkBoxPane;
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;
    private Label[] postLabelArr;
    private String[] testingJoinUser = {"A","B","C","D","E","F","G","H","I","J"};
    JFXCheckBox tmpCheckBox[] = new JFXCheckBox[10];
    List<JFXCheckBox> checkBoxList = new ArrayList<>();
    private String classOwnPost;
    private getProfileAndOwnPostResponse classJsonResponse;
    private List<Integer> countJoinUser = new ArrayList<>();


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
            Logger.getLogger(ManagePostController.class.getName()).log(Level.SEVERE,null,ex);
        }
        /* catch relative post from method */
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
                            ownPosts += postInfo;
                            ownPosts += "=";
                        }
                        //create class variable to use renderAllPost method in update scene
                        classOwnPost = ownPosts;
                        renderAllPost(classOwnPost,classJsonResponse.ownPost.length);
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

    public JFXCheckBox makeCheckBox(String name,String id)  {
        JFXCheckBox checkBox = new JFXCheckBox(name);
        checkBox.setId(id + "'s " + name + " CheckBox");
        return checkBox;
    }

    public void buttonFunction(String tmp)
    {
        boolean hasSelected = false;
        Iterator<JFXCheckBox> checkIterator = checkBoxList.iterator();
        //Write DeleteButton's function here
        while (checkIterator.hasNext())
        {
            JFXCheckBox tmpCheckBox = checkIterator.next();
            if(tmpCheckBox.isSelected())
            {
                /* delete people here */
                if(!hasSelected)
                    hasSelected = true;
                System.out.println(tmpCheckBox);
                checkIterator.remove();
            }
        }
        if(hasSelected)
        {
            managePostVBox.getChildren().clear();
            renderAllPost(classOwnPost,classJsonResponse.ownPost.length);
        }
        /* check checkBoxList */
        for(JFXCheckBox checkBox2:checkBoxList)
            System.out.println(checkBox2.getId());
    }

    public void renderAllPost(String posts,int postsQuantity)
    {
        int postCount = 0;
        int count = 0;
        postLabelArr = new Label[postsQuantity];
        String tmp = "";
        String postID = "";
        for (String retval: posts.split("="))
        {
//            count++;
//            if(count == 5)
//            {
//                tmp += "\n";
//                Label tmpLabel = new Label(tmp);
//                tmpLabel.setFont(new Font(18));
//                tmpLabel.setId(postID);
//                postLabelArr[postCount] = tmpLabel;
//                tmp = "";
//                postCount++;
//                count = 0;
//            }
//            else
//            {
//                if(count == 4)
//                    postID = retval;
//                tmp += retval + " ";
//            }
            Label tmpLabel = new Label(retval+"\n");
            tmpLabel.setFont(new Font(18));
            tmpLabel.setId(postID);
            postLabelArr[postCount++]=tmpLabel;
        }
        for(Label aaa:postLabelArr)
        {
            managePostVBox.getChildren().add(aaa);
            //checkBoxPane = new GridPane();
            for(int i = 0;i < testingJoinUser.length;i++)
            {
                countJoinUser.add(testingJoinUser.length);
                tmpCheckBox[i] = makeCheckBox(testingJoinUser[i],aaa.getId());
                checkBoxList.add(tmpCheckBox[i]);
                //System.out.println(tmpCheckBox[i].getId());
                //checkBoxPane.add(tmpCheckBox[i],i + 1,0);
                managePostVBox.getChildren().add(tmpCheckBox[i]);
            }
            //managePostVBox.getChildren().add(checkBoxPane);
        }
        Button tmpBut = makeButton("我要選這些人！","ChoosePeopleButton");
        managePostVBox.getChildren().add(tmpBut);
    }
}
