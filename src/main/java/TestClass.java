import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class TestClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Starting Normal Initialisation");
		AssignmentSubmission submission = new AssignmentSubmission("/java/lang/String.class", "toCharArray()[C"); 
		ClassNode targetClass = AssignmentSubmission.findClassNode("/java/lang/String.class");
		MethodNode targetMethod = AssignmentSubmission.findMethodNode(targetClass, "toCharArray()[C");
		//System.out.println(targetMethod.instructions.size());
		//System.out.println("Ending Normal Initialisation");
		
		System.out.println("Printing the cfg graph");
		System.out.println(submission.cfg.toString());
		System.out.println("Finished print the graph");
		util.cfg.Graph graph = submission.cfg;
		for (util.cfg.Node node : graph.getNodes()) {
			System.out.println(node.toString());
			//System.out.println(node.getClass());
			System.out.println(node.getInstruction().getClass());
			System.out.println(node.getInstruction().getPrevious());
			System.out.println(node.getInstruction().getPrevious());
			System.out.println(node.getInstruction().toString());
			
		}
		
		
		//for (Node string : submission.cfg) {
			
		//}
		
		//System.out.println(submission.targetClassNode.name);
		//System.out.println(submission.targetMethod.name);
	}

}
