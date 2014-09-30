/* ------------------------------------------------------------------
 * BasicElement.java
 * 
 * Created 2009-02-18 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay.elements;

import java.awt.geom.AffineTransform;

import edu.umd.cs.piccolo.PNode;

public abstract class BasicElement implements Element {

	private PNode root;
	
	public BasicElement() {
		root = new PNode();
	}

	public BasicElement(PNode root) {
		this.root = root;
	}
	
	public PNode getRoot() {
		return root;
	}

	public void setPosition(double x, double y) {
		AffineTransform t = getRoot().getTransformReference(true);
		t.setToIdentity();
		t.translate(x, y);
	}
	
	public void setBounds(double sx, double sy) {}
}
