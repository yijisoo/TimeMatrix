/* ------------------------------------------------------------------
 * NodeCompositor.java
 * 
 * Created 2009-02-12 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.overlay.Overlay;
import edu.purdue.timematrix.overlay.OverlayManager;
import edu.purdue.timematrix.overlay.elements.CompositeElement;
import edu.purdue.timematrix.overlay.elements.Element;
import edu.purdue.timematrix.overlay.elements.ScalingBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

public class NodeCompositor {

	private OverlayManager overlays;
	private Hashtable<AggGraph.AggNode, CompositeElement> nodeTable = new Hashtable<AggGraph.AggNode, CompositeElement>();
	
	private static class NodeBox extends ScalingBox {
		AggNode node;
		public NodeBox(AggNode node, float width, float height) { 
			super(width, height);
			this.node = node;
			PPath shape = (PPath) getRoot();
			
			shape.setStroke(new BasicStroke(0.1f));
//			shape.setStroke(null);
			
			shape.addInputEventListener(new PBasicInputEventHandler() {
				private PPath tooltip;
				public void mouseEntered(PInputEvent e) {
					PText text = new PText(extractLabel(getRoot()));
					PBounds bounds = text.getBounds();
					tooltip = PPath.createRectangle((float) bounds.getMinX() - 2, (float) bounds.getMinY() - 2, (float) bounds.getWidth() + 2, (float) bounds.getHeight() + 2);
					tooltip.addChild(text);
					e.getCamera().addChild(tooltip);
					updateTooltip(e);
				}
				public void mouseMoved(PInputEvent e) { 
					updateTooltip(e);					
				}
				private void updateTooltip(PInputEvent e) { 
					Point2D p = e.getCanvasPosition();
					e.getPath().canvasToLocal(p, e.getCamera());
					tooltip.setOffset(p.getX() + 8, p.getY() - 8);
				}
				public void mouseExited(PInputEvent e) {
					if (tooltip != null)
						e.getCamera().removeChild(tooltip);
				}
			});
			redraw();
		}
		
		private String extractLabel(PNode node) { 
			StringBuffer sbuf = new StringBuffer("");
			if (node instanceof PText) { 
				PText text = (PText) node;
				sbuf.append(text.getText());
			}
			for (int i = 0; i < node.getChildrenCount(); i++) {
				String childLabel = extractLabel(node.getChild(i));
				if (childLabel.length() > 0) { 
					if (sbuf.length() != 0) sbuf.append(" - ");
					sbuf.append(childLabel);
				}
			}
			return sbuf.toString();
		}

		public void redraw() {
			PPath shape = (PPath) getRoot();
			
			//TODO Do we like this color?
			if (node.isLeaf()) shape.setPaint(new Color(1.0f, 0.8f, 0.8f));
			else shape.setPaint(new Color(0.8f, 1.0f, 0.8f));
			
			if (isHighlight) {
//				Color orgColor = (Color) shape.getPaint();
//				shape.setPaint(orgColor.brighter());
				shape.setPaint(Color.yellow);
			}
		}		
	}

	public NodeCompositor(OverlayManager overlays) {
		this.overlays = overlays;
	}
	
	public void setHighlight(AggGraph.AggNode node, boolean isHighlight) {
		CompositeElement element = nodeTable.get(node);
		element.setHighlight(isHighlight);
	}
	
	public Element compose(AggNode node, float yPos, float width, float height, boolean horz) {

		CompositeElement composite; 
		
		// Use existing if we already have it assembled
		if (nodeTable.containsKey(node)) {
			composite = nodeTable.get(node);
			composite.clear();
		}
		// Create new shape
		else {
			composite = new NodeBox(node, width, height);
			nodeTable.put(node, composite);
		}
		
		// Step through all the overlays in the manager
		for (Iterator<Overlay> i = overlays.iterator(); i.hasNext();) {
		
			// Retrieve the overlay
			Overlay overlay = i.next();
			
			// See if we can skip this overlay 
			if (overlay.getType() != Overlay.Type.NodeOverlay) continue;
			if (!overlay.isVisible()) continue;
			if (overlay.getAlpha() <= 0.0) continue;
			
			// We cannot skip it---build it
			Element elem = overlay.buildNodeElement(node, horz);
			if (elem != null) {
				composite.addChild(elem);
			}
		}
		
		// Set geometric allocations 
		composite.setPosition(0, yPos);
		composite.setBounds(width, height);
		
		// Add event listener
		composite.getRoot().addInputEventListener(new NodeCompositorMouseEventListener(node, horz));
		
		return composite;
	}
	
	class NodeCompositorMouseEventListener extends PBasicInputEventHandler {
		AggGraph.AggNode node;
		boolean horz;
		
		public NodeCompositorMouseEventListener(AggGraph.AggNode node, boolean horz) {
			this.node = node;
			this.horz = horz;
		}
		
		public void mouseEntered(PInputEvent event) {
			Manager.getNodeComp(this.horz).setHighlight(this.node, true); 
			ArrayList<AggGraph.AggEdge> edges = (this.horz) ? node.getAllInEdges() : node.getAllOutEdges(); 
			for (AggGraph.AggEdge edge : edges) {
				Manager.getEdgeComp().setHighlight(edge, true);
			}
		}
		
		public void mouseExited(PInputEvent event) {
			Manager.getNodeComp(this.horz).setHighlight(this.node, false);
			ArrayList<AggGraph.AggEdge> edges = (this.horz) ? node.getAllInEdges() : node.getAllOutEdges();
			for (AggGraph.AggEdge edge : edges) {
				Manager.getEdgeComp().setHighlight(edge, false);
			}
		}
	}
}