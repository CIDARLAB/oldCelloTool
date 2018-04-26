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
package org.cellocad.sbolgenerator.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cellocad.common.CObjectCollection;
import org.cellocad.common.target.data.TargetData;
import org.cellocad.sbolgenerator.data.Gate;
import org.cellocad.sbolgenerator.data.Part;
import org.cellocad.sbolgenerator.data.PartType;

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

	public static final Collection<String> getPartRules(TargetData td) {
		return null;
	}

	public static final Collection<String> getGateRules(TargetData td) {
		return null;
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
			p.setSequence(json.get("dnasequence").getAsString());
			JsonElement partUri = json.get("uri");
			if ( partUri != null ) {
				try {
					p.setUri(new URI(partUri.getAsString()));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			parts.add(p);
		}
		return parts;
	}

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
			JsonElement uri = json.get("uri");
			if ( uri != null ) {
				try {
					g.setUri(new URI(uri.getAsString()));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			gates.add(g);
		}
		return gates;
	}

}
