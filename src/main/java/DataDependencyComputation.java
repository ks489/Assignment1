import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.Variable;
import util.DataFlowAnalysis;
import util.cfg.Graph;
import util.cfg.Node;

public class DataDependencyComputation {
	
	public Graph getDataDependencyGraph(Map<Node, List<Variable>> writeMap, Map<Node, List<Variable>> readMap, String targetClass, MethodNode targetMethod, Graph cfg){
		Graph ddg = new Graph();
    	try {
    		DataFlowAnalysis dfa = new DataFlowAnalysis();
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
        return ddg;
	}
	
	public Map<Node, List<Variable>> getAllReadVariables(String targetClass, MethodNode targetMethod, Graph cfg) throws AnalyzerException{
		Map<Node, List<Variable>> writeMap = new HashMap<Node, List<Variable>>();
		DataFlowAnalysis dfa = new DataFlowAnalysis();
		
		for (Node node : cfg.getNodes()) {
			AbstractInsnNode abNode = node.getInstruction();
			if(abNode != null){
				Collection<Variable> definedVariables = dfa.definedBy(targetClass, targetMethod, abNode);
				
				if(!definedVariables.isEmpty()){
					List<Variable> writeVariableList = new ArrayList<Variable>();
					for (Variable variable : definedVariables) {
						writeVariableList.add(variable);
					}
					writeMap.put(node, writeVariableList);					
				}				
			}				
		}
		return writeMap;
	}
	
	public Map<Node, List<Variable>> getAllWriteVariables(String targetClass, MethodNode targetMethod, Graph cfg) throws AnalyzerException{
		Map<Node, List<Variable>> readMap = new HashMap<Node, List<Variable>>();
		DataFlowAnalysis dfa = new DataFlowAnalysis();
		
		for (Node node : cfg.getNodes()) {
			AbstractInsnNode abNode = node.getInstruction();
			if(abNode != null){
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
		return readMap;
	}
}
