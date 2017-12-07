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
package partition.testing;

import common.Utils;
import common.graph.GraphUtils;
import common.graph.graph.Graph;
import partition.common.Partitioner;
import partition.profile.PartitionerProfile;
import partition.profile.PartitionerProfileParser;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 27, 2017
 *
 */
public class PTesting {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PTestingEnv testingEnv = new PTestingEnv(args);
		String profileFilename = testingEnv.getOptionValue(PTestingEnv.PROFILEFILENAME);
		if (profileFilename == null){
	        System.err.println( PTestingEnv.PROFILEFILENAME + " not defined." );
	        System.exit(-1);
		}
		String profileName = testingEnv.getOptionValue(PTestingEnv.PROFILENAME);
		if (profileName == null){
	        System.err.println( PTestingEnv.PROFILENAME + " not defined." );
	        System.exit(-1);
		}
		String graphFilename = testingEnv.getOptionValue(PTestingEnv.GRAPHFILENAME);
		if (graphFilename == null){
	        System.err.println( PTestingEnv.GRAPHFILENAME + " not defined." );
	        System.exit(-1);
		}
		// get Profile
		PartitionerProfileParser PPP = new PartitionerProfileParser(profileFilename);
		PartitionerProfile PP = PPP.getPartionerProfile(profileName);
		if (PP == null){
	        System.err.println( profileName + " not found." );
	        System.exit(-1);
		}
		// read graph
		Graph g = GraphUtils.getGraph(graphFilename);
		// write input graph
		String filename = Utils.getFilename(graphFilename);
		g.setName(filename+"_");
		GraphUtils.writeDotFileForGraph(g, g.getName() + "_Input");
		// execute
		Partitioner P = new Partitioner(g, PP, testingEnv);
		P.run();
		// write output graph
		GraphUtils.writeDotFileForGraph(g, g.getName() + "_Output");
	}

}
