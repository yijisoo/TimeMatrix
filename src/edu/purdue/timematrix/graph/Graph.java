/* ------------------------------------------------------------------
 * Graph.java
 * 
 * Created 2008-12-08 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.graph;

import edu.purdue.timematrix.data.IntColumn;
import edu.purdue.timematrix.data.StringColumn;
import edu.purdue.timematrix.data.Table;

public interface Graph {
	
    int NIL = -1;	
	
    String getName();
    void setName(String name);

    void clear();
    
    boolean isDirected();    
    void setDirected(boolean directed);
    void expandUndirected();

    int getVerticesCount();
    int getEdgesCount();

    int addVertex();
    int addEdge(int from, int to);
    
    Table getEdgeTable();
    Table getVertexTable();
    
    StringColumn getVertexColumn();
    IntColumn getFromColumn();
    IntColumn getToColumn();
    
    public int getEdgeColumnCount();
    public int getVertexColumnCount();
}
