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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;

/**
 * 2号カルテクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class KarteViewer2 extends KarteViewer {
    
    /**
     * Creates new KarteViewer
     */
    public KarteViewer2() {
    }
    
    @Override
    public int getActualHeight() {
        try {
            JTextPane pane = soaPane.getTextPane();
            int pos = pane.getDocument().getLength();
            Rectangle r = pane.modelToView(pos);
            int hsoa = r.y;
            
            pane = pPane.getTextPane();
            pos = pane.getDocument().getLength();
            r = pane.modelToView(pos);
            int hp = r.y;
            
            return Math.max(hsoa, hp);
            
        } catch (BadLocationException ex) {
            ex.printStackTrace(System.err);
        }
        return 0;
    }
    
    @Override
    public void adjustSize() {
        int h = getActualHeight();
        int soaWidth = soaPane.getTextPane().getPreferredSize().width;
        int pWidth = pPane.getTextPane().getPreferredSize().width;
        soaPane.getTextPane().setPreferredSize(new Dimension(soaWidth, h));
        pPane.getTextPane().setPreferredSize(new Dimension(pWidth, h));
    }
    
    /**
     * P Pane を返す。
     * @return pPane
     */
    public KartePane getPPane() {
        return pPane;
    }
    
    /**
     * ２号カルテで初期化する。
     */
    private void initialize() {
        
        KartePanel2M kp2 = new KartePanel2M();
        panel2 = kp2;
        
        // TimeStampLabel を生成する
        timeStampLabel = kp2.getTimeStampLabel();
        timeStampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeStampLabel.setForeground(timeStampFore);
        timeStampLabel.setFont(timeStampFont);
        
//s.oh^ 2013/01/29 過去カルテの修正操作(選択状態)
        //kp2.getTimeStampPanel().setBackground(KarteDocumentViewer.DEFAULT_BGCOLOR);
        //timeStampLabel.setBackground(KarteDocumentViewer.DEFAULT_BGCOLOR);
        //timeStampLabel.setForeground(KarteDocumentViewer.DEFAULT_FGCOLOR);
//s.oh$
        
        // SOA Pane を生成する
        soaPane = new KartePane();
        soaPane.setTextPane(kp2.getSoaTextPane());
        soaPane.setRole(IInfoModel.ROLE_SOA);
        if (model != null) {
            // Schema 画像にファイル名を付けるのために必要
            String docId = model.getDocInfoModel().getDocId();
            soaPane.setDocId(docId);
        }
        
        // P Pane を生成する
        pPane = new KartePane();
        pPane.setTextPane(kp2.getPTextPane());
        pPane.setRole(IInfoModel.ROLE_P);
    
//s.oh^ 2014/01/27 スタンプのテキストコピー機能拡張
        if(getContext() != null && getContext().getPatient() != null) {
            soaPane.setPatID(getContext().getPatient().getPatientId());
            pPane.setPatID(getContext().getPatient().getPatientId());
        }
//s.oh$
        
        setUI(kp2);
    }
    
    /**
     * プログラムを開始する。
     */
    @Override
    public void start() {
        
        // Creates GUI
        this.initialize();
        
        // Model を表示する
        if (this.getModel() != null) {
            
            // 確定日を分かりやすい表現に変える
            String timeStamp = ModelUtils.getDateAsFormatString(
                    model.getDocInfoModel().getFirstConfirmDate(),
                    IInfoModel.KARTE_DATE_FORMAT);
            
            if (model.getDocInfoModel().getStatus().equals(IInfoModel.STATUS_TMP)) {
                // (予定カルテ対応)
                Color bkColor = model.getDocInfoModel().isScheduled() ? GUIConst.SCHEDULE_KARTE_BK_COLOR : GUIConst.TEMP_SAVE_KARTE_BK_COLOR;
                Color foreColor = model.getDocInfoModel().isScheduled() ? GUIConst.SCHEDULE_KARTE_FORE_COLOR : GUIConst.TEMP_SAVE_KARTE_FORE_COLOR;
                StringBuilder sb = new StringBuilder();
                sb.append(timeStamp);
                sb.append(UNDER_TMP_SAVE);
                timeStamp = sb.toString();
                // 背景が DarkBlue、foreを白にする
                KartePanel2M kp2 = (KartePanel2M)panel2;
                kp2.getTimeStampPanel().setOpaque(true);
                // (予定カルテ対応)
                //kp2.getTimeStampPanel().setBackground(GUIConst.TEMP_SAVE_KARTE_COLOR);
                //timeStampLabel.setOpaque(true);
                //timeStampLabel.setBackground(GUIConst.TEMP_SAVE_KARTE_COLOR);
                //timeStampLabel.setForeground(Color.WHITE);
                kp2.getTimeStampPanel().setBackground(bkColor);
                timeStampLabel.setOpaque(true);
                timeStampLabel.setBackground(bkColor);
                timeStampLabel.setForeground(foreColor);
            }
            if (model.getUserModel().getCommonName()!=null) {
                StringBuilder sb = new StringBuilder();
                sb.append(timeStamp).append(" ").append(model.getUserModel().getCommonName());
                timeStamp = sb.toString();
            }
            timeStampLabel.setText(timeStamp);
            KarteRenderer2 renderer = new KarteRenderer2(soaPane, pPane);
            renderer.render(model);
        }
        
        // モデル表示後にリスナ等を設定する
        ChartMediator mediator = getContext().getChartMediator();
        soaPane.init(false, mediator);
        pPane.init(false, mediator);

        // 自分でエンターしている
        enter();
    }

    @Override
    public void enter() {
        super.enter();
        boolean sendOk = true;
        sendOk = sendOk && (getContext().isSendClaim());
        sendOk = sendOk && (model!=null);
        sendOk = sendOk && (model!=null && model.getDocInfoModel().getDocType().equals(IInfoModel.DOCTYPE_KARTE)); // karte のみ
        sendOk = sendOk && (model!=null && (!model.getDocInfoModel().getStatus().equals(IInfoModel.STATUS_TMP))); // 仮保存でないこと
        ChartMediator mediator = getContext().getChartMediator();
        mediator.getAction(GUIConst.ACTION_SEND_CLAIM).setEnabled(sendOk);
    }
    
    @Override
    public void stop() {
        soaPane.clear();
        pPane.clear();
    }
    
    @Override
    public void addMouseListener(MouseListener ml) {
        soaPane.getTextPane().addMouseListener(ml);
        pPane.getTextPane().addMouseListener(ml);
    }
}