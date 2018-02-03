package conditional.analysis;

import java.util.*;

import soot.Local;
import soot.Unit;
import soot.jimple.IfStmt;
import soot.toolkits.graph.UnitGraph;
import util.Variables;

public class ConditionalReachingDefinitions {

	private Map unitToExpressionsBefore; //mapping each units to reachable expressions before
	//private Map unitToExpresionAfter; //mapping each unit to reach expression after
	
	private List <Map> unitToExpressionsFall; //mapping each units to reachable expressions before
	
	private List <Map> unitToExpressionsBranch; //mapping each units to reachable expressions before
	
	private long time;

	public ConditionalReachingDefinitions(UnitGraph graph, String flowList){
		
		String[] branches = flowList.split(",");
		String conditions = "";
		int countOfCond = 0 ;
		int countOfStmt = 1;
		for(Unit u : graph.getBody().getUnits()){
			if(u instanceof IfStmt){
				countOfCond++;
				for(String c : branches){
					if(c.endsWith("f")){
						String id = c.split("f")[0];
						if(id.equals(Integer.toString(countOfCond))){
							conditions+=countOfStmt+"f";
						}
					} else if (c.endsWith("t")){
						String id = c.split("t")[0];
						if(id.equals(Integer.toString(countOfCond))){
							conditions+=countOfStmt+"t";
						}
					} else {
						if(!c.isEmpty()){
							System.out.println("unknown codition " + c);
						}
					}
				}
			}
			countOfStmt++;
		}
		//System.out.println(conditions);
		ConditionalReachingDefinitionsAnalysis analysis = new ConditionalReachingDefinitionsAnalysis(graph, conditions);
		time = analysis.getTime();
		//ReachingDefinitionsOriginal analysis = new ReachingDefinitionsOriginal(graph, "allBranches");
		
		unitToExpressionsBefore = new HashMap(graph.size() * 2 + 1, 07.f); //I don't understand this code
		//unitToExpressionsFall = new ArrayList <HashMap<Local, Set<Unit>>>(); //I don't understand this code
		//unitToExpressionsBranch = new HashMap(graph.size() * 2 + 1, 07.f); //I don't understand this code
		
		Iterator unitIt = graph.iterator();
		
		while(unitIt.hasNext()){
			Unit s = (Unit) unitIt.next();
			
			//MyFlowData myData = analysis.getFlowBefore(s);
			//Map myMap = myData.getData();
			Map myMap = analysis.getFlowBefore(s);
			//now each unit is mapped to a map of variables and reachable expressions
			unitToExpressionsBefore.put(s, myMap);
			
//			Map fallMap = analysis.getFallFlowAfter(s);
//			if (fallMap!= null){
//				unitToExpressionsFall.put(s, fallMap);
//			}
			
//			
//			Map branchMap = (Map) analysis.getBranchFlowAfter(s);
//			if (branchMap != null){
//				unitToExpressionsBranch.put(s, branchMap);
//			}
		}
		
	}
	
	public long getTime(){
		return time;
	}
	
	public String getReachableExpressions(Unit s, String type) {
		StringBuilder toReturn = new StringBuilder();
		toReturn.append(Variables.numbers.get(s)+" " + s +"\n");
		
		
		HashMap<Local, Set<Unit>> defMap = new HashMap<Local, Set<Unit>>();		
		defMap = (HashMap) unitToExpressionsBefore.get(s);
		
		//if (type.equals("before")){
			//defMap = (Map) unitToExpressionsBefore.get(s);}
//		}else if (type.equals("fall")){
			//defMap = (Map) unitToExpressionsFall.get(s);
//		}else{
//			defMap = (Map) unitToExpressionsBranch.get(s);
//		}
		
		if (defMap == null){return toReturn.toString();}
		for (Local l: defMap.keySet()){
			//toReturn.append("Local: ");
//			toReturn.append(l.getNumber());
//			
			
			toReturn.append(l.toString());
			toReturn.append(":");
			toReturn.append(" ");
			
			for (Unit u: defMap.get(l)){
				//toReturn.append(u);
				toReturn.append(Variables.numbers.get(u));
				toReturn.append(": ");
				
			}
			toReturn.delete(toReturn.length()-2, toReturn.length()-1);
			toReturn.append("\n");
			
			
			
		}
		
		
				
		
		return toReturn.toString();
		
		
		
	}

	//@Override
	//public List getReachableExpressionAfter(Unit s) {
		// TODO Auto-generated method stub
		//return null;
	//}
	
	
	
	
	
}
