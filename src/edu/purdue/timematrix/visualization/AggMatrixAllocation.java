/* ------------------------------------------------------------------
 * AggHierAllocation.java
 * 
 * Created 2009-02-11 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.visualization;

import java.util.ArrayList;
import java.util.Collection;

public class AggMatrixAllocation {
	private float alloc = 0.0f; 
	private ArrayList<AggMatrixAllocation> children = new ArrayList<AggMatrixAllocation>();
	public AggMatrixAllocation(float alloc) { 
		this.alloc = alloc;
	}
	public AggMatrixAllocation(Collection<AggMatrixAllocation> children) { 
		this.children.addAll(children);
		for (AggMatrixAllocation child : children) { 
			alloc += child.getAlloc();
		}
	}
	public float getAlloc() {
		return alloc;
	}
	public boolean isLeaf() {
		return children.size() == 0;
	}
	public Collection<AggMatrixAllocation> getChildren() { 
		return children;
	}
	public void redistribute(float newAlloc) {
		
		// Redistribute children according to their ratios of whole allocation
		for (AggMatrixAllocation child : children) { 
			float ratio = child.getAlloc() / getAlloc();
			child.redistribute(ratio * newAlloc);
		}
		
		// Finally store the new allocation
		this.alloc = newAlloc;
	}
}
