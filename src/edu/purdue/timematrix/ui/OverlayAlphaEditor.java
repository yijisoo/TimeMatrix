/* ------------------------------------------------------------------
 * OverlayAlphaEditor.java
 * 
 * Created 2009-02-26 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.ui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

public class OverlayAlphaEditor extends AbstractCellEditor implements TableCellEditor, ChangeListener, MouseListener {
	private static final long serialVersionUID = 1L;
	private JSlider transSlider;
	private double alpha;

	public OverlayAlphaEditor() {
		transSlider = new JSlider();
		transSlider.setMinimum(0);
		transSlider.setMaximum(100);
		transSlider.setMinorTickSpacing(1);
		transSlider.setMajorTickSpacing(10);
		transSlider.addChangeListener(this);
		transSlider.addMouseListener(this);
	}

	public Object getCellEditorValue() {
		return alpha;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		alpha = (Double) value;
		transSlider.setValue((int) (alpha * 100));
		return transSlider;
	}

	public void stateChanged(ChangeEvent e) {
		alpha = transSlider.getValue() / 100.0;
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
		fireEditingStopped();
	}	
}
