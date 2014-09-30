/* ------------------------------------------------------------------
 * NodeColorOverlay.java
 * 
 * Created 2009-02-12 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.overlay;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Hashtable;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.overlay.elements.BasicElement;
import edu.purdue.timematrix.overlay.elements.Element;
import edu.purdue.timematrix.visualization.AggMatrixHeader;
import edu.purdue.timematrix.visualization.ColorUtils;
import edu.umd.cs.piccolo.PNode;

public class NodeColorOverlay extends BasicOverlay {
	
	private Column colorColumn;
	private Hashtable<AggGraph.AggNode, NodeColorElement> horzTable = new Hashtable<AggGraph.AggNode, NodeColorElement>();	
	private Hashtable<AggGraph.AggNode, NodeColorElement> vertTable = new Hashtable<AggGraph.AggNode, NodeColorElement>();
	
	private static Color[] categoricalColors = { 
		Color.pink, Color.blue, Color.green, Color.red, Color.yellow, Color.orange, Color.magenta
	};
	private ArrayList<Color> colorRamp = new ArrayList<Color>();
	
	private class NodeColorElement extends BasicElement {
		private Color base;
		private boolean horz;
		public NodeColorElement(AggNode node, boolean horz) {
			PNode p = getRoot();
			this.horz = horz;
			p.setBounds(0, 0, AggMatrixHeader.cellWidth, AggMatrixHeader.cellSize);
			base = getColor(node);
			updatePaint();
		}
		private Color getColor(AggNode node) {	
			if (node.getItemCount() == 0) { 
				return new Color(1.0f, 1.0f, 1.0f, 0.0f);
			}
			
			int nodeRow = node.getItem(0);
			double val = (colorColumn.getRealValueAt(nodeRow) - colorColumn.getMin()) / (colorColumn.getMax() - colorColumn.getMin());
			return ColorUtils.rampCat(val, colorRamp);
		}
		public void updatePaint() {
			PNode p = getRoot();
			Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (255 * NodeColorOverlay.this.getAlpha()));
//			System.err.println()
			p.setPaint(c);
		}
		public void setBounds(double width, double height) {
//			AffineTransform t = getRoot().getTransformReference(true);
//			t.setToIdentity();
//			t.scale(width, height);
			
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
	
	public NodeColorOverlay(Column labelColumn) { 
		super("Color (" + labelColumn.getName() + ")", Overlay.Type.NodeOverlay);
		this.colorColumn = labelColumn;
		this.setColor(Color.green);
		for (int i = 0; i < categoricalColors.length; i++) { 
			colorRamp.add(categoricalColors[i]);
		}
	}

	public Element buildNodeElement(AggNode node, boolean horz) {
		
		// Look up the right table 
		Hashtable<AggGraph.AggNode, NodeColorElement> nodeTable;
		if (horz) { 
			nodeTable = horzTable;
		}
		else { 
			nodeTable = vertTable;
		}

		NodeColorElement nodeField;
		
		// If we have the node, don't build it again 
		if (nodeTable.containsKey(node)) {
			nodeField = nodeTable.get(node);
			nodeField.updatePaint();
		}
		else { 
			// We don't have it, build it
			nodeField = new NodeColorElement(node, horz);
			nodeTable.put(node, nodeField);
		}

		return nodeField;
	}
}
