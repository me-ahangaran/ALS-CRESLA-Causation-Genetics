package cresla;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import als.ALSUtils;

public class CreslaDataset {
	
	    //hash table of all patients including static and dynamic features for each patient (main dataset)
		private Hashtable<String, CreslaALSPatient> patients = new Hashtable<String, CreslaALSPatient>();
		private int minNumOfTrialPoints = 2;//minimum number of trial points for patients
		ArrayList<CreslaALSPatient> patientsList;
		//threshold values for ALS progression rate based on ALSFRS total slope for classifying the patients
		private double slowProgThresh = -1; //slow: slope >= s
		private double mediumProgThresh = -3; //medium: m =< slope < s and fast: slope < m
		
		//create CRESLA dataset of ALS patients
		public ArrayList<CreslaALSPatient> createDataset(){
			BufferedReader input;	//read input file
			String str;	//string of each line
			String[] line;	//each line in the file
			CreslaALSPatient patient = null;	//patient record
			ALSUtils util = new ALSUtils();	//ALSUtils object
			String id = "";	//patient id as hash key
			String sex = "";
			String birthDateStr = "";
			String birthDate = "";
			String birthMonth = "";
			String birthYear = "";
			String height = "";
			String weightPremorbid = "";
			String weightDiagnosis = "";
			String fvc = "";
			String familiarity = "";
			String genetics = "";
			String ftd = "";
			String onsetDateStr = "";
			String onsetDate = "";
			String onsetMonth = "";
			String onsetYear = "";
			String onsetSite = "";
			String diagnosisDateStr = "";
			String diagnosisDate = "";
			String diagnosisMonth = "";
			String diagnosisYear = "";
			String nivDateStr = "";
			String nivDate = "";
			String nivMonth = "";
			String nivYear = "";
			String pegDateStr = "";
			String pegDate = "";
			String pegMonth = "";
			String pegYear = "";
			String tracheoDateStr = "";
			String tracheoDate = "";
			String tracheoMonth = "";
			String tracheoYear = "";
			String deathDateStr = "";
			String deathDate = "";
			String deathMonth = "";
			String deathYear = "";
			String visitDateStr = "";
			String visitDate = "";
			String visitMonth = "";
			String visitYear = "";
			CreslaALSFRS frs;
			ArrayList<CreslaALSFRS> patientSequence;
			
			String frs1Speech = "";
			String frs2Salivation = "";
			String frs3Swallowing = "";
			String frs4Handwriting = "";
			String frs5CuttingFood = "";
			String frs6DressingHygiene = "";
			String frs7TurningBed = "";
			String frs8Walking = "";
			String frs9ClimbingStairs = "";
			String frs10Dyspnea = "";
			String frs11Orthopnea = "";
			String frs12RespiratoryInsufficiency = "";		
			String frsTotal = "";	//the total value of ALSFRS parameters in the interval [0,48]
			
			//read CRESLA input file
			try {
	            input = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\CRESLA\\CRESLA-Dataset\\cresla.csv"));
	            
	            str = input.readLine();	//skip first line (this line is features names)
	            while ((str = input.readLine()) != null) {
	               line = util.getStrArray(str, 33);	//CRESLA dataset has 33 columns
	               id = line[2];	//patient id
	               
	               if (!patients.containsKey(id)){	//current patient record does not exist in the hash table 
	            	   //we should create new patient and add it to the hash table
		               sex = line[3];	//gender of patient
		               patient = new CreslaALSPatient(id,sex);	//create patient record
		               
		               //birth date
		               birthDateStr = line[4];
		               birthDate = util.getStrArrayElemIdx(birthDateStr, 3, 2, '-');
		               birthMonth = util.getStrArrayElemIdx(birthDateStr, 3, 1, '-');
		               birthYear = util.getStrArrayElemIdx(birthDateStr, 3, 0, '-');
		               patient.setBirthDate(Integer.parseInt(birthDate));
		               patient.setBirthMonth(Integer.parseInt(birthMonth));
		               patient.setBirthYear(Integer.parseInt(birthYear));
		               
		               height = line[5];
		               if(!height.equalsIgnoreCase("NA"))	//if height value is "NA" the value will be zero
		            	   patient.setHeigh(Integer.parseInt(height));
		               weightPremorbid = line[6];
		               if(!weightPremorbid.equalsIgnoreCase("NA"))	//if weightPremorbid value is "NA" the value will be zero
		            	   patient.setWeightPremorbid(Integer.parseInt(weightPremorbid));
		               weightDiagnosis = line[7];
		               if(!weightDiagnosis.equalsIgnoreCase("NA"))	//if weightDiagnosis value is "NA" the value will be zero
		            	   patient.setWeightDiagnosis(Integer.parseInt(weightDiagnosis));
		               fvc = line[8];
		               if(!fvc.equalsIgnoreCase("NA"))	//if fvc value is "NA" the value will be zero
		            	   patient.setFvc(Integer.parseInt(fvc));
		               familiarity = line[9];
		               patient.setFamiliarity(familiarity);
		               genetics = line[10];
		               patient.setGenetics(genetics);
		               ftd = line[11];
		               patient.setFtd(ftd);
		               
		               //onset date
		               onsetDateStr = line[12];
		               onsetDate = util.getStrArrayElemIdx(onsetDateStr, 3, 2, '-');
		               onsetMonth = util.getStrArrayElemIdx(onsetDateStr, 3, 1, '-');
		               onsetYear = util.getStrArrayElemIdx(onsetDateStr, 3, 0, '-');
		               patient.setOnsetDate(Integer.parseInt(onsetDate));
		               patient.setOnsetMonth(Integer.parseInt(onsetMonth));
		               patient.setOnsetYear(Integer.parseInt(onsetYear));
		               
		               onsetSite = line[13];
		               patient.setOnsetSite(onsetSite);
		               
		               //diagnosis date
		               diagnosisDateStr = line[14];
		               diagnosisDate = util.getStrArrayElemIdx(diagnosisDateStr, 3, 2, '-');
		               diagnosisMonth = util.getStrArrayElemIdx(diagnosisDateStr, 3, 1, '-');
		               diagnosisYear = util.getStrArrayElemIdx(diagnosisDateStr, 3, 0, '-');
		               patient.setDiagnosisDate(Integer.parseInt(diagnosisDate));
		               patient.setDiagnosisMonth(Integer.parseInt(diagnosisMonth));
		               patient.setDiagnosisYear(Integer.parseInt(diagnosisYear));
		               
		               //niv date 
		               nivDateStr = line[15];
		               if(!nivDateStr.equalsIgnoreCase("\"not adopted\"") && 
		            		   !nivDateStr.equalsIgnoreCase("NA")){	//if the value was "not adopted" all values of nivDate will be zero
		            	   
			               nivDate = util.getStrArrayElemIdx(nivDateStr.substring(1,nivDateStr.length()-1), 3, 2, '/');		            	  
			               nivMonth = util.getStrArrayElemIdx(nivDateStr.substring(1,nivDateStr.length()-1), 3, 1, '/');
			               nivYear = util.getStrArrayElemIdx(nivDateStr.substring(1,nivDateStr.length()-1), 3, 0, '/');
			               
			               if(nivDate.length() == 2 && nivMonth.length() == 2 && nivYear.length() == 4){
				               patient.setNivDate(Integer.parseInt(nivDate));
				               patient.setNivMonth(Integer.parseInt(nivMonth));
				               patient.setNivYear(Integer.parseInt(nivYear));
			               }//if
		               }//if(nivDateStr)
		               
		               //peg date 
		               pegDateStr = line[16];
		               if(!pegDateStr.equalsIgnoreCase("\"not adopted\"")){	//if the value was "not adopted" all values of pegDate will be zero
		            	   pegDate = util.getStrArrayElemIdx(pegDateStr.substring(1,pegDateStr.length()-1), 3, 2, '/');
		            	   pegMonth = util.getStrArrayElemIdx(pegDateStr.substring(1,pegDateStr.length()-1), 3, 1, '/');
		            	   pegYear = util.getStrArrayElemIdx(pegDateStr.substring(1,pegDateStr.length()-1), 3, 0, '/');
			               patient.setPegDate(Integer.parseInt(pegDate));
			               patient.setPegMonth(Integer.parseInt(pegMonth));
			               patient.setPegYear(Integer.parseInt(pegYear));
		               }//if(pegDateStr)
		               
		               //tracheo date 
		               tracheoDateStr = line[17];
		               if(!tracheoDateStr.equalsIgnoreCase("\"not adopted\"")){	//if the value was "not adopted" all values of tracheoDate will be zero
		            	   tracheoDate = util.getStrArrayElemIdx(tracheoDateStr.substring(1,tracheoDateStr.length()-1), 3, 2, '/');
		            	   tracheoMonth = util.getStrArrayElemIdx(tracheoDateStr.substring(1,tracheoDateStr.length()-1), 3, 1, '/');
		            	   tracheoYear = util.getStrArrayElemIdx(tracheoDateStr.substring(1,tracheoDateStr.length()-1), 3, 0, '/');
		            	  
		            	   if(tracheoDate.length() == 2 && tracheoMonth.length() == 2 && tracheoYear.length() == 4){
			            	   patient.setTracheoDate(Integer.parseInt(tracheoDate));
				               patient.setTracheoMonth(Integer.parseInt(tracheoMonth));
				               patient.setTracheoYear(Integer.parseInt(tracheoYear));
			               }//if
		               }//if(tracheoDateStr)
		               
		               //death date 
		               deathDateStr = line[18];
		               if(!deathDateStr.equalsIgnoreCase("\"alive\"") &&
		            		   !deathDateStr.equalsIgnoreCase("\"not retrieved\"")){	//if the value was "alive" all values of deathDate will be zero
		            	   deathDate = util.getStrArrayElemIdx(deathDateStr.substring(1,deathDateStr.length()-1), 3, 2, '/');
		            	   deathMonth = util.getStrArrayElemIdx(deathDateStr.substring(1,deathDateStr.length()-1), 3, 1, '/');
		            	   deathYear = util.getStrArrayElemIdx(deathDateStr.substring(1,deathDateStr.length()-1), 3, 0, '/');
			               patient.setDeathDate(Integer.parseInt(deathDate));
			               patient.setDeathMonth(Integer.parseInt(deathMonth));
			               patient.setDeathYear(Integer.parseInt(deathYear));
		               }//if(deathDateStr)
		               
		               //visit date
		               visitDateStr = line[19];
		               visitDate = util.getStrArrayElemIdx(visitDateStr, 3, 2, '-');
		               visitMonth = util.getStrArrayElemIdx(visitDateStr, 3, 1, '-');
		               visitYear = util.getStrArrayElemIdx(visitDateStr, 3, 0, '-');
		               
		               //create ALSFRS object and add to the current patient sequence
		               
		               //set ALSFRS visit date
		               frs = new CreslaALSFRS(Integer.parseInt(visitDate), Integer.parseInt(visitMonth), Integer.parseInt(visitYear));
		               
		               //set ALSFRS parameters values
		               frs1Speech = line[20];
		               frs.setFrs1Speech(Integer.parseInt(frs1Speech));
		               
		               frs2Salivation = line[21];
		               frs.setFrs2Salivation(Integer.parseInt(frs2Salivation));
		               
		               frs3Swallowing = line[22];
		               frs.setFrs3Swallowing(Integer.parseInt(frs3Swallowing));
		               
		               frs4Handwriting = line[23];
		               frs.setFrs4Handwriting(Integer.parseInt(frs4Handwriting));
		               
		               frs5CuttingFood = line[24];
		               frs.setFrs5CuttingFood(Integer.parseInt(frs5CuttingFood));
		               
		               frs6DressingHygiene = line[25];
		               frs.setFrs6DressingHygiene(Integer.parseInt(frs6DressingHygiene));
		               
		               frs7TurningBed = line[26];
		               frs.setFrs7TurningBed(Integer.parseInt(frs7TurningBed));
		               
		               frs8Walking = line[27];
		               frs.setFrs8Walking(Integer.parseInt(frs8Walking));
		               
		               frs9ClimbingStairs = line[28];
		               frs.setFrs9ClimbingStairs(Integer.parseInt(frs9ClimbingStairs));
		               
		               frs10Dyspnea = line[29];
		               frs.setFrs10Dyspnea(Integer.parseInt(frs10Dyspnea));
		               
		               frs11Orthopnea = line[30];
		               frs.setFrs11Orthopnea(Integer.parseInt(frs11Orthopnea));
		               
		               frs12RespiratoryInsufficiency = line[31];
		               frs.setFrs12RespiratoryInsufficiency(Integer.parseInt(frs12RespiratoryInsufficiency));
		               
		               frsTotal = line[32];
		               frs.setFrsTotal(Integer.parseInt(frsTotal));
		               
		               patientSequence = new ArrayList<CreslaALSFRS>();
		               patientSequence.add(frs);		               
		               patient.setPatientSequence(patientSequence);
		               
            		   patients.put(id, patient);	//add current patient to the patients hash table
	               }//if
	               
	               else{	//current patient record exists in the hash table
	            	   patient = patients.get(id);	//find current patient record in the hash table
	            	 //visit date
		               visitDateStr = line[19];
		               visitDate = util.getStrArrayElemIdx(visitDateStr, 3, 2, '-');
		               visitMonth = util.getStrArrayElemIdx(visitDateStr, 3, 1, '-');
		               visitYear = util.getStrArrayElemIdx(visitDateStr, 3, 0, '-');
		               
		               //create ALSFRS object and add to the current patient sequence
		               
		               //set ALSFRS visit date
		               frs = new CreslaALSFRS(Integer.parseInt(visitDate), Integer.parseInt(visitMonth), Integer.parseInt(visitYear));
		               
		               //set ALSFRS parameters values
		               frs1Speech = line[20];
		               frs.setFrs1Speech(Integer.parseInt(frs1Speech));
		               
		               frs2Salivation = line[21];
		               frs.setFrs2Salivation(Integer.parseInt(frs2Salivation));
		               
		               frs3Swallowing = line[22];
		               frs.setFrs3Swallowing(Integer.parseInt(frs3Swallowing));
		               
		               frs4Handwriting = line[23];
		               frs.setFrs4Handwriting(Integer.parseInt(frs4Handwriting));
		               
		               frs5CuttingFood = line[24];
		               frs.setFrs5CuttingFood(Integer.parseInt(frs5CuttingFood));
		               
		               frs6DressingHygiene = line[25];
		               frs.setFrs6DressingHygiene(Integer.parseInt(frs6DressingHygiene));
		               
		               frs7TurningBed = line[26];
		               frs.setFrs7TurningBed(Integer.parseInt(frs7TurningBed));
		               
		               frs8Walking = line[27];
		               frs.setFrs8Walking(Integer.parseInt(frs8Walking));
		               
		               frs9ClimbingStairs = line[28];
		               frs.setFrs9ClimbingStairs(Integer.parseInt(frs9ClimbingStairs));
		               
		               frs10Dyspnea = line[29];
		               frs.setFrs10Dyspnea(Integer.parseInt(frs10Dyspnea));
		               
		               frs11Orthopnea = line[30];
		               frs.setFrs11Orthopnea(Integer.parseInt(frs11Orthopnea));
		               
		               frs12RespiratoryInsufficiency = line[31];
		               frs.setFrs12RespiratoryInsufficiency(Integer.parseInt(frs12RespiratoryInsufficiency));
		               
		               frsTotal = line[32];
		               frs.setFrsTotal(Integer.parseInt(frsTotal));
		               
		               patientSequence = patient.getPatientSequence();
		               patientSequence.add(frs);		               
		               patient.setPatientSequence(patientSequence);
		               
	               }//else	                                                       
	                              
	            }//while
	            input.close();
	        } catch (IOException e) {
	            System.out.println("\"CRESLA\" file Read Error!");
	        }		
			
			patientsList = new ArrayList<CreslaALSPatient>(patients.values());
			setPatientsSlopes(); //set slope values for all patients
			System.out.println("CRESLA dataset has been create successfully.");
			createSuppFiles();
			return patientsList;
		}//createDataset
		
