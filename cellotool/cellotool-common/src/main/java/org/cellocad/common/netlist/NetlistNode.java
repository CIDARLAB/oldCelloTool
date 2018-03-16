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

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.common.JSON.JSONUtils;
import org.cellocad.common.graph.graph.VertexTemplate;
import org.cellocad.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 17, 2017
 *
 */
public class NetlistNode extends VertexTemplate<NetlistEdge>{

	private void setDefault() {
		this.setNodeType("");
		this.setPartitionID(-1);
	}
	
	public NetlistNode(){
		super();
		this.setDefault();
	}

	public NetlistNode(final NetlistNode other){
		super(other);
		this.setDefault();
	}
	
	public NetlistNode(final JSONObject JObj){
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
	
	private void parseNodeType(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "nodeType");
		if (value != null) {
			this.setNodeType(value);
		}
	}
	
	private void parsePartitionID(final JSONObject JObj){
		Integer value = ProfileUtils.getInteger(JObj, "partitionID");
		if (value != null) {
			this.setPartitionID(value.intValue());
		}
	}

	private void parseGate(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "gate");
		if (value != null) {
			this.setGate(value);
		}
	}

	private void parseParts(final JSONObject JObj){
		Object value = ProfileUtils.getObject(JObj, "parts");
		if (value != null) {
			JSONArray array = (JSONArray) value;
			CObjectCollection<CObject> parts = new CObjectCollection<>();
			for (Object obj : array) {
				parts.add(this.parsePart((JSONObject)obj));
			}
			this.setParts(parts);
		}
	}

	private CObject parsePart(final JSONObject JObj){
		CObject obj = new CObject();
		String name = ProfileUtils.getString(JObj, "name");
		if (name != null) {
			obj.setName(name);
		}
		Integer type = ProfileUtils.getInteger(JObj, "type");
		if (type != null) {
			obj.setType(type);
		}
		Integer idx = ProfileUtils.getInteger(JObj, "idx");
		if (idx != null) {
			obj.setIdx(idx);
		}
		return obj;
	}
	
	private void parse(final JSONObject JObj){
    	this.parseName(JObj);
    	this.parseNodeType(JObj);
    	this.parsePartitionID(JObj);
		this.parseGate(JObj);
		this.parseParts(JObj);
	}
	
	/*
	 * Inherit
	 */
	@Override
	protected void addMeToSrc(final NetlistEdge e) {
		e.setSrc(this);
	}

	@Override
	protected void addMeToDst(final NetlistEdge e) {
		e.setDst(this);
	}

	@Override
	public NetlistEdge createT(final NetlistEdge e) {
		NetlistEdge rtn = null;
		rtn = new NetlistEdge(e);
		return rtn;
	}

	/*
	 * Partition ID
	 */
	public void setPartitionID(int pID) {
		this.partitionID = pID;
	}
	
	public int getPartitionID() {
		return this.partitionID;
	}
	
	private int partitionID;

	/*
	 * NodeType
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
	public String getNodeType() {
		return this.nodeType;
	}
	
	private String nodeType;
	
	/*
	 * Gate
	 */
	public void setGate(String gate) {
		this.gate = gate;
	}
	
	public String getGate() {
		return this.gate;
	}
	
	private String gate;
	
	/*
	 * Parts
	 */
	/**
	 * @param parts the parts to set
	 */
	public void setParts(CObjectCollection<CObject> parts) {
		this.parts = parts;
	}

	/**
	 * @return the collection of parts that comprise the gate
	 */
	public CObjectCollection<CObject> getParts() {
		return this.parts;
	}

	private CObjectCollection<CObject> parts;
	
	/*
	 * Write
	 */	
	protected String getJSONHeader(){	
		String rtn = "";
		// name
		rtn += JSONUtils.getEntryToString("name", this.getName());
		// NodeType
		rtn += JSONUtils.getEntryToString("nodeType", this.getNodeType());
		// partitionID
		rtn += JSONUtils.getEntryToString("partitionID", this.getPartitionID());
		// gate
		rtn += JSONUtils.getEntryToString("gate", this.getGate());
		// parts
		if (this.getParts() != null
			&&
			this.getParts().size() > 0)
			{
				rtn += JSONUtils.getStartArrayWithMemberString("parts");
				for (CObject p : this.getParts()) {
					String str = "";
					String entryStr = "";
					str += JSONUtils.getStartEntryString();
					entryStr += JSONUtils.getEntryToString("name",p.getName());
					entryStr += JSONUtils.getEntryToString("type",p.getType());
					entryStr += JSONUtils.getEntryToString("idx",p.getIdx());
					entryStr = JSONUtils.addIndent(1,entryStr);
					str += entryStr;
					str += JSONUtils.getEndEntryString();
					entryStr = JSONUtils.addIndent(1,str);
					rtn += entryStr;
				}
				rtn += JSONUtils.getEndArrayString();
			}
		return rtn;
	}
	
	protected String getJSONFooter(){	
		String rtn = "";
		return rtn;
	}
	
	public void writeJSON(int indent, final Writer os) throws IOException {
		String str = null;
		//header
		str = this.getJSONHeader();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		//footer
		str = this.getJSONFooter();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
	}
}
