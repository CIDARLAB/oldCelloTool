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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 6, 2018
 *
 */
public class TargetDataReader {

	public static final Collection<String> getRoadblockRules(TargetData td) {
		Collection<String> rtn = new HashSet<String>();
		Integer num = td.getNumJSONObject("eugene_rules");
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("eugene_rules",i);
			JSONArray jsonPartRules = (JSONArray) json.get("eugene_part_rules");
			for (Object obj : jsonPartRules) {
				rtn.add((String)obj);
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
		Integer num = td.getNumJSONObject("gate_cytometry");
		for (int i = 0; i < num; i++) {
			Map<Double,Histogram> map = new HashMap<>();

			JSONObject json = td.getJSONObjectAtIdx("gate_cytometry",i);

			JSONArray data = (JSONArray)json.get("cytometry_data");

			for (Object obj : data) {
				JSONObject o = (JSONObject)obj;
				Double input = (Double)o.get("input");

				List<Double> bins = new ArrayList<>();
				JSONArray binsArr = (JSONArray)o.get("output_bins");
				for (Object b : binsArr) {
					bins.add((Double)b);
				}

				List<Double> counts = new ArrayList<>();
				JSONArray countsArr = (JSONArray)o.get("output_counts");
				for (Object b : countsArr) {
					counts.add((Double)b);
				}

				Histogram h = new Histogram(bins,counts);
				map.put(input,h);
			}

			Cytometry c = new Cytometry(map);
			c.setName((String)json.get("gate_name"));
			rtn.add(c);
		}
		return rtn;
	}

	public static final CObjectCollection<Toxicity> getGateToxicityData(TargetData td) {
		CObjectCollection<Toxicity> rtn = new CObjectCollection<>();
		Integer num = td.getNumJSONObject("gate_toxicity");
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("gate_toxicity",i);

			JSONArray inputJson = (JSONArray)json.get("input");
			List<Double> input = new ArrayList<>();
			for (Object obj : inputJson) {
				input.add((Double)obj);
			}

			JSONArray growthJson = (JSONArray)json.get("growth");
			List<Double> growth = new ArrayList<>();
			for (Object obj : growthJson) {
				growth.add((Double)obj);
			}

			Toxicity t = new Toxicity(input,growth);
			t.setName((String)json.get("gate_name"));
			rtn.add(t);
		}
		return rtn;
	}

	public static final Double getUnitConversion(TargetData td) {
		Integer num = td.getNumJSONObject("genetic_locations");
		if (num > 0) {
			JSONObject json = td.getJSONObjectAtIdx("genetic_locations",0);
			JSONArray loc = (JSONArray)json.get("output_module_location");
			return (Double)((JSONObject)loc.get(0)).get("unit_conversion");
		} else {
			return 1.0;
		}
	}

