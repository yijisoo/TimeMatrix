/* ------------------------------------------------------------------
 * MatrixCanvas.java
 * 
 * Created 2008-12-08 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.ui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.overlay.OverlayManager;
import edu.purdue.timematrix.visualization.AggMatrix;
import edu.purdue.timematrix.visualization.AggMatrixAllocationManager;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

public class MatrixCanvas extends PCanvas {
	
	private static final long serialVersionUID = 1L;
	
	private class MatrixZoomEventHandler extends PZoomEventHandler {
		private double minScale = 0;
		private double maxScale = Double.MAX_VALUE;
		
		public void mouseWheelRotated(PInputEvent aEvent) {
			super.mouseWheelRotated(aEvent);
			
			PCamera camera = aEvent.getCamera();
			double scaleDelta = (aEvent.getWheelRotation() > 0) ? 0.90 : 1.10;

			double currentScale = camera.getViewScale();
			double newScale = currentScale * scaleDelta;

			if (newScale < minScale) {
				scaleDelta = minScale / currentScale;
			}
			if ((maxScale > 0) && (newScale > maxScale)) {
				scaleDelta = maxScale / currentScale;
			}

			camera.scaleViewAboutPoint(scaleDelta, aEvent.getPosition().getX(), aEvent.getPosition().getY());
		}		
	}
	
	private class RubberRectangleHandler extends PBasicInputEventHandler {
		private PPath rubberband;
		private Point2D pressPoint, dragPoint;
		public void mousePressed(PInputEvent e) { 			
			super.mousePressed(e);
			pressPoint = e.getPosition();
			dragPoint = pressPoint;
			
			rubberband = new PPath();
			rubberband.setStroke(new BasicStroke((float) (1.0 / e.getCamera().getViewScale())));
			layer.addChild(rubberband);
			updateRubberband();
		}
		public void mouseReleased(PInputEvent e) {
			super.mouseReleased(e);
			layer.removeChild(rubberband);
			
			PBounds bounds = rubberband.getBounds();
			int startHorzIndex = matrix.getAllocations().pick((float) bounds.getMinX());
			int endHorzIndex = matrix.getAllocations().pick((float) bounds.getMaxX()) - 2;
			
			if (endHorzIndex <= startHorzIndex) // more than one node should be selected.
				return;
			
			aggregate(startHorzIndex, endHorzIndex - startHorzIndex + 1);
		}
		public void mouseDragged(PInputEvent e) { 
			super.mouseDragged(e);
			dragPoint = e.getPosition();
			updateRubberband();
		}
		public void mouseClicked(PInputEvent e) { 
			int horzIndex = matrix.getAllocations().pick((float) e.getPosition().getX()) - 1;			
			collapse(horzIndex);
		}
		public void updateRubberband() { 
			PBounds b = new PBounds();
			b.add(pressPoint);
			b.add(dragPoint);
            rubberband.setPathTo(b);
		}
	}
	
	private PLayer layer;
	private AggMatrix matrix;
	private AggGraph aggGraph;

	public MatrixCanvas(Dimension size, AggGraph aggGraph, OverlayManager overlayManager) {
		
		this.aggGraph = aggGraph;
		
		// Set preferred size
        setPreferredSize(size);

        // Create the layer
        layer = new PLayer();
        getRoot().addChild(layer);
        getCamera().addLayer(0, layer);
        
        // Create an input handler
        PBasicInputEventHandler squareHandler = new RubberRectangleHandler();
        squareHandler.setEventFilter(new PInputEventFilter(InputEvent.BUTTON2_MASK));
        addInputEventListener(squareHandler);
        
        // Replace Zoom Event Handler
        PZoomEventHandler matrixZoomHandler = new MatrixZoomEventHandler();
        this.setZoomEventHandler(matrixZoomHandler);

        // Add the matrix to the canvas
		matrix = new AggMatrix(aggGraph, overlayManager);
		layer.addChild(matrix);
	}
	
	public void update() { 
		matrix.layoutMatrix();
	}
	
	public AggMatrixAllocationManager getAllocations() { 
		return matrix.getAllocations();
	}

	public void aggregateAll(Column col) {		
		int start = -1, end = 0;		
		Object oldValue = null, newValue = null;
		
		while (end < aggGraph.getNodeCount()) {
			AggNode node = aggGraph.getNode(end);
			
			if (node.isLeaf()) {
				int row = node.getItem(0);			
				newValue = col.getValueAt(row);
			}
			
			if (node.isNode() || newValue != oldValue) {
				if (end - start > 1) 
					aggregate(start, end - start);
				start = start + 1;
				end = start + 1;
				oldValue = newValue;
			}
			else if (end == aggGraph.getNodeCount() - 1) {
				if (end - start > 0) 
					aggregate(start, end + 1 - start);
				start = start + 1;
				end = start + 1;				
			}
			else {
				end++;
			}
		}
	}
	
	public void collapseAll() {
		int i = 0;
		while (i < aggGraph.getNodeCount()) {
			if (aggGraph.getNode(i).isNode()) {
				collapse(i);				
			}
			else {
				i++;
			}
		}
	}
	
	private void aggregate(int start, int length) { 
		// Sanity check
		if (start < 0 || start + length > matrix.getAllocations().size()) {
			assert(false);
			return;
		}
		
		// Aggregate the layout
		matrix.getAllocations().aggregate(start, length);

		// Aggregate the hierarchy
		aggGraph.aggregate(start, length);
	}
	
	private void collapse(int index) {
		
		// Sanity check
		if (index < 0 || index >= matrix.getAllocations().size()) {
			assert(false);
			return;
		}

		// Collapse layout
		matrix.getAllocations().collapse(index);
		
		// Collapse the hierarchy
		aggGraph.collapse(index);
	}
}
