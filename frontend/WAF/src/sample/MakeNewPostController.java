package sample;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.response.loginResponse;
import sample.response.posts.MakeNewPostResponse;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MakeNewPostController implements Initializable {
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    public TextField priceTextField;
    public ComboBox categoryComboBox;
    public Label makeNewPostResult;

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
            else {
                drawer.open();
            }

            HamburgerBackArrowBasicTransition burgerTask2 = new HamburgerBackArrowBasicTransition(hamburger);
            burgerTask2.setRate(-1);
            hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
                burgerTask2.setRate(burgerTask2.getRate() * -1);
                burgerTask2.play();

                if (drawer.isOpened()) {
                    drawer.close();
                    drawer.setMinWidth(0);
                }
                else {
                    drawer.open();
                    drawer.setMinWidth(200);
                }

            });
        }catch (IOException ex){
            Logger.getLogger(ManagePostController.class.getName()).log(Level.SEVERE,null,ex);
        }

        categoryComboBox.getItems().addAll("Spotify","NintendoSwitchOnline");
    }

    public void makeNewPostRequest(ActionEvent actionEvent) throws IOException
    {
        String category = categoryComboBox.getValue().toString();
        String price = priceTextField.getText();
        boolean success = true;

        //verify submit form
        //有選擇種類
        //輸入的是數字 而且是整數 而且數字屬於合理範圍(不會溢位)
        //


        //表單格式皆合法
        if(success){
            try {
                HttpResponse response = RequestController.post("http://127.0.0.1:13261/posts/createPost",
                        new String[]{"accessKey", GlobalVariable.accessKey},
                        new String[]{"category", category},
                        new String[]{"price", price}
                );
                String responseString = EntityUtils.toString(response.getEntity());

                String errorsResult = "";
                int errorsResultCount = 0;

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    MakeNewPostResponse jsonResponse = gson.fromJson(responseString, MakeNewPostResponse.class);

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
                        makeNewPostResult.setText("成功創建貼文");
                    }
                    else{
                        makeNewPostResult.setText(errorsResult);
                    }
                } else {
                    System.out.println(response.getStatusLine());
                    makeNewPostResult.setText(errorsResult);
                }
            }
            catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }

}
