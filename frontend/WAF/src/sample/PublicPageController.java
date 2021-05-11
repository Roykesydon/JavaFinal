package sample;


import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
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

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    public Label getAllPostResult,postsLabel;

    public void initialize(URL url, ResourceBundle rb) {
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
                    String posts = "";
                    for(String postInfo:jsonResponse.posts){
                        posts += postInfo;
                    }
                    postsLabel.setText(posts);
                }

            } else {
                System.out.println(response.getStatusLine());
            }
            getAllPostResult.setText(errorsResult);
        }
        catch (IOException  e) {
            e.printStackTrace();
        }
    }
}
