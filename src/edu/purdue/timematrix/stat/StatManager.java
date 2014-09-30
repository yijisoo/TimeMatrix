/* ------------------------------------------------------------------
 * StatManager.java
 * 
 * Created 2009-03-18 by Ji Soo Yi <yij@purdue.edu>.
 * ------------------------------------------------------------------
 */

package edu.purdue.timematrix.stat;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import edu.purdue.timematrix.aggregation.AggGraph;
import edu.purdue.timematrix.aggregation.AggGraph.AggNode;
import edu.purdue.timematrix.stat.Stat.Measure;

public class StatManager implements Iterable<Stat> {
	
	private ArrayList<Stat> Stats = new ArrayList<Stat>();
	private Hashtable<Measure, Double> statGlobalFroms = new Hashtable<Measure, Double>();
	private Hashtable<Measure, Double> statGlobalTos = new Hashtable<Measure, Double>();
	private Hashtable<Measure, Double> statGlobalMaxs = new Hashtable<Measure, Double>();
	
	public StatManager() { 
		// empty
	}
	
	public Iterator<Stat> iterator() { 
		return Stats.iterator();
	}
	
	public int getStatCount() {
		return Stats.size();
	}
	
	public void addStat(Stat Stat) { 
		System.err.println("Adding Stat: " + Stat.getName());
		Stats.add(Stat);
	}
	
	public void removeStat(Stat Stat) { 
		System.err.println("Removing Stat: " + Stat.getName());
		Stats.remove(Stat);
	}
	
	public void clear() { 
		Stats.clear();
	}
	
	public void updateUnfilteredStat(AggGraph.AggNode node) {
		Iterator<Stat> itr = this.iterator();
		while (itr.hasNext()) {
			Stat s = itr.next();
			if (s.getType() != Stat.Type.NodeStat) continue;
			s.updateUnfilteredTemporalChange(node);
		}
	}
	
	public void udpateUnfilteredValidStat(AggGraph.AggEdge edge) {
		Iterator<Stat> itr = this.iterator();
		while (itr.hasNext()) {
			Stat s = itr.next();
			if (s.getType() != Stat.Type.EdgeStat) continue; 
			s.updateUnfilteredTemporalChange(edge);
		}
	}
	
	public void updateStats(AggNode node) {
		Iterator<Stat> itr = this.iterator();
		while (itr.hasNext()) {
			Stat s = itr.next();
			s.updateStat(node);
		}
		
		for (Measure m: Measure.values()) {
			updateGlobalStat(m);
		}
	}
	
	public void updateStats() {
		
		Iterator<Stat> itr = this.iterator();
		while (itr.hasNext()) {
			Stat s = itr.next();		 
			s.updateStat();
		}
		
		for (Measure m: Measure.values()) {
			updateGlobalStat(m);
		}
	}
	
	public Double getGlobalFrom(Measure measure) {
		if (!statGlobalFroms.containsKey(measure)) {
			updateGlobalStat(measure);
		}
		return statGlobalFroms.get(measure);
	}
	
	public Double getGlobalTo(Measure measure) {
		if (!statGlobalTos.containsKey(measure)) {
			updateGlobalStat(measure);
		}
		return statGlobalTos.get(measure);
	}

	public Double getGlobalMax(Measure measure) {
		if (!statGlobalMaxs.containsKey(measure)) {
			updateGlobalStat(measure);
		}
		return statGlobalMaxs.get(measure);
	}
	
	public void updateGlobalStat(Measure measure) {
		Double to = null;
		Double from = null;
		Double max = null;
		
		Iterator<Stat> itr = this.iterator();
		while (itr.hasNext()) {
			Stat s = itr.next();		 
			if (s.getMeasure() == measure) {
				if (to == null || s.getTo() > to) {
					to = s.getTo();
				}
				if (from == null || s.getFrom() < from) {
					from = s.getFrom();
				}
				if (max == null || s.getMax() > max) {
					max = s.getMax();
				}
			}
		}
		
		if (to == null || from == null || max == null) {
			System.err.println("updateGlobalStat(measure:" + measure + "): failed.");
			return;
		}

		if (statGlobalTos.containsKey(measure))
			statGlobalTos.remove(measure);
		if (statGlobalFroms.containsKey(measure))
			statGlobalFroms.remove(measure);
		if (statGlobalMaxs.containsKey(measure))
			statGlobalMaxs.remove(measure);
		
		statGlobalTos.put(measure, to);
		statGlobalFroms.put(measure, from);
		statGlobalMaxs.put(measure, max);
	}
}