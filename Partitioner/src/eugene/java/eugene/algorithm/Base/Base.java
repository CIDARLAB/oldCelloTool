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
package eugene.algorithm.Base;

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

import org.cidarlab.eugene.Eugene;
import org.cidarlab.eugene.dom.Device;
import org.cidarlab.eugene.dom.NamedElement;
import org.cidarlab.eugene.dom.imp.container.EugeneArray;
import org.cidarlab.eugene.dom.imp.container.EugeneCollection;
import org.cidarlab.eugene.exception.EugeneException;
import org.cidarlab.eugene.util.DeviceUtils;

import common.Utils;
import common.netlist.Netlist;
import common.netlist.NetlistNode;

import eugene.algorithm.EugeneAlgorithm;
import eugene.data.Direction;
import eugene.data.Gate;
import eugene.data.Part;
import eugene.data.UcfReader;
import eugene.runtime.environment.EugeneArgString;

/**
 * @author: Timothy Jones
 * 
 * @date: Dec 6, 2017
 *
 */
public class Base extends EugeneAlgorithm{

	@Override
	protected void setDefaultParameterValues() {
		String filename = this.getRuntimeEnv().getOptionValue(EugeneArgString.CELLODIR) + "eugene";
		this.setEugeneInputFilename(filename + "_input.eug");
	}

	@Override
	protected void setParameterValues() {
		this.setPartRules(UcfReader.getPartRules(this.getTargetData()));
		this.setGateRules(UcfReader.getGateRules(this.getTargetData()));
		this.setPartLibrary(UcfReader.getParts(this.getTargetData()));
		this.setGateLibrary(UcfReader.getGates(this.getTargetData()));
	}

	@Override
	protected void validateParameterValues() {
	}

	@Override
	protected void preprocessing() {
		// generate input file for eugene
		// adapted from generateEugeneFile in EugeneAdaptor.java:261
		
		Collection<Gate> netlistGates = new HashSet<>();

		Netlist netlist = this.getNetlist();

		// dnacompiler just collects "logic" and "output" gates
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			Gate g = gateFromNetlistNode(netlist.getVertexAtIdx(i));
			// TODO set transcriptional units? i.e., which parts compose the gate. DNACompiler.java:1134:PlasmidUtil.setTxnUnits(...)
			netlistGates.add(g);
		}

		// TODO: set sensor module lists?
		// TODO: selectively add scars to part library, according to flag
		// TODO: generate 'circuit' and 'output' plasmids separately
		
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
		List<String> scars = new ArrayList<>();

		Integer i = 1;
		for (Gate g : netlistGates){
			gateNames.add(g.getName());

			eugeneForLoops.add("for(num i" + i + "=0;  i" + i + "<sizeof(" + g.getName() + "_devices);  i" + i + "=i" + i + "+1) {");
			eugeneArrays.add(String.format("%-12s", "gate_" + g.getName()) + " = " + g.getName() + "_devices[i" + i + "];");
			i++;

			String devDef = "Device ";
			devDef += g.getName() + "_device(" + Utils.getNewLine();

			String partRule = "Rule ";
			partRule += g.getName() + "_rules ( On ";
			partRule += g.getName() + "_device:" + Utils.getNewLine();

			String product = String.format("%-15s", g.getName() + "_devices");
			product += " = product(" + g.getName() + "_device" + ");" + Utils.getNewLine();
			eugeneProductDefinitions.add(product);

			eugeneGateDeclarations.add("Device gate_" + g.getName() + "();" + Utils.getNewLine());
			eugeneCircuitRules.add("   " + String.format("%-12s", "gate_" + g.getName()) + " EXACTLY 1 AND" + Utils.getNewLine());

			for (Part p : g.getParts()){
				if (p.getPartType().equals("scar")) {
					scars.add(p.getName());
				}
						
				eugenePartTypes.add("PartType " + p.getPartType() + ";" + Utils.getNewLine());

				String partSequence = p.getPartType();
				partSequence += " " + p.getName();
				partSequence += "(.SEQUENCE(\"" + p.getSequence();
				partSequence += "\"));" + Utils.getNewLine();
				eugenePartSequences.add(partSequence);

				if (p.getPartType().equals("promoter")) {
					devDef += "   " + p.getPartType() + "," + Utils.getNewLine();
					partRule += "   CONTAINS " + p.getName() + " AND" + Utils.getNewLine();
				} else {
					devDef += "   " + p.getName() + "," + Utils.getNewLine();
				}

				for (String r : this.getPartRules()) {
					if (r.contains(p.getName())) {
						if (!partRule.toLowerCase().contains("startswith")) {
							partRule += "   " + r + " AND" + Utils.getNewLine();
						} else {
							// TODO log something about duplicate startswith
						}
					}
				}
			}
			devDef = devDef.substring(0, devDef.length() - 2);
			devDef += Utils.getNewLine() + ");" + Utils.getNewLine();
			eugeneDeviceDefinitions.add(devDef);

			partRule += "ALL_FORWARD"  + Utils.getNewLine() + ");" + Utils.getNewLine();
		}

