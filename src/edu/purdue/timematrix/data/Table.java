/* ------------------------------------------------------------------
 * Table.java
 * 
 * Created 2008-10-30 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.data;

import java.util.Iterator;

public interface Table {
	public void addColumn(Column column);
	public void removeColumn(Column column);
	public Column getColumnAt(int column);
	public Column getColumn(String name);
	public String getColumnName(int column);
	public int getColumnIndex(Column column);
	public Column.Type getColumnType(int column);
	public int getColumnCount();
	public int getRowCount();
	public Object[] getRowAt(int row);
	public void sort(int column, boolean ascending);
	public void addRow(Object... objects);
	public void clearRows();
	public void clear();
	public boolean hasColumn(String name);
	public Iterator<Column> columnIterator();
}
