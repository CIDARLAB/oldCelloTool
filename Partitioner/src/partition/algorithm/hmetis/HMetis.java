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
package partition.algorithm.hmetis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import common.Utils;
import partition.algorithm.PTAlgorithm;
import partition.common.Block;
import partition.common.Move;
import partition.graph.PEdge;
import partition.graph.PGraph;
import partition.graph.PNode;
import partition.testing.PTestingEnv;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 13, 2017
 *
 */
public class HMetis extends PTAlgorithm{

	private void init() {
		integerVertexMap = new HashMap<Integer, PNode>();
		vertexIntegerMap = new HashMap<PNode, Integer>();
	}
	
	public HMetis() {
		this.init();
	}
	
	private void writePartitionFile() throws IOException {
		Map<PNode, Integer> vertexIntegerMap = this.getVertexIntegerMap();
		//header lines are: [number of blocks]
		//				    [number of edges] [number of vertices] 
		//	subsequent lines give each edge, one edge per line
				
		Path out_path = Paths.get("graph_file.txt");
		int num_blocks = this.getPartition().getNumBlock();
		PGraph graph = this.getPGraph();
		
		//for now just write out num_blocks in top line, later can add other params there
		List<String> out_lines = new ArrayList<String>();
		out_lines.add(Integer.toString(num_blocks));
		out_lines.add(Integer.toString(graph.getNumEdge()) + " " + Integer.toString(graph.getNumVertex()));
		
		// vertices
		PNode src = null;
		PNode dst = null;
		Integer srcInteger = null;
		Integer dstInteger = null;
		for(int i=0; i< graph.getNumEdge(); ++i) {
			PEdge edge = graph.getEdgeAtIdx(i);
			String out_line = new String();
			src = edge.getSrc();
			dst = edge.getDst();
			srcInteger = vertexIntegerMap.get(src);
			dstInteger = vertexIntegerMap.get(dst);
			//out_line += edge.getSrc().getName() + " " + edge.getDst().getName();
			out_line += srcInteger.intValue() + " " + dstInteger.intValue();
			out_lines.add(out_line);
		}
				
		Files.write(out_path, out_lines, Charset.forName("UTF-8"),StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING, 
				StandardOpenOption.WRITE);
		
	}
	
	private void readPartitionedFile(Map<PNode, Integer> nodeBlockMap) throws IOException {
		Map<Integer, PNode> integerVertexMap = this.getIntegerVertexMap();
		Path in_path = Paths.get("partitioned_graph.txt");
		
		List<String> all_lines = Files.readAllLines(in_path);
		int current_block = 0;
		for(String line: all_lines) {
			if(line.startsWith("block")) {
				String[] splits = line.split(":");
				current_block = Integer.parseInt(splits[1]);
			}			
			else {
				int vertex = Integer.parseInt(line);
				PNode node = integerVertexMap.get(vertex);
				//vertex_assignments[vertex-1] = current_block; //vertices indexed 1...|v|
				nodeBlockMap.put(node, current_block);
			}
		}
	}
	
	private void assignPNodesToBlocks(Map<PNode, Integer> nodeBlockMap) {
		List<Move> moves = new ArrayList<Move>();
		Move move = null;
		Iterator<Map.Entry<PNode, Integer>> it = nodeBlockMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<PNode, Integer> pair = (Map.Entry<PNode, Integer>) it.next();
	        Integer block_idx = pair.getValue();
			Block block_assignment = this.getPartition().getBlockAtIdx(block_idx);
			PNode node = pair.getKey();
			move = new Move(node, node.getMyBlock(), block_assignment);
			moves.add(move);
	    }	    
	    this.getPartition().doMoves(moves);
	}
	
	private void callMetis() throws IOException {
		PTestingEnv testingEnv = null;
		String OSPath;
		String line;
		//TODO: fix this hack
		if (this.getRuntimeEnv() instanceof PTestingEnv) {
			testingEnv = (PTestingEnv) this.getRuntimeEnv();
		}
		else {
			throw new RuntimeException("Environment var error!");
		}
		OSPath = testingEnv.getOptionValue(PTestingEnv.CELLODIR) + "/external_tools/";
		if (Utils.isMac()) {
			OSPath = OSPath + "OSX/";
		}
		else if (Utils.isUnix()) {
			OSPath = OSPath + "Linux/";
		}
		else {
			throw new RuntimeException("OS not supported!");
		}
		int UBFactor = 1;
		try{
			String cmd = testingEnv.getOptionValue(PTestingEnv.CELLODIR) + "/scripts/run_metis_partitioner.py graph_file.txt " + UBFactor + " " + OSPath;
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = stdInput.readLine()) != null) {
				System.out.println(line);
			}			  
		}catch(InterruptedException | IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void setDefaultParameterValues() {
		
	}

	@Override
	public void setParameterValues() {
		
	}

	@Override
	public void validateParameterValues() {
		
	}

	@Override
	public void preprocessing() {
		Map<Integer, PNode> integerVertexMap = this.getIntegerVertexMap();
		Map<PNode, Integer> vertexIntegerMap = this.getVertexIntegerMap();
		PGraph graph = this.getPGraph();
		for (int i = 0; i < graph.getNumVertex(); i++) {
			int temp = i + 1;
			PNode node = graph.getVertexAtIdx(i);
			vertexIntegerMap.put(node, new Integer(temp));
			integerVertexMap.put(new Integer(temp), node);
		}		
	}

	@Override
	public void run() {
		Map<PNode, Integer> nodeBlockMap= new HashMap<PNode, Integer>();
		try {
			writePartitionFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			callMetis();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		
		try {
			readPartitionedFile(nodeBlockMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// System.out.println(vertex_assignment);
		
		//make moves
		assignPNodesToBlocks(nodeBlockMap);
		
		/*System.out.println("block counts");
		for(int i=0; i<this.getPartition().getNumBlock(); ++i) {
			System.out.println(this.getPartition().getBlockAtIdx(i).getNumPNode());
		}*/
	}

	@Override
	public void postprocessing() {
		
	}
	
	/*
	 * integerVertexMap
	 */
	private Map<Integer, PNode> integerVertexMap;
	
	private Map<Integer, PNode> getIntegerVertexMap(){
		return this.integerVertexMap;
	}
	
	/*
	 * vertexIntegerMap
	 */
	private Map<PNode, Integer> vertexIntegerMap;
	
	private Map<PNode, Integer> getVertexIntegerMap(){
		return this.vertexIntegerMap;
	}
}
