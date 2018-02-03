package original.analysis;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import disjoint.analysis.ValueAnalysis;
import disjoint.domain.Domain;
import disjoint.state.AbstractState;
import driver.StartValue;
import soot.Local;
import soot.Timers;
import soot.Unit;
import soot.grimp.internal.GAndExpr;
import soot.jimple.BinopExpr;
import soot.jimple.Expr;
import soot.toolkits.graph.UnitGraph;

public class ValueAnalysisReport extends ValueAnalysis {

	public ValueAnalysisReport(UnitGraph graph, List<Domain> setDomains, boolean symbolicOn) {
		super(graph, setDomains, symbolicOn);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void report() {
		System.out.println("Done in " + time +" fn "+ Timers.v().totalFlowNodes + ", fc"+ Timers.v().totalFlowComputations + "\n");
		String timeData ="\t"+ time +"\n";
		
		try {
			//StartAnalysisKestrel.timeDataFile.append(timeData);
			String timeDataFile = StartValue.path+"/time/"+StartValue.className+"_"+StartValue.methodId+"_"+StartValue.domain+".txt";
			RandomAccessFile rf = new RandomAccessFile(timeDataFile, "rwd");
			FileChannel fileChannel = rf.getChannel();
			FileLock lock = fileChannel.lock();
			fileChannel.position(fileChannel.size());
			fileChannel.write(Charset.defaultCharset().encode(CharBuffer.wrap(timeData)));
			fileChannel.force(false);
			lock.release();
			fileChannel.close();
			rf.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(StartValue.print){
		Iterator<Unit> iter = b.getUnits().iterator();
		int stmtCount = 0;
		//File to write the output to
		while(iter.hasNext()){
			Unit u = iter.next();
			//check against statetment after
			//which state has been changed
			stmtCount++;
			if(outputStmt.contains(u)){
				//String that keeps that state info for the current state
				String output = stmtCount + " " + u +":" + b.getMethod().getSignature() + "\n";
				//System.out.println(output);
				AbstractState fall = getFallFlowAfter(u);
				if(!fall.getStates().isEmpty() && fall.isFeasible()){
					for(Local l : b.getLocals()){
						if(changedVariables.get(u).contains(l)){
							//System.out.println(" l " + l + " u " + u + " \n" + fall);
							//System.out.flush();
							Set<BinopExpr> varPerState = evaluateStates(fall, l);
							Expr state = null;
							for(Expr be : varPerState){
								if(state == null){
									state = be;
								} else {
									state = new GAndExpr(state, be);
								}
							}
							output += l + "->" + solver.smt2((BinopExpr)state) + "\n";
							//System.out.println(l + "->" + solver.generate((BinopExpr)state));
						}
					}
				}
				//for other branch if exists
				List<AbstractState> branches = getBranchFlowAfter(u);
				if(!branches.isEmpty()){
					for(AbstractState branch : branches){
						if(!branch.getStates().isEmpty() && branch.isFeasible()){
							for(Local l : b.getLocals()){
								if(changedVariables.get(u).contains(l)){
									//System.out.println(" lf " + l + " u " + u + " \n" + fall);
									Set<BinopExpr> varPerState = evaluateStates(branch, l);
									Expr state = null;
									for(Expr be : varPerState){
										if(state == null){
											state = be;
										} else {
											state = new GAndExpr(state, be);
										}
									}
									output += l+"f" + "->" + solver.smt2((BinopExpr)state) + "\n";
									//System.out.println(l+"f" + "->" + solver.generate((BinopExpr)state));
								}
							}
						}

					}
				}
				if(StartValue.writeToFile){
				//write the string to the file
				try {
					StartValue.fileToWrite.write(output);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				} else {
					System.out.println(output);
				}
			}//end outputStmt check
		
	}
		}
	}

}
