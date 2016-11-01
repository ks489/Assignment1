import java.io.IOException;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import util.*;
import util.cfg.Node;

public class TestClass {

	public static void main(String[] args) throws Exception, AnalyzerException {
		System.out.println("Starting AssignmentSubmission");
		
		//string equals method
		//AssignmentSubmission assignment = new AssignmentSubmission("/java/lang/String.class", "equals(Ljava/lang/Object;)Z");
		
		//string trim method
		AssignmentSubmission assignment = new AssignmentSubmission("/java/lang/String.class", "trim()Ljava/lang/String;");
		
		
		
		System.out.println("Ending AssignmentSubmission");
		System.out.println(assignment.cfg);
		assignment.isControlDependentUpon();
		for (util.cfg.Node node : assignment.cfg.getNodes()) {
			//System.out.println(assignment.isControlDependentUpon(node.getInstruction(), node.getInstruction().getNext()));
			//System.out.println(node);
		}
		
		
	}

}
