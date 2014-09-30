/* ------------------------------------------------------------------
 * OverlayManager.java
 * 
 * Created 2009-02-12 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay;

import java.util.ArrayList;
import java.util.Iterator;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggEdge;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;

public class OverlayManager implements Iterable<Overlay> {
	
	private ArrayList<Overlay> overlays = new ArrayList<Overlay>();
	
	public OverlayManager() { 
		// empty
	}
	
	public Iterator<Overlay> iterator() { 
		return overlays.iterator();
	}
	
	public int getOverlayCount() {
		return overlays.size();
	}
	
	public void addOverlay(Overlay overlay) { 
		System.err.println("Adding overlay: " + overlay.getName());
		overlays.add(overlay);
	}
	
	public void removeOverlay(Overlay overlay) { 
		System.err.println("Removing overlay: " + overlay.getName());
		overlays.remove(overlay);
	}
	
	public void clear() { 
		overlays.clear();
	}
	
	public void invalidate(AggGraph.AggNode node) {
		Iterator<Overlay> itr = this.iterator();
		while (itr.hasNext()) {
			Overlay o = itr.next();
			if (o.getType() != Overlay.Type.NodeOverlay) continue;
			o.invalidate(node);
		}
	}
	
	public void invalidate(AggGraph.AggEdge edge) {
		Iterator<Overlay> itr = this.iterator();
		while (itr.hasNext()) {
			Overlay o = itr.next();
			if (o.getType() != Overlay.Type.EdgeOverlay) continue; 
			o.invalidate(edge);
		}
	}

	public void invalidateAll(AggGraph aggGraph) {
		ArrayList<AggNode> nodes = aggGraph.getNodes();
		for (AggNode node: nodes) {
			invalidate(node);
		}
		
		Iterator<AggEdge> edges = aggGraph.getEdges();
		while (edges.hasNext()) {
			invalidate(edges.next());
		}
	}
}
