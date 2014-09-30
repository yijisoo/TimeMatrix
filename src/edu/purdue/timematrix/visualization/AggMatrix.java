/* ------------------------------------------------------------------
 * AggHierMatrix.java
 * 
 * Created 2009-01-20 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.visualization;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.overlay.OverlayManager;
import edu.purdue.timematrix.overlay.elements.Element;
import edu.umd.cs.piccolo.PNode;

public class AggMatrix extends PNode implements PropertyChangeListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final float baseCellSize = 10.0f;
	
	private EdgeCompositor ec;
	private AggGraph aggGraph;
	private AggMatrixAllocationManager allocations;
		
	public AggMatrix(AggGraph aggGraph, OverlayManager overlayManager) { 
		this.aggGraph = aggGraph;
		this.allocations = new AggMatrixAllocationManager(aggGraph.getNodeCount(), baseCellSize);
		aggGraph.addPropertyChangeListener(this);
		this.ec = new EdgeCompositor(overlayManager);
		Manager.setEdgeComp(ec);
		layoutMatrix();
	}
	
	public AggMatrixAllocationManager getAllocations() { 
		return allocations;
	}

	public void layoutMatrix() {
		
		// First clear the matrix
		removeAllChildren();
		
		float y = 0;
		
		// Step through the rows
		for (int row = 0; row < aggGraph.getNodeCount(); row++) {

			float x = 0;
			
			// Retrieve the edges for this row (initialize if it does not exist)
			AggGraph.AggNode rowNode = aggGraph.getNode(row);
			
			// Now step through the columns
			for (int col = 0; col < aggGraph.getNodeCount(); col++) {
				
				AggGraph.AggNode colNode = aggGraph.getNode(col);
				AggGraph.AggEdge edge = rowNode.getConnectingEdge(colNode);
				if (edge != null) {

					// Calculate space allocations
					float cw = allocations.get(col).getAlloc();
					float ch = allocations.get(row).getAlloc();

					// Create the element and add it to the node
					Element elem = ec.compose(edge, x, y, cw, ch);					
					addChild(elem.getRoot());
				}
				
				// Move to the next cell
				x += allocations.get(col).getAlloc();
			}
			
			// Move to the next row
			y += allocations.get(row).getAlloc();
		}
		
		System.err.println("Matrix laid out - " + allocations.size() + " x " + allocations.size());
	}
	
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == AggGraph.PROPERTY_ORDER) {
			allocations.permutate((List<Integer>) evt.getNewValue());
		}
		layoutMatrix();
	}	
}
