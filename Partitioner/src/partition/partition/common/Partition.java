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
package partition.common;

import java.util.List;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import partition.graph.PEdge;
import partition.graph.PNode;
import partition.profile.BlockProfile;
import partition.profile.CapacityProfile;
import partition.profile.PartitionProfile;
import common.CObject;
import common.CObjectCollection;
import common.Utils;
import common.capacity.Capacity;
import common.profile.DerivedProfile;
import common.profile.ProfileObject;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 26, 2017
 *
 */
public class Partition extends DerivedProfile<PartitionProfile>{

	private void init(){
		this.blocks = new ArrayList<Block>();
		this.blockCapacityUnits = new CObjectCollection<CObject>();
		this.blockCapacity = new CObjectCollection<Capacity>();
	}

	private void initBlockCapacityUnits(){
		final PartitionProfile PProfile = this.getProfile();
		ProfileObject unit = null;
		CObject cObj = null;
		for (int i = 0; i < PProfile.getNumBlockCapacityUnits(); i++) {
			unit = PProfile.getBlockCapacityUnitsAtIdx(i);
			cObj = new CObject(unit);
			this.blockCapacityUnits.add(cObj);
		}
	}
	
	private void initBlockCapacity(){
		final PartitionProfile PProfile = this.getProfile();
		CapacityProfile CP = null;
		Capacity capacity = null;
		for (int i = 0; i < PProfile.getNumBlockCapacity(); i++) {
			CP = PProfile.getBlockCapacityAtIdx(i);
			capacity = new Capacity(CP, this.blockCapacityUnits);
			this.blockCapacity.add(capacity);
		}
	}
	
	private void initBlocks(){
		final PartitionProfile PProfile = this.getProfile();
		// initBlocks
		Block block = null;
		BlockProfile BP = null;
		for (int i = 0; i < PProfile.getNumBlockProfile(); i++){
			BP = PProfile.getBlockProfileAtIdx(i);
			block = new Block(BP, this.blockCapacity, this.blockCapacityUnits);
			this.addBlock(block);
		}
	}
	
	private void initBlockInformation(){
		this.initBlockCapacityUnits();
		this.initBlockCapacity();
		this.initBlocks();
	}
	
	private void initInterBlocksInformation(){
		//final PartitionProfile PProfile = this.getProfile();
	}

	public Partition(final PartitionProfile PProfile){
		super(PProfile);
		init();
		initBlockInformation();
		initInterBlocksInformation();	
	}
	
	/*
	 * Blocks
	 */
	private void addBlock(final Block block){
		if (block != null){
			block.setIdx(this.getNumBlock());
			blocks.add(block);
		}
	}
	
	/*private void removeBlock(final Block block){
		if (block != null){
			blocks.remove(block);
		}
	}*/
	
