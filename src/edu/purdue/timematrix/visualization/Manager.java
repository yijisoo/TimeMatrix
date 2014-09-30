package edu.purdue.timematrix.visualization;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.overlay.OverlayManager;
import edu.purdue.timematrix.stat.StatManager;

public class Manager {
    private static EdgeCompositor edgeComp = null;
    private static NodeCompositor[] nodeComp = null; // 0 : Horizontal, 1: Vertical
    private static FilterManager filterManager = null;
    private static OverlayManager overlayManager = null;
    private static StatManager statManager = null;
    private static EventManager eventManager = null;
    private static int maxNodeSize = 1;
    
    static class EventManager implements PropertyChangeListener {
		private AggGraph aggGraph;
		public EventManager(AggGraph aggGraph) {
			this.aggGraph = aggGraph;
		}
		@SuppressWarnings("unchecked")
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(AggGraph.PROPERTY_AGGREGATION)) {
				ArrayList<AggNode> oldNodes = (ArrayList<AggNode>) evt.getOldValue();
				ArrayList<AggNode> newNodes = (ArrayList<AggNode>) evt.getNewValue();

				// Update Statistics 
				if (oldNodes.size() == 1) {  // Collapse
					Manager.statManager.updateStats();
					if (oldNodes.get(0).getTotalItemCount() == maxNodeSize) {
						Manager.maxNodeSize = aggGraph.getMaxTotalItemCount();					
					}
				}
				else if (newNodes.size() == 1) {  // Aggregate
					Manager.statManager.updateStats(newNodes.get(0));
					if (newNodes.get(0).getTotalItemCount() > Manager.maxNodeSize) {
						assert (newNodes.get(0).getTotalItemCount() == aggGraph.getMaxTotalItemCount());
						Manager.maxNodeSize = newNodes.get(0).getTotalItemCount();
					}
				}
				
				// Update Edges
				Manager.overlayManager.invalidateAll(aggGraph);				
			}
		}
    }
    
    private Manager() {
    	// empty
    }
    
    public static void setAggGraph(AggGraph aggGraph) {
    	assert(Manager.eventManager == null);
    	Manager.eventManager = new EventManager(aggGraph);
    	aggGraph.addPropertyChangeListener(Manager.eventManager);
    }
    
    public static void setEdgeComp(EdgeCompositor edgeComp) {
    	assert (Manager.edgeComp == null);    	
		Manager.edgeComp = edgeComp;
	}
    
   	public static EdgeCompositor getEdgeComp() {
   		assert (edgeComp != null);
		return edgeComp;
	}

	public static void setNodeComp(NodeCompositor nodeComp, boolean horz) {
		if (Manager.nodeComp == null)
			Manager.nodeComp = new NodeCompositor [2];
		assert (Manager.nodeComp[horz ? 0 : 1] == null);
		Manager.nodeComp[horz ? 0 : 1] = nodeComp;
	}

	public static NodeCompositor getNodeComp(boolean horz) {
		assert (nodeComp[horz ? 0 : 1] != null);
		return nodeComp[horz ? 0 : 1];
	}
	
	public static void setFilterManager(FilterManager filterManager) {
		assert (Manager.filterManager == null);
		Manager.filterManager = filterManager;
	}
	
	public static FilterManager getFilterManager() {
		assert (filterManager != null);
		return filterManager;
	}

	public static void setOverlayManager(OverlayManager overlayManager) {
		assert (Manager.overlayManager == null);
		Manager.overlayManager = overlayManager;
	}
	
	public static OverlayManager getOverlayManager() {
		assert (overlayManager != null);
		return overlayManager;
	}

	public static void setStatManager(StatManager statManager) {
		assert (Manager.statManager == null);
		Manager.statManager = statManager;
	}

	public static StatManager getStatManager() {
		assert (statManager != null);
		return statManager;
	}
	
	public static int getMaxNodeSize() {
		return Manager.maxNodeSize;
	}
}