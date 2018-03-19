/**
 * Copyright (C) 2017 Boston University (BU)
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
package org.cellocad.eugene.algorithm.Base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Utils;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.eugene.algorithm.EUAlgorithm;
import org.cellocad.eugene.common.TargetDataReader;
import org.cellocad.eugene.data.Direction;
import org.cellocad.eugene.data.Gate;
import org.cellocad.eugene.data.Part;
import org.cellocad.eugene.data.PartType;
import org.cidarlab.eugene.Eugene;
import org.cidarlab.eugene.dom.Device;
import org.cidarlab.eugene.dom.NamedElement;
import org.cidarlab.eugene.dom.imp.container.EugeneArray;
import org.cidarlab.eugene.dom.imp.container.EugeneCollection;
import org.cidarlab.eugene.exception.EugeneException;
import org.cidarlab.eugene.util.DeviceUtils;

/**
 * @author: Timothy Jones
 * 
 * @date: Dec 6, 2017
 *
 */
public class Base extends EUAlgorithm{
	// TODO: generate circuit and output plasmids separately
	// TODO: support partitions
	// TODO: netlist nodes should be transcriptional units (promoter...terminator), not cds...promoter "gates"

	@Override
	protected void setDefaultParameterValues() {
		String filename = this.getRuntimeEnv().getOptionValue("outputDir") + Utils.getFileSeparator() + "eugene";
		this.setEugeneInputFilename(filename + "_script.eug");
	}

	@Override
	protected void setParameterValues() {
		this.setPartRules(TargetDataReader.getPartRules(this.getTargetData()));
		this.setGateRules(TargetDataReader.getGateRules(this.getTargetData()));
		this.setPartLibrary(TargetDataReader.getParts(this.getTargetData()));
		this.setGateLibrary(TargetDataReader.getGates(this.getTargetData()));
	}

	@Override
	protected void validateParameterValues() {
	}

	@Override
	protected void preprocessing() {
		logInfo("building Eugene input script");

		Set<Gate> netlistGates = new HashSet<>();
		Netlist netlist = this.getNetlist();

		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (!node.getNodeType().equals("TopInput") && !node.getNodeType().equals("TopOutput")) {
				Gate g = gateLibrary.findCObjectByName(node.getGate());
				netlistGates.add(g);
			}
		}

		String script = "";

		Set<String> eugenePartTypes = new HashSet<>();
		Set<String> eugenePartSequences = new HashSet<>();
		Collection<String> eugeneDeviceDefinitions = new ArrayList<>();
		Collection<String> eugenePartRules = new ArrayList<>();
		Collection<String> eugeneProductDefinitions = new ArrayList<>();
		Collection<String> eugeneGateDeclarations = new ArrayList<>();
		Set<String> eugeneGateOrderRules = new HashSet<>();
		Collection<String> eugeneCircuitRules = new ArrayList<>();

		List<String> eugeneForLoops = new ArrayList<>();
		List<String> eugeneArrays = new ArrayList<>();
		List<String> eugeneGateNames = new ArrayList<>();

		Set<String> gateNames = new HashSet<>();
		// List<String> scars = new ArrayList<>();

