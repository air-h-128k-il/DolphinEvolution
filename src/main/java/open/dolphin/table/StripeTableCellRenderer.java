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
package open.dolphin.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javafx.application.Platform;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import open.dolphin.client.ClientContext;

/**
 * ストライプテーブルのセルレンダラ
 *
 * @author masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class StripeTableCellRenderer extends DefaultTableCellRenderer {

    private static final Border emptyBorder = BorderFactory.createEmptyBorder();
    private static final Color DEFAULT_ODD_COLOR = ClientContext.getColor("color.odd");
    private static final Color DEFAULT_EVEN_COLOR = ClientContext.getColor("color.even");
    private static final Color[] ROW_COLORS = {DEFAULT_EVEN_COLOR, DEFAULT_ODD_COLOR};
    private static final int ROW_HEIGHT = 18;

    private JTable table;

    public StripeTableCellRenderer() {
        super();
    }

    public StripeTableCellRenderer(JTable table) {
        super();
        setTable(table);
    }

    public final void setTable(JTable table) {
        this.table = table;
        table.setRowHeight(ROW_HEIGHT);
        table.setFillsViewportHeight(true);   // viewportは広げておく
        //table.setShowVerticalLines(false);
        //table.setShowHorizontalLines(false);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setUI(new StripeTableUI());
    }

    public void setDefaultRenderer() {
        table.setDefaultRenderer(Object.class, this);
    }

    // 選択・非選択の色分けはここでする。特に指定したいときは後で上書き
    // ストライプはStripeTableUIが描画する
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        setOpaque(true);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
            ((JComponent) table.getDefaultRenderer(Boolean.class)).setOpaque(true);
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
            ((JComponent) table.getDefaultRenderer(Boolean.class)).setOpaque(false);
        }

        // 選択したときにcellに枠がつくのを消す。Nimbusでは効果がないｗ
        this.setBorder(emptyBorder);

        return this;
    }

    // テーブルにストライプの背景を描く
    // http://explodingpixels.wordpress.com/2008/10/05/making-a-jtable-fill-the-view-without-extension/
    // を改変。popupやtooltip表示後乱れるのを修正
    private static class StripeTableUI extends BasicTableUI {

        @Override
        public void paint(Graphics g, JComponent c) {
//minagawa^ 2013/04/22 残像残る対応
//            // get the row index at the top of the clip bounds (the first row to paint).
//            int rowAtPoint = table.rowAtPoint(g.getClipBounds().getLocation());
//            // get the y coordinate of the first row to paint. if there are no rows in the table, start
//            // painting at the top of the supplied clipping bounds.
//            int topY = rowAtPoint < 0 ? g.getClipBounds().y : table.getCellRect(rowAtPoint, 0, true).y;
//
//            // create a counter variable to hold the current row. if there are no rows in the table,
//            // start the counter at 0.
//            int currentRow = rowAtPoint < 0 ? 0 : rowAtPoint;
//            while (topY < g.getClipBounds().y + g.getClipBounds().height) {
//                int bottomY = topY + table.getRowHeight();
//                g.setColor(ROW_COLORS[currentRow & 1]);
//                g.fillRect(g.getClipBounds().x, topY, g.getClipBounds().width, bottomY);
//                topY = bottomY;
//                currentRow++;
//            }
//        super.paint(g, c);

            final Rectangle clipBounds = g.getClipBounds();
            final int rowHeight = table.getRowHeight();
            final int endY = clipBounds.y + clipBounds.height;

            int topY = clipBounds.y;
            int currentRow = topY / rowHeight;
            int height = rowHeight - topY % rowHeight;

            while (topY < endY) {
                int bottomY = topY + height;
                g.setColor(ROW_COLORS[currentRow & 1]);
                g.fillRect(clipBounds.x, topY, clipBounds.width, Math.min(bottomY, endY));
                topY = bottomY;
                height = rowHeight;
                currentRow++;
            }
            super.paint(g, c);
//minagawa$
        }
    }
}
