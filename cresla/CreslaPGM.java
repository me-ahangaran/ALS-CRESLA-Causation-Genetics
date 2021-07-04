package cresla;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import als.ALSUtils;
import cresla.CreslaPGM.PGMEdge;

//Create probabilistic causal graph of CRESLA dataset
public class CreslaPGM {

	private double causalTendThreshold = 0.2;//0.2 is the best value (Interval=[0, 0.65])
	private double entropyThreshold = 3;//4.2 is the best value (Interval=[3.7, 5.96])
	private double cterThrshold = 0; //(Interval=[0, 0.138])
	private String edgeSelectType = "CT";	//CT (both causal tendency and entropy thresholds) or CTER (CTER threshold)
	ArrayList<Integer> pgmNodes = new ArrayList<Integer>();//all nodes of PGM
	
	private ArrayList<Integer> getPGMNodes(ArrayList<PGMEdge> pgmEdges){
		ArrayList<Integer> pgmNodes = new ArrayList<Integer>();
		
		for(int i=0; i<pgmEdges.size(); i++){
			PGMEdge edge = pgmEdges.get(i);
			int causeFeature = edge.getCauseNum();
			int effectFeature = edge.getEffectNum();
			if(!pgmNodes.contains(causeFeature))
				pgmNodes.add(causeFeature);
			if(!pgmNodes.contains(effectFeature))
				pgmNodes.add(effectFeature);
		}//for
		return pgmNodes;
	}//getPGMNodes
	