		Integer i = 1;
		for (int k = 0; k < netlist.getNumVertex(); k++) {
			NetlistNode node = netlist.getVertexAtIdx(k);
			if (!node.getNodeType().equals("TopInput") && !node.getNodeType().equals("TopOutput")) {
				Gate g = gateLibrary.findCObjectByName(node.getGate());
				gateNames.add(g.getName());

				eugeneForLoops.add("for(num i" + i + "=0;  i" + i + "<sizeof(" + g.getName() + "_devices);	i" + i + "=i" + i + "+1) {");
				eugeneGateNames.add(String.format("%-12s", "gate_" + g.getName()));
				eugeneArrays.add(String.format("%-12s", "gate_" + g.getName()) + " = " + g.getName() + "_devices[i" + i + "];");
				i++;

				String devDef = "Device ";
				devDef += g.getName() + "_device(" + Utils.getNewLine();

				String partRule = "Rule ";
				partRule += g.getName() + "_rules ( ON ";
				partRule += g.getName() + "_device:" + Utils.getNewLine();

				String product = String.format("%-15s", g.getName() + "_devices");
				product += " = product(" + g.getName() + "_device" + ");";
				eugeneProductDefinitions.add(product);

				eugeneGateDeclarations.add("Device gate_" + g.getName() + "();");
				eugeneCircuitRules.add("   " + String.format("%-12s", "gate_" + g.getName()) + " EXACTLY 1 AND");

				CObjectCollection<Part> txnUnitParts = new CObjectCollection<>();
				for (int j = 0; j < node.getNumInEdge(); j++) {
					NetlistNode upstreamNode = node.getInEdgeAtIdx(j).getSrc();
					for (CObject part : upstreamNode.getParts()) {
						Part p = this.getPartLibrary().findCObjectByName(part.getName());
						if (part.getType() == PartType.PROMOTER.ordinal()) {
							txnUnitParts.add(p);
							devDef += "	  " + p.getPartType() + "," + Utils.getNewLine();
							partRule += "	CONTAINS " + p.getName() + " AND" + Utils.getNewLine();
						}
					}
				}
				for (Part p : g.getParts()){
					if (p.getType() != PartType.PROMOTER.ordinal()) {
						txnUnitParts.add(p);
					}
				}
				for (Part p : txnUnitParts){
					eugenePartTypes.add("PartType " + p.getPartType() + ";");

					String partSequence = p.getPartType().toString();
					partSequence += " " + p.getName();
					partSequence += "(.SEQUENCE(\"" + p.getSequence();
					partSequence += "\"));";
					eugenePartSequences.add(partSequence);
				
					if (p.getType() != PartType.PROMOTER.ordinal()) {
						devDef += "	  " + p.getName() + "," + Utils.getNewLine();
					}

					for (String r : this.getPartRules()) {
						if (r.contains(" " + p.getName())) {
							if (!partRule.toLowerCase().contains("startswith")) {
								partRule += "	" + r + " AND" + Utils.getNewLine();
							} else {
								// TODO log something about duplicate startswith
							}
						}
					}
				}
				devDef = devDef.substring(0, devDef.length() - 2);
				devDef += Utils.getNewLine() + ");" + Utils.getNewLine();
				eugeneDeviceDefinitions.add(devDef);

				partRule += "	ALL_FORWARD"  + Utils.getNewLine() + ");" + Utils.getNewLine();
				eugenePartRules.add(partRule);
			}
		}

		for (String r : this.getGateRules()) {
			Set<String> devices = getDeviceNamesFromRule(r);
			if (gateNames.containsAll(devices)) {
				eugeneGateOrderRules.add("	 " + r + " AND");
			}
		}
		// for (String s : scars) {
		//		eugeneCircuitRules.add("   EXACTLY 1 " + s + " AND");
		// }
		// for (String s : scars) {
		//		eugeneCircuitRules.add("   FORWARD " + s + " AND");
		// }
		// for (int j = 0; j < scars.size(); j++) {
		//		eugeneCircuitRules.add("   [" + (j*2) + "] EQUALS " + scars.get(j) + "AND");
		// }

