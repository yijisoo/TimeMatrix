/* ------------------------------------------------------------------
 * TimeCell.java
 * 
 * Semantic Zooming Path
 * 
 * Created 2009-03-14 by Ji Soo Yi <yij@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.visualization.nodes;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.stat.Stat;
import edu.purdue.timematrix.stat.TemporalChange;
import edu.umd.cs.piccolo.PNode;

@SuppressWarnings("serial")
public abstract class TimeCellAbstract extends PNode {
	enum Type {Undefined, NodeType, EdgeType}
	protected Stat stat = null;
	protected AggGraph.AggEdge edge = null;
	protected AggGraph.AggNode node = null;
	protected Type type = Type.Undefined;
	protected boolean isDirty;
//	private static Logger logger = Logger.getLogger("TimeCellAbstract");
	
	public TimeCellAbstract(Stat stat, AggGraph.AggEdge edge) {
		this.stat = stat;
		this.edge = edge;
		this.type = Type.EdgeType;
	}
	
	public TimeCellAbstract(Stat stat, AggGraph.AggNode node) {
		this.stat = stat;
		this.node = node;
		this.type = Type.NodeType;
	}
	
	protected TemporalChange getVTC() {
		TemporalChange vtc = null;
		if (edge != null) {
			vtc = stat.getUnfilteredTemporalChange(edge);
		} else if (node != null) {
			vtc = stat.getUnfilteredTemporalChange(node);
		} else
			assert(false);
		assert (vtc != null);
		return vtc;
	}
	
	protected TemporalChange getTC() {
		TemporalChange tc = null;
		if (edge != null) {
			tc = stat.getTemporalChange(edge);
		} else if (node != null) {
			tc = stat.getTemporalChange(node);
		} else
			assert(false);
		assert (tc != null);
		return tc;
	}
}