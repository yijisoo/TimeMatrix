package edu.purdue.timematrix.overlay;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.overlay.elements.BasicElement;
import edu.purdue.timematrix.overlay.elements.Element;
import edu.purdue.timematrix.stat.DegreeCentralityStat;
import edu.purdue.timematrix.visualization.AggMatrixHeader;
import edu.purdue.timematrix.visualization.nodes.TimeCell;

public class NodeDegreeCentralityOverlay extends BasicOverlay {
	private DegreeCentralityStat stat = null;
	private Hashtable<AggGraph.AggNode, NodeDegreeCentralityElement> horzTable = new Hashtable<AggGraph.AggNode, NodeDegreeCentralityElement>();	
	private Hashtable<AggGraph.AggNode, NodeDegreeCentralityElement> vertTable = new Hashtable<AggGraph.AggNode, NodeDegreeCentralityElement>();	
	
	public class NodeDegreeCentralityElement extends BasicElement {
		public NodeDegreeCentralityElement(float cellWidth, float cellHeight, AggNode node, boolean horz) {
			super(new TimeCell(stat, node));
			updatePaint();
		}
		
		public void updatePaint() {			
			TimeCell root = (TimeCell) getRoot();
			Color base = NodeDegreeCentralityOverlay.this.getColor();
			Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (255 * NodeDegreeCentralityOverlay.this.getAlpha()));
			root.setPaint((Paint) c);
		}

		public void setBounds(double width, double height) {
			AffineTransform t = getRoot().getTransformReference(true);
			t.setToIdentity();
			t.scale(width, height);
		}
		
//		private void setColor(PNode node, Color c) {
//			node.setPaint(c);
//			for (int i = 0; i < node.getChildrenCount(); i++) { 
//				PNode child = node.getChild(i);
//				setColor(child, c);
//			}
//		}
	}
	
	public NodeDegreeCentralityOverlay(DegreeCentralityStat stat) {
		super(stat.getName(), Overlay.Type.NodeOverlay);
		this.stat = stat;
	}
	
	public Element buildNodeElement(AggNode node, boolean horz) {
		// Look up the right table 
		Hashtable<AggGraph.AggNode, NodeDegreeCentralityElement> nodeTable;
		if (horz) { 
			nodeTable = horzTable;
		}
		else { 
			nodeTable = vertTable;
		}

		NodeDegreeCentralityElement element;
		
		// If we have the node, don't build it again 
		if (nodeTable.containsKey(node)) { 
			element = nodeTable.get(node);
			element.updatePaint();
		}
		else { 		
			// We don't have it, build it
			element = new NodeDegreeCentralityElement(AggMatrixHeader.cellWidth, AggMatrixHeader.cellSize, node, horz);
			nodeTable.put(node, element);
		}
		return element;
	}
}