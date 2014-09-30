/* ------------------------------------------------------------------
 * CompositeElement.java
 * 
 * Created 2009-02-17 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay.elements;

import java.util.ArrayList;

import edu.umd.cs.piccolo.PNode;

public class CompositeElement extends BasicElement {

	private ArrayList<Element> children = new ArrayList<Element>();
	protected boolean isHighlight = false; 
	
	public CompositeElement() {
		// empty
	}

	public CompositeElement(PNode root) {
		super(root);
	}

	public void addChild(Element child) { 
		children.add(child);
		if (child.getRoot() != null) { 
			getRoot().addChild(child.getRoot());
		}
		else {
			assert (false);
		}
	}
	
	public void removeChild(Element child) { 
		children.remove(child);
		if (child.getRoot() != null) { 
			getRoot().removeChild(child.getRoot());
		}
		else {
			assert (false);
		}
	}
	
	public void clear() { 
		children.clear();
		getRoot().removeAllChildren();
	}

	public void setBounds(double width, double height) {
		for (Element child : children) { 
			child.setBounds(width, height);
		}
	}

	public void setHighlight(boolean isHighlight) {
		if (this.isHighlight != isHighlight) {
			this.isHighlight = isHighlight;
			redraw();
		}
	}
	
	public void redraw() {
		// this should be implemented by children
		assert (false);
	}
}
