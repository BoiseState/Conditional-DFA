package conditional.scalar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionalInfo {
	
	int line = 0;
	boolean branch = true;
	
	public ConditionalInfo(int line, boolean branch){
		this.line = line;
		this.branch = branch;
	}
	
	public boolean getBranch(){
		return this.branch;
	}
	
	public int getLine(){
		return this.line;
	}
	
	@Override
	public String toString(){
		return Integer.toString(line) + " " + branch;
	}
	
	public static Map<Integer, Boolean> createFlowInfoMap(List<ConditionalInfo> list){
		Map<Integer,Boolean> map = new HashMap<Integer, Boolean>();
		for(ConditionalInfo ci : list){
			map.put(ci.getLine(), ci.getBranch());
		}
		return map;
	}
	
	public static List<ConditionalInfo> createFlowInfoList(String flowList){
		//System.out.println("I'm here " + flowList);
		List<ConditionalInfo> myList = new ArrayList<ConditionalInfo>();
		int lineNumber = 1;
		int last = 0;
		for (int i = 0; i < flowList.length(); i++){
			//System.out.println(i);
			//System.out.println(flowList.charAt(i));
			
			
			Character current = flowList.charAt(i);
			if (current == 'f' || current == 't'){
				lineNumber = Integer.parseInt(flowList.substring(last, i));
				//System.out.println(lineNumber);
				last = i+ 1;
				boolean branch = true;
				if (current == 'f'){
					branch = false;
				}
				myList.add(new ConditionalInfo(lineNumber, branch));
			}
		}
		return myList;
	}
}
