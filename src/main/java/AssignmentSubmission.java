import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.Variable;
import util.DataFlowAnalysis;
import util.DominanceTreeGenerator;
import util.cfg.CFGExtractor;
import util.cfg.Graph;
import util.cfg.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This should be the entry-point of your programming submission.
 *
 * You may add as many of your own classes and packages as you like to this project structure.
 *
 */
public class AssignmentSubmission implements Slicer {

	ClassNode targetClassNode;
	MethodNode targetMethod;
	Graph cfg;
	String targetClassString;
	
	public AssignmentSubmission(String targetClass, String methodSignature){
		targetClassNode = findClassNode(targetClass);
		targetMethod = findMethodNode(targetClassNode, methodSignature);
		targetClassString = targetClass;
		try{
			cfg = CFGExtractor.getCFG(targetClassNode.name, targetMethod);
		} catch (AnalyzerException e) {
			// Failed to extract CFG
			e.printStackTrace();
		}
	}
	
	public static ClassNode findClassNode(String targetClass){
		ClassNode target = new ClassNode(Opcodes.ASM4);
        InputStream in=CFGExtractor.class.getResourceAsStream(targetClass);
        ClassReader classReader;
		try {
			classReader = new ClassReader(in);
			classReader.accept(target, 0);
		} catch (IOException e1) {
			// Fail to read class file.
			e1.printStackTrace();
		} 
		return target;
	}
	
	/**
	 * Find the method with the given methodSignature, belonging to the targetClass.
	 * @param targetClass
	 * @param methodSignature
	 * @return
	 */
	public static MethodNode findMethodNode(ClassNode targetClass, String methodSignature){
		for(MethodNode mn : (List<MethodNode>)targetClass.methods){
	        	String signature = mn.name+mn.desc;
	        	if(!signature.equals(methodSignature))
	        		continue;
	        	else
	        		return mn;
		}
		return null; //Method signature not found.
	}

