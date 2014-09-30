/* ------------------------------------------------------------------
 * LabelCanvas.java
 * 
 * Created 2009-02-11 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.ui;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.overlay.OverlayManager;
import edu.purdue.timematrix.visualization.AggMatrixHeader;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.util.PAffineTransform;

public class LabelCanvas extends PCanvas {

	private static final long serialVersionUID = 1L;
	
	private class ViewSynchronizer implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
//			System.err.println("ViewSynchronizer - propertyChange()" + header.isHorizontal());
			PAffineTransform newTransform = (PAffineTransform) evt.getNewValue();
			PAffineTransform labelTransform = new PAffineTransform();
			if (header.isHorizontal()) {
				labelTransform.translate(newTransform.getTranslateX(), 0.0);
				labelTransform.scale(newTransform.getScaleX(), 1.0);
				header.scale(1.0, newTransform.getScaleY());
			}
			else { 
				labelTransform.translate(0.0, newTransform.getTranslateY());
				labelTransform.scale(1.0, newTransform.getScaleY());
				header.scale(newTransform.getScaleX(), 1.0);
			}
			getCamera().setViewTransform(labelTransform);
		}
	}

	private AggMatrixHeader header;
	private PLayer layer;
	
	public LabelCanvas(Dimension size, AggGraph hierarchy, MatrixCanvas canvas, OverlayManager overlays, /* Column labelColumn, */ boolean horz) {
		
		// Set a preferred size
		setPreferredSize(size);
		
		// Disable zoom and pan
		setPanEventHandler(null);
		setZoomEventHandler(null);
		
		// Listen for view transform changes
		canvas.getCamera().addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, new ViewSynchronizer());
		
		// Add the new layer
        layer = new PLayer();
        getRoot().addChild(layer);
        getCamera().addLayer(0, layer);

        // Create the matrix label and add it
        header = new AggMatrixHeader(hierarchy, canvas.getAllocations(), overlays, horz);
		if (horz) {
			header.rotate(-Math.PI / 2.0);
			header.translate(-AggMatrixHeader.cellWidth, 0);
		}
        layer.addChild(header);
	}
	
	public void update() {
//		System.err.println("LabelCanvas - update()");
		header.layoutLabels();
	}
}
