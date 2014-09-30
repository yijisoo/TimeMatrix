/* ------------------------------------------------------------------
 * ColumnSorter.java
 * 
 * Created 2008-10-30 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ColumnSorter {
	
	@SuppressWarnings("unchecked")
	private static class Item implements Comparable { 
		int index;
		Comparable item;
		public Item(int index, Comparable item) { 
			this.index = index;
			this.item = item;
		}
		public int getIndex() { 
			return index;
		}
		public int compareTo(Object o) {
			if (o instanceof Item) {
				Item i = (Item) o;
				return item.compareTo(i.item);
			}
			else return 0;
		}	
	}
	
	static public List<Integer> revSort(Column c) { 
		List<Integer> perm = sort(c);
		Collections.reverse(perm);
		return perm;
	}
	
	@SuppressWarnings("unchecked")
	static public List<Integer> sort(Column c) {
		
		// Create a dummy list
		ArrayList<Item> items = new ArrayList<Item>();
		for (int i = 0; i < c.getRowCount(); i++) {
			if (c instanceof StringColumn) { 
				items.add(new Item(i, c.getStringValueAt(i)));
			}
			else { 
				items.add(new Item(i, c.getRealValueAt(i)));
			}
		}
		
		// Sort the dummy list
		Collections.sort(items);
		
		// Extract the indices
		ArrayList<Integer> permutation = new ArrayList<Integer>();
		for (Item i : items) { 
			permutation.add(i.getIndex());
		}
		
		return permutation;
	}
}