	public void createIntervalFiles(){
		BufferedReader entropyCausalLinksInput = null, causalTendencyInput = null, CTERInput = null;
		try {
			entropyCausalLinksInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\EntropyCausalLinks.csv"));
			causalTendencyInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CausalTendency.csv"));
			CTERInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CTER.csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str;
		double val;
		ArrayList<Double> entropyCausalLinksList = new ArrayList<Double>();
		ArrayList<Double> causalTendencyList = new ArrayList<Double>();
		ArrayList<Double> CTERList = new ArrayList<Double>();
		try {
			while((str = entropyCausalLinksInput.readLine()) != null){
				val = Double.parseDouble(str);
				entropyCausalLinksList.add(val);
			}//while
			
			while((str = causalTendencyInput.readLine()) != null){
				val = Double.parseDouble(str);
				causalTendencyList.add(val);
			}//while
			
			while((str = CTERInput.readLine()) != null){
				val = Double.parseDouble(str);
				CTERList.add(val);
			}//while
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CreslaUtils util = new CreslaUtils();
		File intervalsFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CausalEdgesInterval.csv");
		try {
			PrintWriter pwInterval = new PrintWriter(intervalsFile);
			pwInterval.println("Measure, Min, Max");
			pwInterval.println("Entropy,"+util.getMinVal(entropyCausalLinksList)+", "+util.getMaxVal(entropyCausalLinksList));
			pwInterval.println("Causal tendency,"+util.getMinVal(causalTendencyList)+", "+util.getMaxVal(causalTendencyList));
			pwInterval.println("CTER,"+util.getMinVal(CTERList)+", "+util.getMaxVal(CTERList));
			pwInterval.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Create intervals file successfully.");
	}//createIntervalFiles
	
	//create severity file for all causal edges
	public void createSeverityFile(){
		ArrayList<PGMEdge> causalEdges = getAllCausalEdges();//all causal edges
		File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\Severity.csv");
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.println("Cause,Effect, Entropy, maxALSFRSAbsChange, maxALSFRSRelativeChange, SeverityAbs, SeverityRelative");
			PGMEdge edge;
			for(int i=0; i<causalEdges.size(); i++){
				edge = causalEdges.get(i);
				pw.print(edge.getCauseNum()+",");
				pw.print(edge.getEffectNum()+",");
				pw.print(edge.getEntropy()+",");
				pw.print(edge.getMaxALSFRSAbsChange()+",");
				pw.print(edge.getMaxALSFRSRelativeChange()+",");
				pw.print(edge.getSeverityALSFRSAbs()+",");
				pw.println(edge.getSeverityALSFRSRelative());
			}//for(i)
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//createSeverityFile
	
	//create a file containing all causal edges and their properties
	public void createAllCausalEdgesFile(){
		ArrayList<PGMEdge> causalEdges = getAllCausalEdges();//all causal edges
		File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CausalEdges.csv");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
			pw.println("Cause, Effect, avgAge, avgMalePercent, avgFemalePercent, avgALSFRSAbsChange, avgALSFRSRelativeChange,"
					+ "avgTimeDist, avgTimePos, avgDeath, avgCause, avgEffect, CTER, entropy, causalTendency,"
					+ "maxCause, maxEffect, maxAge, maxALSFRSAbsChange, maxALSFRSRelativeChange, maxTimeDist, maxTimePos,"
					+ "ALSFRSAbsEntopy, ALSFRSRelativeEntropy, SeverityAbs, SeverityRelative, GeneticsProb, c9orf72, sod1, tardbp, fus");
			PGMEdge edge;
			CreslaGenetics genetics = new CreslaGenetics();
			for(int i=0; i<causalEdges.size(); i++){
				edge = causalEdges.get(i);
				pw.print(edge.getCauseNum()+",");
				pw.print(edge.getEffectNum()+",");
				pw.print(edge.getAvgAge()+",");
				pw.print(edge.getAvgMalePercent()+",");
				pw.print(edge.getAvgFemalePercent()+",");
				pw.print(edge.getAvgALSFRSAbsChange()+",");
				pw.print(edge.getAvgALSFRSRelativeChange()+",");
				pw.print(edge.getAvgTimeDist()+",");
				pw.print(edge.getAvgTimePos()+",");
				pw.print(edge.getAvgDeath()+",");
				pw.print(edge.getAvgCause()+",");
				pw.print(edge.getAvgEffect()+",");
				pw.print(edge.getCTER()+",");
				pw.print(edge.getEntropy()+",");
				pw.print(edge.getCausalTendency()+",");
				pw.print(edge.getMaxCause()+",");
				pw.print(edge.getMaxEffect()+",");
				pw.print(edge.getMaxAge()+",");
				pw.print(edge.getMaxALSFRSAbsChange()+",");
				pw.print(edge.getMaxALSFRSRelativeChange()+",");
				pw.print(edge.getMaxTimeDist()+",");
				pw.print(edge.getMaxTimePos()+",");
				pw.print(edge.getALSFRSAbsEntropy()+",");
				pw.print(edge.getALSFRSRelativeEntropy()+",");
				pw.print(edge.getSeverityALSFRSAbs()+",");
				pw.print(edge.getSeverityALSFRSRelative()+",");
				double[] geneticsProbsList = genetics.getGeneticsAvgCausalEdge(edge.getCauseNum(), edge.getEffectNum());
				double geneticsProb = geneticsProbsList[0]+geneticsProbsList[1]+geneticsProbsList[2]+geneticsProbsList[3];
				pw.print(geneticsProb+",");//total probability of genetics parameters
				pw.print(geneticsProbsList[0]+",");//c9orf72 probability 
				pw.print(geneticsProbsList[1]+",");//sod1 probability
				pw.print(geneticsProbsList[2]+",");//tardbp probability
				pw.print(geneticsProbsList[3]);//fus probability
				pw.println();
			}//for(i)
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pw.close();
	}//createAllCausalEdgesFile
	
	private void createPGMFeaturesFile(ArrayList<Integer> pgmFeatures){
		File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM\\PGMFeatures.csv");
		CreslaUtils util = new CreslaUtils();
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i=0; i<pgmFeatures.size(); i++){
				int fNum = pgmFeatures.get(i);
				String fName = util.getFrsName(fNum);
				pw.println(fName);
			}//for(i)
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//createPGMFeaturesFile
	
	private void createPGMFeaturesFileInOut(ArrayList<Integer> pgmFeatures){
		File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-In-Out\\PGMFeatures-InOut.csv");
		CreslaUtils util = new CreslaUtils();
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i=0; i<pgmFeatures.size(); i++){
				int fNum = pgmFeatures.get(i);
				String fName = util.getFrsName(fNum);
				pw.println(fName);
			}//for(i)
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//createPGMFeaturesFileInOut
	
	private void createPGMFeaturesFileOut(ArrayList<Integer> pgmFeatures){
		File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-Out\\PGMFeatures-Out.csv");
		CreslaUtils util = new CreslaUtils();
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i=0; i<pgmFeatures.size(); i++){
				int fNum = pgmFeatures.get(i);
				String fName = util.getFrsName(fNum);
				pw.println(fName);
			}//for(i)
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//createPGMFeaturesFileOut
	private void createPGMFeaturesFileIn(ArrayList<Integer> pgmFeatures){
		File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-In\\PGMFeatures-In.csv");
		CreslaUtils util = new CreslaUtils();
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i=0; i<pgmFeatures.size(); i++){
				int fNum = pgmFeatures.get(i);
				String fName = util.getFrsName(fNum);
				pw.println(fName);
			}//for(i)
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//createPGMFeaturesFileIn
	
	public ArrayList<PGMEdge> createPGM(){
		createIntervalFiles();//interval file for CTER, Entropy, and Causal tendency among all causal edges
		ArrayList<PGMEdge> pgmEdges = new ArrayList<CreslaPGM.PGMEdge>();
		ArrayList<PGMEdge> causalEdges = getAllCausalEdges();
		
		File pgmFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM\\PGM.csv");
		PrintWriter pwPGM = null;
		try {
			pwPGM = new PrintWriter(pgmFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pwPGM.println("Cause, Effect, avgAge, avgMalePercent, avgFemalePercent, avgALSFRSAbsChange, avgALSFRSRelativeChange,"
				+ "avgTimeDist, avgTimePos, avgDeath, avgCause, avgEffect, CTER, entropy, causalTendency,"
				+ "maxCause, maxEffect, maxAge, maxALSFRSAbsChange, maxALSFRSRelativeChange, maxTimeDist, maxTimePos,"
				+ "ALSFRSAbsEntopy, ALSFRSRelativeEntropy, SeverityAbs, SeverityRelative, c9orf72, sod1, tardbp, fus, GeneticsProb,"
				+ "c9orf72 nums, sod1 nums, tardbp nums, fus nums, Genetics nums, Patients nums");
		boolean selectEdge = false;
		PGMEdge edge;
		//create genetics file for all causal edges
		CreslaGenetics genetics = new CreslaGenetics();
		genetics.createCausalEdgesGeneticsFile();//create genetics file for all causal edges
		double c9orf72Ctr = 0;
		double sod1Ctr = 0;
		double fusCtr = 0;
		double tardbpCtr = 0;
		double patientsCtr = 0;
		double geneticsCtr = 0;
		
		for(int i=0; i<causalEdges.size(); i++){
			edge = causalEdges.get(i);
			selectEdge = selectCausalEdge(edge);
			if(selectEdge == true){	//add causal edge to the PGM
				pgmEdges.add(edge);
				pwPGM.print(edge.getCauseNum()+",");
				pwPGM.print(edge.getEffectNum()+",");
				pwPGM.print(edge.getAvgAge()+",");
				pwPGM.print(edge.getAvgMalePercent()+",");
				pwPGM.print(edge.getAvgFemalePercent()+",");
				pwPGM.print(edge.getAvgALSFRSAbsChange()+",");
				pwPGM.print(edge.getAvgALSFRSRelativeChange()+",");
				pwPGM.print(edge.getAvgTimeDist()+",");
				pwPGM.print(edge.getAvgTimePos()+",");
				pwPGM.print(edge.getAvgDeath()+",");
				pwPGM.print(edge.getAvgCause()+",");
				pwPGM.print(edge.getAvgEffect()+",");
				pwPGM.print(edge.getCTER()+",");
				pwPGM.print(edge.getEntropy()+",");
				pwPGM.print(edge.getCausalTendency()+",");
				pwPGM.print(edge.getMaxCause()+",");
				pwPGM.print(edge.getMaxEffect()+",");
				pwPGM.print(edge.getMaxAge()+",");
				pwPGM.print(edge.getMaxALSFRSAbsChange()+",");
				pwPGM.print(edge.getMaxALSFRSRelativeChange()+",");
				pwPGM.print(edge.getMaxTimeDist()+",");
				pwPGM.print(edge.getMaxTimePos()+",");
				pwPGM.print(edge.getALSFRSAbsEntropy()+",");
				pwPGM.print(edge.getALSFRSRelativeEntropy()+",");
				pwPGM.print(edge.getSeverityALSFRSAbs()+",");
				pwPGM.print(edge.getSeverityALSFRSRelative()+",");
				double[] geneticsProbsList = genetics.getGeneticsAvgCausalEdge(edge.getCauseNum(), edge.getEffectNum());
//				double geneticsProb = geneticsProbsList[0]+geneticsProbsList[1]+geneticsProbsList[2]+geneticsProbsList[3];
//				pwPGM.print(geneticsProb+",");//total probability of genetics parameters
				pwPGM.print(geneticsProbsList[0]+",");//c9orf72 probability 
				pwPGM.print(geneticsProbsList[1]+",");//sod1 probability
				pwPGM.print(geneticsProbsList[2]+",");//tardbp probability
				pwPGM.print(geneticsProbsList[3]+",");//fus probability
				pwPGM.print(geneticsProbsList[4]+",");//genetics probability
				
				pwPGM.print(geneticsProbsList[5]+",");//c9orf72 nums 
				pwPGM.print(geneticsProbsList[6]+",");//sod1 nums
				pwPGM.print(geneticsProbsList[7]+",");//tardbp nums
				pwPGM.print(geneticsProbsList[8]+",");//fus nums
				pwPGM.print(geneticsProbsList[9]+",");//genetics nums
				pwPGM.print(geneticsProbsList[10]);//patients nums
				
				pwPGM.println();
				
				c9orf72Ctr = c9orf72Ctr + geneticsProbsList[5];
				sod1Ctr = sod1Ctr + geneticsProbsList[6];
				tardbpCtr = tardbpCtr + geneticsProbsList[7];
				fusCtr = fusCtr + geneticsProbsList[8];
				geneticsCtr = geneticsCtr + geneticsProbsList[9];
				patientsCtr = patientsCtr + geneticsProbsList[10];
			}//if
		}//for
		
		pwPGM.close();
		
		File pgmGeneticsFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM\\PGMGenetics.csv");
		PrintWriter pwPGMGenetics = null;
		try {
			pwPGMGenetics = new PrintWriter(pgmGeneticsFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pwPGMGenetics.println("Type, c9orf72, sod1, tardbp, fus, Genetics probability");
		pwPGMGenetics.print("All patients,");
		pwPGMGenetics.printf("%.3f", c9orf72Ctr/patientsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", sod1Ctr/patientsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", tardbpCtr/patientsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", fusCtr/patientsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", geneticsCtr/patientsCtr);
		pwPGMGenetics.println();
		
		pwPGMGenetics.print("Genetics patients,");
		pwPGMGenetics.printf("%.3f", c9orf72Ctr/geneticsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", sod1Ctr/geneticsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", tardbpCtr/geneticsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", fusCtr/geneticsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("1");
		
		pwPGMGenetics.close();
		
		createAllCausalEdgesFile();//create all causal edges file containing all properties of all causal edges
		createSeverityFile();//create a file containing severity values for all causal edges
		pgmNodes = getPGMNodes(pgmEdges);
		createPGMFeaturesFile(pgmNodes);
		System.out.println("Cresla Probabilistic Graphical Model was created sucessfully.");
		
		return pgmEdges;
	}//createPGM
	
	//get list of all PGM edges based on out-edges of nodes (out-edges with max CTER for each node)
	private ArrayList<PGMEdge> getCausalEdgesOut(ArrayList<PGMEdge> allEdges){
		
		double maxCTER = 0;
		PGMEdge currEdge = null;
		PGMEdge maxCTEREdge = null;
		ArrayList<PGMEdge> selectedEdges = new ArrayList<CreslaPGM.PGMEdge>();
		for(int featureIdx=1; featureIdx <= CreslaUtils.numOfDynamicParamsCresla; featureIdx++) {
			maxCTER = 0;//set max cter value to zero for next feature
			for(int edgeIdx=0; edgeIdx < allEdges.size(); edgeIdx++) {
				currEdge = allEdges.get(edgeIdx);
				if(currEdge.getCauseNum() != featureIdx)//search for cause feature 'featureIdx'
					continue;
				if(currEdge.getCTER() > maxCTER && currEdge.getEffectNum() != featureIdx) {
					maxCTER = currEdge.getCTER();
					maxCTEREdge = currEdge;
				}//if
			}//for(edgeIdx)
			selectedEdges.add(maxCTEREdge);	//add edge with max CTER for current feature
		}//for(featureIdx)
		return selectedEdges;
	}//getCausalEdgesOut
	
	//get list of all PGM edges based on in-edges of nodes (in-edges with max CTER for each node)
		private ArrayList<PGMEdge> getCausalEdgesIn(ArrayList<PGMEdge> allEdges){
			
			double maxCTER = 0;
			PGMEdge currEdge = null;
			PGMEdge maxCTEREdge = null;
			ArrayList<PGMEdge> selectedEdges = new ArrayList<CreslaPGM.PGMEdge>();
			for(int featureIdx=1; featureIdx <= CreslaUtils.numOfDynamicParamsCresla; featureIdx++) {
				maxCTER = 0;//set max cter value to zero for next feature
				for(int edgeIdx=0; edgeIdx < allEdges.size(); edgeIdx++) {
					currEdge = allEdges.get(edgeIdx);
					if(currEdge.getEffectNum() != featureIdx)//search for effect feature 'featureIdx'
						continue;
					if(currEdge.getCTER() > maxCTER && currEdge.getCauseNum() != featureIdx) {
						maxCTER = currEdge.getCTER();
						maxCTEREdge = currEdge;
					}//if
				}//for(edgeIdx)
				selectedEdges.add(maxCTEREdge);	//add edge with max CTER for current feature
			}//for(featureIdx)
			return selectedEdges;
		}//getCausalEdgesIn
	
	
		//Create PGM based on both out-degree and in-degree of graph nodes
		public ArrayList<PGMEdge> createPGMInOut(){
			createIntervalFiles();//interval file for CTER, Entropy, and Causal tendency among all causal edges
			ArrayList<PGMEdge> pgmEdges = new ArrayList<CreslaPGM.PGMEdge>();
			ArrayList<PGMEdge> pgmEdgesOut = new ArrayList<CreslaPGM.PGMEdge>();
			ArrayList<PGMEdge> pgmEdgesIn = new ArrayList<CreslaPGM.PGMEdge>();
			ArrayList<PGMEdge> causalEdges = getAllCausalEdges();
			File pgmFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-In-Out\\PGM-In-Out.csv");
			PrintWriter pwPGM = null;
			try {
				pwPGM = new PrintWriter(pgmFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pwPGM.println("Cause, Effect, avgAge, avgMalePercent, avgFemalePercent, avgALSFRSAbsChange, avgALSFRSRelativeChange,"
					+ "avgTimeDist, avgTimePos, avgDeath, avgCause, avgEffect, CTER, entropy, causalTendency,"
					+ "maxCause, maxEffect, maxAge, maxALSFRSAbsChange, maxALSFRSRelativeChange, maxTimeDist, maxTimePos,"
					+ "ALSFRSAbsEntopy, ALSFRSRelativeEntropy, SeverityAbs, SeverityRelative, c9orf72, sod1, tardbp, fus, GeneticsProb,"
					+ "c9orf72 nums, sod1 nums, tardbp nums, fus nums, Genetics nums, Patients nums");
			PGMEdge edge;
			//create genetics file for all causal edges
			CreslaGenetics genetics = new CreslaGenetics();
			genetics.createCausalEdgesGeneticsFile();//create genetics file for all causal edges
			double c9orf72Ctr = 0;
			double sod1Ctr = 0;
			double fusCtr = 0;
			double tardbpCtr = 0;
			double patientsCtr = 0;
			double geneticsCtr = 0;
			
			pgmEdgesOut = getCausalEdgesOut(causalEdges);//get all edges of PGM based on out-edges
			pgmEdgesIn = getCausalEdgesIn(causalEdges);//get all edges of PGM based on in-edges
			
			PGMEdge currInOutEdge = null;
			//add out-degree edges
			for(int i=0; i< pgmEdgesOut.size(); i++) {
				currInOutEdge = pgmEdgesOut.get(i);
				pgmEdges.add(currInOutEdge);
			}//for(i)
			
			//add in-degree edges
			for(int i=0; i< pgmEdgesIn.size(); i++) {
				currInOutEdge = pgmEdgesIn.get(i);
				boolean edgeExist = false;
				for(int j=0; j<pgmEdgesOut.size(); j++) {
					if(pgmEdgesOut.get(j).getCauseNum() == currInOutEdge.getCauseNum() &&
							pgmEdgesOut.get(j).getEffectNum() == currInOutEdge.getEffectNum()) {
						edgeExist = true;
						break;
					}//if						
				}//for(j)
				if(!edgeExist)
					pgmEdges.add(currInOutEdge);
			}//for(i)
			
			for(int i=0; i<pgmEdges.size(); i++){
				edge = pgmEdges.get(i);
					pwPGM.print(edge.getCauseNum()+",");
					pwPGM.print(edge.getEffectNum()+",");
					pwPGM.print(edge.getAvgAge()+",");
					pwPGM.print(edge.getAvgMalePercent()+",");
					pwPGM.print(edge.getAvgFemalePercent()+",");
					pwPGM.print(edge.getAvgALSFRSAbsChange()+",");
					pwPGM.print(edge.getAvgALSFRSRelativeChange()+",");
					pwPGM.print(edge.getAvgTimeDist()+",");
					pwPGM.print(edge.getAvgTimePos()+",");
					pwPGM.print(edge.getAvgDeath()+",");
					pwPGM.print(edge.getAvgCause()+",");
					pwPGM.print(edge.getAvgEffect()+",");
					pwPGM.print(edge.getCTER()+",");
					pwPGM.print(edge.getEntropy()+",");
					pwPGM.print(edge.getCausalTendency()+",");
					pwPGM.print(edge.getMaxCause()+",");
					pwPGM.print(edge.getMaxEffect()+",");
					pwPGM.print(edge.getMaxAge()+",");
					pwPGM.print(edge.getMaxALSFRSAbsChange()+",");
					pwPGM.print(edge.getMaxALSFRSRelativeChange()+",");
					pwPGM.print(edge.getMaxTimeDist()+",");
					pwPGM.print(edge.getMaxTimePos()+",");
					pwPGM.print(edge.getALSFRSAbsEntropy()+",");
					pwPGM.print(edge.getALSFRSRelativeEntropy()+",");
					pwPGM.print(edge.getSeverityALSFRSAbs()+",");
					pwPGM.print(edge.getSeverityALSFRSRelative()+",");
					double[] geneticsProbsList = genetics.getGeneticsAvgCausalEdge(edge.getCauseNum(), edge.getEffectNum());
//					double geneticsProb = geneticsProbsList[0]+geneticsProbsList[1]+geneticsProbsList[2]+geneticsProbsList[3];
//					pwPGM.print(geneticsProb+",");//total probability of genetics parameters
					pwPGM.print(geneticsProbsList[0]+",");//c9orf72 probability 
					pwPGM.print(geneticsProbsList[1]+",");//sod1 probability
					pwPGM.print(geneticsProbsList[2]+",");//tardbp probability
					pwPGM.print(geneticsProbsList[3]+",");//fus probability
					pwPGM.print(geneticsProbsList[4]+",");//genetics probability
					
					pwPGM.print(geneticsProbsList[5]+",");//c9orf72 nums 
					pwPGM.print(geneticsProbsList[6]+",");//sod1 nums
					pwPGM.print(geneticsProbsList[7]+",");//tardbp nums
					pwPGM.print(geneticsProbsList[8]+",");//fus nums
					pwPGM.print(geneticsProbsList[9]+",");//genetics nums
					pwPGM.print(geneticsProbsList[10]);//patients nums
					
					pwPGM.println();
					
					c9orf72Ctr = c9orf72Ctr + geneticsProbsList[5];
					sod1Ctr = sod1Ctr + geneticsProbsList[6];
					tardbpCtr = tardbpCtr + geneticsProbsList[7];
					fusCtr = fusCtr + geneticsProbsList[8];
					geneticsCtr = geneticsCtr + geneticsProbsList[9];
					patientsCtr = patientsCtr + geneticsProbsList[10];
			}//for
			
			pwPGM.close();
			
			File pgmGeneticsFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-In-Out\\PGMGenetics-In-Out.csv");
			PrintWriter pwPGMGenetics = null;
			try {
				pwPGMGenetics = new PrintWriter(pgmGeneticsFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pwPGMGenetics.println("Type, c9orf72, sod1, tardbp, fus, Genetics probability");
			pwPGMGenetics.print("All patients,");
			pwPGMGenetics.printf("%.3f", c9orf72Ctr/patientsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", sod1Ctr/patientsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", tardbpCtr/patientsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", fusCtr/patientsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", geneticsCtr/patientsCtr);
			pwPGMGenetics.println();
			
			pwPGMGenetics.print("Genetics patients,");
			pwPGMGenetics.printf("%.3f", c9orf72Ctr/geneticsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", sod1Ctr/geneticsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", tardbpCtr/geneticsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", fusCtr/geneticsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("1");
			
			pwPGMGenetics.close();
			
			createAllCausalEdgesFile();//create all causal edges file containing all properties of all causal edges
			createSeverityFile();//create a file containing severity values for all causal edges
			pgmNodes = getPGMNodes(pgmEdges);	//nodes of PGM
			createPGMFeaturesFileInOut(pgmNodes);
			System.out.println("Cresla PGM-In-Out was created sucessfully.");
			
			return pgmEdges;
		}//createPGMInOut
		
	//Create PGM based on out-degree of the graph nodes
	public ArrayList<PGMEdge> createPGMOut(){
		createIntervalFiles();//interval file for CTER, Entropy, and Causal tendency among all causal edges
		ArrayList<PGMEdge> pgmEdges = new ArrayList<CreslaPGM.PGMEdge>();
		ArrayList<PGMEdge> causalEdges = getAllCausalEdges();
		File pgmFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-Out\\PGM-Out.csv");
		PrintWriter pwPGM = null;
		try {
			pwPGM = new PrintWriter(pgmFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pwPGM.println("Cause, Effect, avgAge, avgMalePercent, avgFemalePercent, avgALSFRSAbsChange, avgALSFRSRelativeChange,"
				+ "avgTimeDist, avgTimePos, avgDeath, avgCause, avgEffect, CTER, entropy, causalTendency,"
				+ "maxCause, maxEffect, maxAge, maxALSFRSAbsChange, maxALSFRSRelativeChange, maxTimeDist, maxTimePos,"
				+ "ALSFRSAbsEntopy, ALSFRSRelativeEntropy, SeverityAbs, SeverityRelative, c9orf72, sod1, tardbp, fus, GeneticsProb,"
				+ "c9orf72 nums, sod1 nums, tardbp nums, fus nums, Genetics nums, Patients nums");
		PGMEdge edge;
		//create genetics file for all causal edges
		CreslaGenetics genetics = new CreslaGenetics();
		genetics.createCausalEdgesGeneticsFile();//create genetics file for all causal edges
		double c9orf72Ctr = 0;
		double sod1Ctr = 0;
		double fusCtr = 0;
		double tardbpCtr = 0;
		double patientsCtr = 0;
		double geneticsCtr = 0;
		
		pgmEdges = getCausalEdgesOut(causalEdges);	//get all edges of PGM based on out-edges
		
		for(int i=0; i<pgmEdges.size(); i++){
			edge = pgmEdges.get(i);
				pwPGM.print(edge.getCauseNum()+",");
				pwPGM.print(edge.getEffectNum()+",");
				pwPGM.print(edge.getAvgAge()+",");
				pwPGM.print(edge.getAvgMalePercent()+",");
				pwPGM.print(edge.getAvgFemalePercent()+",");
				pwPGM.print(edge.getAvgALSFRSAbsChange()+",");
				pwPGM.print(edge.getAvgALSFRSRelativeChange()+",");
				pwPGM.print(edge.getAvgTimeDist()+",");
				pwPGM.print(edge.getAvgTimePos()+",");
				pwPGM.print(edge.getAvgDeath()+",");
				pwPGM.print(edge.getAvgCause()+",");
				pwPGM.print(edge.getAvgEffect()+",");
				pwPGM.print(edge.getCTER()+",");
				pwPGM.print(edge.getEntropy()+",");
				pwPGM.print(edge.getCausalTendency()+",");
				pwPGM.print(edge.getMaxCause()+",");
				pwPGM.print(edge.getMaxEffect()+",");
				pwPGM.print(edge.getMaxAge()+",");
				pwPGM.print(edge.getMaxALSFRSAbsChange()+",");
				pwPGM.print(edge.getMaxALSFRSRelativeChange()+",");
				pwPGM.print(edge.getMaxTimeDist()+",");
				pwPGM.print(edge.getMaxTimePos()+",");
				pwPGM.print(edge.getALSFRSAbsEntropy()+",");
				pwPGM.print(edge.getALSFRSRelativeEntropy()+",");
				pwPGM.print(edge.getSeverityALSFRSAbs()+",");
				pwPGM.print(edge.getSeverityALSFRSRelative()+",");
				double[] geneticsProbsList = genetics.getGeneticsAvgCausalEdge(edge.getCauseNum(), edge.getEffectNum());
//				double geneticsProb = geneticsProbsList[0]+geneticsProbsList[1]+geneticsProbsList[2]+geneticsProbsList[3];
//				pwPGM.print(geneticsProb+",");//total probability of genetics parameters
				pwPGM.print(geneticsProbsList[0]+",");//c9orf72 probability 
				pwPGM.print(geneticsProbsList[1]+",");//sod1 probability
				pwPGM.print(geneticsProbsList[2]+",");//tardbp probability
				pwPGM.print(geneticsProbsList[3]+",");//fus probability
				pwPGM.print(geneticsProbsList[4]+",");//genetics probability
				
				pwPGM.print(geneticsProbsList[5]+",");//c9orf72 nums 
				pwPGM.print(geneticsProbsList[6]+",");//sod1 nums
				pwPGM.print(geneticsProbsList[7]+",");//tardbp nums
				pwPGM.print(geneticsProbsList[8]+",");//fus nums
				pwPGM.print(geneticsProbsList[9]+",");//genetics nums
				pwPGM.print(geneticsProbsList[10]);//patients nums
				
				pwPGM.println();
				
				c9orf72Ctr = c9orf72Ctr + geneticsProbsList[5];
				sod1Ctr = sod1Ctr + geneticsProbsList[6];
				tardbpCtr = tardbpCtr + geneticsProbsList[7];
				fusCtr = fusCtr + geneticsProbsList[8];
				geneticsCtr = geneticsCtr + geneticsProbsList[9];
				patientsCtr = patientsCtr + geneticsProbsList[10];
		}//for
		
		pwPGM.close();
		
		File pgmGeneticsFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-Out\\PGMGenetics-Out.csv");
		PrintWriter pwPGMGenetics = null;
		try {
			pwPGMGenetics = new PrintWriter(pgmGeneticsFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pwPGMGenetics.println("Type, c9orf72, sod1, tardbp, fus, Genetics probability");
		pwPGMGenetics.print("All patients,");
		pwPGMGenetics.printf("%.3f", c9orf72Ctr/patientsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", sod1Ctr/patientsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", tardbpCtr/patientsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", fusCtr/patientsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", geneticsCtr/patientsCtr);
		pwPGMGenetics.println();
		
		pwPGMGenetics.print("Genetics patients,");
		pwPGMGenetics.printf("%.3f", c9orf72Ctr/geneticsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", sod1Ctr/geneticsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", tardbpCtr/geneticsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("%.3f", fusCtr/geneticsCtr);
		pwPGMGenetics.print(",");
		pwPGMGenetics.printf("1");
		
		pwPGMGenetics.close();
		
		createAllCausalEdgesFile();//create all causal edges file containing all properties of all causal edges
		createSeverityFile();//create a file containing severity values for all causal edges
		pgmNodes = getPGMNodes(pgmEdges);	//nodes of PGM
		createPGMFeaturesFileOut(pgmNodes);
		System.out.println("Cresla PGM-Out was created sucessfully.");
		
		return pgmEdges;
	}//createPGMOut
	
	//Create PGM based on in-degree of the graph nodes
		public ArrayList<PGMEdge> createPGMIn(){
			createIntervalFiles();//interval file for CTER, Entropy, and Causal tendency among all causal edges
			ArrayList<PGMEdge> pgmEdges = new ArrayList<CreslaPGM.PGMEdge>();
			ArrayList<PGMEdge> causalEdges = getAllCausalEdges();
			File pgmFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-In\\PGM-In.csv");
			PrintWriter pwPGM = null;
			try {
				pwPGM = new PrintWriter(pgmFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pwPGM.println("Cause, Effect, avgAge, avgMalePercent, avgFemalePercent, avgALSFRSAbsChange, avgALSFRSRelativeChange,"
					+ "avgTimeDist, avgTimePos, avgDeath, avgCause, avgEffect, CTER, entropy, causalTendency,"
					+ "maxCause, maxEffect, maxAge, maxALSFRSAbsChange, maxALSFRSRelativeChange, maxTimeDist, maxTimePos,"
					+ "ALSFRSAbsEntopy, ALSFRSRelativeEntropy, SeverityAbs, SeverityRelative, c9orf72, sod1, tardbp, fus, GeneticsProb,"
					+ "c9orf72 nums, sod1 nums, tardbp nums, fus nums, Genetics nums, Patients nums");
			boolean selectEdge = false;
			PGMEdge edge;
			//create genetics file for all causal edges
			CreslaGenetics genetics = new CreslaGenetics();
			genetics.createCausalEdgesGeneticsFile();//create genetics file for all causal edges
			double c9orf72Ctr = 0;
			double sod1Ctr = 0;
			double fusCtr = 0;
			double tardbpCtr = 0;
			double patientsCtr = 0;
			double geneticsCtr = 0;
			
			pgmEdges = getCausalEdgesIn(causalEdges);	//get all edges of PGM based on out-edges
			
			for(int i=0; i<pgmEdges.size(); i++){
				edge = pgmEdges.get(i);
					pwPGM.print(edge.getCauseNum()+",");
					pwPGM.print(edge.getEffectNum()+",");
					pwPGM.print(edge.getAvgAge()+",");
					pwPGM.print(edge.getAvgMalePercent()+",");
					pwPGM.print(edge.getAvgFemalePercent()+",");
					pwPGM.print(edge.getAvgALSFRSAbsChange()+",");
					pwPGM.print(edge.getAvgALSFRSRelativeChange()+",");
					pwPGM.print(edge.getAvgTimeDist()+",");
					pwPGM.print(edge.getAvgTimePos()+",");
					pwPGM.print(edge.getAvgDeath()+",");
					pwPGM.print(edge.getAvgCause()+",");
					pwPGM.print(edge.getAvgEffect()+",");
					pwPGM.print(edge.getCTER()+",");
					pwPGM.print(edge.getEntropy()+",");
					pwPGM.print(edge.getCausalTendency()+",");
					pwPGM.print(edge.getMaxCause()+",");
					pwPGM.print(edge.getMaxEffect()+",");
					pwPGM.print(edge.getMaxAge()+",");
					pwPGM.print(edge.getMaxALSFRSAbsChange()+",");
					pwPGM.print(edge.getMaxALSFRSRelativeChange()+",");
					pwPGM.print(edge.getMaxTimeDist()+",");
					pwPGM.print(edge.getMaxTimePos()+",");
					pwPGM.print(edge.getALSFRSAbsEntropy()+",");
					pwPGM.print(edge.getALSFRSRelativeEntropy()+",");
					pwPGM.print(edge.getSeverityALSFRSAbs()+",");
					pwPGM.print(edge.getSeverityALSFRSRelative()+",");
					double[] geneticsProbsList = genetics.getGeneticsAvgCausalEdge(edge.getCauseNum(), edge.getEffectNum());
//					double geneticsProb = geneticsProbsList[0]+geneticsProbsList[1]+geneticsProbsList[2]+geneticsProbsList[3];
//					pwPGM.print(geneticsProb+",");//total probability of genetics parameters
					pwPGM.print(geneticsProbsList[0]+",");//c9orf72 probability 
					pwPGM.print(geneticsProbsList[1]+",");//sod1 probability
					pwPGM.print(geneticsProbsList[2]+",");//tardbp probability
					pwPGM.print(geneticsProbsList[3]+",");//fus probability
					pwPGM.print(geneticsProbsList[4]+",");//genetics probability
					
					pwPGM.print(geneticsProbsList[5]+",");//c9orf72 nums 
					pwPGM.print(geneticsProbsList[6]+",");//sod1 nums
					pwPGM.print(geneticsProbsList[7]+",");//tardbp nums
					pwPGM.print(geneticsProbsList[8]+",");//fus nums
					pwPGM.print(geneticsProbsList[9]+",");//genetics nums
					pwPGM.print(geneticsProbsList[10]);//patients nums
					
					pwPGM.println();
					
					c9orf72Ctr = c9orf72Ctr + geneticsProbsList[5];
					sod1Ctr = sod1Ctr + geneticsProbsList[6];
					tardbpCtr = tardbpCtr + geneticsProbsList[7];
					fusCtr = fusCtr + geneticsProbsList[8];
					geneticsCtr = geneticsCtr + geneticsProbsList[9];
					patientsCtr = patientsCtr + geneticsProbsList[10];
			}//for
			
			pwPGM.close();
			
			File pgmGeneticsFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-In\\PGMGenetics-In.csv");
			PrintWriter pwPGMGenetics = null;
			try {
				pwPGMGenetics = new PrintWriter(pgmGeneticsFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pwPGMGenetics.println("Type, c9orf72, sod1, tardbp, fus, Genetics probability");
			pwPGMGenetics.print("All patients,");
			pwPGMGenetics.printf("%.3f", c9orf72Ctr/patientsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", sod1Ctr/patientsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", tardbpCtr/patientsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", fusCtr/patientsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", geneticsCtr/patientsCtr);
			pwPGMGenetics.println();
			
			pwPGMGenetics.print("Genetics patients,");
			pwPGMGenetics.printf("%.3f", c9orf72Ctr/geneticsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", sod1Ctr/geneticsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", tardbpCtr/geneticsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("%.3f", fusCtr/geneticsCtr);
			pwPGMGenetics.print(",");
			pwPGMGenetics.printf("1");
			
			pwPGMGenetics.close();
			
			createAllCausalEdgesFile();//create all causal edges file containing all properties of all causal edges
			createSeverityFile();//create a file containing severity values for all causal edges
			pgmNodes = getPGMNodes(pgmEdges);	//nodes of PGM
			createPGMFeaturesFileIn(pgmNodes);
			System.out.println("Cresla PGM-In was created sucessfully.");
			
			return pgmEdges;
		}//createPGMIn
	
	//get all causal edges with all properties (averages and max points)= 144 causal edges
	public ArrayList<PGMEdge> getAllCausalEdges(){
		ArrayList<PGMEdge> causalEdges = new ArrayList<CreslaPGM.PGMEdge>();
		PGMEdge edge;
		BufferedReader avgCFDMatInput = null;
		BufferedReader maxAgeInput = null;
		BufferedReader maxCausalLinkInput = null;
		BufferedReader maxFrsAbsInput = null;
		BufferedReader maxFrsRelativeInput = null;
		BufferedReader maxTimeDistInput = null;
		BufferedReader maxTimePosInput = null;
		BufferedReader entropyALSFRSAbsInput = null;
		BufferedReader entropyALSFRSRelativeInput = null;
		
		ALSUtils util = new ALSUtils();
		String str;
		String[] line;//each line of CSV file
		String strCauseNum, strEffectNum, strAvgAge, strAvgMalePercent, strAvgFemalePercent, strAvgALSFRSAbsChange, strAvgALSFRSRelativeChange,
			strAvgTimeDist, strAvgTimePos, strAvgDeath, strAvgCause, strAvgEffect, strCTER, strEntropy, strCausalTendency,
			strMaxCause, strMaxEffect, strMaxAge, strMaxALSFRSAbsChange, strMaxALSFRSRelativeChange, strMaxTimeDist, strMaxTimePos,
			strEntropyALSFRSAbs, strEntropyALSFRSRelative;
		
		try {
			avgCFDMatInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CFDMatrixAverages.csv"));
			maxAgeInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\MaxPointsAgesProbDens.csv"));
			maxCausalLinkInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\MaxPointsCausalLinksProbDens.csv"));
			maxFrsAbsInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\MaxPointsFrsAbsProbDens.csv"));
			maxFrsRelativeInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\MaxPointsFrsRelativeProbDens.csv"));
			maxTimeDistInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\MaxPointsTimeDistProbDens.csv"));
			maxTimePosInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\MaxPointsTimePosProbDens.csv"));
			entropyALSFRSAbsInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\EntropyFrsAbs.csv"));			
			entropyALSFRSRelativeInput = new BufferedReader(new FileReader(
					"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\EntropyFrsRelative.csv"));
			
			try {
				avgCFDMatInput.readLine();	//skip first line
				while((str = avgCFDMatInput.readLine()) != null){
					edge = new PGMEdge();
					line = util.getStrArray(str, 15);
					strCauseNum = line[0];
					edge.setCauseNum(Integer.parseInt(strCauseNum));
					strEffectNum = line[1];
					edge.setEffectNum(Integer.parseInt(strEffectNum));
					strCausalTendency = line[2];
					edge.setCausalTendency(Double.parseDouble(strCausalTendency));
					strEntropy = line[3];
					edge.setEntropy(Double.parseDouble(strEntropy));
					strCTER = line[4];
					edge.setCTER(Double.parseDouble(strCTER));
					strAvgAge = line[5];
					edge.setAvgAge(Double.parseDouble(strAvgAge));
					strAvgDeath = line[6];
					edge.setAvgDeath(Double.parseDouble(strAvgDeath));
					strAvgMalePercent = line[7];
					edge.setAvgMalePercent(Double.parseDouble(strAvgMalePercent));
					strAvgFemalePercent = line[8];
					edge.setAvgFemalePercent(Double.parseDouble(strAvgFemalePercent));
					strAvgCause = line[9];
					edge.setAvgCause(Double.parseDouble(strAvgCause));
					strAvgEffect = line[10];
					edge.setAvgEffect(Double.parseDouble(strAvgEffect));
					strAvgALSFRSAbsChange = line[11];
					edge.setAvgALSFRSAbsChange(Double.parseDouble(strAvgALSFRSAbsChange));
					strAvgALSFRSRelativeChange = line[12];
					edge.setAvgALSFRSRelativeChange(Double.parseDouble(strAvgALSFRSRelativeChange));
					strAvgTimePos = line[13];
					edge.setAvgTimePos(Double.parseDouble(strAvgTimePos));
					strAvgTimeDist = line[14];
					edge.setAvgTimeDist(Double.parseDouble(strAvgTimeDist));
					
					str = maxAgeInput.readLine();
					line = util.getStrArray(str, 3);
					strMaxAge = line[0];
					edge.setMaxAge(Double.parseDouble(strMaxAge));
					str = maxCausalLinkInput.readLine();
					line = util.getStrArray(str, 4);
					strMaxCause = line[0];
					strMaxEffect = line[1];
					edge.setMaxCause(Double.parseDouble(strMaxCause));
					edge.setMaxEffect(Double.parseDouble(strMaxEffect));
					str = maxFrsAbsInput.readLine();
					line = util.getStrArray(str, 3);
					strMaxALSFRSAbsChange = line[0];
					edge.setMaxALSFRSAbsChange(Double.parseDouble(strMaxALSFRSAbsChange));
					str = maxFrsRelativeInput.readLine();
					line = util.getStrArray(str, 3);
					strMaxALSFRSRelativeChange = line[0];
					edge.setMaxALSFRSRelativeChange(Double.parseDouble(strMaxALSFRSRelativeChange));
					str = maxTimeDistInput.readLine();
					line = util.getStrArray(str, 3);
					strMaxTimeDist = line[0];
					edge.setMaxTimeDist(Double.parseDouble(strMaxTimeDist));
					str = maxTimePosInput.readLine();
					line = util.getStrArray(str, 3);
					strMaxTimePos = line[0];
					edge.setMaxTimePos(Double.parseDouble(strMaxTimePos));
					str = entropyALSFRSAbsInput.readLine();
					strEntropyALSFRSAbs = str;
					edge.setALSFRSAbsEntropy(Double.parseDouble(strEntropyALSFRSAbs));
					str = entropyALSFRSRelativeInput.readLine();
					strEntropyALSFRSRelative = str;
					edge.setALSFRSRelativeEntropy(Double.parseDouble(strEntropyALSFRSRelative));
					
					//set severity factor
					double severityALSFRSAbs = (-1/edge.getALSFRSAbsEntropy())*edge.getMaxALSFRSAbsChange();
					double severityALSFRSRel = (-1/edge.getALSFRSRelativeEntropy())*edge.getMaxALSFRSRelativeChange();
					edge.setSeverityALSFRSAbs(severityALSFRSAbs);
					edge.setSeverityALSFRSRelative(severityALSFRSRel);
					
					causalEdges.add(edge);
					
				}//while
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			avgCFDMatInput.close();
			maxAgeInput.close();
			maxCausalLinkInput.close();
			maxFrsAbsInput.close();
			maxFrsRelativeInput.close();
			maxTimeDistInput.close();
			maxTimePosInput.close();
			entropyALSFRSAbsInput.close();
			entropyALSFRSRelativeInput.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return causalEdges;
	}//getAllCausalEdges
	
	private boolean selectCausalEdge(PGMEdge edge){
		
		if (edgeSelectType.equalsIgnoreCase("CTER")){
			if(edge.getCTER() >= cterThrshold)
				return true;
		}//if
		
		if(edgeSelectType.equalsIgnoreCase("CT")){
			if(edge.getCausalTendency() >= causalTendThreshold &&
					edge.getEntropy() <= entropyThreshold)
				return true;
		}//if
		return false;
		
	}//selectCausalEdge
	
	class PGMEdge{
		
		 int causeNum = 0;
		 int effectNum = 0;
		 double avgAge = 0;
		 double avgMalePercent = 0;
		 double avgFemalePercent = 0;
		 double avgALSFRSAbsChange = 0;
		 double avgALSFRSRelativeChange = 0;
		 double avgTimeDist = 0;
		 double avgTimePos = 0;
		 double avgDeath = 0;
		 double avgCause = 0;
		 double avgEffect = 0;
		 double CTER = 0;
		 double entropy = 0;
		 double causalTendency = 0;
		 double maxCause = 0;
		 double maxEffect = 0;
		 double maxAge = 0;
		 double maxALSFRSAbsChange = 0;
		 double maxALSFRSRelativeChange = 0;
		 double maxTimeDist = 0;
		 double maxTimePos = 0;
		 double ALSFRSAbsEntropy = 0;
		 double ALSFRSRelativeEntropy = 0;
		//severity factors = (1/entropy(ALSFRSTotal))*ALSFRSChange --> the smaller value of it the severer of the causal edge
		 double severityALSFRSAbs = 0;
		 double severityALSFRSRelative = 0;
		 
		 
		double getSeverityALSFRSAbs() {
			return severityALSFRSAbs;
		}
		void setSeverityALSFRSAbs(double severityALSFRSAbs) {
			this.severityALSFRSAbs = severityALSFRSAbs;
		}
		double getSeverityALSFRSRelative() {
			return severityALSFRSRelative;
		}
		void setSeverityALSFRSRelative(double severityALSFRSRelative) {
			this.severityALSFRSRelative = severityALSFRSRelative;
		}
		double getALSFRSAbsEntropy() {
			return ALSFRSAbsEntropy;
		}
		void setALSFRSAbsEntropy(double aLSFRSAbsEntropy) {
			ALSFRSAbsEntropy = aLSFRSAbsEntropy;
		}
		double getALSFRSRelativeEntropy() {
			return ALSFRSRelativeEntropy;
		}
		void setALSFRSRelativeEntropy(double aLSFRSRelativeEntropy) {
			ALSFRSRelativeEntropy = aLSFRSRelativeEntropy;
		}
		int getCauseNum() {
			return causeNum;
		}
		void setCauseNum(int causeNum) {
			this.causeNum = causeNum;
		}
		int getEffectNum() {
			return effectNum;
		}
		void setEffectNum(int effectNum) {
			this.effectNum = effectNum;
		}
		double getAvgAge() {
			return avgAge;
		}
		void setAvgAge(double avgAge) {
			this.avgAge = avgAge;
		}
		double getAvgMalePercent() {
			return avgMalePercent;
		}
		void setAvgMalePercent(double avgMalePercent) {
			this.avgMalePercent = avgMalePercent;
		}
		double getAvgFemalePercent() {
			return avgFemalePercent;
		}
		void setAvgFemalePercent(double avgFemalePercent) {
			this.avgFemalePercent = avgFemalePercent;
		}
		double getAvgALSFRSAbsChange() {
			return avgALSFRSAbsChange;
		}
		void setAvgALSFRSAbsChange(double avgALSFRSAbsChange) {
			this.avgALSFRSAbsChange = avgALSFRSAbsChange;
		}
		double getAvgALSFRSRelativeChange() {
			return avgALSFRSRelativeChange;
		}
		void setAvgALSFRSRelativeChange(double avgALSFRSRelativeChange) {
			this.avgALSFRSRelativeChange = avgALSFRSRelativeChange;
		}
		double getAvgTimeDist() {
			return avgTimeDist;
		}
		void setAvgTimeDist(double avgTimeDist) {
			this.avgTimeDist = avgTimeDist;
		}
		double getAvgTimePos() {
			return avgTimePos;
		}
		void setAvgTimePos(double avgTimePos) {
			this.avgTimePos = avgTimePos;
		}
		double getAvgDeath() {
			return avgDeath;
		}
		void setAvgDeath(double avgDeath) {
			this.avgDeath = avgDeath;
		}
		double getAvgCause() {
			return avgCause;
		}
		void setAvgCause(double avgCause) {
			this.avgCause = avgCause;
		}
		double getAvgEffect() {
			return avgEffect;
		}
		void setAvgEffect(double avgEffect) {
			this.avgEffect = avgEffect;
		}
		double getCTER() {
			return CTER;
		}
		void setCTER(double cTER) {
			CTER = cTER;
		}
		double getEntropy() {
			return entropy;
		}
		void setEntropy(double entropy) {
			this.entropy = entropy;
		}
		double getCausalTendency() {
			return causalTendency;
		}
		void setCausalTendency(double causalTendency) {
			this.causalTendency = causalTendency;
		}
		double getMaxCause() {
			return maxCause;
		}
		void setMaxCause(double maxCause) {
			this.maxCause = maxCause;
		}
		double getMaxEffect() {
			return maxEffect;
		}
		void setMaxEffect(double maxEffect) {
			this.maxEffect = maxEffect;
		}
		double getMaxAge() {
			return maxAge;
		}
		void setMaxAge(double maxAge) {
			this.maxAge = maxAge;
		}
		double getMaxALSFRSAbsChange() {
			return maxALSFRSAbsChange;
		}
		void setMaxALSFRSAbsChange(double maxALSFRSAbsChange) {
			this.maxALSFRSAbsChange = maxALSFRSAbsChange;
		}
		double getMaxALSFRSRelativeChange() {
			return maxALSFRSRelativeChange;
		}
		void setMaxALSFRSRelativeChange(double maxALSFRSRelativeChange) {
			this.maxALSFRSRelativeChange = maxALSFRSRelativeChange;
		}
		double getMaxTimeDist() {
			return maxTimeDist;
		}
		void setMaxTimeDist(double maxTimeDist) {
			this.maxTimeDist = maxTimeDist;
		}
		double getMaxTimePos() {
			return maxTimePos;
		}
		void setMaxTimePos(double maxTimePos) {
			this.maxTimePos = maxTimePos;
		}
		 
		 
		
	}//class PGMEdge
}//class
