/* ------------------------------------------------------------------
 * BasicGraph.java
 * 
 * Created 2008-12-08 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.graph;

import edu.purdue.timematrix.data.BasicTable;
import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.data.IntColumn;
import edu.purdue.timematrix.data.StringColumn;
import edu.purdue.timematrix.data.Table;

public class BasicGraph implements Graph {
	
	private String name;
	private boolean directed = true;
	private BasicTable vertexTable = new BasicTable(); 
	private BasicTable edgeTable = new BasicTable();
	
	public BasicGraph(String name) {
		this.name = name;
		vertexTable.addColumn(new StringColumn("id", false));
		vertexTable.addColumn(new IntColumn("index", true));
		edgeTable.addColumn(new IntColumn("from", true));
		edgeTable.addColumn(new IntColumn("to", true));
	}
	
	public void expandUndirected() {
		int numEdges = edgeTable.getRowCount();
		for (int row = 0; row < numEdges; row++) { 
			Object[] r = edgeTable.getRowAt(row);
			Object temp = r[0];
			r[0] = r[1];
			r[1] = temp;
			edgeTable.addRow(r);
		}
	}

	public int addVertex() {
		vertexTable.addRow(null, vertexTable.getRowCount());
		return vertexTable.getRowCount() - 1;
	}

	public void clear() {
		vertexTable.clearRows();
		edgeTable.clearRows();
	}

	public Table getEdgeTable() {
		return edgeTable;
	}

	public String getName() {
		return name;
	}

	public Table getVertexTable() {
		return vertexTable;
	}

	public int getVerticesCount() {
		return vertexTable.getRowCount();
	}

	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEdgesCount() {
		return edgeTable.getRowCount();
	}

	public int addEdge(Object... objects) {
		int edgeIndex = edgeTable.getRowCount();
		edgeTable.addRow(objects);
		return edgeIndex;
	}
	
	public int addEdge(int from, int to) {
		int edgeIndex = edgeTable.getRowCount();
		edgeTable.addRow(from, to);
		return edgeIndex;
	}
	
	public IntColumn getFromColumn() {
		return (IntColumn) edgeTable.getColumnAt(0);
	}

	public IntColumn getToColumn() {
		return (IntColumn) edgeTable.getColumnAt(1);
	}

	public StringColumn getVertexColumn() {
		return (StringColumn) vertexTable.getColumnAt(0);
	}

	public int getEdgeColumnCount() {
		int numEdgeColumns = 0;
		for (int i = 0; i < edgeTable.getColumnCount(); i++) { 
			Column c = edgeTable.getColumnAt(i);
			if (!c.isMeta()) numEdgeColumns++;
		}
		return numEdgeColumns;
	}

	public int getVertexColumnCount() {
		int numVertexColumns = 0;
		for (int i = 0; i < vertexTable.getColumnCount(); i++) { 
			Column c = vertexTable.getColumnAt(i);
			if (!c.isMeta()) numVertexColumns++;
		}
		return numVertexColumns;
	}	
}
