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
package org.cellocad.partition.algorithm;

import org.cellocad.common.algorithm.Algorithm;
import org.cellocad.common.profile.AlgorithmProfile;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.cellocad.partition.common.Partition;
import org.cellocad.partition.graph.PGraph;

/**
 * @author: Vincent Mirian
 *
 * @date: Oct 27, 2017
 *
 */
abstract public class PTAlgorithm extends Algorithm{

	public void execute(final PGraph G, final Partition P, final AlgorithmProfile AProfile, final RuntimeEnv runtimeEnv){
		// set members
		this.setPGraph(G);
		this.setPartition(P);
		this.setAlgorithmProfile(AProfile);
		this.setRuntimeEnv(runtimeEnv);
		// execute
		this.setDefaultParameterValues();
		this.setParameterValues();
		this.validateParameterValues();
		this.preprocessing();
		this.run();
		this.postprocessing();
	}

	private void setPGraph(final PGraph g){
		this.pGraph = g;
	}

	private void setPartition(final Partition P){
		this.partition = P;
	}

	private void setAlgorithmProfile(final AlgorithmProfile AP){
		this.algorithmProfile = AP;
	}

	private void setRuntimeEnv(final RuntimeEnv runtimeEnv){
		this.runtimeEnv = runtimeEnv;
	}

	protected PGraph getPGraph(){
		return this.pGraph;
	}

	protected Partition getPartition(){
		return this.partition;
	}

	protected AlgorithmProfile getAlgorithmProfile(){
		return this.algorithmProfile;
	}

	protected RuntimeEnv getRuntimeEnv(){
		return this.runtimeEnv;
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithmProfile == null) ? 0 : algorithmProfile.hashCode());
		result = prime * result + ((pGraph == null) ? 0 : pGraph.hashCode());
		result = prime * result + ((partition == null) ? 0 : partition.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PTAlgorithm other = (PTAlgorithm) obj;
		if (algorithmProfile == null) {
			if (other.algorithmProfile != null)
				return false;
		} else if (!algorithmProfile.equals(other.algorithmProfile))
			return false;
		if (pGraph == null) {
			if (other.pGraph != null)
				return false;
		} else if (!pGraph.equals(other.pGraph))
			return false;
		if (partition == null) {
			if (other.partition != null)
				return false;
		} else if (!partition.equals(other.partition))
			return false;
		return true;
	}

	private PGraph pGraph;
	private Partition partition;
	private AlgorithmProfile algorithmProfile;
	private RuntimeEnv runtimeEnv;

}
