/* ------------------------------------------------------------------
 * NodeLabelOverlay.java
 * 
 * Created 2009-02-12 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.overlay.elements.BasicElement;
import edu.purdue.timematrix.overlay.elements.Element;
import edu.umd.cs.piccolo.nodes.PText;

public class NodeLabelOverlay extends BasicOverlay {
	
	private Column labelColumn;
	private Hashtable<AggGraph.AggNode, NodeLabelElement> horzTable = new Hashtable<AggGraph.AggNode, NodeLabelElement>();	
	private Hashtable<AggGraph.AggNode, NodeLabelElement> vertTable = new Hashtable<AggGraph.AggNode, NodeLabelElement>();	
	
	private class NodeLabelElement extends BasicElement {
		
		private boolean horz;
		
		public NodeLabelElement(AggNode node, boolean horz) {
			super(new PText());
			this.horz = horz;
			if (node.getItemCount() > 0) {			
				int nodeRow = node.getItem(0);
				assert (nodeRow < labelColumn.getRowCount());
				PText text = (PText) getRoot();
				text.setText(labelColumn.getStringValueAt(nodeRow));
			}
			
			updatePaint();
		}
		
		public void updatePaint() { 
			PText text = (PText) getRoot();
			Color base = NodeLabelOverlay.this.getColor();
			Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (255 * NodeLabelOverlay.this.getAlpha()));
			text.setTextPaint(c);
		}
		
		public void setBounds(double width, double height) {
			AffineTransform t = getRoot().getTransformReference(true);
			t.setToIdentity();
			double textScale = Math.min(height / getRoot().getHeight(), width / getRoot().getWidth());
			if (!horz) {
				t.scale(textScale, textScale);
				t.translate(width / textScale - getRoot().getWidth(), 0);
			}
			else { 
				t.scale(textScale, textScale);
			}
		}
	}
	
	public NodeLabelOverlay(Column labelColumn) { 
		super("Label (" + labelColumn.getName() + ")", Overlay.Type.NodeOverlay);
		this.labelColumn = labelColumn;
		this.setColor(Color.black);
	}

	public Element buildNodeElement(AggNode node, boolean horz) {
		
		// Look up the right table 
		Hashtable<AggGraph.AggNode, NodeLabelElement> nodeTable;
		if (horz) { 
			nodeTable = horzTable;
		}
		else { 
			nodeTable = vertTable;
		}

		NodeLabelElement label;
		
		// If we have the node, don't build it again 
		if (nodeTable.containsKey(node)) {
			label = nodeTable.get(node);
			label.updatePaint();
		}
		else { 
			// We don't have it, build it
			label = new NodeLabelElement(node, horz);
			nodeTable.put(node, label);
		}

		return label;
	}
}
