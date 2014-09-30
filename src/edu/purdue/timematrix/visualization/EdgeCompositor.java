/* ------------------------------------------------------------------
 * EdgeCompositor.java
 * 
 * Created 2009-02-18 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.visualization;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Iterator;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggEdge;
import edu.purdue.timematrix.overlay.Overlay;
import edu.purdue.timematrix.overlay.OverlayManager;
import edu.purdue.timematrix.overlay.elements.CompositeElement;
import edu.purdue.timematrix.overlay.elements.Element;
import edu.purdue.timematrix.overlay.elements.ScalingBox;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

public class EdgeCompositor {
	
	private OverlayManager overlays;
	private Hashtable<AggGraph.AggEdge, CompositeElement> edgeTable = new Hashtable<AggGraph.AggEdge, CompositeElement>();
	
	private static class EdgeBox extends ScalingBox {
		AggEdge edge;
		
		public EdgeBox(AggEdge edge, float width, float height) { 
			super(width, height);
			this.edge = edge;
			
			PPath shape = (PPath)getRoot();
			
//			shape.setStroke(new BasicStroke(0.1f));
			shape.setStroke(null);
			
			redraw();
		}

		public void redraw() {
			PPath shape = (PPath) getRoot();
			
			if (edge.isLeaf()) shape.setPaint(new Color(0.9f, 0.9f, 0.9f));
			else shape.setPaint(new Color(0.8f, 0.8f, 0.8f));
			
			if (isHighlight) {
//				Color orgColor = (Color) shape.getPaint();
//				shape.setPaint(orgColor.brighter());
				shape.setPaint(Color.yellow);
			}
		}		
	}
	
	public EdgeCompositor(OverlayManager overlays) {
		this.overlays = overlays;
	}
	
	public void setHighlight(AggGraph.AggEdge edge, boolean isHighlight) {
		CompositeElement element = edgeTable.get(edge);
//		assert (element != null);
		if (element != null) {
			element.setHighlight(isHighlight);
		}
	}
	
	public Element compose(AggEdge edge, float x, float y, float width, float height) {
		CompositeElement composite; 
		
		// Use existing if we already have it assembled
		if (edgeTable.containsKey(edge)) {
			composite = edgeTable.get(edge);
			composite.clear();
		}
		// Create new shape
		else {
			composite = new EdgeBox(edge, width, height);
			edgeTable.put(edge, composite);
		}
				
		// Step through all the overlays in the manager
		for (Iterator<Overlay> i = overlays.iterator(); i.hasNext();) {
		
			// Retrieve the overlay
			Overlay overlay = i.next();
			
			// See if we can skip this overlay 
			if (overlay.getType() != Overlay.Type.EdgeOverlay) continue;
			if (!overlay.isVisible()) continue;
			if (overlay.getAlpha() <= 0.0) continue;
			
			// We cannot skip it---build it
			Element elem = overlay.buildEdgeElement(edge);
			if (elem != null) { 
				composite.addChild(elem);
			}
			else {
				assert (false);
			}
		}
		
		// Set geometric allocations 
		composite.setPosition(x, y);
		composite.setBounds(width, height);
		
		// Add event listener
		composite.getRoot().addInputEventListener(new EdgeCompositorMouseEventListener(edge));
		
		return composite;
	}
	
	class EdgeCompositorMouseEventListener extends PBasicInputEventHandler {
		AggGraph.AggEdge edge;
		
		public EdgeCompositorMouseEventListener(AggGraph.AggEdge edge) {
			this.edge = edge;
		}
		
		public void mouseEntered(PInputEvent event) {
			Manager.getEdgeComp().setHighlight(this.edge, true);
			Manager.getNodeComp(true).setHighlight(this.edge.getDst(), true);
			Manager.getNodeComp(false).setHighlight(this.edge.getSrc(), true);
		}
		
		public void mouseExited(PInputEvent event) {
			Manager.getEdgeComp().setHighlight(this.edge, false);
			Manager.getNodeComp(true).setHighlight(this.edge.getDst(), false);
			Manager.getNodeComp(false).setHighlight(this.edge.getSrc(), false);
		}
	}
}