		script += String.join(Utils.getNewLine(), eugenePartTypes);
		script += Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugenePartSequences);
		script += Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneDeviceDefinitions);
		script += Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugenePartRules);
		script += Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneProductDefinitions);
		script += Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneGateDeclarations) + Utils.getNewLine();
		script += "Device circuit();" + Utils.getNewLine() + Utils.getNewLine();
		script += "Rule allRules( ON circuit:" + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneGateOrderRules);
		script += Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneCircuitRules);
		script += Utils.getNewLine();
		script += "	  ALL_FORWARD" + Utils.getNewLine() + ");" + Utils.getNewLine() + Utils.getNewLine();
		script += "Array allResults;" + Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneForLoops) + Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneArrays) + Utils.getNewLine() + Utils.getNewLine();
		script += "Device circuit(" + Utils.getNewLine();
		script += String.join("," + Utils.getNewLine(), eugeneGateNames);
		// script += String.join("," + Utils.getNewLine(), scars);
		script += Utils.getNewLine() + ");" + Utils.getNewLine() + Utils.getNewLine();
		script += "result = permute(circuit);" + Utils.getNewLine() + Utils.getNewLine();
		script += "allResults = allResults + result;" + Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(),Collections.nCopies(gateNames.size(),"}"));
		script += Utils.getNewLine() + Utils.getNewLine();
		this.setEugeneInputScript(script);
		try {
			OutputStream outputStream = new FileOutputStream(this.getEugeneInputFilename());
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
		logInfo("running Eugene");
		try {
			Eugene eug = new Eugene();

			File cruft = new File(Utils.getWorkingDirectory()
								  + Utils.getFileSeparator()
								  + "exports");
			(new File(cruft.getPath()
					  + Utils.getFileSeparator()
					  + "pigeon")).delete();
			cruft.delete();

			EugeneCollection ec = eug.executeScript(this.getEugeneInputScript());
			this.setEugenePlasmids((EugeneArray) ec.get("allResults"));
		} catch ( EugeneException e ) {
			e.printStackTrace();
		}
	}

	@Override
	protected void postprocessing() {
		logInfo("processing Eugene output");
		NamedElement circuit = null;
		try {
			circuit = this.getEugenePlasmids().getElement(0);
		} catch (EugeneException e) {
			e.printStackTrace();
		}

		CObjectCollection<Part> module = new CObjectCollection<>();
		if (circuit instanceof org.cidarlab.eugene.dom.Device) {

			int gIndex = 0;
			int idx = 0;

			for (NamedElement el : ((Device) circuit).getComponentList()) {

				if (el instanceof org.cidarlab.eugene.dom.Part) {
					Part p = new Part();
					p.setName(el.getName());
					p.setDirection(Direction.UP);
					p.setIdx(idx);
					module.add(p);
					idx++;
				} else if (el instanceof org.cidarlab.eugene.dom.Device) {
					// String gateName = el.getName();
					// Direction gateDirection = Direction.UP;

					String o = "[FORWARD]";
					try {
						o = ((Device) circuit).getOrientations(gIndex).toString();
					} catch (EugeneException e) {
						e.printStackTrace();
					}

					if (o.equals("[REVERSE]")) {
						try {
							Device reverse_gate = DeviceUtils.flipAndInvert((Device) el);
							// gateDirection = Direction.DOWN;
							el = reverse_gate;
						} catch (EugeneException e) {
							e.printStackTrace();
						}
					}

					List<Part> txnUnit = new ArrayList<Part>();
					int pIndex = 0;
					for (NamedElement part : ((Device) el).getComponentList()) {
						// String partName = part.getName();
						Direction pDirection = Direction.UP;

						try {
							String op = ((Device) el).getOrientations(pIndex).toString();
							if (op.equals("[REVERSE]")) {
								pDirection = Direction.DOWN;
							}
						} catch (EugeneException e) {
							e.printStackTrace();
						}

						Part p = new Part();
						p.setName(part.getName());
						p.setDirection(pDirection);
						p.setType(this.getPartLibrary().findCObjectByName(part.getName()).getType());
						p.setIdx(idx);
						txnUnit.add(p);
						idx++;
						pIndex++;
					}
					module.addAll(txnUnit);
				}
				gIndex++;
			}
			// moduleVariants.add(module);
			this.setModule(module);
		}

		logInfo("updating netlist");
		Netlist netlist = this.getNetlist();
		for (int i = 0; i<netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (node.getNodeType().equals("TopOutput")) {
				for (int j = 0; j < node.getNumInEdge(); j++) {
					NetlistNode upstreamNode = node.getInEdgeAtIdx(j).getSrc();
					for (CObject part : upstreamNode.getParts()) {
						if ((part.getType() == PartType.PROMOTER.ordinal()) &&
							(part.getIdx() != UNASSIGNED)){
							part.setIdx(UNASSIGNED); // unassigned -- part of an output plasmid
							break;
						}
					}
				}
			}
		}
		for (int i = 0; i<netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (!node.getNodeType().equals("TopOutput")) {
				for (CObject part : node.getParts()) {
					if (part.getIdx() != UNASSIGNED) {
						Part modulePart = module.findCObjectByName(part.getName());
						part.setIdx(modulePart.getIdx());
					}
				}
			}
		}
	}

	private Set<String> getDeviceNamesFromRule(String rule) {

		Collection<String> keywords = new ArrayList<>();

		// counting
		keywords.add("CONTAINS");
		keywords.add("NOTCONTAINS");
		keywords.add("EXACTLY");
		keywords.add("NOTEXACTLY");
		keywords.add("MORETHAN");
		keywords.add("NOTMORETHAN");
		keywords.add("SAME_COUNT");
		keywords.add("WITH");
		keywords.add("NOTWITH");
		keywords.add("THEN");

		// positioning
		keywords.add("STARTSWITH");
		keywords.add("ENDSWITH");
		keywords.add("AFTER");
		keywords.add("ALL_AFTER");
		keywords.add("SOME_AFTER");
		keywords.add("BEFORE");
		keywords.add("ALL_BEFORE");
		keywords.add("SOME_BEFORE");
		keywords.add("NEXTTO");
		keywords.add("ALL_NEXTTO");
		keywords.add("SOME_NEXTTO");

		// pairing
		keywords.add("EQUALS");
		keywords.add("NOTEQUALS");

		// orientation
		keywords.add("ALL_FORWARD");
		keywords.add("ALL_REVERSE");
		keywords.add("FORWARD");
		keywords.add("REVERSE");
		keywords.add("SAME_ORIENTATION");
		keywords.add("ALL_SAME_ORIENTATION");
		keywords.add("ALTERNATE_ORIENTATION");

		// interaction
		keywords.add("REPRESSES");
		keywords.add("INDUCES");
		keywords.add("DRIVES");

		// logic
		keywords.add("NOT");
		keywords.add("AND");
		keywords.add("OR");

		Set<String> devices = new HashSet<String>();
		StringTokenizer st = new StringTokenizer(rule, " \t\n\r\f,");

		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!keywords.contains(token) && token.substring(0, 1).matches("[a-z,A-Z]")) {
				devices.add(token);
			}
		}

		return devices;
	}

	/*
	 * Getter and Setter
	 */

	protected String getEugeneInputScript() {
		return eugeneInputScript;
	}

	protected void setEugeneInputScript(final String eugeneInputScript) {
		this.eugeneInputScript = eugeneInputScript;
	}

	/**
	 * @return the eugeneInputFilename
	 */
	protected String getEugeneInputFilename() {
		return eugeneInputFilename;
	}

	/**
	 * @param eugeneInputFilename the eugeneInputFilename to set
	 */
	protected void setEugeneInputFilename(final String eugeneInputFilename) {
		this.eugeneInputFilename = eugeneInputFilename;
	}

	/**
	 * @return the eugenePlasmids
	 */
	public EugeneArray getEugenePlasmids() {
		return eugenePlasmids;
	}

	/**
	 * @param eugenePlasmids the eugenePlasmids to set
	 */
	public void setEugenePlasmids(EugeneArray eugenePlasmids) {
		this.eugenePlasmids = eugenePlasmids;
	}

	/**
	 * @return the partRules
	 */
	protected Collection<String> getPartRules() {
		return partRules;
	}

	/**
	 * @param partRules the partRules to set
	 */
	protected void setPartRules(final Collection<String> partRules) {
		this.partRules = partRules;
	}

	/**
	 * @return the gateRules
	 */
	protected Collection<String> getGateRules() {
		return gateRules;
	}

	/**
	 * @param gateRules the gateRules to set
	 */
	protected void setGateRules(final Collection<String> gateRules) {
		this.gateRules = gateRules;
	}

	/**
	 * @return the parts
	 */
	protected CObjectCollection<Part> getPartLibrary() {
		return partLibrary;
	}

	/**
	 * @param parts the parts to set
	 */
	protected void setPartLibrary(final CObjectCollection<Part> parts) {
		this.partLibrary = parts;
	}

	/**
	 * @return the gates
	 */
	protected CObjectCollection<Gate> getGateLibrary() {
		return gateLibrary;
	}

	/**
	 * @param gates the gates to set
	 */
	protected void setGateLibrary(final CObjectCollection<Gate> gates) {
		this.gateLibrary = gates;
	}

	/**
	 * @return the module
	 */
	protected CObjectCollection<Part> getModule() {
		return module;
	}

	/**
	 * @param module the module to set
	 */
	protected void setModule(final CObjectCollection<Part> module) {
		this.module = module;
	}

	public static final int UNASSIGNED = -9999;
	private String eugeneInputScript;
	private String eugeneInputFilename;
	private EugeneArray eugenePlasmids;
	private Collection<String> partRules;
	private Collection<String> gateRules;
	private CObjectCollection<Part> partLibrary;
	private CObjectCollection<Gate> gateLibrary;
	private CObjectCollection<Part> module;
}
