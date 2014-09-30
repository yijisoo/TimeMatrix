/* ------------------------------------------------------------------
 * ScalingBox.java
 * 
 * Created 2009-03-02 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay.elements;

import edu.umd.cs.piccolo.nodes.PPath;

public class ScalingBox extends CompositeElement {
	
	public ScalingBox(float width, float height) { 
		super(new PPath());
	}
	
	public void setBounds(double width, double height) {
		PPath root = (PPath) getRoot();
		root.setPathToRectangle(0.0f, 0.0f, (float) width, (float) height);
		super.setBounds(width, height);
	}
}