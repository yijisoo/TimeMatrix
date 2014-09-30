/** ------------------------------------------------------------------
 * BasicStat.java
 * 
 * Created 2009-02-17 by Ji Soo Yi <yij@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.stat;

import java.util.Hashtable;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggEdge;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.data.BitSetColumn;
import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.visualization.Manager;

public abstract class BasicStat implements Stat {
//	private static Logger logger = Logger.getLogger("BasicStat");
	private boolean isTemporal = true;
	protected Type type = null;
	protected Measure measure = null;
	
	protected AggGraph aggGraph;
	protected Column aggColumn;
	protected BitSetColumn edgeFilterColumn;
	protected BitSetColumn nodeFilterColumn;
	
	protected Column selectColumn = null;
	protected Object selectValue = null;
	
	protected Double _from = null;
	protected Double _to   = null;	
	protected Double _min  = null;
	protected Double _max  = null;
	protected Double _sum_max = null;
	protected Double _sum_min = null;
	
	protected Hashtable<AggGraph.AggNode, TemporalChange> statNodeTable = new Hashtable<AggGraph.AggNode, TemporalChange> ();	
	protected Hashtable<AggGraph.AggNode, TemporalChange> vstatNodeTable = new Hashtable<AggGraph.AggNode, TemporalChange> ();
	protected Hashtable<AggGraph.AggEdge, TemporalChange> statEdgeTable = new Hashtable<AggGraph.AggEdge, TemporalChange> ();	
	protected Hashtable<AggGraph.AggEdge, TemporalChange> vstatEdgeTable = new Hashtable<AggGraph.AggEdge, TemporalChange> ();

	public Type getType() { return type; }
	public Measure getMeasure() { return measure; }
	public boolean isTemporal() { return isTemporal; }
	
	public Double getMin() {return _min;}
	public Double getMax() {return _max;}
	public Double getFrom() {return _from;}
	public Double getTo() {return _to;}
	public Double getSumMax() {return _sum_max;}
	public Double getSumMin() {return _sum_min;}
	
	public BasicStat(AggGraph aggGraph, Column aggColumn) {
		this.aggGraph = aggGraph;
		this.aggColumn = aggColumn;
		this.edgeFilterColumn = Manager.getFilterManager().getEdgeFilterColumn();
		this.nodeFilterColumn = Manager.getFilterManager().getNodeFilterColumn();
	}

	public BasicStat(AggGraph aggGraph, Column aggColumn, Column selectColumn, Object selectValue) {
		this (aggGraph, aggColumn);
		this.selectColumn = selectColumn;
		this.selectValue = selectValue;		
	}
	
	
	protected void initStat() {
		_max = null;
		_min = null;
		_from = null;
		_to = null;
		_sum_max = null;
		_sum_min = null;		
	}
	
	protected void reflectStat(TemporalChange tc) {
		if (tc.size() == 0)
			return;
		
		if (_max == null || tc.max() > _max) _max = tc.max();
		if (_min == null || tc.min() < _min) _min = tc.min();
		if (_from == null || tc.from() < _from) _from = tc.from();
		if (_to == null || tc.to() > _to) _to = tc.to();
		if (_sum_max == null || tc.sum() > _sum_max) _sum_max = tc.sum();
		if (_sum_min == null || tc.sum() < _sum_min) _sum_min = tc.sum();
	}
	
	protected TemporalChange get_temporal_change (AggGraph.AggNode node, boolean isFiltered) {
		throw new AssertionError("Invalid");
	}
	
	protected TemporalChange get_temporal_change (AggGraph.AggEdge edge, boolean isFiltered) {
		throw new AssertionError("Invalid");
	}
	
	public TemporalChange getTemporalChange(AggGraph.AggEdge edge) {
		if (!statEdgeTable.containsKey(edge)) {
			statEdgeTable.put(edge, get_temporal_change(edge, false));
		}
		return statEdgeTable.get(edge);
	}
	
	public TemporalChange getTemporalChange(AggGraph.AggNode node) {
		if (!statNodeTable.containsKey(node)) {
			statNodeTable.put(node, get_temporal_change(node, false));
		}
		return statNodeTable.get(node);
	}

	public TemporalChange getUnfilteredTemporalChange(AggNode node) {
		if (!vstatNodeTable.containsKey(node)) {
			vstatNodeTable.put(node, get_temporal_change(node, true));
		}
		return vstatNodeTable.get(node);
	}

	public TemporalChange getUnfilteredTemporalChange(AggEdge edge) {
		if (!vstatEdgeTable.containsKey(edge)) {
			vstatEdgeTable.put(edge, get_temporal_change(edge, true));
		}
		return vstatEdgeTable.get(edge);
	}

	public void updateUnfilteredTemporalChange(AggNode node) {
//		TemporalChange tc = 
			vstatNodeTable.remove(node);
		TemporalChange new_tc = get_temporal_change(node, true);
		vstatNodeTable.put(node, new_tc);
//		assert (tc == null || !tc.equals(new_tc));
	}

	public void updateUnfilteredTemporalChange(AggEdge edge) {
//		TemporalChange tc = 
			vstatEdgeTable.remove(edge);
		TemporalChange new_tc = get_temporal_change(edge, true);
		vstatEdgeTable.put(edge, new_tc);
//		assert (tc == null || !tc.equals(new_tc));
	}

	public String getName() {
		StringBuffer s = new StringBuffer();
		
		s.append (measure + ":" + aggColumn.getName());
		if (selectColumn != null && selectValue != null) {
			s.append("(" + selectColumn.getName() + " = " + selectValue.toString() + ")");
		}
		
		return s.toString();
	}
	
	public Column getColumn() {
		return this.aggColumn;
	}
}