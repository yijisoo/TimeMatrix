/** ------------------------------------------------------------------
 * Stat.java
 * 
 * Created 2009-02-17 by Ji Soo Yi <yij@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.stat;

import edu.purdue.timematrix.aggregation.AggGraph.AggEdge;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.data.Column;

public interface Stat {
	enum Type { NodeStat, EdgeStat };
	enum Measure {DegreeCentrality, EdgeCount};
	
	Type getType();
	Measure getMeasure();
	boolean isTemporal();
	
	public TemporalChange getTemporalChange(AggNode node);
	public TemporalChange getTemporalChange(AggEdge edge);
	public TemporalChange getUnfilteredTemporalChange(AggNode node);
	public TemporalChange getUnfilteredTemporalChange(AggEdge edge);
	
	public void updateUnfilteredTemporalChange(AggNode node);
	public void updateUnfilteredTemporalChange(AggEdge edge);
	public void updateStat();
	public void updateStat(AggNode node);
		
	public Double getMin();
	public Double getMax();
	public Double getFrom();
	public Double getTo();
	public Double getSumMax();
	public Double getSumMin();
	
	public String getName();
	public Column getColumn();
}