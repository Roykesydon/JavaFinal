package sample;


import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import sample.global.GlobalVariable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminPageController implements Initializable {

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

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
            Logger.getLogger(AdminPageController.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
}
