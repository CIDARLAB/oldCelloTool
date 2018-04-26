/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.technologymapping.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Pair;
import org.cellocad.common.target.data.TargetData;
import org.cellocad.technologymapping.data.Cytometry;
import org.cellocad.technologymapping.data.Gate;
import org.cellocad.technologymapping.data.GateType;
import org.cellocad.technologymapping.data.HillFunction;
import org.cellocad.technologymapping.data.Histogram;
import org.cellocad.technologymapping.data.LinearFunction;
import org.cellocad.technologymapping.data.Part;
import org.cellocad.technologymapping.data.PartType;
import org.cellocad.technologymapping.data.ResponseFunction;
import org.cellocad.technologymapping.data.Toxicity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author: Timothy Jones
 *
 * @date: Mar 6, 2018
 *
 */
public class TargetDataReader {

	public static final Collection<String> getRoadblockRules(TargetData td) {
		Collection<String> rtn = new HashSet<String>();
		Integer num = td.getNumJsonObject("eugene_rules");
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("eugene_rules",i);
			JsonArray jsonPartRules = json.getAsJsonArray("eugene_part_rules");
			for (JsonElement obj : jsonPartRules) {
				rtn.add(obj.getAsString());
			}
		}
		return rtn;
	}

	public static final Collection<String> getInputRoadblocks(TargetData td) {
		Collection<String> rtn = new HashSet<String>();

		Collection<String> rules = getRoadblockRules(td);
		CObjectCollection<Gate> inputSensors = getInputSensors(td);
		Collection<String> inputs = new HashSet<>();
		for (Gate g : inputSensors) {
			inputs.add(g.getName());
		}

		for (String rule : rules) {
			if(rule.toLowerCase().contains("startswith")) {
				List<String> deviceNames = getDeviceNamesFromRule(rule);
				String name = deviceNames.get(0);
				if (inputs.contains(name)) {
					rtn.add(name);
				}
			}
		}

		return rtn;
	}

	public static final Collection<String> getLogicRoadblocks(TargetData td) {
		Collection<String> rtn = new HashSet<String>();

		Collection<String> rules = getRoadblockRules(td);
		CObjectCollection<Gate> inputSensors = getInputSensors(td);
		Collection<String> inputs = new HashSet<>();
		for (Gate g : inputSensors) {
			inputs.add(g.getName());
		}

		for (String rule : rules) {
			if(rule.toLowerCase().contains("startswith")) {
				List<String> deviceNames = getDeviceNamesFromRule(rule);
				String name = deviceNames.get(0);
				if (!inputs.contains(name)) {
					rtn.add(name);
				}
			}
		}

		return rtn;
	}

	private static List<String> getDeviceNamesFromRule(String rule) {

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

		List<String> devices = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(rule, " \t\n\r\f,");

		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!keywords.contains(token) && token.substring(0, 1).matches("[a-z,A-Z]")) {
				devices.add(token);
			}
		}

		return devices;
	}

	public static final CObjectCollection<Cytometry> getGateCytometryData(TargetData td) {
		CObjectCollection<Cytometry> rtn = new CObjectCollection<>();
		Integer num = td.getNumJsonObject("gate_cytometry");
		for (int i = 0; i < num; i++) {
			Map<Double,Histogram> map = new HashMap<>();

			JsonObject json = td.getJsonObjectAtIdx("gate_cytometry",i);

			JsonArray data = json.getAsJsonArray("cytometry_data");

			for (JsonElement obj : data) {
				JsonObject o = obj.getAsJsonObject();
				Double input = o.get("input").getAsDouble();

				List<Double> bins = new ArrayList<>();
				JsonArray binsArr = o.getAsJsonArray("output_bins");
				for (JsonElement b : binsArr) {
					bins.add(b.getAsDouble());
				}

				List<Double> counts = new ArrayList<>();
				JsonArray countsArr = o.getAsJsonArray("output_counts");
				for (JsonElement b : countsArr) {
					counts.add(b.getAsDouble());
				}

				Histogram h = new Histogram(bins,counts);
				map.put(input,h);
			}

			Cytometry c = new Cytometry(map);
			c.setName(json.get("gate_name").getAsString());
			rtn.add(c);
		}
		return rtn;
	}

	public static final CObjectCollection<Toxicity> getGateToxicityData(TargetData td) {
		CObjectCollection<Toxicity> rtn = new CObjectCollection<>();
		Integer num = td.getNumJsonObject("gate_toxicity");
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("gate_toxicity",i);

			JsonArray inputJson = json.getAsJsonArray("input");
			List<Double> input = new ArrayList<>();
			for (JsonElement obj : inputJson) {
				input.add(obj.getAsDouble());
			}

			JsonArray growthJson = json.getAsJsonArray("growth");
			List<Double> growth = new ArrayList<>();
			for (JsonElement obj : growthJson) {
				growth.add(obj.getAsDouble());
			}

			Toxicity t = new Toxicity(input,growth);
			t.setName(json.get("gate_name").getAsString());
			rtn.add(t);
		}
		return rtn;
	}

	public static final Double getUnitConversion(TargetData td) {
		Integer num = td.getNumJsonObject("genetic_locations");
		if (num > 0) {
			JsonObject json = td.getJsonObjectAtIdx("genetic_locations",0);
			JsonArray loc = json.getAsJsonArray("output_module_location");
			return loc.get(0).getAsJsonObject().get("unit_conversion").getAsDouble();
		} else {
			return 1.0;
		}
	}

	public static final CObjectCollection<Part> getParts(TargetData td) {
		CObjectCollection<Part> parts = new CObjectCollection<Part>();
		Integer num = td.getNumJsonObject("parts");
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("parts",i);
			Part p = new Part();
			p.setName(json.get("name").getAsString());
			p.setType(PartType.valueOf(json.get("type").getAsString().toUpperCase()).ordinal());
			p.setPartType(PartType.valueOf(json.get("type").getAsString().toUpperCase()));
			parts.add(p);
		}
		return parts;
	}

	public static final Map<String,Pair<Double,Double>> getInputPromoterActivities(TargetData td) {
		Map<String,Pair<Double,Double>> map = new HashMap<>();
		Integer num = td.getNumJsonObject("input_sensors");
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("input_sensors",i);
			map.put(json.get("promoter").getAsString(),
					new Pair<Double,Double>(json.get("rpu_low").getAsDouble(),json.get("rpu_high").getAsDouble()));
		}
		return map;
	}

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

			// response function
			ResponseFunction<LinearFunction> rf = new ResponseFunction<>();
			LinearFunction lf = new LinearFunction();
			rf.setCurve(lf);
			g.setResponseFunction(rf);

			gates.add(g);
		}
		return gates;
	}

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

			ResponseFunction<LinearFunction> rf = new ResponseFunction<>();
			LinearFunction lf = new LinearFunction(getUnitConversion(td),0.0);
			rf.setCurve(lf);
			g.setResponseFunction(rf);

			gates.add(g);
		}
		return gates;
	}

	public static final Map<String,ResponseFunction<?>> getGateResponseFunctions(TargetData td) {
		Map<String,ResponseFunction<?>> map = new HashMap<>();
		Integer num = td.getNumJsonObject("response_functions");
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("response_functions",i);
			// could have an equation parser, or 'equation_type' field in target data
			if ((json.get("equation").getAsString()).equals("ymin+(ymax-ymin)/(1.0+(x/K)^n)")) {
				ResponseFunction<HillFunction> rf = new ResponseFunction<>();
				rf.setName(json.get("gate_name").getAsString());
				HillFunction hf = new HillFunction();
				JsonObject variables = json.getAsJsonArray("variables").get(0).getAsJsonObject();
				rf.setOnThreshold(variables.get("on_threshold").getAsDouble());
				rf.setOffThreshold(variables.get("off_threshold").getAsDouble());
				JsonArray parameters = json.getAsJsonArray("parameters");
				for (JsonElement obj : parameters) {
					JsonObject jsonObj = obj.getAsJsonObject();
					String name = jsonObj.get("name").getAsString();
					Double value = jsonObj.get("value").getAsDouble();
					if (name.equals("ymax"))
						hf.setYmax(value);
					if (name.equals("ymin"))
						hf.setYmin(value);
					if (name.equals("K"))
						hf.setK(value);
					if (name.equals("n"))
						hf.setN(value);
				}
				rf.setCurve(hf);
				map.put(json.get("gate_name").getAsString(),rf);
			}
		}
		return map;
	}

	public static final Map< String, Pair<Part,CObjectCollection<Part>> > getGateParts(TargetData td) {
		CObjectCollection<Part> parts = getParts(td);
		Map< String, Pair<Part,CObjectCollection<Part>> > gatePartsMap = new HashMap<>();
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
			Part promoter = parts.findCObjectByName(json.get("promoter").getAsString());
			gatePartsMap.put(json.get("gate_name").getAsString(),
							 new Pair<Part,CObjectCollection<Part>>(promoter,gateParts));

		}
		return gatePartsMap;
	}

	public static final CObjectCollection<Gate> getGates(TargetData td) {
		CObjectCollection<Gate> gates = new CObjectCollection<>();

		Map<String,Pair<Part,CObjectCollection<Part>>> gateParts = getGateParts(td);
		Map<String,ResponseFunction<?>> responseFunctions = getGateResponseFunctions(td);
		CObjectCollection<Toxicity> toxicities = getGateToxicityData(td);
		CObjectCollection<Cytometry> cytometries = getGateCytometryData(td);

		Integer num = td.getNumJsonObject("gates");
		for (int i = 0; i < num; i++) {
			JsonObject json = td.getJsonObjectAtIdx("gates",i);
			Gate g = new Gate();

			String name = json.get("gate_name").getAsString();
			g.setName(name);

			String type = json.get("gate_type").getAsString();
			if (type != null) {
				g.setType(GateType.valueOf(type.toUpperCase()).ordinal());
			}

			String group = json.get("group_name").getAsString();
			g.setGroup(group);

			Part promoter = gateParts.get(name).getFirst();
			g.setPromoter(promoter);

			CObjectCollection<Part> parts = gateParts.get(name).getSecond();
			if (parts != null) {
				g.setParts(gateParts.get(name).getSecond());
			}

			ResponseFunction<?> rf = responseFunctions.get(name);
			if (rf != null) {
				g.setResponseFunction(responseFunctions.get(name));
			}

			Toxicity t = toxicities.findCObjectByName(name);
			g.setToxicity(t);

			Cytometry c = cytometries.findCObjectByName(name);
			g.setCytometry(c);

			gates.add(g);
		}
		return gates;
	}

}
