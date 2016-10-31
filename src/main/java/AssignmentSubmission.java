import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import util.DominanceTreeGenerator;
import util.cfg.CFGExtractor;
import util.cfg.Graph;
import util.cfg.Node;

import java.io.IOException;
import java.io.InputStream;
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
	
	public AssignmentSubmission(String targetClass, String methodSignature){
		targetClassNode = findClassNode(targetClass);
		targetMethod = findMethodNode(targetClassNode, methodSignature);
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
        //REPLACE THIS METHOD BODY WITH YOUR OWN CODE
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
    	
    	try {
    		AddStartNode(cfg);
			DominanceTreeGenerator dtg = new DominanceTreeGenerator(cfg);
			Graph postDominatorGraph = dtg.postDominatorTree();
			
			
			System.out.println("manual print");
			for (Node node : cfg.getNodes()) {
				for (Node succ: cfg.getSuccessors(node)) {
					System.out.println(node.toString()+"->"+succ.toString()+"\n");
				}
				//if(cfg.isDecisionEdge(node, cfg.getSuccessors(node)))
				//decisions = new HashMap<DefaultEdge, Boolean>();
				//graph = new DirectedMultigraph<Node, DefaultEdge>(new ClassBasedEdgeFactory<Node, DefaultEdge>(DefaultEdge.class));
			}
			
			//System.out.println(dtg);
			//System.out.println(postDominatorGraph);
		} catch (IOException | AnalyzerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
    }
    
    public boolean isControlDependentUpon() {
    	
    	try {
    		AddStartNode(cfg);
			DominanceTreeGenerator dtg = new DominanceTreeGenerator(cfg);
			Graph postDominatorGraph = dtg.postDominatorTree();
			
			//Get Branches
			Map<Node, Node> branches = new HashMap <Node, Node>();
			System.out.println("manual print");
			for (Node node : cfg.getNodes()) {
				for (Node succ: cfg.getSuccessors(node)) {
					//System.out.println(node.toString()+"->"+succ.toString()+"\n");
					//System.out.println(cfg.isDecisionEdge(node, succ) + " " + node.toString()+"->"+succ.toString()+"\n");
					if(cfg.isDecisionEdge(node, succ)){
						//This is a branch
						branches.put(node, succ);
					}
				}
			}
			
			System.out.println("Starting the post dominance");
			for (Node postDom : postDominatorGraph.getNodes()) { //b
				for (Node node: postDominatorGraph.getSuccessors(postDom)) { //a
					//System.out.println(node.toString()+"->"+succ.toString()+"\n");
					
					if(branches.containsKey(node)){
						
						System.out.println("Contains key");
						if(branches.get(node) == postDom){
							System.out.println("The values are equal");
							System.out.println(postDom.toString()+"->"+node.toString()+"\n");
							//remove that set
							branches.remove(node);
						}
					}
				}
			}
			
			System.out.println("Least common ancestor");
			for(Map.Entry<Node, Node> branch : branches.entrySet()){
				System.out.println(branch);
				Node leastCommon = postDominatorGraph.getLeastCommonAncestor(branch.getKey(), branch.getValue());
				//postDominatorGraph.getTransitiveSuccessors(m)
				System.out.println(leastCommon);
				//if(leastCommon == ){
					
				//}
			}
			
			//Map<Map<string, string>, Map<string, string>> ControlDependencyMap 
			
			//
			
			
			//System.out.println(dtg);
			//System.out.println(postDominatorGraph);
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
    
    //Adding augmented control flow start node
    private void AddStartNode(Graph graph){
    	Node node = new Node("start");    	
    	graph.addNode(node);
    	graph.addEdge(node, graph.getEntry());
    	graph.addEdge(node, graph.getExit());
    }
}
