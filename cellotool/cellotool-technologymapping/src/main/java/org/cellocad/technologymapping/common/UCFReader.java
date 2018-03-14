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

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Pair;
import org.cellocad.common.target.data.TargetData;

import org.cellocad.technologymapping.data.Gate;
import org.cellocad.technologymapping.data.GateType;
import org.cellocad.technologymapping.data.HillFunction;
import org.cellocad.technologymapping.data.LinearFunction;
import org.cellocad.technologymapping.data.Part;
import org.cellocad.technologymapping.data.PartType;
import org.cellocad.technologymapping.data.ResponseFunction;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 6, 2018
 *
 */
public class UCFReader {

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

	public static final Map< String, CObjectCollection<Part> > getGateParts(TargetData td) {
		CObjectCollection<Part> parts = getParts(td);
		Map< String, CObjectCollection<Part> > gatePartsMap = new HashMap<>();
		Integer num = td.getNumJSONObject("gate_parts");
		for (int i = 0; i < num; i++) {
			CObjectCollection<Part> gateParts = new CObjectCollection<>();
			JSONObject json = td.getJSONObjectAtIdx("gate_parts",i);
			JSONArray jsonGateParts = (JSONArray) (((JSONObject) ((JSONArray) json.get("expression_cassettes")).get(0)).get("cassette_parts"));
			for ( Object obj : jsonGateParts ) {
				gateParts.add(parts.findCObjectByName(obj.toString()));
			}
			gateParts.add(parts.findCObjectByName(json.get("promoter").toString()));
			gatePartsMap.put(json.get("gate_name").toString(),gateParts);
		}
		return gatePartsMap;
	}

	public static final CObjectCollection<Gate> getGates(TargetData td) {
		CObjectCollection<Gate> gates = new CObjectCollection<>();
		Integer num = td.getNumJSONObject("gates");
		Map<String,CObjectCollection<Part>> gateParts = getGateParts(td);
		Map<String,ResponseFunction<?>> responseFunctions = getGateResponseFunctions(td);
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("gates",i);
			Gate g = new Gate();
			String name = (String)json.get("gate_name");
			if (name != null) {g.setName(name);}
			String type = (String)json.get("gate_type");
			if (type != null) {g.setType(GateType.valueOf(type.toUpperCase()).ordinal());}
			String group = (String)json.get("group_name");
			if (group != null) {g.setGroup(group);}
			CObjectCollection<Part> parts = gateParts.get(name);
			if (parts != null) {
				g.setParts(gateParts.get(name));
			}
			ResponseFunction<?> rf = responseFunctions.get(name);
			if (rf != null) {
				g.setResponseFunction(responseFunctions.get(name));
			}
			gates.add(g);
		}
		return gates;
	}

}
