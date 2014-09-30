/* ------------------------------------------------------------------
 * OverlayAlphaRenderer.java
 * 
 * Created 2009-02-26 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.ui;

import java.awt.Component;

import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class OverlayAlphaRenderer extends JSlider implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	public OverlayAlphaRenderer() {
		setMinimum(0);
		setMaximum(100);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		double alpha = (Double) value;
		setValue((int) (alpha * 100));
		
		if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }	

		setToolTipText("Alpha " + (alpha * 100) + "%");
		return this;
	}
}