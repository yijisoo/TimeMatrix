/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package edu.purdue.timematrix.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Implements a Swing-based Range slider, which allows the user to
 * enter a range-based value.
 *
 * @author Ben B. Bederson, Jon Meyer and Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class RangeSlider extends JComponent
    implements MouseListener, MouseMotionListener, ChangeListener {
	
	// Avoiding warnings...
	private static final long serialVersionUID = 1L;
	
	// Size of the cutout corner on the range bar.
    static final int	SZ = 6; 

    // Event handling
    static final int	PICK_NONE = 0;

    // Event handling
    static final int	PICK_MIN = 1;

    // Event handling
    static final int	PICK_MAX = 2;

    // Size of the thumb
    static final int THUMB_SIZE = 20;
    
    // Event handling
    static final int		  PICK_MID = 4;
    private BoundedRangeModel model;
    private boolean			  enabled = false;

    // PAINT METHOD
    int[] xPts = new int[7];

    // PAINT METHOD
    int[] yPts = new int[7];
    int   pick;
    int   pick_mouseover;
    int   pickOffset;
    int   mouseX;
    
    static Color pickedColor = new Color(0.6f, 0.6f, 0.7f);
    static Color darkPickedColor = new Color(0.3f, 0.3f, 0.5f);

//    boolean visible_ = false;
    
    // PUBLIC API

    /**
     * Constructs a new range slider.
     *
     * @param minimum - the minimum value of the range.
     * @param maximum - the maximum value of the range.
     * @param lowValue - the current low value shown by the range
     *        slider's bar.
     * @param highValue - the current high value shown by the range
     *        slider's bar.
     */
    public RangeSlider(int minimum, int maximum, int lowValue, int highValue) {
    	this(new DefaultBoundedRangeModel(lowValue, highValue - lowValue, minimum, maximum));
    	setFocusable(false);
    }

    /**
     * Creates a new RangeSlider object.
     *
     * @param model the BoundedRangeModel.
     */
    public RangeSlider(BoundedRangeModel model) {
		this.model = model;
		model.addChangeListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
    }

    /**
     * Returns the current "low" value shown by the range slider's
     * bar. The low value meets the constraint minimum  &lt;=
     * lowValue  &lt;= highValue  &lt;= maximum.
     *
     * @return the current "low" value shown by the range slider's bar.
     */
    public int getLowValue() {
    	return model.getValue();
    }

    /**
     * Returns the current "high" value shown by the range slider's
     * bar. The high value meets the constraint minimum  &lt;=
     * lowValue  &lt;= highValue  &lt;= maximum.
     *
     * @return the current "high" value shown by the range slider's
     * bar.
     */
    public int getHighValue() {
    	return model.getValue() + model.getExtent();
    }

    /**
     * Returns the minimum possible value for either the low value or
     * the high value.
     *
     * @return the minimum possible value for either the low value or
     *         the high value.
     */
    public int getMinimum() {
    	return model.getMinimum();
    }

    /**
     * Returns the maximum possible value for either the low value or
     * the high value.
     *
     * @return the maximum possible value for either the low value or
     *         the high value.
     */
    public int getMaximum() {
    	return model.getMaximum();
    }

//    public void setVisible_(boolean v) {
//    	visible_ = v;
//    }
    
    /**
     * Returns true if the specified value is within the range
     * indicated by this range slider, i&dot;e&dot; lowValue 1 &lt;= v &lt;=
     * highValue.
     *
     * @param v value
     *
     * @return true if the specified value is within the range
     *         indicated by this range slider.
     */
    public boolean contains(int v) {
    	return (v >= getLowValue() && v <= getHighValue());
    }

    /**
     * Sets the low value shown by this range slider. This causes the
     * range slider to be repainted and a RangeEvent to be fired.
     *
     * @param lowValue the low value shown by this range slider
     */
    public void setLowValue(int lowValue) {
        int high;
        if ((lowValue + model.getExtent()) > getMaximum()) {
            high = getMaximum();
        }
        else {
            high = getHighValue();
        }
        int extent = high-lowValue;
       
        model.setRangeProperties(lowValue,extent,
            getMinimum(),getMaximum(),true);
    }

    /**
     * Sets the high value shown by this range slider. This causes
     * the range slider to be repainted and a RangeEvent to be
     * fired.
     *
     * @param highValue the high value shown by this range slider
     */
    public void setHighValue(int highValue) {
    	model.setExtent(highValue - getLowValue());
    }

    /**
     * Sets the minimum value of the sizeModel.
     *
     * @param min the minimum value.
     */
    public void setMinimum(int min) {
    	model.setMinimum(min);
    }

    /**
     * Sets the maximum value of the sizeModel.
     *
     * @param max the maximum value.
     */
    public void setMaximum(int max) {
    	model.setMaximum(max);
    }

    Rectangle getInBounds() {
		Dimension sz = getSize();
		Insets    insets = getInsets();
		return new Rectangle(insets.left, insets.top,
				     sz.width - insets.left - insets.right,
				     sz.height - insets.top - insets.bottom);
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {

    	// --- Temporary hack
//    	if (!visible_) return;

    	g.setColor(Color.darkGray);
    	Rectangle rect = getInBounds();
    	g.fillRect(rect.x, rect.y, rect.width - 1, rect.height - 1);

    	int minX = toScreenX(getLowValue());
    	int maxX = toScreenX(getHighValue());

    	if ((maxX - minX) > 10) {

    		xPts[0] = minX;
    		yPts[0] = rect.y + SZ;
    		xPts[1] = minX + SZ;
    		yPts[1] = rect.y;
    		xPts[2] = minX + 11;
    		yPts[2] = rect.y;
    		xPts[3] = minX + 11;
    		yPts[3] = rect.y + rect.height - 3;
    		xPts[4] = minX;
    		yPts[4] = rect.y + rect.height - 3;
    		g.setColor(!enabled ? Color.darkGray : pick_mouseover == PICK_MIN ? pickedColor : Color.lightGray);
    		g.fillPolygon(xPts, yPts, 5);

    		xPts[0] = minX + 11;
    		yPts[0] = rect.y;
    		xPts[2] = maxX - 10;
    		yPts[2] = rect.y;
    		xPts[3] = maxX - 10;
    		yPts[3] = rect.y + rect.height - 3;
    		xPts[4] = minX + 11;
    		yPts[4] = rect.y + rect.height - 3;
    		g.setColor(!enabled ? Color.darkGray :  pick_mouseover == PICK_MID ? pickedColor : Color.lightGray);
    		g.fillPolygon(xPts, yPts, 5);

    		xPts[0] = maxX - 10;
    		yPts[0] = rect.y;
    		xPts[2] = maxX;
    		yPts[2] = rect.y;
    		xPts[3] = maxX;
    		yPts[3] = rect.y + rect.height - SZ;
    		xPts[4] = maxX - SZ;
    		yPts[4] = rect.y + rect.height - 3;
    		xPts[5] = maxX - 10;
    		yPts[5] = rect.y + rect.height - 3;
    		g.setColor(!enabled ? Color.darkGray :  pick_mouseover == PICK_MAX ? pickedColor : Color.lightGray);
    		g.fillPolygon(xPts, yPts, 6);

    		xPts[0] = minX;
    		yPts[0] = rect.y + SZ;
    		xPts[1] = minX + SZ;
    		yPts[1] = rect.y;
    		xPts[2] = maxX;
    		yPts[2] = rect.y;
    		xPts[3] = maxX;
    		yPts[3] = rect.y + rect.height - SZ;
    		xPts[4] = maxX - SZ;
    		yPts[4] = rect.y + rect.height - 3;
    		xPts[5] = minX;
    		yPts[5] = rect.y + rect.height - 3;
    		xPts[6] = minX;
    		yPts[6] = rect.y + SZ;
    		g.setColor(Color.darkGray);
    		g.drawPolygon(xPts, yPts, 7);

    		////////////////

    		if ((maxX - minX) > 12) {
    			// Draw the little dot pattern
    			for (int y = rect.y + 3; y < (rect.y + rect.height - 3);
    			y += 3) {
    				g.setColor(Color.white);
    				g.fillRect(minX + 2, y + 2, 1, 1);
    				g.fillRect(minX + 5, y, 1, 1);
    				g.fillRect(minX + 8, y - 2, 1, 1);

    				g.setColor(Color.white);
    				g.fillRect(maxX - 2, y - 4, 1, 1);
    				g.fillRect(maxX - 5, y - 2, 1, 1);
    				g.fillRect(maxX - 8, y + 0, 1, 1);

    				g.setColor(pick_mouseover == PICK_MIN ? darkPickedColor : Color.lightGray);
    				g.fillRect(minX + 2, y + 3, 1, 1);
    				g.fillRect(minX + 5, y + 1, 1, 1);
    				g.fillRect(minX + 8, y - 1, 1, 1);

    				g.setColor(pick_mouseover == PICK_MAX ? darkPickedColor : Color.lightGray);
    				g.fillRect(maxX - 2, y - 3, 1, 1);
    				g.fillRect(maxX - 5, y - 1, 1, 1);
    				g.fillRect(maxX - 8, y + 1, 1, 1);
    			}

    			g.setColor(Color.gray);
    			g.drawLine(minX + 10, rect.y + 2, minX + 10,
    					rect.y + rect.height - 2);
    			g.drawLine(maxX - 10, rect.y + 2, maxX - 10,
    					rect.y + rect.height - 2);
    		} else {
    			// Too small to draw the dot pattern - just draw a line down the center
    			g.setColor(Color.darkGray);
    			g.drawLine((minX + maxX) / 3, rect.y + 2, (minX + maxX) / 3,
    					rect.y + rect.height - 2);

    			g.drawLine((2 * (minX + maxX)) / 3, rect.y + 2,
    					(2 * (minX + maxX)) / 3, rect.y + rect.height - 2);
    		}
    	} else {
    		// For very small ranges we just draw a tiny 3D rect
    		if (enabled == true) {
    			g.setColor(Color.white);
    		} else {
    			g.setColor(Color.lightGray);
    		}

    		int w = maxX - minX;

    		if (w < 10) {
    			w = 10;
    		}

    		g.fill3DRect(minX, rect.y, w, rect.y + rect.height, true);
    		g.setColor(Color.gray);
    		g.drawLine((minX + maxX) / 3, rect.y + 2, (minX + maxX) / 3,
    				rect.y + rect.height - 2);
    		g.drawLine((2 * (minX + maxX)) / 3, rect.y + 2,
    				(2 * (minX + maxX)) / 3, rect.y + rect.height - 2);
    	}

    	g.setColor(Color.gray);
    	g.drawLine(xPts[0], yPts[0], xPts[1], yPts[1]);
    	g.drawLine(xPts[1], yPts[1], xPts[2], yPts[2]);
    	g.drawLine(xPts[5], yPts[5], xPts[6], yPts[6]);

    	setToolTipText("");
    	
    	// Darken the component if not enabled
    	if (!isEnabled()) {
    		g.setColor(new Color(0, 0, 0, 0.6f));
    		g.fillRect(0, 0, getWidth(), getHeight());
    	}
    }

    // Converts from screen coordinates to a range value.
    private int toLocalX(int x) {
		Dimension sz = getSize();
		double xScale = (sz.width - 3) / (double)(getMaximum() - getMinimum());
		return (int)((x / xScale) + getMinimum());
    }

    // Converts from a range value to screen coordinates.
    private int toScreenX(int x) {
		Dimension sz = getSize();
		double xScale = (sz.width - 3) / (double)(getMaximum() - getMinimum());
		return (int)((x - getMinimum()) * xScale);
    }

    private int pickHandle(int x) {
		int minX = toScreenX(getLowValue());
		int maxX = toScreenX(getHighValue());	
		int pick = PICK_NONE;
	
		if (x >= minX && x <= minX + 11) {
		    pick = PICK_MIN;
		}
		else if (x <= maxX && x >= maxX - 10) {
		    pick = PICK_MAX;
		}
		else if (x > minX + 11 && x < maxX - 10) {
			pick = PICK_MID;
		}
		return pick;
    }

    private void offset(int dx) {
    	model.setValue(getLowValue() + dx);
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
		if (enabled == false) { // || visible_ == false) {
		    return;
		}
		pick = pickHandle(e.getX());
		pickOffset = e.getX() - toScreenX(getLowValue());
		mouseX = e.getX();
		model.setValueIsAdjusting(true);
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
		if (enabled == false) { // || visible_ == false) {
		    return;
		}
		
		int x = toLocalX(e.getX());
	
		if (x < getMinimum()) {
		    x = getMinimum();
		}
	
		if (x > getMaximum()) {
		    x = getMaximum();
		}
	
		if (pick == (PICK_MIN | PICK_MAX)) {
		    if ((e.getX() - mouseX) > 2) {
		    	pick = PICK_MAX;
		    }
		    else if ((e.getX() - mouseX) < -2) {
		    	pick = PICK_MIN;
		    }
		    else {
		    	return;
		    }
		}
	
		switch (pick) {
		case PICK_MIN:
	    	int x2 = Math.min(x, getHighValue() - 25 * (getMaximum() - getMinimum())/getWidth());
	    	setLowValue(x2);
		    break;
		case PICK_MAX:
		   	int x3 = Math.max(x, getLowValue() + 25 * (getMaximum() - getMinimum())/getWidth());
	    	setHighValue(x3);
		    break;
		case PICK_MID:
		    int dx = toLocalX(e.getX() - pickOffset) - getLowValue();
		    if ((dx < 0) && ((getLowValue() + dx) < getMinimum())) {
		    	dx = getMinimum() - getLowValue();
		    }
		    if ((dx > 0) && ((getHighValue() + dx) > getMaximum())) {
		    	dx = getMaximum() - getHighValue();
		    }
		    if (dx != 0) {
		    	offset(dx);
		    }
		    break;
		}
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
		model.setValueIsAdjusting(false);
		pick = PICK_NONE;
		if (e.getY() < 0 || e.getY() > getHeight())
			pick_mouseover = PICK_NONE;
		else
			pick_mouseover = pickHandle(e.getX());
		repaint();
    }
    
    public void forceRelease() {
    	model.setValueIsAdjusting(false);
    	pick = PICK_NONE;
   		pick_mouseover = PICK_NONE;
    	repaint();
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
		if (enabled == false) { // || visible_ == false) {
		    return;
		}

		pick_mouseover = pickHandle(e.getX());
		repaint();
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            model.setValue(model.getMinimum());
            model.setExtent(model.getMaximum()-model.getMinimum());
            repaint();
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
    	if (pick == PICK_NONE) {
    		pick_mouseover = PICK_NONE;
    		repaint();
    	}
    }

    /**
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    public void setEnabled(boolean v) {
		enabled = v;
		repaint();
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
    	repaint();
    }

    /**
     * Returns the sizeModel.
     * @return BoundedRangeModel
     */
    public BoundedRangeModel getModel() {
    	return model;
    }

    /**
     * Sets the sizeModel.
     * @param model The BoundedRangeModel to set
     */
    public void setModel(BoundedRangeModel model) {
		this.model = model;
		repaint();
    }
	
    /**
     * @see javax.swing.JComponent#getToolTipText(MouseEvent)
     */
    public String getToolTipText(MouseEvent event) {
		return 
		    "[" + getMinimum() +
		    " [" + getLowValue() +
		    ":" + getHighValue() + 
		    "] " + getMaximum() + 
		    "]";
    }

}
