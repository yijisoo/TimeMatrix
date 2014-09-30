/* ------------------------------------------------------------------
 * AggHierHeader.java
 * 
 * Created 2009-02-11 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.visualization;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.overlay.OverlayManager;
import edu.purdue.timematrix.overlay.elements.Element;
import edu.umd.cs.piccolo.PNode;

public class AggMatrixHeader extends PNode implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;

	public static final float cellSize = 10.0f;
	public static final float cellWidth = 150.0f;

	private NodeCompositor nc;
	private boolean horz;
	private AggGraph aggGraph;
	private AggMatrixAllocationManager allocations;

	public AggMatrixHeader(AggGraph aggGraph, AggMatrixAllocationManager allocations, OverlayManager overlays, boolean horz) {
		this.aggGraph = aggGraph;
		this.allocations = allocations;
		this.horz = horz;
		aggGraph.addPropertyChangeListener(this);
		this.nc = new NodeCompositor(overlays);
		
		Manager.setNodeComp(nc, horz);
		
		layoutLabels();
	}

	public boolean isHorizontal() { 
		return horz;
	}
	
	public void layoutLabels() {
		
		// First clear the labels
		removeAllChildren();
		
		// Step through all nodes
		float y = 0;
		for (int i = 0; i < aggGraph.getNodeCount(); i++) {
			AggNode node = aggGraph.getNode(i);
			Element elem = nc.compose(node, y, cellWidth, allocations.get(i).getAlloc(), horz);
			addChild(elem.getRoot());
			
			// Move to the next cell
			y += allocations.get(i).getAlloc();
		}	
	}
	
	public void scale(double scaleX, double scaleY) {}
	
	public void propertyChange(PropertyChangeEvent evt) {
		layoutLabels();
	}
}
