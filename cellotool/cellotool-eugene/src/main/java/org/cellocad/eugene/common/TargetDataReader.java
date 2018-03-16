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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author: Timothy Jones
 * 
 * @date: Feb 27, 2018
 *
 */
public class TargetDataReader {

	public static final Collection<String> getPartRules(TargetData td) {
        Collection<String> partRules = new HashSet<String>();
		Integer num = td.getNumJSONObject("eugene_rules");
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("eugene_rules",i);
            JSONArray jsonPartRules = (JSONArray) json.get("eugene_part_rules");
			for (Object obj : jsonPartRules) {
				partRules.add((String)obj);
			}
		}
		return partRules;
	}

	public static final Collection<String> getGateRules(TargetData td) {
        Collection<String> gateRules = new HashSet<String>();
		Integer num = td.getNumJSONObject("eugene_rules");
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("eugene_rules",i);
            JSONArray jsonGateRules = (JSONArray) json.get("eugene_gate_rules");
			for (Object obj : jsonGateRules) {
				gateRules.add((String)obj);
			}
		}
		return gateRules;
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
			p.setSequence(json.get("dnasequence").toString());
			parts.add(p);
		}
		return parts;
	}

	public static final Map< String, Part > getGatePromoters(TargetData td) {
		CObjectCollection<Part> parts = getParts(td);
		Map< String, Part > gatePromoterMap = new HashMap<>();
		Integer num = td.getNumJSONObject("gate_parts");
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("gate_parts",i);
			gatePromoterMap.put(json.get("gate_name").toString(),parts.findCObjectByName(json.get("promoter").toString()));
		}
		return gatePromoterMap;
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
		Map< String, CObjectCollection<Part> > gateParts = getGateParts(td);
		for (int i = 0; i < num; i++) {
			JSONObject json = td.getJSONObjectAtIdx("gates",i);
			Gate g = new Gate();
			String name = json.get("gate_name").toString();
			g.setName(name);
			g.setParts(gateParts.get(name));
			gates.add(g);
		}
		return gates;
	}

}
