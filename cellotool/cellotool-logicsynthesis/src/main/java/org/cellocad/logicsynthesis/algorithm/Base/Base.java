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
package org.cellocad.logicsynthesis.algorithm.Base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cellocad.common.Utils;
import org.cellocad.common.graph.AbstractVertex.VertexType;
import org.cellocad.common.netlist.NetlistEdge;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.logicsynthesis.algorithm.LSAlgorithm;
import org.cellocad.logicsynthesis.common.LSUtils;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.util.parse.EdifParser;
import edu.byu.ece.edif.util.parse.ParseException;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 21, 2017
 *
 */
public class Base extends LSAlgorithm{

	@Override
	protected void setDefaultParameterValues() {		
	}

	@Override
	protected void setParameterValues() {		
	}

	@Override
	protected void validateParameterValues() {		
	}

	@Override
	protected void preprocessing() {
		String filename = Utils.getFilename(this.getVerilogFile());
		this.setYosysScriptFilename(this.getRuntimeEnv().getOptionValue("outputDir") + Utils.getFileSeparator() + filename + "_YosysScript");	
		this.setYosysEdifFilename(this.getRuntimeEnv().getOptionValue("outputDir") + Utils.getFileSeparator() + filename + ".edif");

		String cellLibraryFileVerilog = "";
		String cellLibraryFileLiberty = "";
		String cellLibraryParameter = this.getAlgorithmProfile().getStringParameter("cell_library").getSecond();
		if (cellLibraryParameter.equals("notnor")) {
			cellLibraryFileVerilog += LSUtils.getResourceAsFile("libraries/notnor.v");
			cellLibraryFileLiberty += LSUtils.getResourceAsFile("libraries/notnor.lib");
		} else if (cellLibraryParameter.substring(0,Math.min(cellLibraryParameter.length(),5)).equals("file:")) {
			cellLibraryFileVerilog = cellLibraryParameter.substring(5,cellLibraryParameter.length()) + ".v";
			if (!(new File(cellLibraryFileVerilog)).isFile()) {
				throw new RuntimeException("'" + cellLibraryFileVerilog + " is not a file!");
			}
			cellLibraryFileLiberty = cellLibraryParameter.substring(5,cellLibraryParameter.length()) + ".lib";
			if (!(new File(cellLibraryFileLiberty)).isFile()) {
				throw new RuntimeException("'" + cellLibraryFileLiberty + " is not a file!");
			}
		}
		
		// exec
		String exec = "";
		File yosysTmp = LSUtils.getResourceAsFile("external_tools/Linux/yosys/yosys");
		File yosysabcTmp = LSUtils.getResourceAsFile("external_tools/Linux/yosys/yosys-abc");
		
		File tempDir = LSUtils.createTempDirectory();
		File yosys = new File(tempDir.toString() + Utils.getFileSeparator() + "yosys");
		yosysTmp.renameTo(yosys);
		yosys.setExecutable(true);

		File yosysabc = new File(tempDir.toString() + Utils.getFileSeparator() + "yosys-abc");
		yosysabcTmp.renameTo(yosysabc);
		yosysabc.setExecutable(true);

		exec += yosys.toString();
		exec += " -s ";
		this.setYosysExec(exec);
		// create Yosys script
		String script = "";
		if (!cellLibraryFileVerilog.isEmpty()) {
			script += "read_verilog -lib ";
			script += cellLibraryFileVerilog;
			script += Utils.getNewLine();
		}
		script += "read_verilog ";
		script += this.getVerilogFile();
		script += Utils.getNewLine();
		script += "flatten";
		script += Utils.getNewLine();
		script += "proc; opt";
		script += Utils.getNewLine();
		script += "techmap; opt";
		script += Utils.getNewLine();
		script += "dfflibmap";
		if (!cellLibraryFileLiberty.isEmpty()) { script += " -liberty " + cellLibraryFileLiberty;}
		script += Utils.getNewLine();
		script += "abc";
		if (!cellLibraryFileLiberty.isEmpty()) { script += " -liberty " + cellLibraryFileLiberty;}
		script += Utils.getNewLine();
		script += "clean";
		script += Utils.getNewLine();
		script += "show -format ps -prefix ";
		script += this.getRuntimeEnv().getOptionValue("outputDir");
		script += Utils.getFileSeparator();
		script += filename;
		script += Utils.getNewLine();
		script += "write_edif ";
		script += this.getYosysEdifFilename();
		script += Utils.getNewLine();
		// write Yosys script
		try {
			OutputStream outputStream = new FileOutputStream(this.getYosysScriptFilename());
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(script);
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	protected void run() {
		Utils.executeAndWaitForCommand(this.getYosysExec() + this.getYosysScriptFilename());
	}

	@Override
	protected void postprocessing() {
		this.convertEdifToNetlist();
		// delete
		Utils.deleteFilename(this.getYosysEdifFilename());
		Utils.deleteFilename(this.getYosysScriptFilename());
	}

	/*
	 * EDIF
	 */
	public void convertEdifToNetlist() {
		String filename = this.getYosysEdifFilename();
		EdifEnvironment edifEnv = null;
		try {
			edifEnv = EdifParser.translate(filename);
		}
		catch (FileNotFoundException | ParseException e) {
			e.printStackTrace();
		}
		EdifCellInstance top = edifEnv.getTopDesign().getTopCellInstance();
		this.getNetlist().setName(top.getOldName());
		Map<String, NetlistNode> map = new HashMap<String, NetlistNode>();
		Collection<EdifNet> nets = top.getCellType().getNetList();
		EdifCellInstance srcCell = null;
		EdifCellInstance dstCell = null;
		NetlistNode srcNode = null;
		NetlistNode dstNode = null;
		for (EdifNet net : nets) {
			// Top Input
			if (this.hasTopInput(net)) {
				// Top PortRef
				for (EdifPortRef topPortRef: net.getInputPortRefs()) {
					if (topPortRef.isTopLevelPortRef()) {
						srcNode = this.getNode(topPortRef.getSingleBitPort().getPortName(), "TopInput", map);
						srcNode.setVertexType(VertexType.SOURCE);
						// Other Input/Output PortRef
						for (EdifPortRef otherPortRef: net.getPortRefList()) {
							if (topPortRef == otherPortRef) {
								continue;
							}
							EdifCellInstance cell = otherPortRef.getCellInstance();
							// generic
							if (cell != null) {
								assert(otherPortRef.getPort().isInput());
								dstNode = this.getNode(cell.getName(), cell.getCellType().getOldName(), map);
								dstNode.setVertexType(VertexType.NONE);
							}
							// Top output
							else {
								assert(otherPortRef.getPort().isOutput());
								dstNode = this.getNode(otherPortRef.getSingleBitPort().getPortName(), "TopOutput", map);
								dstNode.setVertexType(VertexType.SINK);
							}
							// setEdge
							setEdge(srcNode, dstNode, net);
						}
					}
				}
			}
			// Top Output
			else if (this.hasTopOutput(net)) {
				// Top PortRef
				for (EdifPortRef topPortRef: net.getOutputPortRefs()) {
					if (topPortRef.isTopLevelPortRef()) {
						dstNode = this.getNode(topPortRef.getSingleBitPort().getPortName(), "TopOutput", map);
						dstNode.setVertexType(VertexType.SINK);
						// Other Output PortRef
						for (EdifPortRef otherPortRef: net.getOutputPortRefs()) {
							if (topPortRef == otherPortRef) {
								continue;
							}
							EdifCellInstance cell = otherPortRef.getCellInstance();
							// generic
							assert(otherPortRef.getPort().isOutput());
							srcNode = this.getNode(cell.getName(), cell.getCellType().getOldName(), map);
							srcNode.setVertexType(VertexType.NONE);
							// setEdge
							setEdge(srcNode, dstNode, net);
						}
					}
				}				
			}
			// Other
			else {
				// Outputs
				for (EdifPortRef outputPortRef: net.getOutputPortRefs()) {
					srcCell = outputPortRef.getCellInstance();
					// create vertex if not present
					srcNode = this.getNode(srcCell.getName(), srcCell.getCellType().getOldName(), map);
					srcNode.setVertexType(VertexType.NONE);
					for (EdifPortRef inputPortRef: net.getInputPortRefs()) {
						dstCell = inputPortRef.getCellInstance();
						// create vertex if not present
						dstNode = this.getNode(dstCell.getName(), dstCell.getCellType().getOldName(), map);
						dstNode.setVertexType(VertexType.NONE);
						// setEdge
						setEdge(srcNode, dstNode, net);
					}			
				}
			}
		}
	}

	void setEdge(NetlistNode src, NetlistNode dst, EdifNet net) {
		NetlistEdge edge = new NetlistEdge(src, dst);
		edge.setName(net.getOldName());
		src.addOutEdge(edge);
		dst.addInEdge(edge);
		this.getNetlist().addEdge(edge);
	}
	
	NetlistNode getNode(String Name, String type, Map<String, NetlistNode> map) {
		NetlistNode rtn = null;
		rtn = map.get(Name);
		if (rtn == null) {
			rtn = new NetlistNode();
			rtn.setName(Name);
			rtn.setNodeType(type);
			this.getNetlist().addVertex(rtn);
			map.put(Name, rtn);
		}
		return rtn;
	}
	
	boolean hasTopInput(EdifNet net) {
		boolean rtn = false;
		for (EdifPortRef portRef: net.getInputPortRefs()) {
			rtn = rtn || portRef.isTopLevelPortRef();
		}
		return rtn;
	}
	
	boolean hasTopOutput(EdifNet net) {
		boolean rtn = false;
		for (EdifPortRef portRef: net.getOutputPortRefs()) {
			rtn = rtn || portRef.isTopLevelPortRef();
		}
		return rtn;
	}
	
	/*
	 * Getter and Setter
	 */
	protected void setYosysScriptFilename(final String str) {
		this.yosysScriptFilename = str;
	}
	
	protected String getYosysScriptFilename() {
		return this.yosysScriptFilename;
	}
	
	protected void setYosysEdifFilename(final String str) {
		this.yosysEdifFilename = str;
	}
	
	protected String getYosysEdifFilename() {
		return this.yosysEdifFilename;
	}
	
	protected void setYosysExec(final String str) {
		this.yosysExec = str;
	}
	
	protected String getYosysExec() {
		return this.yosysExec;
	}

	private String yosysScriptFilename;
	private String yosysEdifFilename;
	private String yosysExec;

}
