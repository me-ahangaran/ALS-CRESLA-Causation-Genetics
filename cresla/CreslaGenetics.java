package cresla;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CreslaGenetics {
	
	int numeOfGeneticsFilesCols = 2;//number of columns in genetics files
	
	//number of genetics parameters: (1=c9orf72, 2=sod1, 3=tardbp, 4=fus, 5=wt, 6=NA)
	int numOfGeneticsParams = 6;
	
	//create genetics file for all patients
	public void createGeneticsFile(ArrayList<CreslaALSPatient> patients){
		File geneticsFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\Genetics.csv");
		File geneticsAvgFile = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\GeneticsAvg.csv");
		String gen;
		CreslaALSPatient patient;
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(geneticsFile);
			for(int i=0; i<patients.size(); i++){
				patient = patients.get(i);
				gen = patient.getGenetics();
				if(gen.contains("c9orf72"))
					pw.println("1,c9orf72");
				else if(gen.contains("sod1"))
						pw.println("2,sod1");
				else if(gen.contains("tardbp"))
					pw.println("3,tardbp");
				else if(gen.contains("fus"))
					pw.println("4,fus");
				else if(gen.contains("wt"))
					pw.println("5,wt");
				else if(gen.contains("NA"))
					pw.println("6,NA");
			}//for(i)
			
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create genetics average file
		CreslaUtils util = new CreslaUtils();
		String[][] geneticsMatrix = util.CSVFileToMatrix(
				"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\Genetics.csv",
				numeOfGeneticsFilesCols);
		double[] geneticsAvg = getGeneticsAvg(geneticsMatrix);
		double numOfGenPatients = (geneticsAvg[1]+geneticsAvg[2]+geneticsAvg[3]+geneticsAvg[4])*CreslaUtils.numOfCreslaPatients;
		System.out.println("Numbe of genetics patients in the dataset= "+numOfGenPatients);
		
		double geneticsProb = 0;
		geneticsProb = geneticsAvg[1]+geneticsAvg[2]+geneticsAvg[3]+geneticsAvg[4];
		try {
			pw = new PrintWriter(geneticsAvgFile);
			pw.println("Type,c9orf72,sod1,tardbp,fus,wt,NA,Genetics Probability");
			pw.print("All patients,");
			for(int k=1; k<=geneticsAvg.length-1; k++){//average values for current genetics parameter
				pw.printf("%.3f", geneticsAvg[k]);
				pw.print(",");
			}//for(k)
			pw.printf("%.3f", geneticsProb);
			pw.println();
			
			pw.print("Genetics patients,");
			double c9orf72Prob = (geneticsAvg[1]*CreslaUtils.numOfCreslaPatients)/numOfGenPatients;
			double sod1Prob = (geneticsAvg[2]*CreslaUtils.numOfCreslaPatients)/numOfGenPatients;
			double tardbpProb = (geneticsAvg[3]*CreslaUtils.numOfCreslaPatients)/numOfGenPatients;
			double fusProb = (geneticsAvg[4]*CreslaUtils.numOfCreslaPatients)/numOfGenPatients;
			
			pw.printf("%.3f", c9orf72Prob);
			pw.print(",");
			pw.printf("%.3f", sod1Prob);
			pw.print(",");
			pw.printf("%.3f", tardbpProb);
			pw.print(",");
			pw.printf("%.3f", fusProb);
			pw.print(",");
			pw.print("0,0,1");
			
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//createGeneticsFile
	
	//get average values of genetics parameters for a specific causal edge 
	public double[] getGeneticsAvgCausalEdge(int cause, int effect){
		CreslaUtils util = new CreslaUtils();
		String[][] geneticsMatrix = util.CSVFileToMatrix(
				"D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\GeneticsCausalEdges.csv",
				17);
		double[] genetics = new double[11];//genetics parameters
		//the row of matrix related to the causal edge
		int row = ((cause-1)*CreslaUtils.numOfDynamicParamsCresla) + (effect-1)+1;
		
		
		genetics[0] = Double.parseDouble(geneticsMatrix[row][2]);//c9orf72
		genetics[1] = Double.parseDouble(geneticsMatrix[row][3]);//sod1
		genetics[2] = Double.parseDouble(geneticsMatrix[row][4]);//tardbp
		genetics[3] = Double.parseDouble(geneticsMatrix[row][5]);//fus
		genetics[4] = Double.parseDouble(geneticsMatrix[row][8]);//genetics prob
		
		genetics[5] = Double.parseDouble(geneticsMatrix[row][9]);//c9orf72 nums
		genetics[6] = Double.parseDouble(geneticsMatrix[row][10]);//sod1 nums
		genetics[7] = Double.parseDouble(geneticsMatrix[row][11]);//tardbp nums
		genetics[8] = Double.parseDouble(geneticsMatrix[row][12]);//fus nums
		
		genetics[9] = Double.parseDouble(geneticsMatrix[row][15]);//genetics nums
		genetics[10] = Double.parseDouble(geneticsMatrix[row][16]);//patients nums
		
		return genetics;
	}//getGeneticsAvgCausalEdge

	public void createCausalEdgesGeneticsFile(){
		
		String geneticsFilePath = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\Genetics\\";
		String [][] matrix;
		//output genetics file containing average values of every causal link
		File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\GeneticsCausalEdges.csv");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String geneticsFileName;
		CreslaUtils util = new CreslaUtils();
		int numOfParameters = CreslaUtils.numOfDynamicParamsCresla;
		pw.println("Cause, Effect, c9orf72, sod1, tardbp, fus, wt, NA, Genetics Probability, "
				+ "c9orf72 nums, sod1 nums, tardbp nums, fus nums, wt nums, NA nums, Genetics nums, Patients nums");
		for(int i=1; i<=numOfParameters; i++){//for every genetics file of causal links
			for(int j=1; j<=numOfParameters; j++){
				pw.print(i+","+j);
				geneticsFileName = geneticsFilePath+i+"-"+j+".csv";
				matrix = util.CSVFileToMatrix(geneticsFileName, numeOfGeneticsFilesCols);
				double[] geneticsAvg = getGeneticsAvg(matrix);//average values for current genetics file
				for(int k=1; k<=geneticsAvg.length-1; k++){//average values for current genetics parameter
					pw.print(",");
					pw.printf("%.3f", geneticsAvg[k]);
				}//for(k)
				//the total probability of genetics parameters
				double genProb = geneticsAvg[1]+geneticsAvg[2]+geneticsAvg[3]+geneticsAvg[4];
				pw.print(",");
				pw.printf("%.3f", genProb);
				
				double[] geneticsNums = getGeneticsNumbers(matrix);
				for(int k=1; k<=geneticsNums.length-1; k++){//average values for current genetics parameter
					pw.print(",");
					pw.printf("%.3f", geneticsNums[k]);
				}//for(k)
				double genNums = geneticsNums[1]+geneticsNums[2]+geneticsNums[3]+geneticsNums[4];
				pw.print(",");
				pw.print(genNums);//number of all genetic patients
				double patientsNums = geneticsNums[1]+geneticsNums[2]+geneticsNums[3]+
						geneticsNums[4] + geneticsNums[5]+geneticsNums[6];
				pw.print(",");
				pw.print(patientsNums);//number of all patients
				pw.println();//next causal link
			}//for(j)
		}//for(i)
		pw.close();
		System.out.println("Genetics file has been created successgully.");
	}//createCausalLinksGeneticsFile
	
	//get average value for each genetics parameter (6 parameters) for every causal edge
	public double[] getGeneticsAvg(String[][] geneticsMatrix){
		//average value for every genetics parameter (index i is dedicated to i'th genetics parameter)
		double[] geneticsAvg = new double[numOfGeneticsParams+1];
		for(int i=0; i<geneticsMatrix.length; i++){
			int val = Integer.parseInt(geneticsMatrix[i][0]);//current genetics value
			geneticsAvg[val]++;			
		}//for(i)

		for(int j=0; j<geneticsAvg.length; j++)
			//create average value of every genetics parameter
			geneticsAvg[j] = geneticsAvg[j]/geneticsMatrix.length;
		
		return geneticsAvg;
	}//getGeneticsAvg
	
	//get absolute value for each genetics parameter (6 parameters) for every causal edge
	public double[] getGeneticsNumbers(String[][] geneticsMatrix){
		//absolute value for every genetics parameter (index i is dedicated to i'th genetics parameter)
		double[] geneticsAvg = new double[numOfGeneticsParams+1];
		for(int i=0; i<geneticsMatrix.length; i++){
			int val = Integer.parseInt(geneticsMatrix[i][0]);//current genetics value
			geneticsAvg[val]++;			
		}//for(i)
		
		return geneticsAvg;
	}//getGeneticsNumbers
	
	
}//CreslaGenetics
