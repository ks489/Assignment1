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
		
		//This is for Control Dependency Graph
		//ComputeControlDependency(assignment);
		
		//This for the Data Dependency Graph
		ComputeDataDependency(assignment);
		System.out.println("Ending AssignmentSubmission");
	}
	
	private static void ComputeControlDependency(AssignmentSubmission assignment){
		System.out.println("-----Computing Control Dependency-----");
		System.out.println(assignment.cfg);
		assignment.isControlDependentUpon1();
		for (util.cfg.Node node : assignment.cfg.getNodes()) {
			//System.out.println(assignment.isControlDependentUpon(node.getInstruction(), node.getInstruction().getNext()));
			//System.out.println(node);
		}
		System.out.println("Ending Control Dependency");
	}
	
	private static void ComputeDataDependency(AssignmentSubmission assignment){
		System.out.println("-----Computing Data Dependency-----");
		assignment.isDataDepence();
		System.out.println("Ending Data Dependency");
	}

}
