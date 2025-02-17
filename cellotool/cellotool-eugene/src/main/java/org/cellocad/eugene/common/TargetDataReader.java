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
package org.cellocad.eugene.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.cellocad.common.CObjectCollection;
import org.cellocad.common.target.data.TargetData;
import org.cellocad.eugene.data.Gate;
import org.cellocad.eugene.data.Part;
import org.cellocad.eugene.data.PartType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author: Timothy Jones
 *
 * @date: Feb 27, 2018
 *
 */
public class TargetDataReader {

	/**
	 * Get the part rules from the target data.
	 *
	 * @param td The TargetData from which to extract part rules.
	 * @return The part rules.
	 */
	public static final Collection<String> getPartRules(TargetData td) {
		Collection<String> partRules = new HashSet<String>();
		Integer num = td.getNumJsonObject("eugene_rules");
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("eugene_rules",i);
			JsonArray jsonPartRules = json.getAsJsonArray("eugene_part_rules");
			for (JsonElement obj : jsonPartRules) {
				partRules.add(obj.getAsString());
			}
		}
		return partRules;
	}

	/**
	 * Get the gate rules from the target data.
	 *
	 * @param td The TargetData from which to extract gate rules.
	 * @return The gate rules.
	 */
	public static final Collection<String> getGateRules(TargetData td) {
		Collection<String> gateRules = new HashSet<String>();
		Integer num = td.getNumJsonObject("eugene_rules");
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("eugene_rules",i);
			JsonArray jsonGateRules = json.getAsJsonArray("eugene_gate_rules");
			for (JsonElement obj : jsonGateRules) {
				gateRules.add(obj.getAsString());
			}
		}
		return gateRules;
	}

	/**
	 * Get the parts library from the target data.
	 *
	 * @param td The TargetData from which to extract the parts library.
	 * @return The parts library.
	 */
	public static final CObjectCollection<Part> getParts(TargetData td) {
		CObjectCollection<Part> parts = new CObjectCollection<Part>();
		Integer num = td.getNumJsonObject("parts");
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("parts",i);
			Part p = new Part();
			p.setName(json.get("name").getAsString());
			p.setType(PartType.valueOf(json.get("type").getAsString().toUpperCase()).ordinal());
			p.setPartType(PartType.valueOf(json.get("type").getAsString().toUpperCase()));
			p.setSequence(json.get("dnasequence").getAsString());
			parts.add(p);
		}
		return parts;
	}

	/**
	 * Get the input sensor "gates" from the target data.
	 *
	 * @param td The TargetData from which to extract the input sensors.
	 * @return The input sensors.
	 */
	public static final CObjectCollection<Gate> getInputSensors(TargetData td) {
		CObjectCollection<Gate> gates = new CObjectCollection<>();
		Integer num = td.getNumJsonObject("input_sensors");
		CObjectCollection<Part> parts = getParts(td);
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("input_sensors",i);

			Gate g = new Gate();

			// name
			String name = json.get("promoter").getAsString();
			if (name != null)
				g.setName(name);

			// promoter
			Part promoter = parts.findCObjectByName(name);
			if (promoter != null) {
				g.setPromoter(promoter);
			}

			// parts
			JsonArray array = json.getAsJsonArray("parts");
			CObjectCollection<Part> inputParts = new CObjectCollection<>();
			for (JsonElement obj : array) {
				Part part = parts.findCObjectByName(obj.getAsString());
				if (part != null) {
					inputParts.add(part);
				}
			}
			g.setParts(inputParts);

			gates.add(g);
		}
		return gates;
	}

	/**
	 * Get the output reporter "gates" from the target data.
	 *
	 * @param td The TargetData from which to extract the output reporters.
	 * @return The output reporters.
	 */
	public static final CObjectCollection<Gate> getOutputReporters(TargetData td) {
		CObjectCollection<Gate> gates = new CObjectCollection<>();
		Integer num = td.getNumJsonObject("output_reporters");
		CObjectCollection<Part> parts = getParts(td);
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("output_reporters",i);

			Gate g = new Gate();

			// name
			String name = json.get("name").getAsString();
			if (name != null)
				g.setName(name);

			// parts
			JsonArray jsonParts = json.getAsJsonArray("parts");
			CObjectCollection<Part> outputParts = new CObjectCollection<>();
			for (JsonElement obj : jsonParts) {
				Part part = parts.findCObjectByName(obj.getAsString());
				if (part != null) {
					outputParts.add(part);
				}
			}
			g.setParts(outputParts);

			gates.add(g);
		}
		return gates;
	}

	/**
	 * Get the parts that comprise each gate from the target data.
	 *
	 * @param td The TargetData from which to extract the gate parts.
	 * @return A map from a gate name to a collection of parts.
	 */
	public static final Map< String, CObjectCollection<Part> > getGateParts(TargetData td) {
		CObjectCollection<Part> parts = getParts(td);
		Map< String, CObjectCollection<Part> > gatePartsMap = new HashMap<>();
		Integer num = td.getNumJsonObject("gate_parts");
		for (int i = 0; i < num; i++) {
			CObjectCollection<Part> gateParts = new CObjectCollection<>();
			JsonObject json = td.getJsonObjectAtIdx("gate_parts",i);
			JsonArray jsonGateParts =
				json.getAsJsonArray("expression_cassettes")
				.get(0).getAsJsonObject()
				.getAsJsonArray("cassette_parts");
			for ( JsonElement obj : jsonGateParts ) {
				gateParts.add(parts.findCObjectByName(obj.getAsString()));
			}
			gateParts.add(parts.findCObjectByName(json.get("promoter").getAsString()));
			gatePartsMap.put(json.get("gate_name").getAsString(),gateParts);
		}
		return gatePartsMap;
	}

	/**
	 * Get the gates from the target data.
	 *
	 * @param td The TargetData from which to extract the gates.
	 * @return The gates.
	 */
	public static final CObjectCollection<Gate> getGates(TargetData td) {
		CObjectCollection<Gate> gates = new CObjectCollection<>();
		Integer num = td.getNumJsonObject("gates");
		Map< String, CObjectCollection<Part> > gateParts = getGateParts(td);
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("gates",i);
			Gate g = new Gate();
			String name = json.get("gate_name").getAsString();
			g.setName(name);
			g.setParts(gateParts.get(name));
			gates.add(g);
		}
		for (Gate g : getInputSensors(td)) {
			gates.add(g);
		}
		for (Gate g : getOutputReporters(td)) {
			gates.add(g);
		}
		return gates;
	}

}
