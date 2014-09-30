/* ------------------------------------------------------------------
 * BasicOverlay.java
 * 
 * Created 2009-02-12 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay;

import java.awt.Color;

import edu.purdue.timematrix.aggregation.AggGraph.AggEdge;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.overlay.elements.Element;
import edu.purdue.timematrix.visualization.nodes.TimeCell;

public abstract class BasicOverlay implements Overlay {

	private String name;
	private Type type;
	private double alpha = 1.0;
	private boolean visible = false;
	private Color color = Color.blue;
	
	public BasicOverlay(String name, Type type) { 
		this.name = name;
		this.type = type;
	}
	
	public String getName() { 
		return this.name;
	}

	public void setName(String name) { 
		this.name = name;
	}
	
	public String toString() { 
		return getName();
	}
	
	public double getAlpha() {
		return alpha;
	}

	public Type getType() {
		return type;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setAlpha(double alpha) {
		assert (alpha <= 1.0d && alpha >= 0.0d);
		this.alpha = alpha;
		System.err.println("Changed alpha to " + alpha);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void setColor(Color color) {
		this.color = color;
		System.err.println("Changed color to " + color);
	}
	
	public Color getColor() { 
		return color;
	}

	public Element buildNodeElement(AggNode node, boolean horz) {
		return null;
	}
	
	public Element buildEdgeElement(AggEdge edge) { 
		return null;
	}
	
	public void invalidate(AggNode node) {
		Element e;
		TimeCell root;
		
		e = buildNodeElement(node, true);
		if (e.getRoot() instanceof TimeCell) {
			root = (TimeCell) e.getRoot();
			root.setDirty();
		}
//		root.invalidatePaint();
		
		e = buildNodeElement(node, false);
		if (e.getRoot() instanceof TimeCell) {
			root = (TimeCell) e.getRoot();
			root.setDirty();
		}
//		root.invalidatePaint();
	}
	
	public void invalidate(AggEdge edge) {
		Element e = buildEdgeElement(edge);
		if (e.getRoot() instanceof TimeCell) {
			TimeCell root = (TimeCell) e.getRoot();
			root.setDirty();
		}
//		root.invalidatePaint();
	}
}
