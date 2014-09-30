/** ------------------------------------------------------------------
 * DegreeCentrality.java
 * 
 * Created 2009-02-17 by Ji Soo Yi <yij@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.stat;

import java.util.ArrayList;
import java.util.Collection;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.data.Column;

public class DegreeCentralityStat extends BasicStat {
	
	private boolean fullStatCalc = false;
	
	public DegreeCentralityStat(AggGraph aggGraph, Column aggColumn) {
		super(aggGraph, aggColumn);
		type = Type.NodeStat;
		measure = Measure.DegreeCentrality;
		
		init();
	}
	
	public DegreeCentralityStat(AggGraph aggGraph, Column aggColumn, Column selectColumn, Object selectValue) {
		super(aggGraph, aggColumn, selectColumn, selectValue);
		type = Type.NodeStat;
		measure = Measure.DegreeCentrality;
		
		init();
	}
	
	private void init() {
		assert !fullStatCalc;
				
		int ncount = aggGraph.getNodeCount();
		for (int i=0; i<ncount; i++) {
			AggNode node = aggGraph.getNode(i);
			TemporalChange tc = getTemporalChange(node);
			if (tc.size() <= 0) continue;
			reflectStat(tc);
			updateUnfilteredTemporalChange(node);
		}
		
		fullStatCalc = true;
	}
	
//	public TemporalChange getNormalizedStat(AggGraph.AggNode node) {
//		assert fullStatCalc;
//
//		return getStat(node).getNormalized(0.0, globalMax);
//	}
	
	protected TemporalChange get_temporal_change(AggGraph.AggNode node, boolean isFiltered) {
		TemporalChange tc = new TemporalChange();
		
		if (isFiltered) {
			for (int i = 0; i < node.getItemCount(); i++) {
				int rowInVertexColumn = node.getItem(i);
				if (nodeFilterColumn.getBooleanValue(rowInVertexColumn) == false)
					return tc;
			}
		}
			
		ArrayList<AggGraph.AggEdge> edges = node.getAllInEdges();
		Double newValue = null, oldValue = null;
		int index;
		
		for (AggGraph.AggEdge edge : edges) {
			Collection<Integer> rows = edge.getAllItems();
			for (Integer row : rows) {
				index = aggColumn.getIntValueAt(row);
				
				if (isFiltered && edgeFilterColumn != null) 
					if (edgeFilterColumn.getBooleanValue(row) == false)
						continue;
				
				if (selectColumn != null && !selectColumn.getValueAt(row).equals(selectValue))
					continue;

				newValue = 1.0d;
				oldValue = tc.get(index);
				if (oldValue != null)
					 newValue += oldValue;
				tc.set(index, newValue);
			}
		}
		
		return tc;
	}

	public void updateStat() {
		initStat();
		for (AggNode n: aggGraph.getNodes()) {			
			if (n.getAllOutEdges().size() <= 0) {
				System.err.println(n.toString());
				continue;
			}
			updateStat(n);
		}
	}
	
	public void updateStat(AggNode n) {
		TemporalChange tc = this.getTemporalChange(n);			
		reflectStat(tc);
	}
}