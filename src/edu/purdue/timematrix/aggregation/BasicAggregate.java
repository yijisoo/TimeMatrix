package edu.purdue.timematrix.aggregation;

import java.util.ArrayList;
import java.util.Collection;

public class BasicAggregate<E> implements Aggregate<E> {
	protected ArrayList<E> items = new ArrayList<E>();
	protected ArrayList<Aggregate<E>> aggregates = new ArrayList<Aggregate<E>>();

	public void addAggregate(Aggregate<E> aggregate) {
		aggregates.add(aggregate);
	}

	public void addItem(E item) {
		items.add(item);
	}

	public Aggregate<E> getAggregate(int index) {
		return aggregates.get(index);
	}

	public int getAggregateCount() {
		return aggregates.size();
	}

	public E getItem(int index) {
		return items.get(index);
	}

	public int getItemCount() {
		return items.size();
	}

	public boolean isLeaf() {
		return getAggregateCount() == 0;
	}

	public boolean isNode() {
		return getAggregateCount() != 0;
	}
	
	public int getTotalItemCount() {
		int sum = getItemCount();
		for (Aggregate<E> aggregate : aggregates) { 
			sum += aggregate.getTotalItemCount();
		}
		return sum;
	}

	public boolean containsAggregate(Aggregate<E> aggregate) {
		if (this.equals(aggregate)) return true;
		for (Aggregate<E> child : aggregates) { 
			if (child.containsAggregate(aggregate)) return true;
		}
		return false;
	}

	public boolean containsItem(E item) {
		if (items.contains(item)) return true;
		for (Aggregate<E> child : aggregates) { 
			if (child.containsItem(item)) return true; 
		}
		return false;
	}

	public Collection<E> getAllItems() {
		ArrayList<E> itemList = new ArrayList<E>(items);
		for (Aggregate<E> aggregate : aggregates) { 
			Collection<E> subItemList = aggregate.getAllItems();
			itemList.addAll(subItemList);
		}
		return itemList;
	}

	public Collection<Aggregate<E>> getAllLeaves() {
		//TODO: Ji Soo wonders if items are leaves or not
		ArrayList<Aggregate<E>> leafList = new ArrayList<Aggregate<E>>();
		if (isLeaf()) { 
			leafList.add(this);
		}
		else { 
			for (Aggregate<E> aggregate : aggregates) {
				Collection<Aggregate<E>> subLeafList = aggregate.getAllLeaves();
				leafList.addAll(subLeafList);
			}
		}
		return leafList;
	}
	
	public int getDepth() {
		int maxDepth = 0;
		for (Aggregate<E> aggregate : aggregates) {
			int depth = aggregate.getDepth();
			if (depth > maxDepth) maxDepth = depth;
		}
		return maxDepth + 1;
	}
	
//	public void setDirty(boolean dirty) {
//		this.dirty = dirty;
//	}
//	
//	public boolean getDirty() {
//		return this.dirty;
//	}
}