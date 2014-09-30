/** ------------------------------------------------------------------
 * EdgeCountStat.java
 * 
 * Created 2009-03-13 by Ji Soo Yi <yij@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggEdge;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.data.Column;

public class EdgeCountStat extends BasicStat {
	
	private boolean fullStatCalc = false;
	
	public EdgeCountStat(AggGraph aggGraph, Column aggColumn) {
		super(aggGraph, aggColumn);
		type = Type.EdgeStat;
		measure = Measure.EdgeCount;
		
		init();
	}
	
	public EdgeCountStat(AggGraph aggGraph, Column aggColumn, Column selectColumn, Object selectValue) {
		super(aggGraph, aggColumn, selectColumn, selectValue);
		type = Type.EdgeStat;
		measure = Measure.EdgeCount;
		
		init();
	}
	
	private void init() {
		assert !fullStatCalc;
				
		int ncount = aggGraph.getNodeCount();
		for (int i=0; i<ncount; i++) {
			for (int j=i+1; j<ncount; j++) {
				AggGraph.AggNode fromNode = aggGraph.getNode(i);
				AggGraph.AggNode toNode = aggGraph.getNode(j);
				AggGraph.AggEdge edge = fromNode.getConnectingEdge(toNode);
				
				if (edge == null) continue;
				
				TemporalChange tc = getTemporalChange(edge);
				
				if (tc.size() <= 0) continue;
				
				reflectStat(tc);
				updateUnfilteredTemporalChange(edge);
			}
		}
		
		fullStatCalc = true;
	}
	
	protected TemporalChange get_temporal_change(AggGraph.AggEdge edge, boolean isFiltered) {
		TemporalChange tc = new TemporalChange();
		
		if (isFiltered) {
			ArrayList<AggNode> nodes = new ArrayList<AggNode>();
			nodes.add(edge.getSrc()); 
			nodes.add(edge.getDst());
			
			for (AggNode node: nodes) {
				for (int i = 0; i < node.getItemCount(); i++) {
					int rowInVertexColumn = node.getItem(i);
					if (nodeFilterColumn.getBooleanValue(rowInVertexColumn) == false)
						return tc;
				}
			}
		}
		
		Double newValue = null, oldValue = null;
		int index;
		
		Collection<Integer> rows = edge.getAllItems();
		for (Integer row : rows) {
			index = aggColumn.getIntValueAt(row);			
			
			if (isFiltered && edgeFilterColumn != null) 
				if (edgeFilterColumn.getBooleanValue(row) == false)
					continue;
			
			if (selectColumn != null && !selectColumn.getValueAt(row).equals(selectValue))
				continue;
			
			newValue = 1.0;
			oldValue = tc.get(index);
			if (oldValue != null)
				 newValue += oldValue;
			tc.set(index, newValue);
		}
		
		return tc;
	}
	
	public void updateStat() {
		initStat();
		
		Iterator<AggEdge> edges = aggGraph.getEdges();
		while (edges.hasNext()) {
			updateStat(edges.next());
		}
	}

	public void updateStat(AggNode node) {
		for (AggEdge edge: node.getAllInEdges()) {
			updateStat(edge);
		}
		for (AggEdge edge: node.getAllOutEdges()) {
			updateStat(edge);
		}
	}

	private void updateStat(AggEdge edge) {
		TemporalChange tc = this.getTemporalChange(edge);
		reflectStat(tc);
	}
}