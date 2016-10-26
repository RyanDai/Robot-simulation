package problem;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

	public static void main(String[] args) throws IOException {
		String inputFileName = args[2];
		String outputFileName = args[3];
		Solution s = new Solution(inputFileName);
		ProblemSpec pS = s.getProblemSpec();
		FileWriter output = new FileWriter(outputFileName);
		
		s.sampling3(300);
		s.generateGraph();
		List<ArmConfig> path = new ArrayList<ArmConfig>();
		path = s.searchGraph(pS.getInitialState());
		for(int i = 0; i < path.size() - 1; i++){
			s.printValidPath(path.get(i), path.get(i + 1));
		}
		List<ArmConfig> finalPath = s.getFinalPath();
		
		output.write(finalPath.size() - 2 + "\n");
		output.write((finalPath.get(0).toString()));
		for(int i = 1; i < finalPath.size(); i++){
			output.write("\n");
			output.write((finalPath.get(i).toString()));
		}
		output.close();
		/*String inputFileName = args[2];
		String outputFileName = args[3];
		Solution s = new Solution(inputFileName);
		ProblemSpec pS = s.getProblemSpec();
		FileWriter output = new FileWriter(outputFileName);
		Random rd = new Random();
		int prob = rd.nextInt();
		if( prob % 15 > 2){
			s.sampling3(300);
		}
		else if(prob % 15 <= 1){
			s.sampling3(600);
		}
		else{
			s.sampling3(900);
		}
		s.sampling3(300);
		s.generateGraph();

		List<ArmConfig> path = new ArrayList<ArmConfig>();
		path = s.searchGraph(pS.getInitialState());
		for(int i = 0; i < path.size() - 1; i++){
			s.printValidPath(path.get(i), path.get(i + 1));
		}
		List<ArmConfig> finalPath = s.getFinalPath();
		
		output.write(finalPath.size() - 2 + "\n");
		output.write((finalPath.get(0).toString()));
		for(int i = 1; i < finalPath.size(); i++){
			output.write("\n");
			output.write((finalPath.get(i).toString()));
		}
		output.close();*/
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
