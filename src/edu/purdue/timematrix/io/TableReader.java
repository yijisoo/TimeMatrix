/* ------------------------------------------------------------------
 * TableReader.java
 * 
 * Created 2008-10-30 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.io;

import java.io.File;
import java.util.Iterator;

import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.data.IntColumn;
import edu.purdue.timematrix.data.RealColumn;
import edu.purdue.timematrix.data.StringColumn;
import edu.purdue.timematrix.data.Table;

public class TableReader {
	private File file;
	private Table table;
	
	public TableReader(File file, Table table) {
		this.file = file;
		this.table = table;
	}
	
	private void readHeader(Iterator<String> si) {
		String names = si.next();
		String types = si.next();
		if (names == null || types == null) return;
		String[] nameTokens = tokenize(names);
		String[] typeTokens = tokenize(types);
		for (int i = 0; i < nameTokens.length; i++) {
			if (nameTokens[i].length() == 0) { 
				if (i == nameTokens.length - 1) continue;
				nameTokens[i] = "column" + table.getColumnCount();
			}
			Column.Type type = Column.Type.String;
			if (i < typeTokens.length) {
				String typeString = typeTokens[i].toLowerCase().substring(0, 3);
				if (typeString.equals("int")) { 
					type = Column.Type.Integer;
				}
				else if (typeString.equals("rea") || typeString.equals("dou") || typeString.equals("flo") ) {
					type = Column.Type.Real;
				}
				else {
					type = Column.Type.String; 
				}
			}
			Column c = null;
			switch (type) {
			case Real:
				c = new RealColumn(nameTokens[i]);
				break;
			case Integer:
				c = new IntColumn(nameTokens[i]);
				break;
			case String:
				c = new StringColumn(nameTokens[i]);
				break;
			}
			table.addColumn(c);
		}
	}
	
	private String[] tokenize(String line) { 
		String[] tokens = line.split(";");
		for (String token : tokens) { 
			token = token.trim();
		}
		return tokens;
	}
	
	public void load() {
		LineReadIterator lri = new LineReadIterator(file.getAbsolutePath());
		
		// If there is no table header, read it
		if (table.getColumnCount() == 0) { 
			readHeader(lri);
		}
		// If there is one, discard the header data
		else { 
			lri.next();
			lri.next();
		}
		
		// Read the contents of the file
		while (lri.hasNext()) { 
			String line = lri.next();
			String[] tokens = tokenize(line);
			for (int i = 0; i < Math.max(tokens.length, table.getColumnCount()); i++) {
				if (i < table.getColumnCount()) {
					if (i < tokens.length) { 
						table.getColumnAt(i).addValue(tokens[i]);
					} 
					else { 
						table.getColumnAt(i).addValue("0");
					}
				}
			}
		}
	}
}
