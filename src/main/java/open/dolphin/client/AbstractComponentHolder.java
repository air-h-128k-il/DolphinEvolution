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
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;

/**
 * ComponentHolder
 *
 * @author  Kazushi Minagawa
 */
public abstract class AbstractComponentHolder extends JLabel {  //implements MouseListener, MouseMotionListener {
    
    protected static final Color SELECTED_BORDER = new Color(255, 0, 153);
    
    /** Creates new ComponentHolder */
    public AbstractComponentHolder() {
        
        this.putClientProperty("karteCompositor", AbstractComponentHolder.this);
        this.setFocusable(true);
        
        // Double Click
        this.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // StampEditor から戻った後に動作しないため
                boolean focus = requestFocusInWindow();

                if (!focus) {
                    requestFocus();
                }

                if (e.getClickCount()==2 && (!e.isPopupTrigger())) {
                    edit();
                }
            }
        });
        
        // Dragg
        DragDetect dt = new DragDetect();
        this.addMouseListener(dt);
        this.addMouseMotionListener(dt);
        
        // Popup
        this.addMouseListener(new PopupListner());
        
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        ActionMap map = this.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
    }
    
    class DragDetect implements MouseListener, MouseMotionListener {
    
        private MouseEvent firstMouseEvent;
        
        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }

        @Override
        public void mousePressed(MouseEvent e) {
            firstMouseEvent = e;
            e.consume();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            if (firstMouseEvent != null) {

                e.consume();

                //If they are holding down the control key, COPY rather than MOVE
                int ctrlMask = InputEvent.CTRL_DOWN_MASK;
                int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask)
                ? TransferHandler.COPY
                        : TransferHandler.MOVE;

                int dx = Math.abs(e.getX() - firstMouseEvent.getX());
                int dy = Math.abs(e.getY() - firstMouseEvent.getY());

                if (dx > 5 || dy > 5) {
                    JComponent c = (JComponent) e.getSource();
                    TransferHandler handler = c.getTransferHandler();
                    handler.exportAsDrag(c, firstMouseEvent, action);
                    firstMouseEvent = null;
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) { }

        @Override
        public void mouseClicked(MouseEvent me) {}
    }
    
    public abstract void edit();
    
    class PopupListner extends MouseAdapter {
        
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getClickCount()!=2) {
                mabeShowPopup(e);
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount()!=2) {
                mabeShowPopup(e);
            }
        }
    }
    
    public abstract void mabeShowPopup(MouseEvent e);
}