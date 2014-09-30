/* ------------------------------------------------------------------
 * Overlay.java
 * 
 * Created 2009-02-12 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay;

import java.awt.Color;

import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.aggregation.AggGraph.AggEdge;
import edu.purdue.timematrix.overlay.elements.Element;

public interface Overlay {
	enum Type { NodeOverlay, EdgeOverlay };
	
	String getName();
	void setName(String name);
	
	boolean isVisible();
	void setVisible(boolean visible);
	
	double getAlpha();
	void setAlpha(double alpha);
	
	Type getType();
	
	void setColor(Color color);
	Color getColor();
	
	Element buildNodeElement(AggNode node, boolean horz);
	Element buildEdgeElement(AggEdge edge);
	
	void invalidate (AggNode node);
	void invalidate (AggEdge edge);
}
