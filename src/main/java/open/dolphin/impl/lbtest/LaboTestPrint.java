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
package open.dolphin.impl.lbtest;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import javax.swing.JTable;

/**
 * ラボテストの印刷
 *
 * @author Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class LaboTestPrint implements Printable {

    private JTable table;

    /**
     * Creates a new instance of Panel2
     */
    public LaboTestPrint() {
        super();
    }

    public void setTable(JTable val) {
        table = val;
    }

    public JTable getTable() {
        return table;
    }

    public void printTable(PageFormat pageFormat, int numOfCopies, String jobName, MessageFormat header, MessageFormat footer) {

//        boolean buffered = this.isDoubleBuffered();
//        this.setDoubleBuffered(false);
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setCopies(numOfCopies);
        pj.setJobName(jobName);
        if (pageFormat == null) {
            pageFormat = pj.defaultPage();
        }
        Printable printable = getTable().getPrintable(JTable.PrintMode.FIT_WIDTH, header, footer);
        pj.setPrintable(printable, pageFormat);

        if (pj.printDialog()) {
            try {
                pj.print();
            } catch (PrinterException printErr) {
                printErr.printStackTrace(System.err);
            }
        }
//        this.setDoubleBuffered(buffered);
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {

        /*
         Graphics2D g2 = (Graphics2D) g;
         Font f = new Font("Courier", Font.ITALIC, 9);
         g2.setFont(f);
         g2.setPaint(Color.black);
         g2.setColor(Color.black);
        
         //
         int fontHeight = g2.getFontMetrics().getHeight();
         int fontDescent = g2.getFontMetrics().getDescent();
         double footerHeight = fontHeight;
         double pageHeight = pf.getImageableHeight() - footerHeight;
         double pageWidth = pf.getImageableWidth();
         //
         //        double componentHeight = height == 0 ? this.getSize().getHeight() : (double) height;
         //        double componentWidth = this.getSize().getWidth();
        
         //
         double scale = 1d;
         //        if (componentWidth >= pageWidth) {
         //            scale = pageWidth / componentWidth;// shrink
         //        }
         //
         //        double scaledComponentHeight = componentHeight*scale;
         //        int totalNumPages = (int)Math.ceil(scaledComponentHeight/pageHeight);

         //        if (pi >= totalNumPages) {
         //           return Printable.NO_SUCH_PAGE;
         //        }

         // footer
         g2.translate(pf.getImageableX(), pf.getImageableY());
         StringBuilder sb = new StringBuilder();
         if (printName) {
         sb.append(patientName);
         }
         sb.append("  Page: ");
         sb.append((pi + 1));
         sb.append(" of ");
         //        sb.append(totalNumPages);
         String footerString =  sb.toString();
         int strW = SwingUtilities.computeStringWidth(g2.getFontMetrics(), footerString);
         g2.drawString(
         footerString, 
         (int)pageWidth/2 - strW/2,
         (int)(pageHeight + fontHeight - fontDescent)
         //(int)(pageHeight + fontHeight)
         );

         // page
         g2.translate(0d, 0d);
         g2.translate(0d, - pi * pageHeight);

         //        if (pi == totalNumPages - 1) {
         //            g2.setClip(
         //                0, (int)(pageHeight * pi),
         //                (int)Math.ceil(pageWidth),
         //                (int)(scaledComponentHeight - pageHeight * (totalNumPages - 1))
         //            );
         //        } else {
         //            g2.setClip(
         //                0, (int)(pageHeight * pi),
         //                (int)Math.ceil(pageWidth),
         //                (int)Math.ceil(pageHeight)
         //            );
         //        }

         //        g2.scale(scale, scale);

         //        boolean wasBuffered = isDoubleBuffered();
         //        paint(g2);
         //        setDoubleBuffered(wasBuffered);
         */
        return Printable.PAGE_EXISTS;
    }
}