	public Block getBlockAtIdx(int index){
		Block rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumBlock())
			){
			rtn = blocks.get(index);	
		} 
		return rtn;
	}
	
	public int getNumBlock(){
		int rtn = blocks.size();
		return rtn;
	}
	
	private boolean blockExists(final Block block){
		boolean rtn = (block != null) && (blocks.contains(block));
		return rtn;
	}
	
	/*
	 * Move
	 */
	public boolean doMoves(final List<Move> moves){
		boolean rtn = true;
		Move move;
		Iterator<Move> movesIt = moves.iterator();
		while (rtn &&
				(movesIt.hasNext())){
			move = movesIt.next();
			rtn = rtn && this.doMove(move);
		}
		return rtn;
	}
	
	private boolean doMove(final Move move){
		boolean rtn = false;
		boolean moveIsValid = move.isValid();
		PNode node = move.getPNode();
		Block srcBlock = move.getSrcBlock();
		Block dstBlock = move.getDstBlock();
		boolean srcExist = (srcBlock == null) || this.blockExists(srcBlock);
		boolean dstExist = (dstBlock == null) || this.blockExists(dstBlock);
		rtn = srcExist && dstExist && moveIsValid;
		if (rtn){
			if (srcBlock != null){
				srcBlock.removePNode(node);
				node.setMyBlock(null);
				assert (node.getMyBlock() == null);
			}
			if (dstBlock != null){
				dstBlock.addPNode(node);
				node.setMyBlock(dstBlock);
				assert (node.getMyBlock() == dstBlock);
			}
		}
		return rtn;
	}
	
	/*
	 * dot file
	 */
	protected String getDotHeader(){
		String rtn = "";
		rtn += "digraph ";
		rtn += this.getName();
		rtn += " {";
		rtn += System.lineSeparator();
		return rtn;
	}
	protected String getDotFooter(){
		String rtn = "";
		rtn += "}";
		rtn += System.lineSeparator();
		return rtn;
	}
	protected String getDotSubgraphHeader(final Block block){
		String rtn = "";
		// cluster header
		rtn += "subgraph cluster";
		rtn += block.getIdx();
		rtn += " {";
		rtn += System.lineSeparator();
		return rtn;
	}
	protected String getDotSubgraphFooter(){
		return getDotFooter();
	}
	
	public void printDot(final Writer os) throws IOException{
		os.write(this.getDotHeader());
		// for each block
		for (int i = 0; i < this.getNumBlock(); i ++) {
			Block block = this.getBlockAtIdx(i);
			// print subgraph header
			os.write(this.getDotSubgraphHeader(block));
			// print nodes
			for (int j = 0; j < block.getNumPNode(); j++) {
				PNode node = block.getPNodeAtIdx(j);	
				node.printDot(os);			
			}
			// print subgraph footer
			os.write(this.getDotSubgraphFooter());
			// print edges
			for (int j = 0; j < block.getNumPNode(); j++) {
				PNode node = block.getPNodeAtIdx(j);
				for (int k = 0; k < node.getNumOutEdge(); k++) {
					PEdge edge = node.getOutEdgeAtIdx(k);
					edge.printDot(os);
				}
			}
		}
		os.write(this.getDotFooter());		
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((blocks == null) ? 0 : blocks.hashCode());
		result = prime * result + ((blockCapacityUnits == null) ? 0 : blockCapacityUnits.hashCode());
		result = prime * result + ((blockCapacity == null) ? 0 : blockCapacity.hashCode());
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
		Partition other = (Partition) obj;
		if (blocks == null) {
			if (other.blocks != null)
				return false;
		} else if (!blocks.equals(other.blocks))
			return false;
		if (blockCapacityUnits == null) {
			if (other.blockCapacityUnits != null)
				return false;
		} else if (!blockCapacityUnits.equals(other.blockCapacityUnits))
			return false;
		if (blockCapacity == null) {
			if (other.blockCapacity != null)
				return false;
		} else if (!blockCapacity.equals(other.blockCapacity))
			return false;
		return true;
	}

	/*
	 * toString
	 */
	protected String getBlockToString() {
		String rtn = "";
		rtn = rtn + blocks.toString();
		rtn = Utils.addIndent(1, rtn);		
		return rtn;
	}
	
	@Override
	public String toString() {
		String rtn = "";
		String superStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", this.getName());
		// profile
		rtn = rtn + this.getEntryToString("profile", this.getProfile().getName());
		// blocks
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "blocks = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getBlockToString();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();	
		// toString
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "toString() = ";
		rtn = rtn + Utils.getNewLine();
		superStr = super.toString();
		superStr = Utils.addIndent(1, superStr);
		rtn = rtn + superStr;
		rtn = rtn + ",";
		rtn = rtn + Utils.getNewLine();
		// end
		rtn = rtn + "]";
		return rtn;
	}

	private List<Block> blocks;
	private CObjectCollection<CObject> blockCapacityUnits;
	private CObjectCollection<Capacity> blockCapacity;
}