	public static final CObjectCollection<Part> getParts(TargetData td) {
		CObjectCollection<Part> parts = new CObjectCollection<Part>();
		Integer num = td.getNumJSONObject("parts");
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("parts",i);
			Part p = new Part();
			p.setName(json.get("name").toString());
			p.setType(PartType.valueOf(json.get("type").toString().toUpperCase()).ordinal());
			p.setPartType(PartType.valueOf(json.get("type").toString().toUpperCase()));
			parts.add(p);
		}
		return parts;
	}

	public static final Map<String,Pair<Double,Double>> getInputPromoterActivities(TargetData td) {
		Map<String,Pair<Double,Double>> map = new HashMap<>();
		Integer num = td.getNumJSONObject("input_sensors");
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("input_sensors",i);
			map.put((String)json.get("promoter"),
					new Pair<Double,Double>((Double)json.get("rpu_low"),(Double)json.get("rpu_high")));
		}
		return map;
	}

	public static final CObjectCollection<Gate> getInputSensors(TargetData td) {
		CObjectCollection<Gate> gates = new CObjectCollection<>();
		Integer num = td.getNumJSONObject("input_sensors");
		CObjectCollection<Part> parts = getParts(td);
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("input_sensors",i);
			Gate g = new Gate();
			ResponseFunction<LinearFunction> rf = new ResponseFunction<>();
			LinearFunction lf = new LinearFunction();
			rf.setCurve(lf);
			g.setResponseFunction(rf);
			String name = (String)json.get("promoter");
			if (name != null) {g.setName(name);}
			Part part = parts.findCObjectByName(name);
			if (part != null) {
				CObjectCollection<Part> promoter = new CObjectCollection<>();
				promoter.add(part);
				g.setParts(promoter);
			}
			gates.add(g);
		}
		return gates;
	}

	public static final CObjectCollection<Gate> getOutputReporters(TargetData td) {
		CObjectCollection<Gate> gates = new CObjectCollection<>();
		Integer num = td.getNumJSONObject("output_reporters");
		CObjectCollection<Part> parts = getParts(td);
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("output_reporters",i);
			Gate g = new Gate();
			ResponseFunction<LinearFunction> rf = new ResponseFunction<>();
			LinearFunction lf = new LinearFunction(getUnitConversion(td),0.0);
			rf.setCurve(lf);
			g.setResponseFunction(rf);
			String name = (String)json.get("name");
			if (name != null) {g.setName(name);}
			JSONArray jsonParts = (JSONArray)json.get("parts");
			CObjectCollection<Part> outputParts = new CObjectCollection<>();
			for (Object obj : jsonParts) {
				Part part = parts.findCObjectByName((String)obj);
				if (part != null) {
					outputParts.add(part);
				}
			}
			g.setParts(outputParts);
			gates.add(g);
		}
		return gates;
	}

	public static final Map<String,ResponseFunction<?>> getGateResponseFunctions(TargetData td) {
		Map<String,ResponseFunction<?>> map = new HashMap<>();
		Integer num = td.getNumJSONObject("response_functions");
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("response_functions",i);
			// could have an equation parser, or 'equation_type' field in target data 
			if (((String)json.get("equation")).equals("ymin+(ymax-ymin)/(1.0+(x/K)^n)")) {
				ResponseFunction<HillFunction> rf = new ResponseFunction<>();
				rf.setName((String)json.get("gate_name"));
				HillFunction hf = new HillFunction();
				JSONObject variables = (JSONObject) ((JSONArray) json.get("variables")).get(0);
				rf.setOnThreshold((Double)variables.get("on_threshold"));
				rf.setOffThreshold((Double)variables.get("off_threshold"));
				JSONArray parameters = (JSONArray) json.get("parameters");
				for (Object obj : parameters) {
					JSONObject jsonObj = (JSONObject) obj;
					String name = (String)jsonObj.get("name");
					Double value = (Double)jsonObj.get("value");
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
				map.put((String)json.get("gate_name"),rf);
			}
		}
		return map;
	}

	public static final Map< String, Pair<String,CObjectCollection<Part>> > getGateParts(TargetData td) {
		CObjectCollection<Part> parts = getParts(td);
		Map< String, Pair<String,CObjectCollection<Part>> > gatePartsMap = new HashMap<>();
		Integer num = td.getNumJSONObject("gate_parts");
		for (int i = 0; i < num; i++) {
			CObjectCollection<Part> gateParts = new CObjectCollection<>();
			JSONObject json = td.getJSONObjectAtIdx("gate_parts",i);
			JSONArray jsonGateParts = (JSONArray) (((JSONObject) ((JSONArray) json.get("expression_cassettes")).get(0)).get("cassette_parts"));
			for ( Object obj : jsonGateParts ) {
				gateParts.add(parts.findCObjectByName(obj.toString()));
			}
			gateParts.add(parts.findCObjectByName(json.get("promoter").toString()));
			gatePartsMap.put(json.get("gate_name").toString(),
							 new Pair<String,CObjectCollection<Part>>(json.get("promoter").toString(),gateParts));

		}
		return gatePartsMap;
	}

	public static final CObjectCollection<Gate> getGates(TargetData td) {
		CObjectCollection<Gate> gates = new CObjectCollection<>();

		Map<String,Pair<String,CObjectCollection<Part>>> gateParts = getGateParts(td);
		Map<String,ResponseFunction<?>> responseFunctions = getGateResponseFunctions(td);
		CObjectCollection<Toxicity> toxicities = getGateToxicityData(td);
		CObjectCollection<Cytometry> cytometries = getGateCytometryData(td);

		Integer num = td.getNumJSONObject("gates");
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("gates",i);
			Gate g = new Gate();

			String name = (String)json.get("gate_name");
			g.setName(name);

			String type = (String)json.get("gate_type");
			if (type != null) {
				g.setType(GateType.valueOf(type.toUpperCase()).ordinal());
			}

			String group = (String)json.get("group_name");
			g.setGroup(group);

			String promoter = gateParts.get(name).getFirst();
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
