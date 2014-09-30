/* ------------------------------------------------------------------
 * TimeMatrixApplication.java
 * 
 * Created 2008-12-08 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.data.Table;
import edu.purdue.timematrix.graph.BasicGraph;
import edu.purdue.timematrix.graph.Graph;
import edu.purdue.timematrix.io.AbstractReader;
import edu.purdue.timematrix.io.GraphMLReader;
import edu.purdue.timematrix.io.PermutationReader;
import edu.purdue.timematrix.overlay.AggEdgeOverlay;
import edu.purdue.timematrix.overlay.NodeColorOverlay;
import edu.purdue.timematrix.overlay.NodeDegreeCentralityOverlay;
import edu.purdue.timematrix.overlay.NodeLabelOverlay;
import edu.purdue.timematrix.overlay.Overlay;
import edu.purdue.timematrix.overlay.OverlayManager;
import edu.purdue.timematrix.stat.DegreeCentralityStat;
import edu.purdue.timematrix.stat.EdgeCountStat;
import edu.purdue.timematrix.stat.Stat;
import edu.purdue.timematrix.stat.StatManager;
import edu.purdue.timematrix.ui.LabelCanvas;
import edu.purdue.timematrix.ui.MatrixCanvas;
import edu.purdue.timematrix.ui.OverlayAlphaEditor;
import edu.purdue.timematrix.ui.OverlayAlphaRenderer;
import edu.purdue.timematrix.ui.OverlayColorEditor;
import edu.purdue.timematrix.ui.OverlayColorRenderer;
import edu.purdue.timematrix.ui.OverlayTable;
import edu.purdue.timematrix.ui.OverviewCanvas;
import edu.purdue.timematrix.ui.RangeSlider;
import edu.purdue.timematrix.visualization.FilterManager;
import edu.purdue.timematrix.visualization.Manager;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolox.swing.PScrollPane;

public class TimeMatrixApplication extends JFrame implements TableModelListener, ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private Graph graph; // = new BasicGraph("graph");
	private MatrixCanvas matrixCanvas;
	private LabelCanvas rowCanvas, columnCanvas;
	private OverviewCanvas overviewCanvas;
	private FilterManager filterManager;
	private OverlayManager overlayManager; 
	private StatManager statManager;
	private AggGraph aggGraph;
	private RangeSlider rangeSlider;
	private JSplitPane sp;
	private JComboBox dimBox, filterBox;
	private JLabel valueLabel;
	

	public TimeMatrixApplication(Graph graph) {
		setTitle("TimeMatrix - Copyright (c) 2009 Purdue University (Elmqvist/Lee/Yi)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1024, 768));
//        
//        // Load the graph file
//        if (!loadGraph(file)) {
//        	System.exit(-1);
//        }
        this.graph = graph;
    	
        // Create the basic data structures
        aggGraph = new AggGraph(graph);
        Manager.setAggGraph(aggGraph);
    	int numCulled = aggGraph.cullEmptyNodes();
    	System.err.println("Culled " + numCulled + " empty nodes.");
        filterManager = new FilterManager(aggGraph, graph);
        Manager.setFilterManager(filterManager);
        
        // Create the user interface
    	buildUI();
    	pack();
	}
	
	public void run() {
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setVisible(true);		
	}
	
	private OverlayManager buildOverlays() { 
		
		boolean first = true;
        Table nodeTable = graph.getVertexTable();
        Table edgeTable = graph.getEdgeTable();
        
        statManager = new StatManager();
        OverlayManager overlays = new OverlayManager();
        
    	for (int i = 0; i < nodeTable.getColumnCount(); i++) {
    		Column colorColumn = nodeTable.getColumnAt(i);
    		if (colorColumn.isMeta() || colorColumn.getType() == Column.Type.String) continue;
    		Overlay overlay = new NodeColorOverlay(colorColumn);
    		overlays.addOverlay(overlay);
    		if (first) { 
    			first = false;
    			overlay.setVisible(true);
    		}
    	}
        
    	for (int i = 0; i < nodeTable.getColumnCount(); i++) {
    		Column labelColumn = nodeTable.getColumnAt(i);
    		if (labelColumn.isMeta()) continue;
    		Overlay overlay = new NodeLabelOverlay(labelColumn);
    		overlays.addOverlay(overlay);
    		if (first) { 
    			first = false;
    			overlay.setVisible(true);
    		}
    	}
        
        // Add the edge overlays
    	
    	Column timeColumn = null, typeColumn = null;
        
        for (int i = 0; i < edgeTable.getColumnCount(); i++) {
        	if (edgeTable.getColumnAt(i).getName().equalsIgnoreCase("year")) {
        		timeColumn = edgeTable.getColumnAt(i);
        	}
        	else if (edgeTable.getColumnAt(i).getName().equalsIgnoreCase("edgetype")) {
        		typeColumn = edgeTable.getColumnAt(i);
        	}
        }
        
        if (timeColumn != null && typeColumn != null) {
    		Stat stat;
        	Overlay overlay;
        	
        	stat = new EdgeCountStat(aggGraph, timeColumn, typeColumn, new Integer(1));
        	statManager.addStat(stat);
        	overlay = new AggEdgeOverlay((EdgeCountStat)stat);    		
        	overlays.addOverlay(overlay);
    		overlay.setVisible(true);
    		
    		stat = new EdgeCountStat(aggGraph, timeColumn, typeColumn, new Integer(2));
        	statManager.addStat(stat);
        	overlay = new AggEdgeOverlay((EdgeCountStat)stat);    		
        	overlays.addOverlay(overlay);
    		overlay.setVisible(false);
    		
        	stat = new DegreeCentralityStat(aggGraph, timeColumn, typeColumn, new Integer(1));
    		statManager.addStat(stat);
        	overlay = new NodeDegreeCentralityOverlay((DegreeCentralityStat)stat);
        	overlays.addOverlay(overlay);
        	overlay.setVisible(true);
        	
        	stat = new DegreeCentralityStat(aggGraph, timeColumn, typeColumn, new Integer(2));
    		statManager.addStat(stat);
        	overlay = new NodeDegreeCentralityOverlay((DegreeCentralityStat)stat);
        	overlays.addOverlay(overlay);
        	overlay.setVisible(false);
        }
        else {
        	first = true;
            for (int i = 0; i < edgeTable.getColumnCount(); i++) { 
        		Column aggColumn = edgeTable.getColumnAt(i);
        		if (aggColumn.isMeta()) continue;
        		EdgeCountStat stat = new EdgeCountStat(aggGraph, aggColumn);
        		statManager.addStat(stat);
        		Overlay overlay = new AggEdgeOverlay(stat);    		
            	overlays.addOverlay(overlay);
        		if (first) { 
        			first = false;
        			overlay.setVisible(true);
        		}
            }
            
         // Add the degree centrality overlays
            first = true;
            for (int i = 0; i < edgeTable.getColumnCount(); i++) {
            	Column aggColumn = edgeTable.getColumnAt(i);
        		if (aggColumn.isMeta()) continue;
        		DegreeCentralityStat stat = new DegreeCentralityStat(aggGraph, aggColumn);
        		statManager.addStat(stat);
            	Overlay overlay = new NodeDegreeCentralityOverlay(stat);
            	overlays.addOverlay(overlay);
            	if (first) {
            		first = false;
            		overlay.setVisible(true);
            	}
            }
        }
    	
        Manager.setStatManager(statManager);
        
        return overlays;
	}
	
	private void buildMenuBar() {
		
    	JMenu menu;
    	JMenuBar menuBar;
    	JMenuItem menuItem;
    	
    	menuBar = new JMenuBar();
    	
    	menu = new JMenu("File");
    	menu.setMnemonic(KeyEvent.VK_F);
    	
    	menuItem = new JMenuItem("Open aggregation");
    	menuItem.setMnemonic(KeyEvent.VK_O);
    	menuItem.addActionListener(this);
    	
    	menu.add(menuItem);
    	menuItem = new JMenuItem("Save aggregation");
    	menuItem.setMnemonic(KeyEvent.VK_S);
    	menuItem.addActionListener(this);
    	menu.add(menuItem);
    	
    	menu.addSeparator();

    	menuItem = new JMenuItem("Load permutation");
    	menuItem.addActionListener(this);
    	menu.add(menuItem);

    	menuItem = new JMenuItem("Save permutation");
    	menuItem.addActionListener(this);
    	menu.add(menuItem);

    	menu.addSeparator();

    	menuItem = new JMenuItem("Exit");
    	menuItem.setMnemonic(KeyEvent.VK_X);
    	menuItem.addActionListener(this);
    	menu.add(menuItem);

    	menuBar.add(menu);

    	menu = new JMenu("Help");
    	menu.setMnemonic(KeyEvent.VK_H);

    	menuItem = new JMenuItem("About");
    	menuItem.setMnemonic(KeyEvent.VK_A);
    	menuItem.addActionListener(this);
    	menu.add(menuItem);

    	menuBar.add(menu);

    	// Set the menu bar
    	setJMenuBar(menuBar);		
	}
	
	private void buildUI() {
		
		// Configure layout
		setLayout(new BorderLayout());
		
		// Create the overlays
    	overlayManager = buildOverlays();
    	Manager.setOverlayManager(overlayManager);
 
    	// Create the matrix canvas
    	matrixCanvas = new MatrixCanvas(new Dimension(1000, 1000), aggGraph, overlayManager);
    	    	
        // Create the label canvases
    	columnCanvas = new LabelCanvas(new Dimension(1000, 150), aggGraph, matrixCanvas, overlayManager, true);
    	rowCanvas = new LabelCanvas(new Dimension(150, 1000), aggGraph, matrixCanvas, overlayManager, false);
    	
    	// Create the overview canvas
    	overviewCanvas = new OverviewCanvas();
    	overviewCanvas.connect(matrixCanvas, new PLayer[]{matrixCanvas.getLayer()});
		
    	// Create PScrollPane
        PScrollPane scrollPane = new PScrollPane(matrixCanvas);
//        scrollPane.setCorner(PScrollPane.UPPER_LEFT_CORNER, overviewCanvas);
        scrollPane.setRowHeaderView(rowCanvas);
        scrollPane.setColumnHeaderView(columnCanvas);
        
		// Create the control panel
		JPanel controlPanel = buildControlPanel(overlayManager);
		        
		// Create the split panel
		scrollPane.setMinimumSize(new Dimension(1000, 1000));
		scrollPane.setPreferredSize(new Dimension(1000, 1000));
		controlPanel.setMinimumSize(new Dimension(100, 100));
		controlPanel.setPreferredSize(new Dimension(500, 1000));
		sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, controlPanel);
		
		getContentPane().add(sp);
		
		// Build the menu bar
		buildMenuBar();
	}

	private JPanel buildControlPanel(OverlayManager overlays) { 
		JPanel cp = new JPanel();
		cp.setLayout(new BoxLayout(cp, BoxLayout.PAGE_AXIS));
		
		// Create the overlay table
		OverlayTable table = new OverlayTable();
		table.addTableModelListener(this);
		for (Overlay overlay : overlays) { 
			table.addOverlay(overlay);
		}
		JTable overlayTable = new JTable(table);
		
		// Color selection 
		overlayTable.setDefaultRenderer(Color.class, new OverlayColorRenderer(true));
		overlayTable.setDefaultEditor(Color.class, new OverlayColorEditor());
		
		// Transparency (alpha) slider
		overlayTable.setDefaultRenderer(Double.class, new OverlayAlphaRenderer());
		overlayTable.setDefaultEditor(Double.class, new OverlayAlphaEditor());

		JScrollPane listScroller = new JScrollPane(overlayTable);
		listScroller.setPreferredSize(new Dimension(400, 200));
		cp.add(listScroller);
		
		// Sorting box
        Table nodeTable = graph.getVertexTable();
        Table edgeTable = graph.getEdgeTable();
		dimBox = new JComboBox();
		dimBox.setMaximumSize(new Dimension(10000, dimBox.getMinimumSize().height));
		for (int i = 0; i < nodeTable.getColumnCount(); i++) {
			Column column = nodeTable.getColumnAt(i);
			dimBox.addItem(column);
		}
		
		JButton ascSortButton = new JButton("Sort ascending");
		JButton descSortButton = new JButton("Sort descending");
		ascSortButton.addActionListener(this);
		descSortButton.addActionListener(this);
		
		JButton aggregateAllButton = new JButton("Aggregate All");
		aggregateAllButton.addActionListener(this);
		
		JButton collapseAllButton = new JButton("Collapse All");
		collapseAllButton.addActionListener(this);
		
		JPanel sortPanel = new JPanel();
		sortPanel.add(ascSortButton);
		sortPanel.add(descSortButton);
		sortPanel.add(aggregateAllButton);
		sortPanel.add(collapseAllButton);

		cp.add(new JLabel("Node ordering:"));
		cp.add(dimBox);
		cp.add(sortPanel);

		// Create the range slider
		rangeSlider = new RangeSlider(0,100, 0, 100);
		rangeSlider.setVisible(true);
		rangeSlider.setEnabled(true);
		rangeSlider.getModel().addChangeListener(this);

		// Filter control box
		filterBox = new JComboBox();
		filterBox.addActionListener(this);
		filterBox.setMaximumSize(new Dimension(10000, filterBox.getMinimumSize().height));
		for (int i = 0; i < edgeTable.getColumnCount(); i++) { 
			Column column = edgeTable.getColumnAt(i);
			if (!column.isMeta() && (column.getType() == Column.Type.Integer || column.getType() == Column.Type.Real)) filterBox.addItem(column);
		}
		for (int i = 0; i < nodeTable.getColumnCount(); i++) { 
			Column column = nodeTable.getColumnAt(i);
			if (!column.isMeta() && (column.getType() == Column.Type.Integer || column.getType() == Column.Type.Real)) filterBox.addItem(column);
		}
		
		valueLabel = new JLabel(filterBox.getItemAt(0).toString() + ":");
		
		cp.add(new JLabel("Range Slider:"));
		cp.add(filterBox);
		cp.add(rangeSlider);
		cp.add(valueLabel);
		
		return cp;
	}
	
//	private boolean loadGraph(File file) {
//		try {
//			InputStream in = new FileInputStream(file);
//			GraphMLReader reader = new GraphMLReader(in, "graph", graph);
//			boolean result = reader.load();
//			System.err.println("Read " + (graph.isDirected() ? "directed" : "undirected") + " graph '" + graph.getName() + "' with " + graph.getVerticesCount() + " vertices and " + graph.getEdgesCount() + " edges.");
//			return result;
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
	
	public void redraw() { 
		matrixCanvas.update();
		rowCanvas.update();
		columnCanvas.update();		
	}
	
	public void tableChanged(TableModelEvent e) {
		redraw();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Sort ascending")) { 
			aggGraph.sortNodes((Column) dimBox.getSelectedItem(), false);
		}
		else if (e.getActionCommand().equals("Sort descending")) {
			aggGraph.sortNodes((Column) dimBox.getSelectedItem(), true);
		}
		else if (e.getActionCommand().equals("Load permutation")) {
			JFileChooser fileChooser = new JFileChooser(".");
	        int ret = fileChooser.showOpenDialog(null);
	        if (ret == JFileChooser.APPROVE_OPTION) {
				List<Integer> permutation = PermutationReader.load(fileChooser.getSelectedFile());
				List<Integer> nodePerm = aggGraph.mapRowsToAggNodes(permutation);
				System.err.println("Size: " + permutation.size() + ", " + nodePerm.size());
				aggGraph.permutate(nodePerm);
	        }
		}
		else if (e.getSource() == filterBox) {
			BoundedRangeModel model = rangeSlider.getModel();
			FilterManager.Filter f = filterManager.getFilter((Column) filterBox.getSelectedItem());
			model.setRangeProperties((int) (f.getMin() * 100.0), (int) ((f.getMax() - f.getMin()) * 100.0), 0, 100, model.getValueIsAdjusting());
		}
		else if (e.getActionCommand().equals("Aggregate All")) {
			aggGraph.sortNodes((Column) dimBox.getSelectedItem(), false);
			matrixCanvas.aggregateAll((Column) dimBox.getSelectedItem());			
		}
		else if (e.getActionCommand().equals("Collapse All")) {
			matrixCanvas.collapseAll();
		}
		else {
			assert (false);
		}
	}
	
	public void stateChanged(ChangeEvent e) {
		BoundedRangeModel model = rangeSlider.getModel();
		FilterManager.Filter f = filterManager.getFilter((Column) filterBox.getSelectedItem());
		double minRatio = model.getValue() / 100.0;
		double maxRatio = (model.getValue() + model.getExtent()) / 100.0;
		f.update(minRatio, maxRatio);
		valueLabel.setText(((Column) filterBox.getSelectedItem()).getName() + ":" + f.getMinBound() + " - " + f.getMaxBound());
	}
	
	static class LoadAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private String url;
	    private JFileChooser fileChooser;
	    public LoadAction(String name, String url) {
	        super(name);
	        this.url = url;
	    }
        public void setFileChooser(JFileChooser fileChooser) {
            this.fileChooser = fileChooser;
        }        
        public JFileChooser getFileChooser() {
            return fileChooser;
        }
	    public void actionPerformed(ActionEvent e) {
	        if (fileChooser != null) {
	            fileChooser.cancelSelection();
	        }
	        startApplication(url);
	    }
	}
	
	public static Graph loadGraph(String url) {
		System.err.println("Using dataset " + url);
	    Graph graph = new BasicGraph("graph");	    
		try {
			InputStream in = AbstractReader.open(url);
			GraphMLReader reader = new GraphMLReader(in, "graph", graph);
			if (reader.load() == false) return null;
			System.err.println("Read " + (graph.isDirected() ? "directed" : "undirected") + " graph '" + graph.getName() + "' with " + graph.getVerticesCount() + " vertices and " + graph.getEdgesCount() + " edges.");
			return graph;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void startApplication(String url) {
		Graph g = loadGraph(url); 
		if (g == null) return; 
		TimeMatrixApplication app = new TimeMatrixApplication(g);
		app.run();
	}
    
    static final LoadAction[] dataSets = {
        new LoadAction("Tech Development (I)", "https://engineering.purdue.edu/~elm/projects/timematrix/techdevel.graphml")
        ,new LoadAction("Tech Development (I and K)", "http://web.ics.purdue.edu/~yij/projects/timematrix/techdevel2.graphml")
    };
    
	public static void main(String[] args) {
		JFileChooser fileChooser = new JFileChooser(".");
		Box buttonPanel = Box.createVerticalBox();
		for (int i = 0; i < dataSets.length; i++) {
            LoadAction la = dataSets[i];
            la.setFileChooser(fileChooser);
            JButton button = new JButton(la);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(
                    new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
            buttonPanel.add(button);
        }
        fileChooser.setAccessory(buttonPanel);		
        int ret = fileChooser.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
        	startApplication(fileChooser.getSelectedFile().getAbsolutePath());
        }
	}
}
