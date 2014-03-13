/*
 * Copyright (C) 2014 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp.
 * 825 Sylk BLDG., 1-Yamashita-Cho, Naka-Ku, Kanagawa-Ken, Yokohama-City, JAPAN.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 3 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA.
 * 
 * (R)OpenDolphin version 2.4, Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp. 
 * (R)OpenDolphin comes with ABSOLUTELY NO WARRANTY; for details see the GNU General 
 * Public License, version 3 (GPLv3) This is free software, and you are welcome to redistribute 
 * it under certain conditions; see the GPLv3 for details.
 */
package open.dolphin.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.project.Project;

/**
 * DisplayCtrl Controller class
 *
 * @author chikara fujihira <fujihirach@sandi.co.jp>
 */
public class DisplayCtrlController implements Initializable {

    @FXML
    ImageView ctrlView;
    @FXML
    ToggleButton karteSw;
    @FXML
    ToggleButton karteToolCk;
    @FXML
    Text clinicName;
    
    private Stage mainStage;
    private Evolution application;

    private final int SPLIT2_HIGHT = 305;
    private final int SPLIT3_WIDTH = 481;


    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //- クリニック名取得表示
        FacilityModel facility = Project.getUserModel().getFacilityModel();
        clinicName.setText(facility.getFacilityName());
    }

    public void ctrlReleaseKey(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            ctrlView.setVisible(false);
        }
    }

    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public Stage getStage() {
        return mainStage;
    }

    public void setApp(Evolution application) {
        this.application = application;
    }

    public Evolution getApplication() {
        return application;
    }

    public void setWinCtrlReset() {
        application.evoWindow.getSplitPane2().setDividerLocation(SPLIT2_HIGHT);
        application.evoWindow.getSplitPane3().setDividerLocation(application.evoWindow.getFrame().getWidth() - SPLIT3_WIDTH);
        application.evoWindow.reFleshJFrame1();
    }

    public void setWinCtrlPvtSet() {
        application.evoWindow.getSplitPane2().setDividerLocation(application.evoWindow.getFrame().getHeight());
        application.evoWindow.getSplitPane3().setDividerLocation(0);
    }

    public void setWinCtrlKarteSet() {
        if (Boolean.valueOf(Project.getString("karte.tool"))) {
            application.evoWindow.getSplitPane2().setDividerLocation(0);
            application.evoWindow.getSplitPane3().setDividerLocation(application.evoWindow.getFrame().getWidth() - SPLIT3_WIDTH);
        } else {
            application.evoWindow.getSplitPane2().setDividerLocation(0);
            application.evoWindow.getSplitPane3().setDividerLocation(application.evoWindow.getFrame().getWidth());
        }
    }
    
    public void karteViewSw() {
        if(karteSw.isSelected()) {
            Project.setString("karte.view", "true");
        } else {
            Project.setString("karte.view", "false");
        }
    }
    
    public void setWinCtrlKarteToolSet() {
        if(karteToolCk.isSelected()) {
            Project.setString("karte.tool", "true");
        } else {
            Project.setString("karte.tool", "false");
        }
    }
}