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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.project.Project;

/**
 * SaveDialog
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SaveDialog {
    
    private static final String[] PRINT_COUNT = {
        "0", "1",  "2",  "3",  "4", "5"
    };
    
    private static final String[] TITLE_LIST = {"経過記録", "処方", "処置", "検査", "画像", "指導"};
    
    private static final String TITLE = "ドキュメント保存";
    private static final String SAVE = "保存";
    private static final String TMP_SAVE = "仮保存";
    
    private JCheckBox patientCheck;
    private JCheckBox clinicCheck;
    
    // 保存ボタン
    private JButton okButton;
    
    // キャンセルボタン
    private JButton cancelButton;
    
    // 仮保存ボタン
    private JButton tmpButton;
    
    private JTextField titleField;
    private JComboBox titleCombo;
    private JComboBox printCombo;
    private JLabel departmentLabel;
    
    // CLAIM 送信
    private JCheckBox sendClaim;

    // LabTest 送信
    private JCheckBox sendLabtest;
    
    // 戻り値のSaveParams/
    private SaveParams value;
    
    // ダイアログ
    private JDialog dialog;
    
    /** 
     * Creates new OpenKarteDialog  
     */
    public SaveDialog(Window parent) {
        
        JPanel contentPanel = createComponent();
        
        Object[] options = new Object[]{okButton, tmpButton, cancelButton};
        
        JOptionPane jop = new JOptionPane(
                contentPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                okButton);
        
        dialog = jop.createDialog(parent, ClientContext.getFrameTitle(TITLE));
    }
    
    public void start() {
        dialog.setVisible(true);
    }
    
    public SaveParams getValue() {
        return value;
    }
    
    /**
     * コンポーネントにSaveParamsの値を設定する。
     */
    public void setValue(SaveParams params) {
        
        // Titleを表示する
        String val = params.getTitle();
        if (val != null && (!val.equals("") &&(!val.equals("経過記録")))) {
            titleCombo.insertItemAt(val, 0);
        }
        titleCombo.setSelectedIndex(0);
        
        // 診療科を表示する
        // 受付情報からの診療科を設定する
        val = params.getDepartment();
        if (val != null) {
            String[] depts = val.split("\\s*,\\s*");
            if (depts[0] != null) {
                departmentLabel.setText(depts[0]);
            } else {
                departmentLabel.setText(val);
            }
        }
        
        // 印刷部数選択
        int count = params.getPrintCount();
        if (count != -1) {
            printCombo.setSelectedItem(String.valueOf(count));
            
        } else {
            printCombo.setEnabled(false);
        }

        //--------------------------------
        // CLAIM 送信をチェックする
        //--------------------------------
        sendClaim.setSelected(params.isSendClaim());

        //-------------------------------
        // MML 送信の場合、アクセス権を設定する
        //-------------------------------
        if (params.getSendMML()) {
            // 患者への参照と診療歴のある施設の参照許可を設定する
            boolean permit = params.isAllowPatientRef();
            patientCheck.setSelected(permit);
            permit = params.isAllowClinicRef();
            clinicCheck.setSelected(permit);
            
        } else {
            // MML 送信をしないときdiasbleにする
            patientCheck.setEnabled(false);
            clinicCheck.setEnabled(false);
        }

        //--------------------------------
        // 送信機能の自体の enable/disable
        //--------------------------------
        boolean send = params.isSendEnabled();
        sendClaim.setEnabled(send);

        //-------------------------------
        // 検体検査オーダー送信
        //-------------------------------
        sendLabtest.setSelected(params.isSendLabtest() && params.isHasLabtest());
        sendLabtest.setEnabled((send && params.isHasLabtest()));
        
        checkTitle();
    }

    
    /**
     * GUIコンポーネントを初期化する。
     */
    private JPanel createComponent() {
                
        // content
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(0, 1));
        
        // 文書Title
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleCombo = new JComboBox(TITLE_LIST);
        titleCombo.setPreferredSize(new Dimension(220, titleCombo.getPreferredSize().height));
        titleCombo.setMaximumSize(titleCombo.getPreferredSize());
        titleCombo.setEditable(true);
        p.add(new JLabel("タイトル:"));
        p.add(titleCombo);
        content.add(p);
        
        // ComboBox のエディタコンポーネントへリスナを設定する
        titleField = (JTextField) titleCombo.getEditor().getEditorComponent();
        titleField.addFocusListener(AutoKanjiListener.getInstance());
        titleField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkTitle();
            }
        });
        
        // 診療科、印刷部数を表示するラベルとパネルを生成する
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        departmentLabel = new JLabel();
        p1.add(new JLabel("診療科:"));
        p1.add(departmentLabel);
        
        p1.add(Box.createRigidArea(new Dimension(11, 0)));
        
        // Print
        printCombo = new JComboBox(PRINT_COUNT);
        printCombo.setSelectedIndex(1);
        p1.add(new JLabel("印刷部数:"));
        p1.add(printCombo);
        
        content.add(p1);
        
        // AccessRightを設定するボタンとパネルを生成する
        patientCheck = new JCheckBox("患者に参照を許可する");
        clinicCheck = new JCheckBox("診療歴のある病院に参照を許可する");
        
        //---------------------------
        // CLAIM 送信ありなし
        //---------------------------
        sendClaim = new JCheckBox("診療行為を送信する（仮保存の場合は送信しない）");
        JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p5.add(sendClaim);
        content.add(p5);

        //---------------------------
        // 検体検査オーダー送信ありなし
        //---------------------------
        sendLabtest = new JCheckBox("検体検査オーダー（仮保存の場合はしない）");
        if (Project.getBoolean(Project.SEND_LABTEST)) {
            JPanel p6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p6.add(sendLabtest);
            content.add(p6);
        }

        // OK button
        okButton = new JButton(SAVE);
        okButton.setToolTipText("診療行為の送信はチェックボックスに従います。");
        okButton.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "doOk"));
        okButton.setEnabled(false);
        
        // Cancel Button
