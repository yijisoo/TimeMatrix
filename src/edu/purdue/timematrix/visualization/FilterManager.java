/* ------------------------------------------------------------------
 * FilterManager.java
 * 
 * Created 2009-03-17 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.visualization;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggEdge;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.data.BitSetColumn;
import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.data.Table;
import edu.purdue.timematrix.graph.Graph;

public class FilterManager {

	public final static String FILTER_COLUMN_NAME = "_filter";
	enum Type {Edge, Vertex};
	
	public static class Filter {
		private int index;
		private AggGraph aggGraph;
		private Column column;
		private BitSetColumn filter;
		private double minRatio = 0.0, maxRatio = 1.0;
		private Type type;
		private HashSet<AggNode> dirtyNodes = new HashSet<AggNode>();
		private HashSet<AggEdge> dirtyEdges = new HashSet<AggEdge>();
		
		public Filter(Column column, int index, BitSetColumn filter, AggGraph aggGraph, Type type) {
			this.aggGraph = aggGraph;
			this.column = column;
			this.index = index;
			this.filter = filter;
			this.type = type;
		}
		
		public void update(double minRatio, double maxRatio) { 
			this.minRatio = minRatio;
			this.maxRatio = maxRatio;
			apply();
		}
		
		public void updateMin(double minRatio) { 
			this.minRatio = minRatio;
			apply();
		}
		
		public void updateMax(double maxRatio) { 
			this.maxRatio = maxRatio;
			apply();
		}
		
		public double getMin() { 
			return minRatio;
		}
		
		public double getMax() { 
			return maxRatio;
		}
		
		public double getMinBound() {
			double range = column.getMax() - column.getMin();
			return minRatio * range + column.getMin();
		}
		
		public double getMaxBound() {
			double range = column.getMax() - column.getMin();
			return maxRatio * range + column.getMin();
		}
		
		private void apply() {
			double minBound = getMinBound();
			double maxBound = getMaxBound();
			
			boolean oldbset, newbset;
			
			assert(dirtyEdges.isEmpty());
			assert(dirtyNodes.isEmpty());
			
			System.err.println("Min, Max: " + minBound + ", " + maxBound);
			
			for (int row = 0; row < column.getRowCount(); row++) {
				BitSet bset = filter.getBitSetValueAt(row);
				double value = column.getRealValueAt(row);
				oldbset = filter.getBooleanValue(row);
				if (value < minBound || value > maxBound) {
					bset.clear(index);
				}
				else { 
					bset.set(index);
				}
				newbset = filter.getBooleanValue(row);
				if (oldbset != newbset) {
					addDirtyCandidate(row);
				}
			}
			
			markDirties();
		}
		
		private void addDirtyCandidate(int row) {
			if (type == Type.Edge) {
				addDirtyEdgeCandidate(row);
				return;
			}
			else if (type == Type.Vertex) {
				addDirtyVertextCandidate(row);
				return;
			} 
			throw new AssertionError("Unknown Type "+ type);
		}
		
		private void addDirtyEdgeCandidate (int row) {
			AggEdge edge = aggGraph.getEdge(row);
			
			assert (edge != null);
			
			dirtyEdges.add(edge);
			dirtyNodes.add(edge.getDst());
			dirtyNodes.add(edge.getSrc());			
		}
		
		private void addDirtyVertextCandidate (int row) {
			int index = aggGraph.mapRowToAggNode(row);
			
			if (index > 0) {
				AggNode node = aggGraph.getNode(index);
				//Manager.getOverlayManager().invalidate(node);
				dirtyNodes.add(node);
				
				ArrayList<AggEdge> edges = node.getAllInEdges();
				for (AggEdge edge: edges) {
					//Manager.getOverlayManager().invalidate(edge);
					dirtyEdges.add(edge);
				}
				
				edges = node.getAllOutEdges();
				for (AggEdge edge: edges) {
					//Manager.getOverlayManager().invalidate(edge);
					dirtyEdges.add(edge);
				}
			}
		}
		
		private void markDirties() {
			Iterator<AggNode> nodes = dirtyNodes.iterator();
			while (nodes.hasNext()) {
				AggNode node = nodes.next();
				Manager.getOverlayManager().invalidate(node);
			}
			dirtyNodes.clear();
			
			Iterator<AggEdge> edges = dirtyEdges.iterator();
			while (edges.hasNext()) {
				AggEdge edge = edges.next();
				Manager.getOverlayManager().invalidate(edge);
			}
			dirtyEdges.clear();
		}			
	}

	private AggGraph aggGraph;
	private BitSetColumn edgeFilterColumn, nodeFilterColumn;
	private Hashtable<Column, Filter> filters = new Hashtable<Column, Filter>();
	
	public FilterManager(AggGraph aggGraph, Graph graph) {
		this.aggGraph = aggGraph;
		
		// Create the edge and node filters
		edgeFilterColumn = createFilterColumn(graph.getEdgeTable(), graph.getEdgeColumnCount());
		nodeFilterColumn = createFilterColumn(graph.getVertexTable(), graph.getVertexColumnCount());
		
		// Now create the filters
		filters.clear();
		addTableFilters(graph.getEdgeTable(), edgeFilterColumn, Type.Edge);
		addTableFilters(graph.getVertexTable(), nodeFilterColumn, Type.Vertex);
	}
	
	private void addTableFilters(Table table, BitSetColumn filter, Type type) {
		int count = 0;
		for (int i = 0; i < table.getColumnCount(); i++) { 
			Column c = table.getColumnAt(i);
			if (c.isMeta()) continue;
			filters.put(c, new Filter(c, count++, filter, aggGraph, type));
		}		
	}
	
	private BitSetColumn createFilterColumn(Table table, int numBits) { 
		if (!table.hasColumn(FILTER_COLUMN_NAME)) {
			BitSetColumn filterColumn = new BitSetColumn(FILTER_COLUMN_NAME, numBits, true);
			for (int i = 0; i < table.getRowCount(); i++) { 
				filterColumn.addValue(true);
			}
			table.addColumn(filterColumn);
		}
		return (BitSetColumn) table.getColumn(FILTER_COLUMN_NAME);
	}
	
	public void updateFilter(Column c, double min, double max) { 
		Filter filter = getFilter(c);
		if (filter == null) return;
		filter.update(min, max);
	}
	
	public Filter getFilter(Column c) { 
		return filters.get(c);
	}
	
	public int getFilterCount() {
		return filters.size();
	}
	
	public Enumeration<Filter> filters() { 
		return filters.elements();
	}
	
	public BitSetColumn getEdgeFilterColumn() {
		return this.edgeFilterColumn;
	}
	
	public BitSetColumn getNodeFilterColumn() {
		return this.nodeFilterColumn;
	}
}