    /**
     * Returns true if there is a data dependence relation from a to b.
     *
     * In other words, a variable is assigned a value at a, and that value is
     * subsequently used at b, without any intervening definitions of that variable.
     *
     * @param a
     * @param b
     * @return
     */
    @Override
    public boolean isDataDepence(AbstractInsnNode a, AbstractInsnNode b) {
        
    	DataFlowAnalysis dfa = new DataFlowAnalysis();    	
    	
    	//Class for computing the data dependency graph and helper functions
    	DataDependencyComputation ddc = new DataDependencyComputation();
    	try {
    		//Get all defined variables for a specific Node instruction
			Map<Node, List<Variable>> writeMap = ddc.getAllWriteVariables(targetClassString, targetMethod, cfg);
			//Get all used by variables for a specific Node instruction
			Map<Node, List<Variable>> readMap = ddc.getAllReadVariables(targetClassString, targetMethod, cfg);
			
			//Get complete Data Dependency Graph
			Graph ddg = ddc.getDataDependencyGraph(writeMap, readMap, targetClassString, targetMethod, cfg);
			
			//Query the complete Data Dependency Graph between two abstract instruction nodes
			return ddc.isDataDependent(a, b, ddg);
			
			//TODO Check Dependency
			
		} catch (AnalyzerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return false;
    }
    
    //This is my test method
    public boolean isDataDepence() {
    	Graph ddg = new Graph();
    	DataFlowAnalysis dfa = new DataFlowAnalysis();
    	
    	Map<Node, List<Variable>> writeMap = new HashMap<Node, List<Variable>>();
    	Map<Node, List<Variable>> readMap = new HashMap<Node, List<Variable>>();
    	
    	try {
    		System.out.println("Starting dfa definedBy");
	    	for (Node node : cfg.getNodes()) {
				AbstractInsnNode abNode = node.getInstruction();
				if(abNode != null){
					Collection<Variable> definedVariables = dfa.definedBy("/java/lang/String.class", targetMethod, abNode);
					
					if(!definedVariables.isEmpty()){
						List<Variable> writeVariableList = new ArrayList<Variable>();
						for (Variable variable : definedVariables) {
							writeVariableList.add(variable);
						}
						writeMap.put(node, writeVariableList);
						//ddg.addNode(node);
					}

					Collection<Variable> usedVariables = dfa.usedBy("/java/lang/String.class", targetMethod, abNode);
					
					if(!usedVariables.isEmpty()){

						List<Variable> readVariableList = new ArrayList<Variable>();
						for (Variable variable : usedVariables) {
							readVariableList.add(variable);
						}
						readMap.put(node, readVariableList);
					}
				}				
			}
	    	//System.out.println(writeMap);
			//System.out.println(readMap);
			for (Node writeNode : writeMap.keySet()) {
				for (Node readNode : cfg.getNodes()) {
					AbstractInsnNode abNode = readNode.getInstruction();
					if(abNode != null){
						Collection<Variable> usedVariables = dfa.usedBy("/java/lang/String.class", targetMethod, abNode);
						
						if(!usedVariables.isEmpty()){

							List<Variable> readVariableList = new ArrayList<Variable>();
							for (Variable readVariable : usedVariables) {
								List<Variable> writeList = writeMap.get(writeNode);
								for (Variable writeVariable : writeList) {
									
									if(writeVariable.equals(readVariable)){
										if(!writeNode.equals(readNode)){
											ddg.addNode(writeNode);
											ddg.addNode(readNode);
											ddg.addEdge(writeNode, readNode);
										}										
									}
								}
							}							
						}
					}					
				}				
			}
			
			System.out.println(ddg);

    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
    }
    
    
    /**
     * Returns true if a is dependent upon b and false otherwise.
     *
     * In other words, returns true if b represents a conditional instruction that
     * determines whether or not b will execute (following the definition of control
     * dependence discussed in lectures).
     *
     * @param a
     * @param b
     * @return
     */
    @Override
    public boolean isControlDependentUpon(AbstractInsnNode a, AbstractInsnNode b) {
    	try{
    		ControlDependencyComputation cdc = new ControlDependencyComputation();
        	cdc.AddStartNode(cfg);
        	
        	DominanceTreeGenerator dtg = new DominanceTreeGenerator(cfg);
    		Graph postDominatorGraph = dtg.postDominatorTree();
        	
        	Map<Node, Node> branches = cdc.getAllBranches(cfg);
        	
        	Graph controlDependencyGraph = cdc.getControlDependencyGraph(postDominatorGraph, branches);
        	
        	//TODO check if a control dependent on b
        	//TODO write method
        	
    	}catch (IOException | AnalyzerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
    }   

    //My Test Method
    public boolean isControlDependentUpon1() {
    	
    	try {
    		AddStartNode(cfg);
			DominanceTreeGenerator dtg = new DominanceTreeGenerator(cfg);
			Graph postDominatorGraph = dtg.postDominatorTree();
			
			//Get Branches
			Map<Node, Node> branches = new HashMap <Node, Node>();
			System.out.println("manual print");
			for (Node node : cfg.getNodes()) {
				for (Node succ: cfg.getSuccessors(node)) {
					if(cfg.isDecisionEdge(node, succ)){
						branches.put(node, succ);
					}
				}
			}
			
			System.out.println("Starting the post dominance");
			
			//System.out.println(postDominatorGraph.getNodes());
			for (Node postDom : postDominatorGraph.getNodes()) { //b
				for (Node node: postDominatorGraph.getSuccessors(postDom)) { //a
					//System.out.println(postDom.toString()+"->"+node.toString()+"\n");
				}
			}
			
			//Control Dependence Graph
			Graph cdg = new Graph();
			//System.out.println("Nodes");
			//System.out.println(postDominatorGraph.getNodes());
			boolean addToGraph = false;
			for(Map.Entry<Node, Node> branch : branches.entrySet()){
				Node leastCommonNode;
				if(branch.getKey().toString().equals("start")){
					leastCommonNode = branch.getKey();
				}else{
					leastCommonNode = postDominatorGraph.getLeastCommonAncestor(branch.getKey(), branch.getValue());
				}
				
				
				//String dotString = null;
				
				for (Node node : postDominatorGraph.getNodes()) {
					for (Node succ: postDominatorGraph.getPredecessors(node)) {
						//dotString+=node.toString()+"->"+succ.toString()+"\n";
						
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
				//System.out.println(dotString);
			}
			System.out.println("Control Dependency Graph");
			System.out.println(cdg);
			
			
		} catch (IOException | AnalyzerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
    }

    /**
     * Should return a backward slice on the criterion statement (for all variables).
     * @param criterion
     * @return
     */
    @Override
    public List<AbstractInsnNode> backwardSlice(AbstractInsnNode criterion) {
        //REPLACE THIS METHOD BODY WITH YOUR OWN CODE
    	
        return null;
    }
    
    //Custom internal method helpers
    //Adding augmented control flow start node
    private void AddStartNode(Graph graph){
    	Node node = new Node("start");    	
    	graph.addNode(node);
    	graph.addEdge(node, graph.getEntry());
    	graph.addEdge(node, graph.getExit());
    	//graph.addEdge(graph.getEntry(), node);
    	//graph.addEdge(graph.getExit(), node);
    }
}
