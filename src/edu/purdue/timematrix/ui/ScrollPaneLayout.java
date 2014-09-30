/* ------------------------------------------------------------------
 * ScrollPaneLayout.java
 * 
 * Created 2009-02-11 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * 
 * @author Niklas Elmqvist
 *
 */
public class ScrollPaneLayout implements LayoutManager {
	
	private Component viewport;
	private JScrollBar vsb;
	private JScrollBar hsb;
	private Component rowHead;
	private Component colHead;
	private Component lowerLeft;
	private Component lowerRight;
	private Component upperLeft;
	private Component upperRight;

	public ScrollPaneLayout() {
		// empty
	}

	public void addLayoutComponent(String key, Component component) {
		if (key == JScrollPane.VIEWPORT)
			viewport = component;
		else if (key == JScrollPane.VERTICAL_SCROLLBAR)
			vsb = (JScrollBar) component;
		else if (key == JScrollPane.HORIZONTAL_SCROLLBAR)
			hsb = (JScrollBar) component;
		else if (key == JScrollPane.ROW_HEADER)
			rowHead = component;
		else if (key == JScrollPane.COLUMN_HEADER)
			colHead = component;
		else if (key == JScrollPane.LOWER_RIGHT_CORNER)
			lowerRight = component;
		else if (key == JScrollPane.UPPER_RIGHT_CORNER)
			upperRight = component;
		else if (key == JScrollPane.LOWER_LEFT_CORNER)
			lowerLeft = component;
		else if (key == JScrollPane.UPPER_LEFT_CORNER)
			upperLeft = component;
	}

	public void layoutContainer(Container parent) {
		int x1 = 0, x2 = 0, x3 = 0, x4 = 0;
        int y1 = 0, y2 = 0, y3 = 0, y4 = 0;
	 
        Rectangle scrollPaneBounds = parent.getBounds();
	 
        x1 = scrollPaneBounds.x;
        y1 = scrollPaneBounds.y;
        x4 = scrollPaneBounds.x + scrollPaneBounds.width;
        y4 = scrollPaneBounds.y + scrollPaneBounds.height;
             
        if (colHead != null)
        	y2 = y1 + colHead.getPreferredSize().height;
        else
        	y2 = y1;
         
        if (rowHead != null)
        	x2 = x1 + rowHead.getPreferredSize().width;
        else
        	x2 = x1;
 
        x3 = x4 - vsb.getPreferredSize().width;
        y3 = y4 - hsb.getPreferredSize().height;
 
        // Set the layout
        if (viewport != null)
        	viewport.setBounds(new Rectangle(x2, y2, x3 - x2, y3 - y2));

        if (colHead != null)
        	colHead.setBounds(new Rectangle(x2, y1, x3 - x2, y2 - y1));
         
        if (rowHead != null)
        	rowHead.setBounds(new Rectangle(x1, y2, x2 - x1, y3 - y2));

        vsb.setBounds(new Rectangle(x3, y2, x4 - x3, y3 - y2));
        hsb.setBounds(new Rectangle(x2, y3, x3 - x2, y4 - y3));

        if (upperLeft != null)
        	upperLeft.setBounds(new Rectangle(x1, y1, x2 - x1, y2 - y1));
         
        if (upperRight != null)
        	upperRight.setBounds(new Rectangle(x3, y1, x4 - x3, y2 - y1));
 
        if (lowerLeft != null)
        	lowerLeft.setBounds(new Rectangle(x1, y3, x2 - x1, y4 - y3));
         
        if (lowerRight != null)
        	lowerRight.setBounds(new Rectangle(x3, y3, x4 - x3, y4 - y3));
	}

	public Dimension minimumLayoutSize(Container parent) {
		return parent.getMinimumSize();
	}

	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension(1024, 768);
	}

	public void removeLayoutComponent(Component component) {
		if (component == viewport)
			viewport = null;
		else if (component == vsb)
			vsb = null;
		else if (component == hsb)
			hsb = null;
		else if (component == rowHead)
			rowHead = null;
		else if (component == colHead)
			colHead = null;
		else if (component == lowerRight)
			lowerRight = null;
		else if (component == upperRight)
			upperRight = null;
		else if (component == lowerLeft)
			lowerLeft = null;
		else if (component == upperLeft)
			upperLeft = null;
	}
}