		//create supplementary files
		private void createSuppFiles() {
			createSlopesFile();
			createGeneticFiles();
			createGeneticSlopesFiles();
			printGeneticProgFile();
			createCharacteristicsFile();
			createALSFRSFile();
			createNonEmptyFVCFile();
			createALSFRSMeansFile();
		}//createSuppFiles
	
		private void createALSFRSMeansFile() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\ALSFRS-Mean.csv");
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CreslaALSPatient patient = null;
			double alsfrsTotal = 0; 
			for(int i=0; i<patientsList.size(); i++) {
				patient = patientsList.get(i);
				ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
				for(int j=0; j<frsList.size(); j++) 
					alsfrsTotal = alsfrsTotal + frsList.get(j).getFrsTotal();
				
				pw.println(alsfrsTotal / frsList.size());
				alsfrsTotal = 0;
			}//for(i)
			pw.close();
		}//createALSFRSMeansFile
		
		//create file includes ALSFRS-R changes for all patients
		private void createALSFRSFile() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRS.csv");
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CreslaALSPatient patient = null;
			for(int i=0; i<patientsList.size(); i++) {
				patient = patientsList.get(i);
				ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
				for(int j=0; j<frsList.size(); j++) {
					pw.print(frsList.get(j).getFrsTotal()+",");
				}//for(j)
				pw.println();
			}//for(i)
			pw.close();
		}//createALSFRSFile
		
		//create CSV file including ID,Gender, Onset age, Onset site (Spinal, Bulbar, Respiratory), Genetics, FTD, FVC, Slope
		private void createCharacteristicsFile() {
			CreslaALSPatient patient = null;
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\Characteristics.csv");
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String id = null;
			String gender = null;
			String onsetAge = null;
			String onsetSite = null;
			String genetics = null;
			String ftd = null;
			String fvc = null;
			String slope = null;
			CreslaUtils util = new CreslaUtils();
			int numOfMale = 0;
			double numOfPatients = patientsList.size();
			int numOfSpinal = 0;
			int numOfBulbar = 0;
			int numOfRespiratory = 0;
		
			pw.println("ID,Gender,Onset age,Onset site,Genetics,FTD,FVC,Slope,ALSFRS-R mean");
			for(int i=0; i<patientsList.size(); i++) {
				patient = patientsList.get(i);//current patient
				id = patient.getId();
				gender = patient.getSex(); //M or F
				if(gender.equalsIgnoreCase("\"M\""))
					gender = "Male";
				if(gender.equalsIgnoreCase("\"F\""))
					gender = "Female";
				onsetAge = Double.toString(Math.ceil(util.getDays(patient.getBirthDate(), patient.getBirthMonth(), patient.getBirthYear(),
						patient.getOnsetDate(), patient.getOnsetMonth(), patient.getOnsetYear()) / 365));
				onsetSite = patient.getOnsetSite();
				if(onsetSite.equalsIgnoreCase("\"S\""))
					onsetSite = "Spinal";
				if(onsetSite.equalsIgnoreCase("\"B\""))
					onsetSite = "Bulbar";
				if(onsetSite.equalsIgnoreCase("\"R\""))
					onsetSite = "Respiratory";
				genetics = patient.getGenetics();
				if(genetics.equalsIgnoreCase("\"NA\""))
					genetics = "Not available";
				if(genetics.equalsIgnoreCase("\"WT\""))
					genetics = "Negative";
				ftd = patient.getFtd();
				if(ftd.equalsIgnoreCase("\"Y\""))
					ftd = "Yes";
				if(ftd.equalsIgnoreCase("\"N\""))
					ftd = "No";
				if(ftd.equalsIgnoreCase("NA"))
					ftd = "Not available";
				fvc = Integer.toString(patient.getFvc());
				slope = Double.toString(patient.getSlope());
				
				double alsfrsTotal = 0;
				ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
				for(int j=0; j<frsList.size(); j++) 
					alsfrsTotal = alsfrsTotal + frsList.get(j).getFrsTotal();
				
				double alsfrsMean = (alsfrsTotal / frsList.size());
				
				
				if(patient.getSex() == null)
					gender = "Not available";
				if(patient.getOnsetSite() == null)
					onsetSite = "Not available";
				if(patient.getGenetics() == null)
					genetics = "Not available";
				if(patient.getFtd() == null)
					ftd = "Not available";
				
				pw.println(id+","+gender+","+onsetAge+","+onsetSite+","+genetics+","+ftd+","+fvc+","+slope+","+alsfrsMean);
				
				if(gender.equalsIgnoreCase("Male"))
					numOfMale++;
				if(onsetSite.equalsIgnoreCase("Spinal"))
					numOfSpinal++;
				if(onsetSite.equalsIgnoreCase("Bulbar"))
					numOfBulbar++;
				if(onsetSite.equalsIgnoreCase("Respiratory"))
					numOfRespiratory++;
				 id = null;
				 gender = null;
				 onsetAge = null;
				 onsetSite = null;
				 genetics = null;
				 ftd = null;
			}//for(i)
			
			double fvcPatients = 0;//patients with FVC != 0
			double fvcTotal = 0; //total FVC of patients
			for(int i=0; i<numOfPatients; i++){
				CreslaALSPatient currPatient = patientsList.get(i);
				double fvcPatient = currPatient.getFvc();
				if(fvcPatient != 0) {
					fvcTotal = fvcTotal + fvcPatient;
					fvcPatients++;
				}//if
			}//for(i)
			System.out.println("Average FVC = "+(fvcTotal / fvcPatients));
			System.out.println("Men percentage = "+(numOfMale / numOfPatients));
			System.out.println("Spinal percentage = "+(numOfSpinal / numOfPatients));
			System.out.println("Bulbar percentage = "+(numOfBulbar / numOfPatients));
			System.out.println("Respiratory percentage = "+(numOfRespiratory / numOfPatients));
			pw.close();
			
		}//createCharacteristicsFile
		
		private void createNonEmptyFVCFile() {
			File fileFVC = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\FVCNonEmpty.csv");
			PrintWriter pwFVC = null;
			try {
				pwFVC = new PrintWriter(fileFVC);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int fvc = 0;
			for(int i=0; i< patientsList.size(); i++) {
				fvc = patientsList.get(i).getFvc();
				if(fvc != 0)
					pwFVC.println(fvc);
			}//for
			pwFVC.close();
		}//createNonEmptyFVCFile
		
		//create 4 genetic files includes slope of ALSFRS total for patients
		private void createGeneticSlopesFiles() {
			createALSFRSSlopesC9orf72();
			createALSFRSSlopesFUS();
			createALSFRSSlopesSod1();
			createALSFRSSlopesTardbp();
			printProgGeneticsFile();
		}//createGeneticSlopesFiles
		
		private void printProgGeneticsFile() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALS-Progression-Genetics.csv");
			try {
				PrintWriter pw = new PrintWriter(file);
				pw.println("Progression type,All patients,Genetic patients,c9orf72,sod1,fus,tardbp");
//				printVerySlowProgFile(pw);
				printSlowProgFile(pw);
				printMediumProgFile(pw);
				printFastProgFile(pw);
//				printVeryFastProgFile(pw);
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//printProgFile
		
//		private void printVeryFastProgFile(PrintWriter pw) {
//			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRS-VeryFast.csv");
//			PrintWriter pwProg = null;
//			try {
//				pwProg = new PrintWriter(file);
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			double slope;
//			double numOfPatients=0;
//			double numOfPatientsGenetic=0;
//			double numOfPatientsVeryFast=0;
//			double datasetPerc=0;
//			double geneticPerc=0;
//			double fusPerc=0;
//			double tardbpPerc=0;
//			double c9orf72Perc=0;
//			double sod1Perc=0;
//			CreslaALSPatient patient;
//			for(int i=0; i<patientsList.size(); i++){
//				patient = patientsList.get(i);//current patient
//				ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
//				int frsListSize = frsList.size();
//				if(frsListSize < minNumOfTrialPoints)
//					continue;
//				numOfPatients++;
//				slope = (patient.getPatientSequence().get(frsListSize-1).getFrsTotal() - 
//				patient.getPatientSequence().get(0).getFrsTotal())/(frsListSize-1);
//				if(slope >= fastProgThresh)
//					continue;
//				for(int k=0; k<frsListSize; k++) {
//					pwProg.print(frsList.get(k).getFrsTotal()+",");
//				}//for
//				pwProg.println();
//				numOfPatientsVeryFast++;
//				if(!patient.getGenetics().contains("c9orf72") &&
//						!patient.getGenetics().contains("sod1") &&
//						!patient.getGenetics().contains("fus") &&
//						!patient.getGenetics().contains("tardbp"))
//					continue;
//				numOfPatientsGenetic++;
//				
//				if(patient.getGenetics().contains("c9orf72"))
//					c9orf72Perc++;
//				if(patient.getGenetics().contains("sod1"))
//					sod1Perc++;
//				if(patient.getGenetics().contains("fus"))
//					fusPerc++;
//				if(patient.getGenetics().contains("tardbp"))
//					tardbpPerc++;
//			}//for(i)
//			pwProg.close();
//			datasetPerc = numOfPatientsVeryFast/numOfPatients; //percentage of patients with related progression rate
//			geneticPerc = numOfPatientsGenetic/numOfPatientsVeryFast; //percentage of patients with genetic origin with related progression rate
//			c9orf72Perc = c9orf72Perc/numOfPatientsGenetic;
//			fusPerc = fusPerc/numOfPatientsGenetic;
//			sod1Perc = sod1Perc/numOfPatientsGenetic;
//			tardbpPerc = tardbpPerc/numOfPatientsGenetic;
//			pw.println("Very Fast,"+datasetPerc+","+geneticPerc+","+c9orf72Perc+","+sod1Perc+","+fusPerc+","+tardbpPerc);
//		}//printVeryFastProgFile
		
		private void printGeneticProgFile(){
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\Genetics-Progression.csv");
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CreslaALSPatient patient;
            String progressionType=null;
			double numOfPatients=0; double numOfPatientsFast=0; double numOfPatientsMedium=0;
			double numOfPatientsSlow=0; double numOfPatientsGenetic=0;
			double numOfPatientsgeneticFast=0; double numOfPatientsgeneticMedium=0; double numOfPatientsgeneticSlow=0;
			pw.println("Proogression Type, All Patients, Genetics Patients");
			for(int i=0; i<patientsList.size(); i++){
				patient = patientsList.get(i);//current patient
				ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
				int frsListSize = frsList.size();
				if(frsListSize < minNumOfTrialPoints)
					continue;
				numOfPatients++;
				progressionType = patient.getProgressionType();
				if(progressionType.equalsIgnoreCase("Fast"))
					numOfPatientsFast++;
				if(progressionType.equalsIgnoreCase("Medium"))
					numOfPatientsMedium++;
				if(progressionType.equalsIgnoreCase("Slow"))
					numOfPatientsSlow++;
				if(patient.getGenetics().contains("c9orf72") ||	patient.getGenetics().contains("sod1") ||
						patient.getGenetics().contains("fus") || patient.getGenetics().contains("tardbp"))
					numOfPatientsGenetic++;
				
				if((patient.getGenetics().contains("c9orf72") ||	patient.getGenetics().contains("sod1") ||
						patient.getGenetics().contains("fus") || patient.getGenetics().contains("tardbp")) &&
						patient.getProgressionType().equalsIgnoreCase("Fast"))
					numOfPatientsgeneticFast++;
				if((patient.getGenetics().contains("c9orf72") ||	patient.getGenetics().contains("sod1") ||
						patient.getGenetics().contains("fus") || patient.getGenetics().contains("tardbp")) &&
						patient.getProgressionType().equalsIgnoreCase("Medium"))
					numOfPatientsgeneticMedium++;
				if((patient.getGenetics().contains("c9orf72") ||	patient.getGenetics().contains("sod1") ||
						patient.getGenetics().contains("fus") || patient.getGenetics().contains("tardbp")) &&
						patient.getProgressionType().equalsIgnoreCase("Slow"))
					numOfPatientsgeneticSlow++;
				
			}//for(i)
			
			
			pw.println("Slow, "+numOfPatientsSlow/numOfPatients+", "+ numOfPatientsgeneticSlow/numOfPatientsGenetic);
			pw.println("Medium, "+numOfPatientsMedium/numOfPatients+", "+ numOfPatientsgeneticMedium/numOfPatientsGenetic);
			pw.println("Fast, "+numOfPatientsFast/numOfPatients+", "+ numOfPatientsgeneticFast/numOfPatientsGenetic);
			
			pw.close();
		}//printGeneticProgFile
		
		private void printFastProgFile(PrintWriter pw) {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRS-Fast.csv");
			PrintWriter pwProg = null;
			try {
				pwProg = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double slope;
			double numOfPatients=0;
			double numOfPatientsGenetic=0;
			double numOfPatientsFast=0;
			double datasetPerc=0;
			double geneticPerc=0;
			double fusPerc=0;
			double tardbpPerc=0;
			double c9orf72Perc=0;
			double sod1Perc=0;
			CreslaALSPatient patient;
			for(int i=0; i<patientsList.size(); i++){
				patient = patientsList.get(i);//current patient
				ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
				int frsListSize = frsList.size();
				if(frsListSize < minNumOfTrialPoints)
					continue;
				numOfPatients++;
				slope = patient.getSlope();
				if(slope >= mediumProgThresh)
					continue;
				for(int k=0; k<frsListSize; k++) {
					pwProg.print(frsList.get(k).getFrsTotal()+",");
				}//for
				pwProg.println();
				numOfPatientsFast++;
				if(!patient.getGenetics().contains("c9orf72") &&
						!patient.getGenetics().contains("sod1") &&
						!patient.getGenetics().contains("fus") &&
						!patient.getGenetics().contains("tardbp"))
					continue;
				numOfPatientsGenetic++;
				
				if(patient.getGenetics().contains("c9orf72"))
					c9orf72Perc++;
				if(patient.getGenetics().contains("sod1"))
					sod1Perc++;
				if(patient.getGenetics().contains("fus"))
					fusPerc++;
				if(patient.getGenetics().contains("tardbp"))
					tardbpPerc++;
			}//for(i)
			pwProg.close();
			datasetPerc = numOfPatientsFast/numOfPatients; //percentage of patients with related progression rate
			geneticPerc = numOfPatientsGenetic/numOfPatientsFast; //percentage of patients with genetic origin with related progression rate
			c9orf72Perc = c9orf72Perc/numOfPatientsGenetic;
			fusPerc = fusPerc/numOfPatientsGenetic;
			sod1Perc = sod1Perc/numOfPatientsGenetic;
			tardbpPerc = tardbpPerc/numOfPatientsGenetic;
			pw.println("Fast,"+datasetPerc+","+geneticPerc+","+c9orf72Perc+","+sod1Perc+","+fusPerc+","+tardbpPerc);
		}//printFastProgFile
		
		private void printMediumProgFile(PrintWriter pw) {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRS-Medium.csv");
			PrintWriter pwProg = null;
			try {
				pwProg = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double slope;
			double numOfPatients=0;
			double numOfPatientsGenetic=0;
			double numOfPatientsMedium=0;
			double datasetPerc=0;
			double geneticPerc=0;
			double fusPerc=0;
			double tardbpPerc=0;
			double c9orf72Perc=0;
			double sod1Perc=0;
			CreslaALSPatient patient;
			for(int i=0; i<patientsList.size(); i++){
				patient = patientsList.get(i);//current patient
				ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
				int frsListSize = frsList.size();
				if(frsListSize < minNumOfTrialPoints)
					continue;
				numOfPatients++;
				slope = patient.getSlope();
				if(slope >= slowProgThresh || slope < mediumProgThresh)
					continue;
				for(int k=0; k<frsListSize; k++) {
					pwProg.print(frsList.get(k).getFrsTotal()+",");
				}//for
				pwProg.println();
				numOfPatientsMedium++;
				if(!patient.getGenetics().contains("c9orf72") &&
						!patient.getGenetics().contains("sod1") &&
						!patient.getGenetics().contains("fus") &&
						!patient.getGenetics().contains("tardbp"))
					continue;
				numOfPatientsGenetic++;
				
				if(patient.getGenetics().contains("c9orf72"))
					c9orf72Perc++;
				if(patient.getGenetics().contains("sod1"))
					sod1Perc++;
				if(patient.getGenetics().contains("fus"))
					fusPerc++;
				if(patient.getGenetics().contains("tardbp"))
					tardbpPerc++;
			}//for(i)
			pwProg.close();
			datasetPerc = numOfPatientsMedium/numOfPatients; //percentage of patients with related progression rate
			geneticPerc = numOfPatientsGenetic/numOfPatientsMedium; //percentage of patients with genetic origin with related progression rate
			c9orf72Perc = c9orf72Perc/numOfPatientsGenetic;
			fusPerc = fusPerc/numOfPatientsGenetic;
			sod1Perc = sod1Perc/numOfPatientsGenetic;
			tardbpPerc = tardbpPerc/numOfPatientsGenetic;
			pw.println("Medium,"+datasetPerc+","+geneticPerc+","+c9orf72Perc+","+sod1Perc+","+fusPerc+","+tardbpPerc);
		}//printMediumProgFile
		
		private void printSlowProgFile(PrintWriter pw) {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRS-Slow.csv");
			PrintWriter pwProg = null;
			try {
				pwProg = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double slope;
			double numOfPatients=0;
			double numOfPatientsGenetic=0;
			double numOfPatientsSlow=0;
			double datasetPerc=0;
			double geneticPerc=0;
			double fusPerc=0;
			double tardbpPerc=0;
			double c9orf72Perc=0;
			double sod1Perc=0;
			CreslaALSPatient patient;
			for(int i=0; i<patientsList.size(); i++){
				patient = patientsList.get(i);//current patient
				ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
				int frsListSize = frsList.size();
				if(frsListSize < minNumOfTrialPoints)
					continue;
				numOfPatients++;
				slope = patient.getSlope();
				if(slope < slowProgThresh)
					continue;
				for(int k=0; k<frsListSize; k++) {
					pwProg.print(frsList.get(k).getFrsTotal()+",");
				}//for
				pwProg.println();
				numOfPatientsSlow++;
				if(!patient.getGenetics().contains("c9orf72") &&
						!patient.getGenetics().contains("sod1") &&
						!patient.getGenetics().contains("fus") &&
						!patient.getGenetics().contains("tardbp"))
					continue;
				numOfPatientsGenetic++;
				
				if(patient.getGenetics().contains("c9orf72"))
					c9orf72Perc++;
				if(patient.getGenetics().contains("sod1"))
					sod1Perc++;
				if(patient.getGenetics().contains("fus"))
					fusPerc++;
				if(patient.getGenetics().contains("tardbp"))
					tardbpPerc++;
			}//for(i)
			pwProg.close();
			datasetPerc = numOfPatientsSlow/numOfPatients; //percentage of patients with related progression rate
			geneticPerc = numOfPatientsGenetic/numOfPatientsSlow; //percentage of patients with genetic origin with related progression rate
			c9orf72Perc = c9orf72Perc/numOfPatientsGenetic;
			fusPerc = fusPerc/numOfPatientsGenetic;
			sod1Perc = sod1Perc/numOfPatientsGenetic;
			tardbpPerc = tardbpPerc/numOfPatientsGenetic;
			pw.println("Slow,"+datasetPerc+","+geneticPerc+","+c9orf72Perc+","+sod1Perc+","+fusPerc+","+tardbpPerc);
		}//printSlowProgFile
		
//		private void printVerySlowProgFile(PrintWriter pw) {
//			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRS-VerySlow.csv");
//			PrintWriter pwProg = null;
//			try {
//				pwProg = new PrintWriter(file);
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			double slope;
//			double numOfPatients=0;
//			double numOfPatientsGenetic=0;
//			double numOfPatientsVerySlow=0;
//			double datasetPerc=0;
//			double geneticPerc=0;
//			double fusPerc=0;
//			double tardbpPerc=0;
//			double c9orf72Perc=0;
//			double sod1Perc=0;
//			CreslaALSPatient patient;
//			for(int i=0; i<patientsList.size(); i++){
//				patient = patientsList.get(i);//current patient
//				ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
//				int frsListSize = frsList.size();
//				if(frsListSize < minNumOfTrialPoints)
//					continue;
//				numOfPatients++;
//				slope = (patient.getPatientSequence().get(frsListSize-1).getFrsTotal() - 
//				patient.getPatientSequence().get(0).getFrsTotal())/(frsListSize-1);
//				if(slope < verySlowProgThresh)
//					continue;
//				for(int k=0; k<frsListSize; k++) {
//					pwProg.print(frsList.get(k).getFrsTotal()+",");
//				}//for
//				pwProg.println();
//				numOfPatientsVerySlow++;
//				if(!patient.getGenetics().contains("c9orf72") &&
//						!patient.getGenetics().contains("sod1") &&
//						!patient.getGenetics().contains("fus") &&
//						!patient.getGenetics().contains("tardbp"))
//					continue;
//				numOfPatientsGenetic++;
//				
//				if(patient.getGenetics().contains("c9orf72"))
//					c9orf72Perc++;
//				if(patient.getGenetics().contains("sod1"))
//					sod1Perc++;
//				if(patient.getGenetics().contains("fus"))
//					fusPerc++;
//				if(patient.getGenetics().contains("tardbp"))
//					tardbpPerc++;
//			}//for(i)
//			pwProg.close();
//			datasetPerc = numOfPatientsVerySlow/numOfPatients; //percentage of patients with related progression rate
//			geneticPerc = numOfPatientsGenetic/numOfPatientsVerySlow; //percentage of patients with genetic origin with related progression rate
//			c9orf72Perc = c9orf72Perc/numOfPatientsGenetic;
//			fusPerc = fusPerc/numOfPatientsGenetic;
//			sod1Perc = sod1Perc/numOfPatientsGenetic;
//			tardbpPerc = tardbpPerc/numOfPatientsGenetic;
//			pw.println("Very slow,"+datasetPerc+","+geneticPerc+","+c9orf72Perc+","+sod1Perc+","+fusPerc+","+tardbpPerc);
//		}//printVerySlowProgFile
		
		private void createALSFRSSlopesC9orf72() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRSSlope-c9orf72.csv");
			PrintWriter pw = null;
			CreslaALSPatient patient;
			double slope;
			try {
				pw = new PrintWriter(file);
				for(int i=0; i<patientsList.size(); i++){
					patient = patientsList.get(i);//current patient
					if(!patient.getGenetics().contains("c9orf72"))
						continue;
					ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
					int frsListSize = frsList.size();
					if(frsListSize < minNumOfTrialPoints)
						continue;
					slope = patient.getSlope();
					pw.println(slope);
				}//for(i)
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//createALSFRSSlopesC9orf72
		
		private void createALSFRSSlopesFUS() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRSSlope-fus.csv");
			PrintWriter pw = null;
			CreslaALSPatient patient;
			double slope;
			try {
				pw = new PrintWriter(file);
				for(int i=0; i<patientsList.size(); i++){
					patient = patientsList.get(i);//current patient
					if(!patient.getGenetics().contains("fus"))
						continue;
					ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
					int frsListSize = frsList.size();
					if(frsListSize < minNumOfTrialPoints)
						continue;
					slope = patient.getSlope();
					pw.println(slope);
				}//for(i)
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//createALSFRSSlopesFUS
		
		private void createALSFRSSlopesSod1() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRSSlope-sod1.csv");
			PrintWriter pw = null;
			CreslaALSPatient patient;
			double slope;
			try {
				pw = new PrintWriter(file);
				for(int i=0; i<patientsList.size(); i++){
					patient = patientsList.get(i);//current patient
					if(!patient.getGenetics().contains("sod1"))
						continue;
					ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
					int frsListSize = frsList.size();
					if(frsListSize < minNumOfTrialPoints)
						continue;
					slope = patient.getSlope();
					pw.println(slope);
				}//for(i)
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//createALSFRSSlopesSod1
		
		private void createALSFRSSlopesTardbp() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRSSlope-tardbp.csv");
			PrintWriter pw = null;
			CreslaALSPatient patient;
			double slope;
			try {
				pw = new PrintWriter(file);
				for(int i=0; i<patientsList.size(); i++){
					patient = patientsList.get(i);//current patient
					if(!patient.getGenetics().contains("tardbp"))
						continue;
					ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
					int frsListSize = frsList.size();
					if(frsListSize < minNumOfTrialPoints)
						continue;
					slope = patient.getSlope();
					pw.println(slope);
				}//for(i)
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//createALSFRSSlopesTardbp
		
		//create 4 genetic files includes ALSFRS total at different time points
		private void createGeneticFiles() {
			createALSFRSFileC9orf72();
			createALSFRSFileFus();
			createALSFRSFileSod1();
			createALSFRSFileTardbp();
		}//createGeneticFiles
		
		private void createALSFRSFileC9orf72() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRS-c9orf72.csv");
			PrintWriter pw = null;
			CreslaALSPatient patient;
			
			try {
				pw = new PrintWriter(file);
				for(int i=0; i<patientsList.size(); i++){
					patient = patientsList.get(i);//current patient
					if(!patient.getGenetics().contains("c9orf72"))
						continue;
					ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
					int frsListSize = frsList.size();
					if(frsListSize < minNumOfTrialPoints)
						continue;
					for(int j=0; j<frsList.size(); j++) {
						double currFrs = frsList.get(j).getFrsTotal();
						pw.print(currFrs+",");
					}//for
					pw.println();
				}//for(i)
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//createALSFRSFileC9orf72
		
		private void createALSFRSFileSod1() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRS-sod1.csv");
			PrintWriter pw = null;
			CreslaALSPatient patient;
			
			try {
				pw = new PrintWriter(file);
				for(int i=0; i<patientsList.size(); i++){
					patient = patientsList.get(i);//current patient
					if(!patient.getGenetics().contains("sod1"))
						continue;
					ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
					int frsListSize = frsList.size();
					if(frsListSize < minNumOfTrialPoints)
						continue;
					for(int j=0; j<frsList.size(); j++) {
						double currFrs = frsList.get(j).getFrsTotal();
						pw.print(currFrs+",");
					}//for
					pw.println();
				}//for(i)
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//createALSFRSFileSod1
		
		private void createALSFRSFileTardbp() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRS-tardbp.csv");
			PrintWriter pw = null;
			CreslaALSPatient patient;
			
			try {
				pw = new PrintWriter(file);
				for(int i=0; i<patientsList.size(); i++){
					patient = patientsList.get(i);//current patient
					if(!patient.getGenetics().contains("tardbp"))
						continue;
					ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
					int frsListSize = frsList.size();
					if(frsListSize < minNumOfTrialPoints)
						continue;
					for(int j=0; j<frsList.size(); j++) {
						double currFrs = frsList.get(j).getFrsTotal();
						pw.print(currFrs+",");
					}//for
					pw.println();
				}//for(i)
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//createALSFRSFileTardbp
		
		private void createALSFRSFileFus() {
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\SupplementaryFiles\\ALSFRS-fus.csv");
			PrintWriter pw = null;
			CreslaALSPatient patient;
			
			try {
				pw = new PrintWriter(file);
				for(int i=0; i<patientsList.size(); i++){
					patient = patientsList.get(i);//current patient
					if(!patient.getGenetics().contains("fus"))
						continue;
					ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
					int frsListSize = frsList.size();
					if(frsListSize < minNumOfTrialPoints)
						continue;
					for(int j=0; j<frsList.size(); j++) {
						double currFrs = frsList.get(j).getFrsTotal();
						pw.print(currFrs+",");
					}//for
					pw.println();
				}//for(i)
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//createALSFRSFileTardbp
		
		//set slope values for all patients according to (LastFRS-FirstFRS)/(number of months)
		private void setPatientsSlopes() {
			CreslaALSPatient patient;
			CreslaUtils util = new CreslaUtils();
			double slope = 0;
			double firstALSFRS, lastALSFRS;
			CreslaALSFRS firstFRS, lastFRS;
			int days; //number of days between first and last visit
			double months;	//number of months between first and last visit
			for(int i=0; i<patientsList.size(); i++){
				patient = patientsList.get(i);//current patient
				ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
				int frsListSize = frsList.size();
				if(frsListSize < minNumOfTrialPoints)
					continue;
				firstFRS = patient.getPatientSequence().get(0);
				lastFRS = patient.getPatientSequence().get(frsListSize-1);
				firstALSFRS = firstFRS.getFrsTotal();
				lastALSFRS = lastFRS.getFrsTotal();
				days = util.getDays(firstFRS.getVisitDate(), firstFRS.getVisitMonth(), firstFRS.getVisitYear(),
						lastFRS.getVisitDate(), lastFRS.getVisitMonth(), lastFRS.getVisitYear());
				months = Math.ceil(days/30);	//round number of months
				if(months == 0)
					months = 1;
				slope = (lastALSFRS - firstALSFRS)/(months);
				patient.setSlope(slope);
				if(slope < mediumProgThresh)
					patient.setProgressionType("Fast");
				if(slope >= mediumProgThresh && slope < slowProgThresh)
					patient.setProgressionType("Medium");
				if(slope >= slowProgThresh)
					patient.setProgressionType("Slow");
			}//for(i)
		}//setPatientsSlopes
		
		private void createSlopesFile(){
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\\\SupplementaryFiles\\Slopes.csv");
			File fileFast = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\\\SupplementaryFiles\\SlopesFast.csv");
			File fileMedium = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\\\SupplementaryFiles\\SlopesMedium.csv");
			File fileSlow = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\\\SupplementaryFiles\\SlopesSlow.csv");
			PrintWriter pw = null;
			PrintWriter pwFast = null;
			PrintWriter pwMedium = null;
			PrintWriter pwSlow = null;
			
			CreslaALSPatient patient;
			ArrayList<Double> slopes = new ArrayList<Double>();
			double slope;
			int numOfPatients = 0;
			
			try {
				pw = new PrintWriter(file);
				pwFast = new PrintWriter(fileFast);
				pwMedium = new PrintWriter(fileMedium);
				pwSlow = new PrintWriter(fileSlow);
				for(int i=0; i<patientsList.size(); i++){
					patient = patientsList.get(i);//current patient
					ArrayList<CreslaALSFRS> frsList = patient.getPatientSequence();
					int frsListSize = frsList.size();
					if(frsListSize < minNumOfTrialPoints)
						continue;
//					slope = (patient.getPatientSequence().get(frsListSize-1).getFrsTotal() - 
//							patient.getPatientSequence().get(0).getFrsTotal())/(frsListSize-1);
					slope = patient.getSlope();
					pw.println(slope);
					if(patient.getProgressionType().equalsIgnoreCase("Fast"))
						pwFast.println(slope);
					else if(patient.getProgressionType().equalsIgnoreCase("Medium"))
						pwMedium.println(slope);
					else
						pwSlow.println(slope);
					slopes.add(slope);
					numOfPatients++;
				}//for(i)
				pw.close();
				pwFast.close();
				pwMedium.close();
				pwSlow.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CreslaUtils cu = new CreslaUtils();
			System.out.println("Number of patients with at least "+minNumOfTrialPoints+" trial points = "
					+numOfPatients);
			System.out.println("Minimum slope = "+cu.getMinVal(slopes));
			System.out.println("Maximum slope = "+cu.getMaxVal(slopes));
		}//createSlopeFile
		public static void main(String[] args) {
			CreslaDataset dataset = new CreslaDataset();
			dataset.createDataset();
		}//main
}//class CreslaDataset
