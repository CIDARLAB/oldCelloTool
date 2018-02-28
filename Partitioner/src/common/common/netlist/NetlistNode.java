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
package common.netlist;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONObject;

import common.CObject;
import common.CObjectCollection;
import common.JSON.JSONUtils;
import common.graph.graph.VertexTemplate;
import common.profile.ProfileUtils;

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
	
	private void parse(final JSONObject JObj){
    	this.parseName(JObj);
    	this.parseNodeType(JObj);
    	this.parsePartitionID(JObj);
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
	 * Promoter
	 */
	public void setPromoter(String promoter) {
		this.promoter = promoter;
	}
	
	public String getPromoter() {
		return this.promoter;
	}
	
	private String promoter;
	
	/*
	 * NetListData placeholder class
	 */
	public void setNetListData(NetListData nodeType) {
		this.netListData = nodeType;
	}
	
	public NetListData getNetListData() {
		return this.netListData;
	}
	
	private NetListData netListData;

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
	 * @return The collection of parts that comprise the gate
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
