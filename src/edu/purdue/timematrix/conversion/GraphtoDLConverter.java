/* ------------------------------------------------------------------
 * GraphtoTSPConverter.java
 * 
 * Created 2008-12-08 by Niklas Elmqvist <elm@purdue.edu>.
 * Borrowed 2009-03-04 by Ji Soo Yi <yij@purdue.edu>.
 * ------------------------------------------------------------------
 */

package edu.purdue.timematrix.conversion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFileChooser;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggEdge;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.graph.BasicGraph;
import edu.purdue.timematrix.graph.Graph;
import edu.purdue.timematrix.io.GraphMLReader;

public class GraphtoDLConverter {
	private static final long serialVersionUID = 1L;
	private Graph graph = new BasicGraph("graph");
	private AggGraph aggGraph;
	
	public GraphtoDLConverter () {
		JFileChooser fileChooser = new JFileChooser(".");
        int ret = fileChooser.showOpenDialog(null);
        if (ret != JFileChooser.APPROVE_OPTION) {
        	System.err.println("User cancelation.");
        	return;
        }
        
        if (!loadGraph(fileChooser.getSelectedFile())) {
        	System.err.println("Failed to load graph file: " + fileChooser.getSelectedFile());
        	return;
        }

        aggGraph = new AggGraph(graph);
    	int numCulled = aggGraph.cullEmptyNodes();
    	System.err.println("Culled " + numCulled + " empty nodes.");
        
    	String outFileName = fileChooser.getSelectedFile().getAbsolutePath() + ".dl";
        if (!generateDLFile(outFileName)) {
        	System.err.println("Failed to create DL file: " + outFileName);
        	return;
        }

        System.err.println("Successfully create DL file: " + outFileName);
	}
	
	private boolean generateDLFile(String outFileName) {
		// Now write the graph
		try { 
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outFileName)));
			
//			int n = hierarchy.getNodeCount();
			Column colID = graph.getVertexTable().getColumn("id");
			
			// Boilerplate
			// The following example shows the DL format used for UCINET for Windows
			// The example can be found at page 31 of Ucinet 6 User's Guide
			//		dl n = 4 format = edgelist1
			//		labels:
			//		Sanders,Skvoretz,S.Smith,T.Smith
			//		data:
			//		1 2 1
			//		1 3 2
			//		2 1 1
			//		2 3 1
			//		2 4 
			//		3 1 1
			//		3 2 na
			//		4 2 10.3

			pw.println("dl n = " + colID.getValueAt(colID.getRowCount()-1) + " format = edgelist1");
			pw.println("data:");
			
			// Write Node
			
			ArrayList<AggNode> nodes = aggGraph.getNodes();
			
			
			for (AggNode node : nodes) {
				String src_id = (String) colID.getValueAt(node.getId());
				
				ArrayList<AggEdge> edges = node.getAllOutEdges();
				for (AggEdge edge : edges) {
					String dst_id = (String) colID.getValueAt(edge.getDst().getId());
					Collection<Integer> rows = edge.getAllItems();
					pw.println(src_id + " " + dst_id + " " + rows.size());
				}
			}
			
			pw.close();
		}
		catch (IOException e) { 
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private boolean loadGraph(File file) {
		try {
			InputStream in = new FileInputStream(file);
			GraphMLReader reader = new GraphMLReader(in, "graph", graph);
			reader.load();
			System.err.println("Read " + (graph.isDirected() ? "directed" : "undirected") + " graph '" + graph.getName() + "' with " + graph.getVerticesCount() + " vertices and " + graph.getEdgesCount() + " edges.");
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		new GraphtoDLConverter();
	}
}
