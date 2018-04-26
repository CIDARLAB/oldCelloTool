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

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.common.graph.AbstractVertex;
import org.cellocad.common.graph.graph.VertexTemplate;
import org.cellocad.common.profile.ProfileUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

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
		this.setNodeType(other.getNodeType());
		this.setGate(other.getGate());
	}

	public NetlistNode(final JsonObject JObj){
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

	private void parseNodeType(final JsonObject JObj){
		String value = ProfileUtils.getString(JObj, "nodeType");
		if (value != null) {
			this.setNodeType(value);
		}
	}

	private void parseVertexType(final JsonObject JObj){
		String value = ProfileUtils.getString(JObj, "vertexType");
		if (value != null) {
			if (value.equals(AbstractVertex.VertexType.SOURCE.name())) {
				this.setVertexType(AbstractVertex.VertexType.SOURCE);
			} else if (value.equals(AbstractVertex.VertexType.SINK.name())) {
				this.setVertexType(AbstractVertex.VertexType.SINK);
			} else {
				this.setVertexType(AbstractVertex.VertexType.NONE);
			}
		}
	}

	private void parsePartitionID(final JsonObject JObj){
		Integer value = ProfileUtils.getInteger(JObj, "partitionID");
		if (value != null) {
			this.setPartitionID(value.intValue());
		}
	}

	private void parseGate(final JsonObject JObj){
		String value = ProfileUtils.getString(JObj, "gate");
		if (value != null) {
			this.setGate(value);
		}
	}

	private void parseParts(final JsonObject JObj){
		JsonElement value = ProfileUtils.getJsonElement(JObj, "parts");
		if (value != null) {
			JsonArray array = value.getAsJsonArray();
			CObjectCollection<CObject> parts = new CObjectCollection<>();
			for (JsonElement obj : array) {
				parts.add(this.parsePart(obj.getAsJsonObject()));
			}
			this.setParts(parts);
		}
	}

	private CObject parsePart(final JsonObject JObj){
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

	private void parse(final JsonObject JObj){
		this.parseName(JObj);
		this.parseNodeType(JObj);
		this.parseVertexType(JObj);
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
	protected void writeJSONHeader(JsonWriter writer) throws IOException {
		// name
		writer.name("name").value(this.getName());
		// NodeType
		writer.name("nodeType").value(this.getNodeType());
		// NodeType
		writer.name("vertexType").value(this.getVertexType().name());
		// partitionID
		writer.name("partitionID").value(this.getPartitionID());
		// gate
		if (this.getGate() != null) {
			writer.name("gate").value(this.getGate());
		}
		writer.flush();
		// parts
		if (this.getParts() != null
				&&
				this.getParts().size() > 0)
		{
			writer.name("parts");
			writer.beginArray();
			for (CObject p : this.getParts()) {
				writer.beginObject();
				writer.name("name").value(p.getName());
				writer.name("type").value(p.getType());
				writer.name("idx").value(p.getIdx());
				writer.endObject();
			}
			writer.endArray();
			writer.flush();
		}
	}

	protected void writeJSONFooter(JsonWriter writer) throws IOException {
	}

	public void writeJSON(final JsonWriter writer) throws IOException {
		//header
		this.writeJSONHeader(writer);
		writer.flush();
		//footer
		this.writeJSONFooter(writer);
		writer.flush();
	}
}
