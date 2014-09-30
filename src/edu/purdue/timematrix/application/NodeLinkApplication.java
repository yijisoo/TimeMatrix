/* ------------------------------------------------------------------
 * NodeLinkApplication.java
 * 
 * Created 2008-12-08 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import edu.purdue.timematrix.data.IntColumn;
import edu.purdue.timematrix.data.StringColumn;
import edu.purdue.timematrix.data.Table;
import edu.purdue.timematrix.graph.BasicGraph;
import edu.purdue.timematrix.graph.Graph;
import edu.purdue.timematrix.io.GraphMLReader;
import edu.purdue.timematrix.ui.NodeLinkCanvas;

public class NodeLinkApplication extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private File file;
	private Graph graph = new BasicGraph("graph");
	private NodeLinkCanvas nodeLinkCanvas;
	
	public NodeLinkApplication(File file) {
		this.file = file;
		setTitle("Node-Link Graph Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nodeLinkCanvas = new NodeLinkCanvas(1024, 768);
        getContentPane().add(nodeLinkCanvas);
        pack();
        setVisible(true);
        initialize();
	}

	public void initialize() {
		try {
			InputStream in = new FileInputStream(file);
			GraphMLReader reader = new GraphMLReader(in, "graph", graph);
			reader.load();
			System.err.println("Read graph '" + graph.getName() + "' with " + graph.getVerticesCount() + " vertices and " + graph.getEdgesCount() + " edges.");
			
			Random random = new Random();
			Table vertexTable = graph.getVertexTable();
			StringColumn idColumn = (StringColumn) vertexTable.getColumn("id");
			for (int i = 0; i < vertexTable.getRowCount(); i++) { 
				float x = random.nextInt(nodeLinkCanvas.getWidth());
	            float y = random.nextInt(nodeLinkCanvas.getHeight());
				nodeLinkCanvas.addNode(idColumn.getStringValueAt(i), x, y);
			}
			
			Table edgeTable = graph.getEdgeTable();
			IntColumn fromColumn = (IntColumn) edgeTable.getColumn("from");
			IntColumn toColumn = (IntColumn) edgeTable.getColumn("to");
			for (int i = 0; i < edgeTable.getRowCount(); i++) {
				int from = fromColumn.getIntValueAt(i); 
				int to = toColumn.getIntValueAt(i);
				nodeLinkCanvas.addEdge(from, to);
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		JFileChooser fileChooser = new JFileChooser(".");
        int ret = fileChooser.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {        	
    		new NodeLinkApplication(fileChooser.getSelectedFile());
        }		
	}
}
