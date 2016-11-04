import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.tree.analysis.AnalyzerException;

import util.DominanceTreeGenerator;
import util.cfg.Graph;
import util.cfg.Node;

public class ControlDependencyComputation {
	
	public Graph getControlDependencyGraph(Graph postDominatorGraph, Map<Node, Node> branches){
		Graph cdg = new Graph();

		boolean addToGraph = false;
		for(Map.Entry<Node, Node> branch : branches.entrySet()){
			Node leastCommonNode;
			if(branch.getKey().toString().equals("start")){
				leastCommonNode = branch.getKey();
			}else{
				leastCommonNode = postDominatorGraph.getLeastCommonAncestor(branch.getKey(), branch.getValue());
			}
			for (Node node : postDominatorGraph.getNodes()) {
				for (Node succ: postDominatorGraph.getPredecessors(node)) {
					
					//From the b root of the branch set all the way to common ancestor
					if(node.equals(branch.getValue())){
						addToGraph = false;
					}else if(node.equals(leastCommonNode)){
						addToGraph = true;
					}
					
					if(addToGraph){							
						cdg.addNode(node);
						cdg.addNode(succ);
						cdg.addEdge(node, succ);
					}						
				}
			}
		}
		System.out.println("Control Dependency Graph");
		System.out.println(cdg);
		return cdg;

	}
	
	public void AddStartNode(Graph graph){
		Node node = new Node("start");    	
    	graph.addNode(node);
    	graph.addEdge(node, graph.getEntry());
    	graph.addEdge(node, graph.getExit());
	}
	
	public Map<Node, Node> getAllBranches(Graph cfg){
		Map<Node, Node> branches = new HashMap <Node, Node>();
		for (Node node : cfg.getNodes()) {
			for (Node succ: cfg.getSuccessors(node)) {
				if(cfg.isDecisionEdge(node, succ)){
					branches.put(node, succ);
				}
			}
		}
		return branches;
	}
}
