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
package org.cellocad.common.netlist;

import java.io.IOException;

import org.cellocad.common.graph.graph.GraphTemplate;
import org.cellocad.common.profile.ProfileUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 17, 2017
 *
 */
// TODO: Make all netlist data strings, place into Map?
public class Netlist extends GraphTemplate<NetlistNode, NetlistEdge>{

	public Netlist () {
		super();
	}

	public Netlist (final JsonObject JObj) {
		this();
		this.parse(JObj);
	}

	/*
	 * Parse
	 */
	private void parseName(final JsonObject JObj){
		String name = ProfileUtils.getString(JObj, "name");
		if (name != null) {
			this.setName(name);
		}
	}

	private void parseNetlistNodes(final JsonObject JObj){
		JsonArray jsonArr;
		jsonArr = JObj.getAsJsonArray("nodes");
		if (jsonArr == null) {
			throw new RuntimeException("'nodes' missing in Netlist!");
		}
		for (int i = 0; i < jsonArr.size(); i++)
		{
			JsonObject jsonObj = jsonArr.get(i).getAsJsonObject();
			NetlistNode node = new NetlistNode(jsonObj);
			this.addVertex(node);
		}
	}

	private NetlistNode getNetlistNode(final JsonObject JObj, final String str){
		NetlistNode rtn = null;
		String name = null;
		name = ProfileUtils.getString(JObj, str);
		if (name == null) {
			throw new RuntimeException("No name for" + str + "edges in Netlist!");
		}
		rtn = this.getVertexByName(name);
		if (rtn == null) {
			throw new RuntimeException("Node missing in Netlist " + name + ".");
		}
		return rtn;
	}

	private void parseNetlistEdges(final JsonObject JObj){
		JsonArray jsonArr;
		NetlistNode node = null;
		jsonArr = JObj.getAsJsonArray("edges");
		if (jsonArr == null) {
			throw new RuntimeException("'edges' missing in Netlist!");
		}
		for (int i = 0; i < jsonArr.size(); i++)
		{
			JsonObject jsonObj = jsonArr.get(i).getAsJsonObject();
			NetlistEdge edge = new NetlistEdge(jsonObj);
			this.addEdge(edge);
			node = getNetlistNode(jsonObj, "src");
			node.addOutEdge(edge);
			edge.setSrc(node);
			node = getNetlistNode(jsonObj, "dst");
			node.addInEdge(edge);
			edge.setDst(node);
		}
	}

	private void parse(final JsonObject JObj){
		this.parseName(JObj);
		this.parseNetlistNodes(JObj);
		this.parseNetlistEdges(JObj);
	}

	/*
	 * WriteJSON
	 */
	protected void writeJSONHeader(JsonWriter writer) throws IOException {
		// name
		writer.name("name").value(this.getName());
	}

	protected void writeJSONFooter(JsonWriter writer) throws IOException {
	}

	public void writeJSON(JsonWriter writer) throws IOException {
		//header
		this.writeJSONHeader(writer);
		writer.flush();
		// nodes
		writer.name("nodes");
		writer.beginArray();
		for (int i = 0; i < this.getNumVertex(); i++){
			writer.beginObject();
			this.getVertexAtIdx(i).writeJSON(writer);
			writer.endObject();
			writer.flush();
		}
		writer.endArray();
		writer.flush();
		// edges
		writer.name("edges");
		writer.beginArray();
		for (int i = 0; i < this.getNumEdge(); i++){
			writer.beginObject();
			this.getEdgeAtIdx(i).writeJSON(writer);
			writer.endObject();
			writer.flush();
		}
		writer.endArray();
		writer.flush();
		//footer
		this.writeJSONFooter(writer);
		writer.flush();
	}

	@Override
	public NetlistNode createV(final NetlistNode other) {
		NetlistNode rtn = new NetlistNode(other);
		return rtn;
	}

	@Override
	public NetlistEdge createE(final NetlistEdge other) {
		NetlistEdge rtn = new NetlistEdge(other);
		return rtn;
	}

}
