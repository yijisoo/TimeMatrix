/* ------------------------------------------------------------------
 * AggHierAllocationManager.java
 * 
 * Created 2009-02-11 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.visualization;

import java.util.ArrayList;
import java.util.List;

public class AggMatrixAllocationManager {

	public static final String PROPERTY_ALLOCS = "allocs";
	
	private ArrayList<AggMatrixAllocation> allocs = new ArrayList<AggMatrixAllocation>(); 
	
	public AggMatrixAllocationManager(int nodeCount, float baseCellSize) {
		initialize(nodeCount, baseCellSize);
	}
	
	public void clear() { 
		allocs.clear();
	}
	
	public int size() { 
		return allocs.size();
	}
	
	public void initialize(int nodeCount, float baseCellSize) {
		clear();
		for (int i = 0; i < nodeCount; i++) {
			allocs.add(new AggMatrixAllocation(baseCellSize));
		}		
	}
	
	public AggMatrixAllocation get(int index) {
		return allocs.get(index);
	}

	public int pick(float position) {
		float sumPosition = 0.0f;
		int index = 0;
		while (index < allocs.size() && position > sumPosition) { 
			sumPosition += allocs.get(index++).getAlloc();
		}
		if (position > sumPosition) index++;
		return index;
	}
	
	public void permutate(List<Integer> permutation) { 
		ArrayList<AggMatrixAllocation> newAllocList = new ArrayList<AggMatrixAllocation>();
		for (Integer i : permutation) {
			newAllocList.add(allocs.get(i));
		}
		allocs = newAllocList;
	}
	
	public void aggregate(int start, int length) { 
		ArrayList<AggMatrixAllocation> children = new ArrayList<AggMatrixAllocation>();
		for (int i = 0; i < length; i++) { 
			children.add(allocs.remove(start));
		}
		allocs.add(start, new AggMatrixAllocation(children));
	}

	public void collapse(int index) {
		
		// Cannot collapse leaves!
		AggMatrixAllocation alloc = allocs.get(index);
		if (alloc.isLeaf()) return;
		
		// Add the children instead
		allocs.remove(alloc);
		allocs.addAll(index, alloc.getChildren());
	}
}