		for (String r : this.getGateRules()) {
			Set<String> devices = getDeviceNamesFromRule(r);
			if (gateNames.containsAll(devices)) {
				eugeneGateOrderRules.add("   " + r + " AND");
			}
		}
		for (String s : scars) {
			eugeneCircuitRules.add("   EXACTLY 1 " + s + " AND");
		}
		for (String s : scars) {
			eugeneCircuitRules.add("   FORWARD " + s + " AND");
		}
		for (int j = 0; j < scars.size(); j++) {
			eugeneCircuitRules.add("   [" + (j*2) + "] EQUALS " + scars.get(j) + "AND");
		}

		script += String.join(Utils.getNewLine(), eugenePartTypes) + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugenePartSequences) + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneDeviceDefinitions) + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugenePartRules) + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneProductDefinitions) + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneGateDeclarations) + Utils.getNewLine();
		script += "Device circuit();" + Utils.getNewLine() + Utils.getNewLine();
		script += "Rule allRules( ON circuit:" + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneGateOrderRules);
		script += String.join(Utils.getNewLine(), eugeneCircuitRules);
		script += "   ALL_FORWARD" + Utils.getNewLine() + ");" + Utils.getNewLine() + Utils.getNewLine();
		script += "Array allResults;" + Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneForLoops) + Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneArrays) + Utils.getNewLine() + Utils.getNewLine();
		script += "Device circuit(" + Utils.getNewLine() + Utils.getNewLine();
		script += String.join(Utils.getNewLine(), eugeneGateNames);
		script += String.join("," + Utils.getNewLine(), eugeneGateNames);
		script += String.join("," + Utils.getNewLine(), scars);
		script += Utils.getNewLine() + ");" + Utils.getNewLine() + Utils.getNewLine();
		script += "result = permute(circuit);" + Utils.getNewLine() + Utils.getNewLine();
		script += "allResults = allResults + result;" + Utils.getNewLine() + Utils.getNewLine();
		script += Collections.nCopies(gateNames.size(),"}" + Utils.getNewLine());
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

		// differences from dnacompiler:
		// part definitions not alphabetically sorted in input eugene file
		// dna sequences included by default (no flag to choose)
	}

	@Override
	protected void run() {
		// TODO executeScript(this.getEugeneInputFilename());
		try {
			Eugene eug = new Eugene();
			EugeneCollection ec = eug.executeScript(this.getEugeneInputScript());
			this.setEugenePlasmids((EugeneArray) ec.get("allResults"));
		} catch ( EugeneException e ) {
			e.printStackTrace();
		}
	}

	@Override
	protected void postprocessing() {
		NamedElement circuit = this.getEugenePlasmids().getElement(0);

		if (circuit instanceof org.cidarlab.eugene.dom.Device) {

			List<Part> module = new ArrayList<Part>();

			int gIndex = 0;

			for (NamedElement e : ((Device) circuit).getComponentList()) {

				if (e instanceof org.cidarlab.eugene.dom.Part) {
					Part p = new Part();
					p.setName(e.getName());
					p.setDirection(Direction.UP);

					module.add(p);
				} else if (e instanceof org.cidarlab.eugene.dom.Device) {
					String gateName = e.getName();
					Direction gateDirection = Direction.UP;

					String o = ((Device) circuit).getOrientations(gIndex).toString();

					if (o.equals("[REVERSE]")) {
						gateDirection = Direction.DOWN;
						Device reverse_gate = DeviceUtils.flipAndInvert((Device) e);
						e = reverse_gate;
					}

					String egate = gateDirection + gateName;

					List<Part> txnUnit = new ArrayList<Part>();

					int pIndex = 0;

					for (NamedElement part : ((Device) e)
							.getComponentList()) {

						String partName = part.getName();

						Direction pDirection = Direction.UP;

						String op = ((Device) e).getOrientations(pIndex).toString();

						if (op.equals("[REVERSE]")) {
							pDirection = Direction.DOWN;
						}

						Part p = new Part();
						p.setName(part.getName());
						p.setDirection(pDirection);
						txnUnit.add(p);

						pIndex++;

					}

					module.addAll(txnUnit);
				}

				gIndex++;

			}

			// module_variants.add(module);
			this.setModule(module);

		}
		// TODO: update netlist with parts and directions
	}

	private Gate gateFromNetlistNode(NetlistNode node) {
		// TODO gateFromNetlistNode function
		return new Gate();
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
	protected Collection<Part> getPartLibrary() {
		return partLibrary;
	}

	/**
	 * @param parts the parts to set
	 */
	protected void setPartLibrary(final Collection<Part> parts) {
		this.partLibrary = parts;
	}

	/**
	 * @return the gates
	 */
	protected Collection<Gate> getGateLibrary() {
		return gateLibrary;
	}

	/**
	 * @param gates the gates to set
	 */
	protected void setGateLibrary(final Collection<Gate> gates) {
		this.gateLibrary = gates;
	}

	/**
	 * @return the module
	 */
	protected List<Part> getModule() {
		return module;
	}

	/**
	 * @param module the module to set
	 */
	protected void setModule(final List<Part> module) {
		this.module = module;
	}

	private String eugeneInputScript;
	private String eugeneInputFilename;
	private EugeneArray eugenePlasmids;
	private Collection<String> partRules;
	private Collection<String> gateRules;
	private Collection<Part> partLibrary;
	private Collection<Gate> gateLibrary;
	private List<Part> module;
}
