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

import org.cellocad.common.CObjectCollection;
import org.cellocad.common.profile.AlgorithmProfile;
import org.cellocad.common.profile.ProfileObject;
import org.cellocad.common.profile.ProfileUtils;

import com.google.gson.JsonObject;

/**
 * @author: Vincent Mirian
 *
 * @date: Oct 27, 2017
 *
 */
public class PartitionerProfile extends ProfileObject {


	public PartitionerProfile(final CObjectCollection<PartitionProfile> PProfiles,
			final CObjectCollection<AlgorithmProfile> AProfiles,
			final JsonObject JObj){
		super(JObj);
		parse(PProfiles, AProfiles, JObj);
	}

	/*
	 * Parse
	 */
	private void parsePartitionProfile(final CObjectCollection<PartitionProfile> PProfiles, final JsonObject JObj){
		PartitionProfile PPObj;
		String PartitionProfile = ProfileUtils.getString(JObj, "PartitionProfile");
		if (PartitionProfile == null) {
			throw new RuntimeException("PartitionProfile not specified for " + this.getName() + ".");
		}
		PPObj = PProfiles.findCObjectByName(PartitionProfile);
		if (PPObj == null){
			throw new RuntimeException("PartitionProfile not found for " + this.getName() + ".");
		}
		this.setPProfile(PPObj);
	}

	private void parseAlgorithmProfile(final CObjectCollection<AlgorithmProfile> AProfiles, final JsonObject JObj){
		AlgorithmProfile APObj;
		String AlgorithmProfile = ProfileUtils.getString(JObj, "AlgorithmProfile");
		if (AlgorithmProfile == null) {
			throw new RuntimeException("AlgorithmProfile not specified for " + this.getName() + ".");
		}
		APObj = AProfiles.findCObjectByName(AlgorithmProfile);
		if (APObj == null){
			throw new RuntimeException("AlgorithmProfile not found for " + this.getName() + ".");
		}
		this.setAProfile(APObj);
	}

	private void parse(final CObjectCollection<PartitionProfile> PProfiles,
			final CObjectCollection<AlgorithmProfile> AProfiles,
			final JsonObject JObj){
		// name
		// this.parseName(JObj);
		// PartitionProfile
		this.parsePartitionProfile(PProfiles, JObj);
		// AlgorithmProfile
		this.parseAlgorithmProfile(AProfiles, JObj);
	}


	private void setPProfile(final PartitionProfile PProfile){
		this.PProfile = PProfile;
	}

	public PartitionProfile getPProfile(){
		return this.PProfile;
	}

	private void setAProfile(final AlgorithmProfile AProfile){
		this.AProfile = AProfile;
	}

	public AlgorithmProfile getAProfile(){
		return this.AProfile;
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((AProfile == null) ? 0 : AProfile.hashCode());
		result = prime * result + ((PProfile == null) ? 0 : PProfile.hashCode());
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
		PartitionerProfile other = (PartitionerProfile) obj;
		if (AProfile == null) {
			if (other.AProfile != null)
				return false;
		} else if (!AProfile.equals(other.AProfile))
			return false;
		if (PProfile == null) {
			if (other.PProfile != null)
				return false;
		} else if (!PProfile.equals(other.PProfile))
			return false;
		return true;
	}

	private PartitionProfile PProfile;
	private AlgorithmProfile AProfile;
}
