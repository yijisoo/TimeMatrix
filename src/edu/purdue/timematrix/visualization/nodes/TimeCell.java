/* ------------------------------------------------------------------
 * TimeCellNode.java
 * 
 * Semantic Zooming Path
 * 
 * Created 2009-03-14 by Ji Soo Yi <yij@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.visualization.nodes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.stat.Stat;
import edu.purdue.timematrix.stat.TemporalChange;
import edu.purdue.timematrix.visualization.Manager;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class TimeCell extends TimeCellAbstract {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("TimeCell");
	private ZoomLevel zoomLevel = ZoomLevel.Undefined;
//	private TimeCellAbstract[] zoomableChildren;
	
	public TimeCell(Stat stat, AggGraph.AggEdge edge) {
		super(stat, edge);
		logger.info("");
		logger.setLevel(Level.OFF);
		init();
	}
	
	public TimeCell(Stat stat, AggGraph.AggNode node) {
		super(stat, node);
		logger.info("");
		logger.setLevel(Level.OFF);
		init();
	}
	
	private void init() {
		logger.info("");
		
		// Create Zoomable Children
		
//		zoomableChildren = new TimeCellAbstract[ZoomLevel.values().length];
		
//		TimeCellBarChart tcbc = null;
		TimeCellPixel tcp = null;
		
		switch (type) {
		case EdgeType:
//			tcbc = new TimeCellBarChart(this.stat, this.edge);
			tcp = new TimeCellPixel(this.stat, this.edge);
			break;
		case NodeType:
//			tcbc = new TimeCellBarChart(this.stat, this.node);
			tcp = new TimeCellPixel(this.stat, this.node);
			break;
		default:
			throw new AssertionError("Unknown type: " + type);
		}
		
//		this.addChild(tcbc);
		this.addChild(tcp);
		
//		zoomableChildren[ZoomLevel.ZoomedIn.ordinal()] = (TimeCellAbstract) tcbc; 
//		zoomableChildren[ZoomLevel.ZoomedOut.ordinal()] = (TimeCellAbstract) tcp;
//		zoomableChildren[ZoomLevel.LessThanPixel.ordinal()] = (TimeCellAbstract) tcp;		
	}
	
	public void setPaint(Paint newPaint) {
		logger.info("");
		super.setPaint(newPaint);
		for (int i=0; i < getChildrenCount(); i++) {
			getChild(i).setPaint(newPaint);
		}
//		for (int i = 0; i < zoomableChildren.length; i++) {
//			if (zoomableChildren[i] == null) continue;
//			zoomableChildren[i].setPaint(newPaint);
//		}
	}
	
	public boolean setBounds(double x, double y, double width, double height) {
		boolean ret = super.setBounds(x, y, width, height);
		for (int i=0; i < getChildrenCount(); i++) {
			getChild(i).setBounds(x, y, width, height);
		}
		return ret;
	}
	
	public void setDirty() {
		logger.info("setDirty" + edge + " + " + node);
		isDirty = true;
		invalidatePaint();
	}
	
	protected void paint(PPaintContext aPaintContext) {
		double s = aPaintContext.getScale();
		
		ZoomLevel oldZoomLevel = zoomLevel;
		
		if (s < 1.0)
			zoomLevel = ZoomLevel.LessThanPixel;
		else if (s <= 10.0)
			zoomLevel = ZoomLevel.ZoomedOut;
		else
			zoomLevel = ZoomLevel.ZoomedIn;
		
		if (oldZoomLevel != this.zoomLevel) {
			this.removeAllChildren();
			TimeCellAbstract tc;
			switch (this.zoomLevel) {
			case LessThanPixel:
			case ZoomedOut:
				if (this.edge != null)
					tc = new TimeCellPixel(this.stat, this.edge);
				else
					tc = new TimeCellPixel(this.stat, this.node);
				break;
			case ZoomedIn:
				if (this.edge != null)
					tc = new TimeCellBarChart(this.stat, this.edge);
				else
					tc = new TimeCellBarChart(this.stat, this.node);
				break;
			default:
				throw new AssertionError("Unknown ZoomLevel: " + this.zoomLevel);
			}
			
			tc.setPaint(getPaint());
			addChild(tc);
		}
		
		if (isDirty) {
			if (edge != null) {
				stat.updateUnfilteredTemporalChange(edge);
			}
			else if (node != null) {
				stat.updateUnfilteredTemporalChange(node);
			}
			else {
				assert(false);
			}
			isDirty = false;
			
			for (int i = 0; i < getChildrenCount(); i++) {
				getChild(i).invalidatePaint();
			}
		}
	}

//	private void changeZoomedChild(ZoomLevel zl) {
//		if (zoomableChildren[zl.ordinal()] == null) return;
//		this.removeAllChildren();
//		this.addChild(zoomableChildren[zl.ordinal()]);
//	}
	
	class TimeCellPixel extends TimeCellAbstract {
		private static final long serialVersionUID = 1L;

		public TimeCellPixel(Stat stat, AggGraph.AggEdge edge) {
			super(stat, edge);
			logger.info("");
			init();
		}
		
		public TimeCellPixel(Stat stat, AggGraph.AggNode node) {
			super(stat, node);
			logger.info("");
			init();
		}
		
		public void paint(PPaintContext aPaintContext) {
			if (edge != null)
				logger.info("");
			
			Graphics2D g2 = aPaintContext.getGraphics();
			double s = aPaintContext.getScale();
					
			TemporalChange vtc = getVTC();
		
			Double normValue = null;
			Color c = null;
			
			if (s < 3.0) {
				normValue = (vtc.sum() > 0) ? 1.0d : 0.0d;
			}
			else {
				int currentCellSize = 1;
				
				if (edge != null) {
					currentCellSize = edge.getSrc().getTotalItemCount();
				}
				else if (node != null) {
					currentCellSize = node.getTotalItemCount();
				}
				else {
					assert(false);
				}
				
//				Double globalMax = Manager.getStatManager().getGlobalMax(stat.getMeasure());
				
				normValue = (vtc.sum() * Manager.getMaxNodeSize()) / (stat.getSumMax() * currentCellSize);
				
				if (!(normValue >= 0 && normValue <= 1.0d)) {
					System.err.println("nv:" + normValue + ", vtc.sum:" + vtc.sum() +", getMaxNodeSize:" + Manager.getMaxNodeSize() + ", stat.getSumMax():" + stat.getSumMax() + ", currentCellSize:" + currentCellSize);
				}
			}
			
			c = (Color) getPaint();
			
			//assert(normValue >= 0 && normValue <= 1.0d);
			
			if (normValue > 1.0f)
				normValue = 1.0d;
			if (normValue < 0)
				normValue = 0.0d;

			Color encodedColor = new Color(	(int) (c.getRed() + (1.0d - normValue) * (255 - c.getRed())), 
										(int) (c.getGreen() + (1.0d - normValue) * (255 - c.getGreen())), 
										(int) (c.getBlue() + (1.0d - normValue) * (255 - c.getBlue())), 
										c.getAlpha());

			g2.setColor(encodedColor);
			g2.fill(getBounds());
		}
		
		private void init() {
			logger.info("");
			
			this.setBounds(0, 0, 1.0f, 1.0f);
//			this.setStroke(null);
			
			this.addInputEventListener(new PBasicInputEventHandler() {
				private PPath tooltip;
				public void mouseEntered(PInputEvent e) {
					TimeCellPixel tcp = (TimeCellPixel) e.getPickedNode();
					PText text = new PText(tcp.extractLabel());
					PBounds bounds = text.getBounds();
					tooltip = PPath.createRectangle((float) bounds.getMinX() - 2, (float) bounds.getMinY() - 2, (float) bounds.getWidth() + 2, (float) bounds.getHeight() + 2);
					tooltip.addChild(text);
					e.getCamera().addChild(tooltip);
					updateTooltip(e);
				}
				public void mouseMoved(PInputEvent e) { 
					updateTooltip(e);					
				}
				private void updateTooltip(PInputEvent e) { 
					Point2D p = e.getCanvasPosition();
					e.getPath().canvasToLocal(p, e.getCamera());
					tooltip.setOffset(p.getX() + 8, p.getY() - 8);
				}
				public void mouseExited(PInputEvent e) { 
					e.getCamera().removeChild(tooltip);
				}
			});
		}
		
		public String extractLabel() {
			logger.info("");
			StringBuffer sbuf = new StringBuffer("");
			
//			sbuf.append("Between ");
//			AggGraph.AggNode srcNode = this.edge.getSrc();
//			AggGraph.AggNode dstNode = this.edge.getDst();
//			
//			CompositorManager cm = CompositorManager.getInstance();
//			NodeCompositor hnc = (NodeCompositor) cm.getNodeComp(true);
//			NodeCompositor vnc = (NodeCompositor) cm.getNodeComp(false);
			
			TemporalChange vtc = getVTC();
			
			Vector<Double> vec = new Vector<Double>(vtc.getValues().keySet());
		    Collections.sort(vec);
		    
		    for (Enumeration<Double>ks = vec.elements(); ks.hasMoreElements(); ) {
		    	Double k = ks.nextElement();
		    	Double v = vtc.get(k);
		    	sbuf.append(k.intValue() + ":" + v.intValue()+",");
		    }
			
			if (sbuf.length()>0)
				sbuf.deleteCharAt(sbuf.length()-1);
						
			return sbuf.toString();
		}
	}

	class TimeCellBarChart extends TimeCellAbstract {
		private static final long serialVersionUID = 1L;
		private Hashtable<PPath, Double> bars = new Hashtable<PPath, Double>();
		private Hashtable<PPath, Double> greybars = new Hashtable<PPath, Double>();
		private TemporalChange vtc;
		private Double globalTo = null;
		private Double globalFrom = null;
		private Double globalMax = null;
		
		public TimeCellBarChart(Stat stat, AggGraph.AggEdge edge) {
			super(stat, edge);
			logger.info("");
			
			update();
		}
		
		public TimeCellBarChart(Stat stat, AggGraph.AggNode node) {
			super(stat, node);
			logger.info("");
//			this.setPathToRectangle(0, 0, 1.0f, 1.0f);
//			this.setStroke(new BasicStroke(0.1f));
			update();
		}
		
		public void paint(PPaintContext aPaintContext) {
			logger.info("");
			
			boolean needsUpdate = false;
			
			TemporalChange new_vtc = getVTC();
			
			if (vtc == null || !vtc.equals(new_vtc)) {
				vtc = new_vtc;
				needsUpdate = true;
			}
			
			if (	!globalTo.equals(stat.getTo()) ||
					!globalFrom.equals(stat.getFrom()) ||
					!globalMax.equals(stat.getMax())) {
				needsUpdate = true;
			}
			
			if (needsUpdate)
				update();
		}
		
		public void setPaint(Paint newPaint) {
			logger.info("");

			super.setPaint(newPaint);
			
			Enumeration<PPath> bs = bars.keys();
			while (bs.hasMoreElements()) {
				PPath b = bs.nextElement();
				b.setPaint(newPaint);
			}
			
//			Color c = (Color) getPaint();
//			System.err.println("alpha = " + c.getAlpha());
		}
		
		private void update() {
			logger.info("");
			
//			this.setPathToRectangle(0, 0, 1.0f, 1.0f);
//			this.setStroke(new BasicStroke(0.1f));
			
			this.removeAllChildren();
			bars.clear();
			greybars.clear();
			
			globalFrom = Manager.getStatManager().getGlobalFrom(stat.getMeasure());
			globalTo = Manager.getStatManager().getGlobalTo(stat.getMeasure());
			globalMax = Manager.getStatManager().getGlobalMax(stat.getMeasure());
			
			// Aggregate values
			float duration = (float) Math.ceil(globalTo - globalFrom + 1);

			// Build a visual representation
			float barWidth = 1.0f / duration; 
			float barHeight = 0.0f;
			float vbarHeight = 0.0f;
			
			for (int i = 0; i < duration; i++) {
				TemporalChange tc = getTC();
				TemporalChange vtc = getVTC();
				
				Double d = tc.get(i + globalFrom);
				Double vd = vtc.get(i + globalFrom);
				
				float f = (d == null) ? 0.0f : d.floatValue();
				float vf = (vd == null) ? 0.0f : vd.floatValue();
				
				PPath greybar = null;
				PPath bar = null;
				
				int currentCellSize = 1;
//				
				if (edge != null) {
					currentCellSize = edge.getSrc().getTotalItemCount();
				}
				else if (node != null) {
					currentCellSize = node.getTotalItemCount();
				}
				else {
					assert(false);
				}
				
				barHeight = (f * Manager.getMaxNodeSize()) / (globalMax.floatValue() * currentCellSize);
//				if (!(barHeight >= 0 && barHeight <= 1.0d)) {
//					System.err.println("barHeight:" +barHeight + ", f:" + f +", getMaxNodeSize:" + Manager.getMaxNodeSize() + ", globalMax:" + globalMax + ", currentCellSize:" + currentCellSize);
//				}
				if (barHeight > 1.0f) barHeight = 1.0f;
				
				vbarHeight = (vf * Manager.getMaxNodeSize())/ (globalMax.floatValue() * currentCellSize);
//				if (!(barHeight >= 0 && barHeight <= 1.0d)) {
//					System.err.println("vbarHeight:" +vbarHeight + ", vf:" + vf +", getMaxNodeSize:" + Manager.getMaxNodeSize() + ", globalMax:" + globalMax + ", currentCellSize:" + currentCellSize);
//				}
				if (vbarHeight > 1.0f) vbarHeight = 1.0f; 
				
				if (f > 0.0f && f > vf) {
					greybar = PPath.createRectangle(i * barWidth, 1.0f - barHeight, barWidth, barHeight - vbarHeight);
					greybar.setStroke(null);
//					greybar.setStroke(new PFixedWidthStroke(0.5f));
					Color org_c = (Color) getPaint();
					Color c = new Color(Color.gray.getRed(), Color.gray.getGreen(), Color.gray.getBlue(), (org_c == null) ? 255 : org_c.getAlpha());
					greybar.setPaint(c);
					greybars.put(greybar, i + globalFrom);
				}
				
				if (vf > 0.0f) {
					bar = PPath.createRectangle(i * barWidth, 1.0f - vbarHeight, barWidth, vbarHeight);
					bar.setStroke(null);
//					bar.setStroke(new PFixedWidthStroke(0.5f));
					bar.setPaint(getPaint());
					bars.put(bar, i + globalFrom);
				}
				
				addEventListenerToBar(greybar);
				addEventListenerToBar(bar);
				
//				PPath boundary = PPath.createRectangle(0, 0, 1.0f, 1.0f);
//				boundary.setStroke(new BasicStroke(0.01f));
//				boundary.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.0f));
//				this.addChild(boundary);
				
				if (greybar != null) this.addChild(greybar);
				if (bar != null) this.addChild(bar);	
			}
		}
		
		private void addEventListenerToBar(PPath bar) {
			logger.info("");
			
			if (bar == null) return;
				
			bar.addInputEventListener(new PBasicInputEventHandler() {
				private PPath tooltip;
				public void mouseEntered(PInputEvent e) {
					TimeCellBarChart tcbc = (TimeCellBarChart) e.getPickedNode().getParent();
					PText text = new PText(tcbc.extractLabel((PPath) e.getPickedNode()));
					PBounds bounds = text.getBounds();
					tooltip = PPath.createRectangle((float) bounds.getMinX() - 2, (float) bounds.getMinY() - 2, (float) bounds.getWidth() + 2, (float) bounds.getHeight() + 2);
					tooltip.addChild(text);
					e.getCamera().addChild(tooltip);
					updateTooltip(e);
				}
				public void mouseMoved(PInputEvent e) { 
					updateTooltip(e);					
				}
				private void updateTooltip(PInputEvent e) { 
					Point2D p = e.getCanvasPosition();
					e.getPath().canvasToLocal(p, e.getCamera());
					tooltip.setOffset(p.getX() + 8, p.getY() - 8);
				}
				public void mouseExited(PInputEvent e) { 
					e.getCamera().removeChild(tooltip);
				}
			});
		}
		
		public String extractLabel(PPath bar) {
			logger.info("");
			StringBuffer sbuf = new StringBuffer("");
			TemporalChange tc = getTC();
			Double index = bars.get(bar);			
			if (index != null) {
				Double value = tc.get(index);
				sbuf.append(index.intValue() + ":" + value.intValue());
			}
			else {
				index = greybars.get(bar);
				sbuf.append("(");
				if (index != null) {
					Double value = tc.get(index);
					sbuf.append(index.intValue() + ":" + value.intValue());
				}
				sbuf.append(")");
			}
			
			return sbuf.toString();
		}
	}	
}