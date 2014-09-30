/* ------------------------------------------------------------------
 * LineReadIterator.java
 * 
 * Created 2008-10-30 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class LineReadIterator implements Iterator<String> {
	private BufferedReader fw;
	private String currLine;
	
	public LineReadIterator(String file) {
		try {
			fw = new BufferedReader(new FileReader(file));
			currLine = fw.readLine();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public LineReadIterator(File file) {
		this(file.getAbsolutePath());
	}
	
	public boolean hasNext() {
		return currLine != null;
	}
	
	public String next() {
		String lastLine = currLine;
		try {
			currLine = fw.readLine();
		}
		catch (IOException e) {
			e.printStackTrace();
			currLine = null;
		}						
		return lastLine;
	}
	
	public void remove() {}
}
