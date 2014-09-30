/* ------------------------------------------------------------------
 * GraphMLReader.java
 * 
 * Created 2008-12-08 by Niklas Elmqvist <elm@purdue.edu>.
 * Based on code by Jean-Daniel Fekete from the IVTK (ivtk.sf.net).
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.io;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.purdue.timematrix.data.Column;
import edu.purdue.timematrix.data.IntColumn;
import edu.purdue.timematrix.data.RealColumn;
import edu.purdue.timematrix.data.StringColumn;
import edu.purdue.timematrix.graph.Graph;

public class GraphMLReader extends AbstractXMLReader {
    protected Graph graph;
    protected int inNode;
    protected int inEdge;
    protected String keyFor;
    protected String type;
    protected String ID;
    protected Map<String, String> idName;
    protected StringBuffer characters;
    protected StringColumn nodeIdColumn;
    protected Map<String, Integer> nodeMap;
    
    public GraphMLReader(InputStream in, String name, Graph graph) {
        super(in, name);
        this.graph = graph;
    }
    
    private Column createColumn(String type, String name) {
    	Column c = null;
    	type = type.toLowerCase();
    	if (type.equals("categorical")) { 
    		c = new StringColumn(name);
    	}
    	else if (type.equals("string")) {
    		c = new StringColumn(name);
    	}
    	else if (type.equals("integer")) {
    		c = new IntColumn(name);
    	}
    	else if (type.equals("float")) {
    		c = new RealColumn(name);
    	}
    	return c;
    }

    public void declareKey(String keyFor, String ID, String type) {
        String name = ID;
        if (idName != null) {
            name = (String) idName.get(ID);
        }
        if (keyFor.equals("node")) {
            Column c = graph.getVertexTable().getColumn(name);
            if (c == null) {
                c = createColumn(type, name);
                graph.getVertexTable().addColumn(c);
            }
        }
        else if (keyFor.equals("edge")) {
            Column c = graph.getEdgeTable().getColumn(name);
            if (c == null) {
                c = createColumn(type, name);
                graph.getEdgeTable().addColumn(c);
            }
        }
    }

    public int findNode(String id) {
        Integer i = (Integer) nodeMap.get(id);
        if (i == null) {
            int node = graph.addVertex();
            nodeIdColumn.setValueAt(node, id);
            nodeMap.put(id, new Integer(node));
            return node;
        }
        else {
            return i.intValue();
        }
    }

    protected void addIdName(String ID, String name) {
        if (idName == null) {
            idName = new HashMap<String, String>();
        }
        idName.put(ID, name);
    }

    public void startDocument() throws SAXException {
        inNode = inEdge = Graph.NIL;
        nodeMap = new HashMap<String, Integer>();
        nodeIdColumn = (StringColumn) graph.getVertexTable().getColumn("id");
    }

    public void startElement(
            String namespaceURI,
            String localName,
            String qName,
            Attributes atts) throws SAXException {
        if (firstTag) {
            if (!qName.equals("graphml")) { 
                throw new SAXException("Expected a graph toplevel element");
            }
            firstTag = false;
        }
        if (qName.equals("graph")) {
            String edgedefault = atts.getValue("edgedefault");
            if ("directed".equals(edgedefault)) {
                graph.setDirected(true);
            }
            else if ("undirected".equals(edgedefault)) {
                graph.setDirected(false);
            }
            ID = atts.getValue("id");
            if (ID != null) { 
            	graph.setName(ID);
            }
        }
        
        if (qName.equals("key")) {
            keyFor = atts.getValue("for");
            ID = atts.getValue("id");
            String name = atts.getValue("attr.name");
            if (name != null) {
                addIdName(ID, name);
            }
            type = atts.getValue("attr.type");
            if (type == null) {
                characters = new StringBuffer();
            }
            else {
                characters = null;
                declareKey(keyFor, ID, type);
            }
        }
        else if (qName.equals("type")) {
            type = atts.getValue("name");
            characters = null;
            declareKey(keyFor, ID, type);
        }
        else if (qName.equals("node")) {
            ID = atts.getValue("id");
            inNode = findNode(ID);
        }
        else if (qName.equals("edge")) {
            String source = atts.getValue("source");
            String target = atts.getValue("target");

            int from = findNode(source);
            int to = findNode(target);
            inEdge = graph.addEdge(from, to);
        }
        else if (qName.equals("data")) {
            if (inNode != Graph.NIL || inEdge != Graph.NIL) {
                keyFor = atts.getValue("key");
                characters = new StringBuffer();
            }
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (qName.equals("key")) {
            if (type == null) {
                type = characters.toString();
                declareKey(keyFor, ID, type);
            }
            characters = null;
            ID = null;
        }
        else if (qName.equals("type")) {
            ; // nothing special
        }
        else if (qName.equals("node")) {
            inNode = Graph.NIL;
            ID = null;
        }
        else if (qName.equals("edge")) {
            inEdge = Graph.NIL;
            ID = null;
        }
        else if (qName.equals("data")) {
            Column c = null;
            int row = Graph.NIL;
            if (inNode != Graph.NIL) {
                c = graph.getVertexTable().getColumn(keyFor);
                row = inNode;
            }
            else if (inEdge != Graph.NIL) {
                c = graph.getEdgeTable().getColumn(keyFor);
                row = inEdge;
            }

            if (c != null) {
                String value = characters.toString();
                c.setValueAt(row, value);
            }
            characters = null;
            keyFor = null;
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (characters != null) {
            characters.append(ch, start, length);
        }
    }
}
