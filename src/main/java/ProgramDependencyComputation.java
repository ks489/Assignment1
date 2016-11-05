import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;

import util.cfg.Graph;
import util.cfg.Node;

import util.DataFlowAnalysis;


public class ProgramDependencyComputation {
	
	//I was not able to get help on how program dependency graphs are generated. I did post on the forum with no reponse. 
	//So this is my interpretation of the program dependency graph.
	public Graph getProgramDependencyGraph(Graph ddg, Graph cdg){
		Graph pdg = new Graph();
		for (Node a : ddg.getNodes()) {
			for (Node b : ddg.getSuccessors(a)) {
				pdg.addNode(a);
				pdg.addNode(b);
				pdg.addEdge(a, b);
			}
		}
		
		for (Node a : cdg.getNodes()) {
			for (Node b : cdg.getSuccessors(a)) {
				pdg.addNode(a);
				pdg.addNode(b);
				pdg.addEdge(a, b);
			}
		}
		
		return pdg;
	}
	
	public List<AbstractInsnNode> getAllInstructionsOfSlice(Graph pdg, AbstractInsnNode abNode){
		List<AbstractInsnNode> nodeList = new ArrayList<AbstractInsnNode>();
		for (Node a : pdg.getNodes()) {
			for (Node b : pdg.getSuccessors(a)) {
				//Add the instruction to the list when they are matched in the program dependency graph
				if(abNode.equals(b.getInstruction())){
					nodeList.add(b.getInstruction());
				}
				if(abNode.equals(a.getInstruction())){
					nodeList.add(a.getInstruction());
				}
			}
		}
		return nodeList;
	}
	
	

}
