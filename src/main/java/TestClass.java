import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class TestClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("asdf");
		AssignmentSubmission submission = new AssignmentSubmission("/java/lang/String.class", "toCharArray()[C"); 
		ClassNode targetClass = AssignmentSubmission.findClassNode("/java/lang/String.class");
		MethodNode targetMethod = AssignmentSubmission.findMethodNode(targetClass, "toCharArray()[C");
		System.out.println(targetMethod.instructions.size());
	}

}
