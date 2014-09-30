/* ------------------------------------------------------------------
 * AggEdgeOverlay.java
 * 
 * Created 2009-02-26 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggEdge;
import edu.purdue.timematrix.overlay.elements.BasicElement;
import edu.purdue.timematrix.overlay.elements.Element;
import edu.purdue.timematrix.stat.EdgeCountStat;
import edu.purdue.timematrix.visualization.nodes.TimeCell;

public class AggEdgeOverlay extends BasicOverlay {
	private EdgeCountStat stat = null;
	
	private class AggEdgeElement extends BasicElement {
		public AggEdgeElement(AggEdge edge) {
			super(new TimeCell(stat, edge));
			updatePaint();
		}
		
		public void updatePaint() {			
			TimeCell root = (TimeCell) getRoot();
			Color base = AggEdgeOverlay.this.getColor();
			Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (255 * AggEdgeOverlay.this.getAlpha()));
			root.setPaint((Paint) c);
		}
		
		public void setBounds(double width, double height) {
			AffineTransform t = getRoot().getTransformReference(true);
			t.setToIdentity();
			t.scale(width, height);			
		}
	}
	
	private Hashtable<AggGraph.AggEdge, AggEdgeElement> edgeTable = new Hashtable<AggGraph.AggEdge, AggEdgeElement>();

	public AggEdgeOverlay(EdgeCountStat stat) {
		super(stat.getName(), Overlay.Type.EdgeOverlay);	
		this.stat = stat;
	}
	
	public Element buildEdgeElement(AggEdge edge) { 
		AggEdgeElement element;
		
		// If we have the node, don't build it again 
		if (edgeTable.containsKey(edge)) { 
			element = edgeTable.get(edge);
			element.updatePaint();
		}
		else { 
			// We don't have it, build it
			element = new AggEdgeElement(edge);
			edgeTable.put(edge, element);
		}
		
		return element;
	}
}