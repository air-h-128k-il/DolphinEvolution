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
package open.dolphin.impl.schema;

import java.awt.*;
import javax.swing.JComponent;

/**
 * SchemaCanvas
 *
 * @author Kazushi Minagawa
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class SchemaCanvas extends JComponent {

    private Image srcImg;
    private SchemaEditorImpl controller;
    private Insets margin;

    public SchemaCanvas() {
    }

    public SchemaCanvas(Image srcImg, Insets margin) {
        this();
        this.srcImg = srcImg;
        this.margin = margin;
        if (srcImg != null) {
            int width = srcImg.getWidth(null);
            int height = srcImg.getHeight(null);
            if (margin != null) {
                width = margin.left + width + margin.right;
                height = margin.top + height + margin.bottom;
            }
            this.setPreferredSize(new Dimension(width, height));
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (getSourceImg() != null) {
            int iWidth = getSourceImg().getWidth(null);
            int iHeight = getSourceImg().getHeight(null);
            int cx = margin.left;
            int cy = margin.top;
            g2d.drawImage(getSourceImg(), cx, cy, iWidth, iHeight, this);
        }

        if (getController() != null) {
            getController().draw(g2d);
        }
    }

    public SchemaEditorImpl getController() {
        return controller;
    }

    public void setController(SchemaEditorImpl controller) {
        this.controller = controller;
    }

    public Image getSourceImg() {
        return srcImg;
    }

    public void setSourceImg(Image srcImg) {
        this.srcImg = srcImg;
        if (srcImg != null) {
            this.setPreferredSize(new Dimension(srcImg.getWidth(null), srcImg.getHeight(null)));
        }
        this.repaint();
    }
}
