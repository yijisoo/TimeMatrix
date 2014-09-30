package edu.purdue.timematrix.aggregation;

import java.util.Collection;

public interface Aggregate<E> {
	public boolean isLeaf();
	public boolean isNode();
	public int getTotalItemCount();
	public int getAggregateCount();
	public int getItemCount();
	public void addItem(E item);
	public void addAggregate(Aggregate<E> aggregate);
	public E getItem(int index);
	public Aggregate<E> getAggregate(int index);
	public boolean containsItem(E item);
	public boolean containsAggregate(Aggregate<E> item);
	public Collection<E> getAllItems();
	public Collection<Aggregate<E>> getAllLeaves();
	public int getDepth();
}
