/* ------------------------------------------------------------------
 * OverlayColorEditor.java
 * 
 * Created 2009-02-26 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class OverlayColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	private static final long serialVersionUID = 1L;
	private Color currentColor;
	private JButton button;
	private JColorChooser colorChooser;
	private JDialog dialog;
	protected static final String EDIT = "edit";

	public OverlayColorEditor() {
		button = new JButton();
		button.setActionCommand(EDIT);
		button.addActionListener(this);
		button.setBorderPainted(false);

		//Set up the dialog that the button brings up.
		colorChooser = new JColorChooser();
		dialog = JColorChooser.createDialog(button, "Pick a Color", true, colorChooser, this,  null);
	}

	public void actionPerformed(ActionEvent e) {
		if (EDIT.equals(e.getActionCommand())) {
			button.setBackground(currentColor);
			colorChooser.setColor(currentColor);
			dialog.setVisible(true);
			fireEditingStopped();
		}
		else {
			currentColor = colorChooser.getColor();
		}
	}

	public Object getCellEditorValue() {
		return currentColor;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		currentColor = (Color) value;
		return button;
	}
}

