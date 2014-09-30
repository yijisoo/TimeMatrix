import junit.framework.TestCase;
import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.data.IntColumn;
import edu.purdue.timematrix.graph.BasicGraph;
import edu.purdue.timematrix.stat.DegreeCentralityStat;
import edu.purdue.timematrix.stat.TemporalChange;

public class DegreeCentralityStatTest extends TestCase {
	protected AggGraph agg;
	protected DegreeCentralityStat stat;
	
	protected void setUp() throws Exception {
		super.setUp();
		BasicGraph g = new BasicGraph("test");
		
		g.addVertex(); // 0
		g.addVertex(); // 1
		g.addVertex(); // 2
		g.addVertex(); // 3
		
		Column c = new IntColumn("Year");
		g.getEdgeTable().addColumn(c);
		
		g.addEdge(0, 1, 1999);
		g.addEdge(0, 2, 1999);
		g.addEdge(1, 2, 2000);
		g.addEdge(1, 3, 2000);
		g.addEdge(2, 3, 2000);
		g.addEdge(0, 3, 2001);
		
		g.expandUndirected();
		
		agg = new AggGraph(g);
		
		stat = new DegreeCentralityStat(agg, c);
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDegreeCentralityStat() {
//		fail("Not yet implemented");
	}

	public void testGetStat_NoAggregate() {
		AggNode n0 = agg.getNode(0); 
		AggNode n1 = agg.getNode(1); 
		AggNode n2 = agg.getNode(2); 
		AggNode n3 = agg.getNode(3);
		
		TemporalChange tc0 = stat.getTemporalChange(n0);
		TemporalChange tc0_correct = new TemporalChange();
		tc0_correct.set(1999.0, new Double(2));
		tc0_correct.set(2001.0, new Double(1));
		if (!tc0.equals(tc0_correct))
			fail("error");
		
//		TemporalChange tc0n = stat.getNormalizedStat(n0);
//		TemporalChange tc0n_correct = new TemporalChange();
//		tc0n_correct.set(1999.0, new Double(1));
//		tc0n_correct.set(2001.0, new Double(0.5));
//		tc0n.print();
//		tc0n_correct.print();
//		if (!tc0n.equals(tc0n_correct))
//			fail("error");
		
		TemporalChange tc1 = stat.getTemporalChange(n1);
		TemporalChange tc1_correct = new TemporalChange();
		tc1_correct.set(1999.0, new Double(1));
		tc1_correct.set(2000.0, new Double(2));
		if (!tc1.equals(tc1_correct))
			fail("error");
		
		TemporalChange tc2 = stat.getTemporalChange(n2);
		TemporalChange tc2_correct = new TemporalChange();
		tc2_correct.set(1999.0, new Double(1));
		tc2_correct.set(2000.0, new Double(2));
		if (!tc2.equals(tc2_correct))
			fail("error");
		
		TemporalChange tc3 = stat.getTemporalChange(n3);
		TemporalChange tc3_correct = new TemporalChange();
		tc3_correct.set(2000.0, new Double(2));
		tc3_correct.set(2001.0, new Double(1));
		if (!tc3.equals(tc3_correct))
			fail("error");
	}

	public void testGetStat_Aggregate() {
		agg.aggregate(1, 2);
		
		AggNode n4 = agg.getNode(1);
		
		TemporalChange tc4 = stat.getTemporalChange(n4);
		TemporalChange tc4_correct = new TemporalChange();
		tc4_correct.set(1999.0, new Double(2));
		tc4_correct.set(2000.0, new Double(4));
		
		tc4.print();
		tc4_correct.print();
		
		if (!tc4.equals(tc4_correct))
			fail("error");
	}
	
	public void testGetCentrality() {	

	}

	public void testGetType() {

	}

	public void testIsTemporal() {
		
	}
}
