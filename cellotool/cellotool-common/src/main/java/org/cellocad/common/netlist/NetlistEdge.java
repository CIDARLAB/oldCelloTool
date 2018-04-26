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

import org.cellocad.common.graph.graph.EdgeTemplate;
import org.cellocad.common.profile.ProfileUtils;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 17, 2017
 *
 */
public class NetlistEdge extends EdgeTemplate<NetlistNode>{

	public NetlistEdge(){
		super();
	}

	public NetlistEdge(final NetlistNode Src, final NetlistNode Dst) {
		super(Src);
		this.setDst(Dst);
	}

	public NetlistEdge(final NetlistEdge other) {
		super(other);
		this.setSrc(other.getSrc());
		this.setDst(other.getDst());
	}

	public NetlistEdge(final JsonObject JObj){
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
	private void parse(final JsonObject JObj){
		this.parseName(JObj);
	}

	/*
	 * Write
	 */
	protected void writeJSONHeader(JsonWriter writer) throws IOException {
		// name
		writer.name("name").value(this.getName());
		// src
		writer.name("src").value(this.getSrc().getName());
		// dst
		writer.name("dst").value(this.getDst().getName());
	}

	protected void writeJSONFooter(JsonWriter writer){
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
