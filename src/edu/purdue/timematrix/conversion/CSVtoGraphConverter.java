package edu.purdue.timematrix.conversion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;

import edu.purdue.timematrix.data.BasicTable;
import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.data.Table;
import edu.purdue.timematrix.io.TableReader;

public class CSVtoGraphConverter {
	
	public static void main(String[] args) { 
		JFileChooser fileChooser = new JFileChooser(".");
        int ret = fileChooser.showOpenDialog(null);
        if (ret != JFileChooser.APPROVE_OPTION) {
        	return;
        }
        
    	Table nodeTable = new BasicTable();
    	TableReader nodeReader = new TableReader(fileChooser.getSelectedFile(), nodeTable);
		nodeReader.load();
//		for (int col = 0; col < nodeTable.getColumnCount(); col++) {
//			Column c = nodeTable.getColumn(col);
//			System.out.println("NEW COLUMN: " + c.getName() + " (" + c.getType() + ")");
//			for (int row = 0; row < nodeTable.getRowCount(); row++) {
//				System.out.println(c.getStringValue(row) + ",");
//			}
//		}

		ret = fileChooser.showOpenDialog(null);
        if (ret != JFileChooser.APPROVE_OPTION) {
        	return;
        }
    	Table edgeTable = new BasicTable();
    	TableReader edgeReader = new TableReader(fileChooser.getSelectedFile(), edgeTable);
		edgeReader.load();
		
		// Now write the graph
		try { 
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("output.graphml")));
			
			// Boilerplate
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			pw.println("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"");
			pw.println("	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			pw.println("	xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns"); 
			pw.println("	http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">");
			
			// Write the node keys
			for (int c = 0; c < nodeTable.getColumnCount(); c++) {
				Column col = nodeTable.getColumnAt(c);
				if (col.getName().equals("id")) continue;
				pw.println("	<key id=\"" + col.getName() + "\" for=\"node\" attr.name=\"" + col.getName() + "\" attr.type=\"" + col.getType() + "\"/>");
			}
				
			// Write the edge keys
			for (int c = 0; c < edgeTable.getColumnCount(); c++) {
				Column col = edgeTable.getColumnAt(c);
				if (col.getName().equals("from")) continue;
				if (col.getName().equals("to")) continue;
				pw.println("	<key id=\"" + col.getName() + "\" for=\"edge\" attr.name=\"" + col.getName() + "\" attr.type=\"" + col.getType() + "\"/>");
			}
			
			// Start the graph proper
			pw.println("	<graph id=\"G\" edgedefault=\"undirected\">");
			
			// Write the nodes
			Column idColumn = nodeTable.getColumn("id");
			for (int row = 0; row < nodeTable.getRowCount(); row++) { 
				pw.println("		<node id=\"" + idColumn.getStringValueAt(row) + "\">");
				for (int c = 0; c < nodeTable.getColumnCount(); c++) {
					Column col = nodeTable.getColumnAt(c);
					if (col.getName().equals("id")) continue;
					pw.print("			<data key=\"" + col.getName() + "\">");
					pw.print(col.getStringValueAt(row));
					pw.println("</data>");
				}
				pw.println("		</node>");
			}
			
			// Write the edges
			Column fromColumn = edgeTable.getColumn("from");
			Column toColumn = edgeTable.getColumn("to");
			for (int row = 0; row < edgeTable.getRowCount(); row++) {
				pw.println("		<edge id=\"e" + row +
						"\" source=\"" + fromColumn.getStringValueAt(row) +
						"\" target=\"" + toColumn.getStringValueAt(row) + "\">");
				for (int c = 0; c < edgeTable.getColumnCount(); c++) {
					Column col = edgeTable.getColumnAt(c);
					if (col.getName().equals("from")) continue;
					if (col.getName().equals("to")) continue;
					pw.print("			<data key=\"" + col.getName() + "\">");
					pw.print(col.getStringValueAt(row));
					pw.println("</data>");
				}
				pw.println("		</edge>");
			}

			pw.println("	</graph>");
			pw.println("</graphml>");
			pw.close();
		}
		catch (IOException e) { 
			e.printStackTrace();
		}
	}	
}
