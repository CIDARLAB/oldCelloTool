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
import java.io.Writer;

import org.cellocad.common.JSON.JSONUtils;
import org.cellocad.common.graph.graph.GraphTemplate;
import org.cellocad.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

	public Netlist (final JSONObject JObj) {
		this();
		this.parse(JObj);
	}

	/*
	 * Parse
	 */
	private void parseName(final JSONObject JObj){
		String name = ProfileUtils.getString(JObj, "name");
		if (name != null) {
			this.setName(name);
		}
	}

	private void parseNetlistNodes(final JSONObject JObj){
		JSONArray jsonArr;
		jsonArr = (JSONArray) JObj.get("nodes");
		if (jsonArr == null) {
			throw new RuntimeException("'nodes' missing in Netlist!");
		}
		for (int i = 0; i < jsonArr.size(); i++)
		{
			JSONObject jsonObj = (JSONObject) jsonArr.get(i);
			NetlistNode node = new NetlistNode(jsonObj);
			this.addVertex(node);
		}
	}

	private NetlistNode getNetlistNode(final JSONObject JObj, final String str){
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

	private void parseNetlistEdges(final JSONObject JObj){
		JSONArray jsonArr;
		NetlistNode node = null;
		jsonArr = (JSONArray) JObj.get("edges");
		if (jsonArr == null) {
			throw new RuntimeException("'edges' missing in Netlist!");
		}
		for (int i = 0; i < jsonArr.size(); i++)
		{
			JSONObject jsonObj = (JSONObject) jsonArr.get(i);
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

	private void parse(final JSONObject JObj){
		this.parseName(JObj);
		this.parseNetlistNodes(JObj);
		this.parseNetlistEdges(JObj);
	}

	/*
	 * WriteJSON
	 */
	protected String getJSONHeader(){
		String rtn = "";
		// name
		rtn += JSONUtils.getEntryToString("name", this.getName());
		return rtn;
	}

	protected String getJSONFooter(){
		String rtn = "";
		return rtn;
	}

	public void writeJSON(int indent, Writer os) throws IOException {
		String str = null;
		//header
		str = this.getJSONHeader();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		// nodes
		str = JSONUtils.getStartArrayWithMemberString("nodes");
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		for (int i = 0; i < this.getNumVertex(); i++){
			str = JSONUtils.addIndent(indent + 1, JSONUtils.getStartEntryString());
			os.write(str);
			this.getVertexAtIdx(i).writeJSON(indent + 2, os);
			str = JSONUtils.addIndent(indent + 1, JSONUtils.getEndEntryString());
			os.write(str);
		}
		str = JSONUtils.getEndArrayString();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		// edges
		str = JSONUtils.getStartArrayWithMemberString("edges");
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		for (int i = 0; i < this.getNumEdge(); i++){
			str = JSONUtils.addIndent(indent + 1, JSONUtils.getStartEntryString());
			os.write(str);
			this.getEdgeAtIdx(i).writeJSON(indent + 2, os);
			str = JSONUtils.addIndent(indent + 1, JSONUtils.getEndEntryString());
			os.write(str);
		}
		str = JSONUtils.getEndArrayString();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		//footer
		str = this.getJSONFooter();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
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
