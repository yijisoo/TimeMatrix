/* ------------------------------------------------------------------
 * PermutationReader.java
 * 
 * Created 2009-03-11 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PermutationReader {
	
	static public List<Integer> load(File file) {
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		for (Iterator<String> i = new LineReadIterator(file); i.hasNext(); ) {
			
			// Read the next string
			String line = i.next();
			if (line == null || line.length() == 0) continue;
			
			// Tokenize the string 
			String[] tokens = line.split("\\s");
			for (int ndx = 0; ndx < tokens.length; ndx++) {
				try { 
					int value = Integer.parseInt(tokens[ndx]);
					nodes.add(value);
				}
				catch (NumberFormatException e) {}
			}
		}
		
		return nodes;
	}

}
