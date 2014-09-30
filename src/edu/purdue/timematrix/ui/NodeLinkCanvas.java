package edu.purdue.timematrix.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class NodeLinkCanvas extends PCanvas {
	
	private static final long serialVersionUID = 1L;
	private PLayer nodeLayer, edgeLayer;

	public NodeLinkCanvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));

        // Initialize, and create a layer for the edges (always underneath the nodes)
        nodeLayer = getLayer();
        edgeLayer = new PLayer();
        getRoot().addChild(edgeLayer);
        getCamera().addLayer(0, edgeLayer);
        
        // Create event handler to move nodes and update edges
        nodeLayer.addInputEventListener(new PDragEventHandler() {
            {
                PInputEventFilter filter = new PInputEventFilter();
                filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
                setEventFilter(filter);
            }

            public void mouseEntered(PInputEvent e) {
                super.mouseEntered(e);
                if (e.getButton() == MouseEvent.NOBUTTON) {
                    e.getPickedNode().setPaint(Color.RED);
                }
            }
            
            public void mouseExited(PInputEvent e) {
                super.mouseExited(e);
                if (e.getButton() == MouseEvent.NOBUTTON) {
                    e.getPickedNode().setPaint(Color.WHITE);
                }
            }
            
            protected void startDrag(PInputEvent e) {
                super.startDrag(e);
                e.setHandled(true);
                e.getPickedNode().moveToFront();
            }
            
			@SuppressWarnings("unchecked")
			protected void drag(PInputEvent e) {
                super.drag(e);
                
                ArrayList<PPath> edges = (ArrayList<PPath>) e.getPickedNode().getAttribute("edges");
                for (int i = 0; i < edges.size(); i++) {
                    NodeLinkCanvas.this.updateEdge((PPath) edges.get(i));
                }
            }
        });
    }
	
	public void addNode(String name, float x, float y) {
		PPath node = PPath.createEllipse(0, 0, 20, 20);
        node.addAttribute("edges", new ArrayList<PPath>());
        PText text = new PText(name);
        text.translate(0, -15);
        node.addChild(text);
        node.translate(x, y);
        node.setChildrenPickable(false);
        nodeLayer.addChild(node);
	}
	
	@SuppressWarnings("unchecked")
	public void addEdge(int n1, int n2) {
		PNode node1 = nodeLayer.getChild(n1);
        PNode node2 = nodeLayer.getChild(n2);

        PPath edge = new PPath();
        ((ArrayList<PPath>) node1.getAttribute("edges")).add(edge);
        ((ArrayList<PPath>) node2.getAttribute("edges")).add(edge);
        edge.addAttribute("nodes", new ArrayList<PNode>());
        ((ArrayList<PNode>) edge.getAttribute("nodes")).add(node1);
        ((ArrayList<PNode>) edge.getAttribute("nodes")).add(node2);
        edgeLayer.addChild(edge);
        updateEdge(edge);    
	}
    
    @SuppressWarnings("unchecked")
	public void updateEdge(PPath edge) {
        PNode node1 = (PNode) ((ArrayList<PNode>) edge.getAttribute("nodes")).get(0);
        PNode node2 = (PNode) ((ArrayList<PNode>) edge.getAttribute("nodes")).get(1);
        Point2D start = node1.getFullBoundsReference().getCenter2D();
        Point2D end = node2.getFullBoundsReference().getCenter2D();
        edge.reset();
        edge.moveTo((float) start.getX(), (float) start.getY());
        edge.lineTo((float) end.getX(), (float) end.getY());
    }

}