//minagawa^        (予定カルテ対応)
//        String buttonText =  (String)UIManager.get("OptionPane.cancelButtonText");
        String buttonText =  GUIFactory.getCancelButtonText();
//minagawa$        
        cancelButton = new JButton(buttonText);
        cancelButton.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "doCancel"));
        
        // 仮保存 button
        tmpButton = new JButton(TMP_SAVE);
        tmpButton.setToolTipText("診療行為は送信しません。");
        tmpButton.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "doTemp"));
        tmpButton.setEnabled(false);
        
        return content;
    }
    
    /**
     * タイトルフィールドの有効性をチェックする。
     */
    public void checkTitle() {    
        boolean enabled = titleField.getText().trim().equals("") ? false : true;
        okButton.setEnabled(enabled);
        tmpButton.setEnabled(enabled);
    }
    
    
    /**
     * GUIコンポーネントから値を取得し、saveparamsに設定する。
     */
    public void doOk() {
        
        // 戻り値のSaveparamsを生成する
        value = new SaveParams();
        
        // 文書タイトルを取得する
        String val = (String) titleCombo.getSelectedItem();
        if (! val.equals("")) {
            value.setTitle(val);
        } else {
            value.setTitle("経過記録");
        }
        
        // Department
        val = departmentLabel.getText();
        value.setDepartment(val);
        
        // 印刷部数を取得する
        int count = Integer.parseInt((String)printCombo.getSelectedItem());
        value.setPrintCount(count);
        
        //-------------------
        // CLAIM 送信
        //-------------------
        value.setSendClaim(sendClaim.isSelected());
        
        // 患者への参照許可を取得する
        boolean b = patientCheck.isSelected();
        value.setAllowPatientRef(b);
        
        // 診療歴のある施設への参照許可を設定する
        b = clinicCheck.isSelected();
        value.setAllowClinicRef(b);

        //-------------------
        // LabTest 送信
        //-------------------
        value.setSendLabtest(sendLabtest.isSelected());
        
        close();
    }
    
      
    /**
     * 仮保存の場合のパラメータを設定する。
     */
    public void doTemp() {
        
        // 戻り値のSaveparamsを生成する
        value = new SaveParams();
        
        //------------------------
        // 仮保存であることを設定する
        //------------------------
        value.setTmpSave(true);
        
        // 文書タイトルを取得する
        String val = (String) titleCombo.getSelectedItem();
        if (! val.equals("")) {
            value.setTitle(val);
        }
        
        // Department
        val = departmentLabel.getText();
        value.setDepartment(val);
        
        //-----------------------
        // 印刷部数を取得する
        // 仮保存でも印刷するかも知れない
        //-----------------------
        int count = Integer.parseInt((String)printCombo.getSelectedItem());
        value.setPrintCount(count);
        
        //-----------------------
        // CLAIM 送信
        //-----------------------
        value.setSendClaim(false);
        
        // 患者への参照許可を取得する
        boolean b = false;
        value.setAllowPatientRef(b);
        
        // 診療歴のある施設への参照許可を設定する
        b = false;
        value.setAllowClinicRef(b);

        //-------------------
        // LabTest 送信
        //-------------------
        value.setSendLabtest(false);
        
        close();
    }
    
    /**
     * キャンセルしたことを設定する。
     */
    public void doCancel() {
        value = null;
        close();
    }
    
    private void close() {
        dialog.setVisible(false);
        dialog.dispose();
    }
}