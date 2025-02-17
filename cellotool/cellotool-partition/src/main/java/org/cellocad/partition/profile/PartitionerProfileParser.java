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
package org.cellocad.partition.profile;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.common.profile.AlgorithmProfile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author: Vincent Mirian
 *
 * @date: Oct 27, 2017
 *
 */
public class PartitionerProfileParser extends CObject {

	private void init(){
		profiles = new CObjectCollection<PartitionerProfile>();
		pProfiles = new CObjectCollection<PartitionProfile>();
		aProfiles = new CObjectCollection<AlgorithmProfile>();
	}

	public PartitionerProfileParser(final String filename){
		init();
		// parse
		this.parse(filename);
	}

	public PartitionerProfile getPartionerProfile(final String name){
		PartitionerProfile rtn = null;
		rtn = profiles.findCObjectByName(name);
		return rtn;
	}

	/*
	 * Parse
	 */
	private void parse(final String filename){
		JsonParser parser = new JsonParser();
		try{
			JsonObject jsonTop = parser.parse(new FileReader(filename)).getAsJsonObject();
			JsonArray jsonArr;
			// parse PartitionProfile
			jsonArr = jsonTop.getAsJsonArray("PartitionProfiles");
			if (jsonArr == null) {
				throw new RuntimeException("PartitionProfiles missing in " + filename + ".");
			}
			for (int i = 0; i < jsonArr.size(); i++)
			{
				JsonObject jsonObj = jsonArr.get(i).getAsJsonObject();
				PartitionProfile P = new PartitionProfile(jsonObj);
				pProfiles.add(P);
			}
			// parse AlgorithmProfile
			jsonArr = jsonTop.getAsJsonArray("AlgorithmProfiles");
			if (jsonArr == null) {
				throw new RuntimeException("AlgorithmProfiles missing in " + filename + ".");
			}
			for (int i = 0; i < jsonArr.size(); i++)
			{
				JsonObject jsonObj = jsonArr.get(i).getAsJsonObject();
				AlgorithmProfile A = new AlgorithmProfile(jsonObj);
				aProfiles.add(A);
			}
			// parse PartitionerProfile
			jsonArr = jsonTop.getAsJsonArray("PartitionerProfiles");
			if (jsonArr == null) {
				throw new RuntimeException("PartitionerProfiles missing in " + filename + ".");
			}
			for (int i = 0; i < jsonArr.size(); i++)
			{
				JsonObject jsonObj = jsonArr.get(i).getAsJsonObject();
				PartitionerProfile PP = new PartitionerProfile(pProfiles, aProfiles, jsonObj);
				profiles.add(PP);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((aProfiles == null) ? 0 : aProfiles.hashCode());
		result = prime * result + ((pProfiles == null) ? 0 : pProfiles.hashCode());
		result = prime * result + ((profiles == null) ? 0 : profiles.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartitionerProfileParser other = (PartitionerProfileParser) obj;
		if (aProfiles == null) {
			if (other.aProfiles != null)
				return false;
		} else if (!aProfiles.equals(other.aProfiles))
			return false;
		if (pProfiles == null) {
			if (other.pProfiles != null)
				return false;
		} else if (!pProfiles.equals(other.pProfiles))
			return false;
		if (profiles == null) {
			if (other.profiles != null)
				return false;
		} else if (!profiles.equals(other.profiles))
			return false;
		return true;
	}

	private CObjectCollection<PartitionerProfile> profiles;
	private CObjectCollection<PartitionProfile> pProfiles;
	private CObjectCollection<AlgorithmProfile> aProfiles;
}
