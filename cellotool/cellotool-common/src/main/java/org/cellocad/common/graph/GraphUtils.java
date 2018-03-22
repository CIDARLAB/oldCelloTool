/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.common.graph;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.cellocad.common.Utils;
import org.cellocad.common.graph.graph.Edge;
import org.cellocad.common.graph.graph.Graph;
import org.cellocad.common.graph.graph.Vertex;

/**
 * @author: Vincent Mirian
 *
 * @date: Oct 28, 2017
 *
 */
public class GraphUtils {

	static private Vertex getAndAddIfNotPresent(final String name, final Graph g){
		Vertex rtn = null;
		rtn = g.getVertexByName(name);
		if (rtn == null){
			rtn = new Vertex();
			rtn.setName(name);
			g.addVertex(rtn);
		}
		return rtn;
	}

	static private void addVertexEdge(final String src, final String dst, final Graph g){
		Vertex Src = null;
		Vertex Dst = null;
		Src = getAndAddIfNotPresent(src, g);
		Dst = getAndAddIfNotPresent(dst, g);
		Edge e = new Edge(Src, Dst);
		e.setName(src + "." + dst);
		Src.addOutEdge(e);
		Dst.addInEdge(e);
		g.addEdge(e);
	}

	static public Graph getGraph(final String filename){
		// read csv
		Graph rtn = new Graph();
		Reader in = null;
		try {
			in = new FileReader(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Iterable<CSVRecord> records = null;
		try {
			records = CSVFormat.DEFAULT.parse(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (CSVRecord record : records) {
			if (record.size() < 2){
				continue;
			}
			String src = record.get(0);
			String dst = record.get(1);
			if ((src != null) &&
					(dst != null))
			{
				GraphUtils.addVertexEdge(src,dst, rtn);
			}
		}
		return rtn;
	}

	static public void writeDotFileForGraph(final Graph G, final String filename){
		try {
			String dotFilename = filename + ".dot";
			OutputStream outputStream = new FileOutputStream(dotFilename);
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			G.printDot(outputStreamWriter);
			outputStreamWriter.close();
			outputStream.close();
			// Unux-based only
			Utils.executeDot2PDFShellFilename(dotFilename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public void writeDotFileForGraph(final Graph G){
		String name = G.getName();
		if (name.isEmpty()){
			name = Utils.getTimeString();
		}
		GraphUtils.writeDotFileForGraph(G, name);
	}
}
