/** ------------------------------------------------------------------
 * TemporalChange.java
 * 
 * Created 2009-02-17 by Ji Soo Yi <yij@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.stat;

import java.util.Enumeration;
import java.util.Hashtable;

public class TemporalChange {
	private Hashtable<Double, Double> values = new Hashtable<Double, Double>();
	
	private Double min = null;
	private Double max = null;
	private Double from = null;
	private Double to = null;
	private Double sum = 0.0d;
	
	public Double max() { return this.max; }
	public Double min() { return this.min; }
	public Double from() { return this.from; }
	public Double to() { return this.to; }
	public Double sum() { return this.sum; }
	
	public int size() { return values.size(); }
	
	public TemporalChange() {
	}
	
	public void set(double atTime, Double value) {
		boolean recalc_min_max = false;
		
		if (values.containsKey(atTime)) {
			Double removedValue = values.remove(atTime);
			sum -= removedValue;
			recalc_min_max = (removedValue == min || removedValue == max || atTime == from || atTime == to);			
		}
		
		if (min == null || value < min) min = value;
		if (max == null || value > max) max = value;
		if (from == null || atTime < from) from = atTime;
		if (to == null || atTime > to) to = atTime;
		
		values.put(atTime, value);
		sum += value;
		
		if (recalc_min_max) {
			recalcMinMaxFromTo();
		}			
	}
	
	private void recalcMinMaxFromTo() {
		min = null;
		max = null;
		from = null;
		to = null;

		Enumeration<Double> ks = values.keys();
		while (ks.hasMoreElements()) { 
			Double k = ks.nextElement();
			Double v = values.get(k);
			if (min == null || v < min) min = v;
			if (max == null || v > max) max = v;
			if (from == null || k < from) from = k;
			if (to == null || k > to) to = k;
		}
	}
	
	public Double get(double atTime) {
		return this.values.get(atTime);
	}
	
	public Hashtable<Double, Double> getValues() {
		return values;
	}
	
//	public TemporalChange getBetween(double fromTime, double toTime) {
//		assert fromTime <= toTime;
//		
//		TemporalChange subtc = new TemporalChange();
//		
//		Enumeration<Double> ts = values.keys();
//		while (ts.hasMoreElements()) { 
//			Double t = ts.nextElement();
//			if (t >= fromTime && t <= toTime)
//				subtc.set(t, values.get(t));
//		}
//		
//		return subtc;
//	}
	
	public String toString() {
		String s = "[";
		
		Enumeration<Double> ts = values.keys();
		while (ts.hasMoreElements()) { 
			Double t = ts.nextElement();
			s += String.valueOf(t) + ":";
			s += String.valueOf(values.get(t)) + ", ";
		}
		
		s += "]";
		
		return s;
	}
	
	public void print() {
		System.err.println(this.toString());
	}
	
	public boolean equals(TemporalChange tc) {
		if (this.size() != tc.size()) return false;		
		
		Enumeration<Double> ts = values.keys();
		while (ts.hasMoreElements()) { 
			Double t = ts.nextElement();
			if (!this.get(t).equals(tc.get(t)))
				return false;
		}
		
		return true;
	}
	
	public TemporalChange getNormalized(double overallMin, double overallMax) {
		TemporalChange normtc = new TemporalChange();
		
		Enumeration<Double> ts = values.keys();
		while (ts.hasMoreElements()) { 
			Double t = ts.nextElement();
			normtc.set(t, new Double((values.get(t) - overallMin) / (overallMax - overallMin)));
		}
		
		return normtc;
	}
}