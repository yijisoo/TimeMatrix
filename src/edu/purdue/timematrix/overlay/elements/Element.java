/* ------------------------------------------------------------------
 * Element.java
 * 
 * Created 2009-02-17 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay.elements;


import edu.umd.cs.piccolo.PNode;

public interface Element {
	PNode getRoot();
	void setPosition(double x, double y);
	void setBounds(double width, double height);
}
