/* ------------------------------------------------------------------
 * NodeHierarchyOverlay.java
 * 
 * Created 2009-02-12 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay;

import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.overlay.elements.CompositeElement;
import edu.purdue.timematrix.overlay.elements.Element;

public class NodeHierarchyOverlay extends BasicOverlay {

	public class NodeHierarchyElement extends CompositeElement {}

	public NodeHierarchyOverlay() { 
		super("Node hierarchy", Overlay.Type.NodeOverlay);
	}

	public Element buildNodeElement(AggNode node, boolean horz) {
		
		// Build it
//		NodeHierarchyElement element = new NodeHierarchyElement(AggHierHeader.cellWidth, AggHierHeader.cellSize, node, horz);
//		nodeTable.put(node, element);
//		return element;
		return null;
	}

}
