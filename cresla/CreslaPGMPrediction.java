package cresla;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.FNEG;

import cresla.CreslaPGM.PGMEdge;


public class CreslaPGMPrediction {
	int numOfPredictionEvents = 6; //number of future events for prediction (2 first events are given to the model)
	int numOfGivenEvents = 2;//number of given events for prediction
	ArrayList<CreslaPredictedEvent[]> predictedALSFRSListAllPatients = new ArrayList<CreslaPredictedEvent[]>();
	ArrayList<Integer> pgmNodes = new ArrayList<Integer>();//all nodes of PGM
	ArrayList<PGMEdge> pgmEdges = new ArrayList<CreslaPGM.PGMEdge>();//all edges of PGM
	ArrayList<PGMEdge> pgmEdgesIn = new ArrayList<CreslaPGM.PGMEdge>();//all edges of PGM in-degree
	ArrayList<PGMEdge> pgmEdgesOut = new ArrayList<CreslaPGM.PGMEdge>();//all edges of PGM out-degree
	
	double frsChgThreshPred = 1;//threshold value for change of FRS parameters in prediction phase
	String frsParamsPredMaxAvgType = "MAX";//type of prediction for FRS parameters ("AVG" or "MAX"). MAX is better than AVG
	
	//type of prediction for ALSFRS total ("AVG" or "MAX"). MAX is better than AVG
	String ALSFRSPredMaxAvgType = "MAX";
	
	//type of prediction for ALSFRS total ("REL" or "ABS") based on relative or absolute values. REL is better than ABS
	String ALSFRSPredAbsRelType = "REL";
	String inOutMode = "OUT";	//in-degree graph or out-degree graph ("IN" or "OUT") for prediction process
	int minNumOfEventsForSlope = 4;//minimum number of events for slope calculation
	int maxVisitIntervalDays = 120;	//max days between two consecutive visits for calculating RMSE of ALSFRS-R prediction
	
	public void predict(ArrayList<CreslaALSPatient> testPatients, ArrayList<PGMEdge> edges){
		
		pgmEdges = edges;
		CreslaALSPatient patient;
		int numOfTestPatients = testPatients.size();
		pgmNodes = getPGMNodes(pgmEdges);
		//list of predicted ALSFRS and FRS parameters for all patients (size=number of patients)
		
		for(int i=0; i<numOfTestPatients; i++){
			patient = testPatients.get(i);//current patient
			ArrayList<CreslaALSFRS> ALSFRSList = patient.getPatientSequence();//list of ALSFRS for current patient in all time points
			int numOfEventsCurrPatient = ALSFRSList.size();//number of events for current patient
			if(numOfEventsCurrPatient <= numOfGivenEvents)//skip if the number of events is smaller than 2
				continue;
			CreslaALSFRS firstALSFRS = ALSFRSList.get(0);//first ALSFRS for current patient
			CreslaALSFRS secondALSFRS = ALSFRSList.get(1);//second ALSFRS for current patient
			
			
			//ALSFRS and FRS parameters predicted list for current patient
			CreslaPredictedEvent[] predictedALSFRSListCurrPatient = new CreslaPredictedEvent[2+numOfPredictionEvents];
			predictedALSFRSListCurrPatient[0] = setFirstPredEvent(firstALSFRS);//set first ALSFRS total
			predictedALSFRSListCurrPatient[1] = setSecondPredEvent(firstALSFRS, secondALSFRS);//set second ALSFRS total
			
			for(int j=numOfGivenEvents; j<numOfPredictionEvents+numOfGivenEvents; j++){
				if(j == numOfEventsCurrPatient)//skip if reach to the last time point for current patient sequence
					break;
				CreslaPredictedEvent predEvent = predictCurrentEvent(predictedALSFRSListCurrPatient[j-1]);
				predEvent.setRealFrs(ALSFRSList.get(j));//set real ALSFRS parameters for current predicted event
				//TODO
				predictedALSFRSListCurrPatient[j] = predEvent;//predicted ALSFRS for current event
			}//for(j=numOfPredictionEvents)
			
			//add current predict event to list of predictions for all patients
			predictedALSFRSListAllPatients.add(predictedALSFRSListCurrPatient);
		}//for(i=numOfTestPatients)
		
		double[] ALSFRSTotalPredRMSE = getALSFRSTotalPredRMSE();//RMSE for prediction of ALSFRS total
		printALSFRSTotalRMSEFile(ALSFRSTotalPredRMSE);//print RMSE for ALSFRS total to a file
		double[][] FRSParamsPredRMSE = getFrsParamsPredRMSE();//RMSE for prediction of FRS parameters
		printFRSParamsRMSEFile(FRSParamsPredRMSE);//print RMSE for FRS parameters to a file
//		createPGMFeaturesFile(pgmNodes);
		double slopePredRMSE = getSlopePredRMSE();
		System.out.println("Average RMSE error of slope prediction is "+slopePredRMSE);
		System.out.println("Prediction of ALSFRS for CRESLA test dataset is done successfully.");
	}//predict
	
	public void predictInOut(ArrayList<CreslaALSPatient> testPatients, ArrayList<PGMEdge> edges){
		
		pgmEdges = edges;
		CreslaPGM pgm = new CreslaPGM();
		pgmEdgesIn = pgm.createPGMIn();
		pgmEdgesOut = pgm.createPGMOut();
		
		CreslaALSPatient patient;
		int numOfTestPatients = testPatients.size();
		pgmNodes = getPGMNodes(pgmEdges);
		//list of predicted ALSFRS and FRS parameters for all patients (size=number of patients)
		
		for(int i=0; i<numOfTestPatients; i++){
			patient = testPatients.get(i);//current patient
			ArrayList<CreslaALSFRS> ALSFRSList = patient.getPatientSequence();//list of ALSFRS for current patient in all time points
			int numOfEventsCurrPatient = ALSFRSList.size();//number of events for current patient
			if(numOfEventsCurrPatient <= numOfGivenEvents)//skip if the number of events is smaller than 2
				continue;
			CreslaALSFRS firstALSFRS = ALSFRSList.get(0);//first ALSFRS for current patient
			CreslaALSFRS secondALSFRS = ALSFRSList.get(1);//second ALSFRS for current patient
			
			//if second event does not change relative to first event, then discard this patient
//			if(firstALSFRS.getFrs1Speech() == secondALSFRS.getFrs1Speech() &&
//					firstALSFRS.getFrs2Salivation() == secondALSFRS.getFrs2Salivation() &&
//					firstALSFRS.getFrs3Swallowing() == secondALSFRS.getFrs3Swallowing() &&
//					firstALSFRS.getFrs4Handwriting() == secondALSFRS.getFrs4Handwriting() &&
//					firstALSFRS.getFrs5CuttingFood() == secondALSFRS.getFrs5CuttingFood() &&
//					firstALSFRS.getFrs6DressingHygiene() == secondALSFRS.getFrs6DressingHygiene() &&
//					firstALSFRS.getFrs7TurningBed() == secondALSFRS.getFrs7TurningBed() &&
//					firstALSFRS.getFrs8Walking() == secondALSFRS.getFrs8Walking() &&
//					firstALSFRS.getFrs9ClimbingStairs() == secondALSFRS.getFrs9ClimbingStairs() &&
//					firstALSFRS.getFrs10Dyspnea() == secondALSFRS.getFrs10Dyspnea() &&
//					firstALSFRS.getFrs11Orthopnea() == secondALSFRS.getFrs11Orthopnea() &&
//					firstALSFRS.getFrs12RespiratoryInsufficiency() == secondALSFRS.getFrs12RespiratoryInsufficiency())
//				
//				continue;
				
			//ALSFRS and FRS parameters predicted list for current patient
			CreslaPredictedEvent[] predictedALSFRSListCurrPatient = new CreslaPredictedEvent[2+numOfPredictionEvents];
			predictedALSFRSListCurrPatient[0] = setFirstPredEvent(firstALSFRS);//set first ALSFRS total
			predictedALSFRSListCurrPatient[1] = setSecondPredEvent(firstALSFRS, secondALSFRS);//set second ALSFRS total
			
			for(int j=numOfGivenEvents; j<numOfPredictionEvents+numOfGivenEvents; j++){
				if(j == numOfEventsCurrPatient)//skip if reach to the last time point for current patient sequence
					break;
				CreslaPredictedEvent predEvent = predictCurrentEventInOut(predictedALSFRSListCurrPatient[j-1]);
				predEvent.setRealFrs(ALSFRSList.get(j));//set real ALSFRS parameters for current predicted event
				
				predictedALSFRSListCurrPatient[j] = predEvent;//predicted ALSFRS for current event
			}//for(j=numOfPredictionEvents)
			
			//add current predict event to list of predictions for all patients
			predictedALSFRSListAllPatients.add(predictedALSFRSListCurrPatient);
		}//for(i=numOfTestPatients)
		
		double[] ALSFRSTotalPredError = getALSFRSTotalPredRelativeError();//relative error for prediction of ALSFRS total
		printALSFRSRelativeErrorFileInOut(ALSFRSTotalPredError);//print RMSE for ALSFRS total to a file
		double[][] FRSParamsPredError = getFrsParamsPredRelativeError();//relative error for prediction of FRS parameters
		printFRSParamsRelativeErrorFileInOut(FRSParamsPredError);//print RMSE for FRS parameters to a file
//		createPGMFeaturesFile(pgmNodes);
		double slopePredRMSE = getSlopePredRMSE();
		System.out.println("Average RMSE error of slope prediction is "+slopePredRMSE);
		System.out.println("Prediction of ALSFRS for CRESLA test dataset is done successfully.");
	}//predictInOut
	
	//print RMSE for FRS parameters to a file
	private void printFRSParamsRMSEFile(double[][] FRSParamsPredRMSE){
		int numOfPredEvents = numOfPredictionEvents+numOfGivenEvents;//number of prediction levels (number of columns of input matrix)
		File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM\\FRSParamsRMSE.csv");
		CreslaUtils util = new CreslaUtils();
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i=1; i<FRSParamsPredRMSE.length; i++){
				String FRSName = util.getFrsName(i);//current FRS parameter name
				pw.print(FRSName+",");
				for(int j=0; j<numOfPredEvents; j++){
					double error = FRSParamsPredRMSE[i][j];
					pw.print(error+",");
				}//for(numOfPredEvents)
				pw.println();
			}//for(FRSParams)
			
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//printFRSParamsRMSEFile
	
	private void printFRSParamsRelativeErrorFileInOut(double[][] FRSParamsPredRMSE){
		int numOfPredEvents = numOfPredictionEvents+numOfGivenEvents;//number of prediction levels (number of columns of input matrix)
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-In-Out\\FRSParamsRelativeError-InOut.csv");
			CreslaUtils util = new CreslaUtils();
			try {
				PrintWriter pw = new PrintWriter(file);
				for(int i=1; i<FRSParamsPredRMSE.length; i++){
					String FRSName = util.getFrsName(i);//current FRS parameter name
					pw.print(FRSName+",");
					for(int j=0; j<numOfPredEvents; j++){
						double error = FRSParamsPredRMSE[i][j];
						pw.printf("%.3f",error);
						pw.print(",");
					}//for(numOfPredEvents)
					pw.println();
				}//for(FRSParams)
				
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}//printFRSParamsRMSEFile
	
	//print RMSE for ALSFRS total to a file
	private void printALSFRSTotalRMSEFile(double[] ALSFRSTotalPredRMSE){
		File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM\\ALSFRSTotalRMSE.csv");
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i=0; i<ALSFRSTotalPredRMSE.length; i++)
				pw.print(ALSFRSTotalPredRMSE[i]+",");
			
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//printErrorFiles
	
	private void printALSFRSRelativeErrorFileInOut(double[] ALSFRSTotalPredRMSE) {

			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\PGM-In-Out\\ALSFRSRelativeError-InOut.csv");
			try {
				PrintWriter pw = new PrintWriter(file);
				for(int i=0; i<ALSFRSTotalPredRMSE.length; i++) {
					pw.printf("%.3f",ALSFRSTotalPredRMSE[i]);
					pw.print(",");
				}//for
				
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}//printALSFRSRelativeErrorFileInOut
	
	//calculate RMSE average error of prediction of FRS parameters for all patients
	private double[][] getFrsParamsPredRMSE(){
		int numOfPGMNodes = pgmNodes.size();
		int numOfPredEvents = numOfPredictionEvents+numOfGivenEvents;
		int numOfPatients = predictedALSFRSListAllPatients.size();
		double frsParamError = 0;//total prediction error for current FRS parameter
		double ctr = 0;//number of prediction for current FRS parameter
		
		//matrix of error for every FRS parameter
		double[][] errorListFrsParamsPred = new double[CreslaUtils.numOfDynamicParamsCresla+1][numOfPredEvents];	
		//set all indexes of error list to -1
		for(int i=0; i<CreslaUtils.numOfDynamicParamsCresla+1; i++)
			for(int j=0; j<numOfPredEvents; j++)
				errorListFrsParamsPred[i][j] = -1;
		
		for(int pred=0; pred<numOfPredEvents; pred++){//for each time point
			for(int param=0; param<numOfPGMNodes; param++){//for each FRS parameter
				int frsNum = pgmNodes.get(param);//current FRS parameter
				for(int patient=0; patient<numOfPatients; patient++){//for each patient
					
					//predict event for current patient
					CreslaPredictedEvent[] currPredEventList = predictedALSFRSListAllPatients.get(patient);
					if(currPredEventList == null)
						continue;
					if(pred >= currPredEventList.length || currPredEventList[pred] == null)
						continue;
					CreslaPredictedEvent currPredEvent = currPredEventList[pred];//current time point for current prediction event
					ArrayList<Integer> frsNums = currPredEvent.getFrsNums();//current FRS numbers
					ArrayList<Double> frsVals = currPredEvent.getFrsVals();//current FRS values
					
					int frsIdx = frsNums.indexOf(frsNum);//index of current FRS parameter
					if(frsIdx == -1)//current FRS parameter does not exist in current prediction event
						continue;
					double frsValPredicted = frsVals.get(frsIdx);//predicted value for current FRS parameter
					double frsValReal = 0;//real value for current FRS parameter
					CreslaALSFRS currRealFrs = currPredEvent.getRealFrs();//CRESLA ALSFRS object
					if(frsNum == 1)
						frsValReal = currRealFrs.getFrs1Speech();
					else if(frsNum == 2)
						frsValReal = currRealFrs.getFrs2Salivation();
					else if(frsNum == 3)
						frsValReal = currRealFrs.getFrs3Swallowing();
					else if(frsNum == 4)
						frsValReal = currRealFrs.getFrs4Handwriting();
					else if(frsNum == 5)
						frsValReal = currRealFrs.getFrs5CuttingFood();
					else if(frsNum == 6)
						frsValReal = currRealFrs.getFrs6DressingHygiene();
					else if(frsNum == 7)
						frsValReal = currRealFrs.getFrs7TurningBed();
					else if(frsNum == 8)
						frsValReal = currRealFrs.getFrs8Walking();
					else if(frsNum == 9)
						frsValReal = currRealFrs.getFrs9ClimbingStairs();
					else if(frsNum == 10)
						frsValReal = currRealFrs.getFrs10Dyspnea();
					else if(frsNum == 11)
						frsValReal = currRealFrs.getFrs11Orthopnea();
					else if(frsNum == 12)
						frsValReal = currRealFrs.getFrs12RespiratoryInsufficiency();
					
					double currError = Math.pow(Math.abs(frsValReal - frsValPredicted),2);
					frsParamError = frsParamError + currError;
					ctr++;
				}//for(patient)
				double frsParamAvgError = Math.sqrt(frsParamError/ctr);//RMSE for prediction of current FRS parameter
				if(ctr == 0)//prevent to NAN value
					frsParamAvgError = -1;
				errorListFrsParamsPred[frsNum][pred] = frsParamAvgError;//update error for index of current FRS parameter and prediction level
				ctr = 0;
				frsParamError = 0;
			}//for(param)
		}//for(pred)
		
		return errorListFrsParamsPred;
	}//getFrsParamsPredRMSE
	
	//calculate total relative average error of prediction of FRS parameters for all patients
	private double[][] getFrsParamsPredRelativeError(){
		int numOfPGMNodes = pgmNodes.size();
		int numOfPredEvents = numOfPredictionEvents+numOfGivenEvents;
		int numOfPatients = predictedALSFRSListAllPatients.size();
		double frsParamError = 0;//total prediction error for current FRS parameter
		double ctr = 0;//number of prediction for current FRS parameter
		
		//matrix of error for every FRS parameter
		double[][] errorListFrsParamsPred = new double[CreslaUtils.numOfDynamicParamsCresla+1][numOfPredEvents];	
		//set all indexes of error list to -1
		for(int i=0; i<CreslaUtils.numOfDynamicParamsCresla+1; i++)
			for(int j=0; j<numOfPredEvents; j++)
				errorListFrsParamsPred[i][j] = -1;
		
		for(int pred=0; pred<numOfPredEvents; pred++){//for each time point
			for(int param=0; param<numOfPGMNodes; param++){//for each FRS parameter
				int frsNum = pgmNodes.get(param);//current FRS parameter
				for(int patient=0; patient<numOfPatients; patient++){//for each patient
					
					//predict event for current patient
					CreslaPredictedEvent[] currPredEventList = predictedALSFRSListAllPatients.get(patient);
					if(currPredEventList == null)
						continue;
					if(pred >= currPredEventList.length || currPredEventList[pred] == null)
						continue;
					if(pred >0 && patientExceedMaxVisitInterval(currPredEventList[pred-1], currPredEventList[pred]))
						continue;
					CreslaPredictedEvent currPredEvent = currPredEventList[pred];//current time point for current prediction event
					ArrayList<Integer> frsNums = currPredEvent.getFrsNums();//current FRS numbers
					ArrayList<Double> frsVals = currPredEvent.getFrsVals();//current FRS values
					
					int frsIdx = frsNums.indexOf(frsNum);//index of current FRS parameter
					if(frsIdx == -1)//current FRS parameter does not exist in current prediction event
						continue;
					double frsValPredicted = frsVals.get(frsIdx);//predicted value for current FRS parameter
					double frsValReal = 0;//real value for current FRS parameter
					CreslaALSFRS currRealFrs = currPredEvent.getRealFrs();//CRESLA ALSFRS object
					if(frsNum == 1)
						frsValReal = currRealFrs.getFrs1Speech();
					else if(frsNum == 2)
						frsValReal = currRealFrs.getFrs2Salivation();
					else if(frsNum == 3)
						frsValReal = currRealFrs.getFrs3Swallowing();
					else if(frsNum == 4)
						frsValReal = currRealFrs.getFrs4Handwriting();
					else if(frsNum == 5)
						frsValReal = currRealFrs.getFrs5CuttingFood();
					else if(frsNum == 6)
						frsValReal = currRealFrs.getFrs6DressingHygiene();
					else if(frsNum == 7)
						frsValReal = currRealFrs.getFrs7TurningBed();
					else if(frsNum == 8)
						frsValReal = currRealFrs.getFrs8Walking();
					else if(frsNum == 9)
						frsValReal = currRealFrs.getFrs9ClimbingStairs();
					else if(frsNum == 10)
						frsValReal = currRealFrs.getFrs10Dyspnea();
					else if(frsNum == 11)
						frsValReal = currRealFrs.getFrs11Orthopnea();
					else if(frsNum == 12)
						frsValReal = currRealFrs.getFrs12RespiratoryInsufficiency();
					
					double currError = 0;
					if(frsValReal == 0)
//						currError = Math.abs(frsValPredicted);
						currError = 0;
					else
						currError = Math.abs((frsValReal - frsValPredicted) / frsValReal);
//						currError = Math.pow(Math.abs((frsValReal - frsValPredicted) / frsValReal), 2);
					frsParamError = frsParamError + currError;
					ctr++;
				}//for(patient)
				double frsParamAvgError = frsParamError/ctr;//RMSE for prediction of current FRS parameter
//				double frsParamAvgError = Math.sqrt(frsParamError/ctr);
				if(ctr == 0)//prevent to NAN value
					frsParamAvgError = -1;
				errorListFrsParamsPred[frsNum][pred] = frsParamAvgError;//update error for index of current FRS parameter and prediction level
				ctr = 0;
				frsParamError = 0;
			}//for(param)
		}//for(pred)
		
		return errorListFrsParamsPred;
	}//getFrsParamsPredRelativeError
	
	private double getSlopePredRMSE(){
		double totalError = 0;//total average slope error
		double ctr = 0;//counter of patients
		CreslaPredictedEvent[] predEventsList;
		for(int i=0; i<predictedALSFRSListAllPatients.size(); i++){
			predEventsList = predictedALSFRSListAllPatients.get(i);//current predicted events list
//			int predEventListSize = predEventsList.length;//size of current predicted events list
			int predEventsListSize = getPredEventsListSize(predEventsList);
			if(predEventsListSize < minNumOfEventsForSlope)
				continue;
			double currRealSlope = (predEventsList[predEventsListSize-1].getRealFrs().getFrsTotal() - 
					predEventsList[numOfGivenEvents].getRealFrs().getFrsTotal())
					/(predEventsListSize-1-numOfGivenEvents);
			double currPredSlope = (predEventsList[predEventsListSize-1].getPredictedALSFRS() - 
					predEventsList[numOfGivenEvents].getPredictedALSFRS())
					/(predEventsListSize-1-numOfGivenEvents);
			double currError = Math.pow((Math.abs(currRealSlope - currPredSlope)),2);//current error
			totalError = totalError + currError;
			ctr++;
		}//for(i)
		double avgSlopeRMSE = Math.sqrt((1/ctr)*totalError);
		return avgSlopeRMSE;
	}//getSlopePredRMSE
	
	private int getPredEventsListSize(CreslaPredictedEvent[] predEventsList){
		int predEventsSize = 0;
		for(int i=0; i<predEventsList.length; i++){
			if(predEventsList[i] == null)
				break;
			predEventsSize++;
		}//for(i)
		return predEventsSize;
	}//getPredEventsListSize
	
	//calculate RMSE average error of prediction of ALSFRS total for all patients
	private double[] getALSFRSTotalPredRMSE(){
		double[] errorListALSFrsPred = new double[numOfPredictionEvents+numOfGivenEvents];//list of prediction error of ALSFRS for all time points
		double avgError = 0;
		double ctr = 0;
		for(int i=numOfGivenEvents; i<numOfPredictionEvents+numOfGivenEvents; i++){//for every time point of prediction
			for(int j=0; j<predictedALSFRSListAllPatients.size(); j++){
				CreslaPredictedEvent[] predEvent = predictedALSFRSListAllPatients.get(j);//predicted event for current patient
				if(predEvent == null)
					continue;
				if(i >= predEvent.length || predEvent[i] == null)//time point i was not defined for current patient
					continue;

				double error = Math.pow((Math.abs(predEvent[i].getPredictedALSFRS()
						- predEvent[i].getRealFrs().getFrsTotal())),2);//RMSE value (RMSE = RADICAL (1/N * SIGMA(x-y)^2))
				avgError = avgError + error;
				ctr++;
			}//for(j)
			double currTimePointRMSE = Math.sqrt(avgError/ctr);
			errorListALSFrsPred[i] = currTimePointRMSE;//RMSE for current time point
			ctr = 0;
			avgError = 0;
		}//for(i)
		return errorListALSFrsPred;
	}//getPredictionError
	
	//average total relative error of ALSFRS prediction
	private double[] getALSFRSTotalPredRelativeError(){
		double[] errorListALSFrsPred = new double[numOfPredictionEvents+numOfGivenEvents];//list of prediction error of ALSFRS for all time points
		double avgError = 0;
		double ctr = 0;
		for(int i=numOfGivenEvents; i<numOfPredictionEvents+numOfGivenEvents; i++){//for every time point of prediction
			for(int j=0; j<predictedALSFRSListAllPatients.size(); j++){
				CreslaPredictedEvent[] predEvent = predictedALSFRSListAllPatients.get(j);//predicted event for current patient
				if(predEvent == null)
					continue;
				if(i >= predEvent.length || predEvent[i] == null)//time point i was not defined for current patient
					continue;
				if(patientExceedMaxVisitInterval(predEvent[i-1], predEvent[i]))
					continue;
				double realALSFRS = predEvent[i].getRealFrs().getFrsTotal();
				double predictedALSFRS = predEvent[i].getPredictedALSFRS();
				double error = 0;
				if(realALSFRS == predictedALSFRS || realALSFRS == 0)
					error = 0;
				else
						error = Math.abs((realALSFRS - predictedALSFRS)/realALSFRS);
//						error = Math.pow(Math.abs((realALSFRS - predictedALSFRS)/realALSFRS), 2);
				avgError = avgError + error;
				ctr++;
			}//for(j)
			double currTimePointRelErr = avgError/ctr;
//			double currTimePointRelErr = Math.sqrt(avgError/ctr);
			errorListALSFrsPred[i] = currTimePointRelErr;//RMSE for current time point
			ctr = 0;
			avgError = 0;
		}//for(i)
		return errorListALSFrsPred;
	}//getALSFRSTotalPredRelativeError
	
	//if interval of two consecutive events exceeds 6 month return true
	private boolean patientExceedMaxVisitInterval(CreslaPredictedEvent prevPredEvent, CreslaPredictedEvent currPredEvent) {
		
		CreslaUtils util = new CreslaUtils();
			
			if(prevPredEvent == null ||
					currPredEvent == null)
				return true;
			
			int prevDate = prevPredEvent.getRealFrs().getVisitDate();
			int prevMonth = prevPredEvent.getRealFrs().getVisitMonth();
			int prevYear = prevPredEvent.getRealFrs().getVisitYear();
			
			int currDate = currPredEvent.getRealFrs().getVisitDate();
			int currMonth = currPredEvent.getRealFrs().getVisitMonth();
			int currYear = currPredEvent.getRealFrs().getVisitYear();
			int days = util.getDays(prevDate, prevMonth, prevYear, currDate, currMonth, currYear);
			if(days > maxVisitIntervalDays)
				return true;
			return false;
	}//patientExceedMaxVisitInterval
	
		//predict FRS values for current event based on previous event
		private CreslaPredictedEvent predictCurrentEventInOut(CreslaPredictedEvent prevPredEvent){
			
			//current predicted object
			CreslaPredictedEvent currPredEvent = new CreslaPredictedEvent();
			ArrayList<Integer> currPredFrsNums = new ArrayList<Integer>();
			ArrayList<Double> currPredFrsVals = new ArrayList<Double>();
			ArrayList<Double> currPredFrsChanges = new ArrayList<Double>();
			
			
			ArrayList<Integer> prevFrsNums = prevPredEvent.getFrsNums();
			ArrayList<Double> prevFrsChanges = prevPredEvent.getFrsChanges();
			ArrayList<Double> prevFrsVals = prevPredEvent.getFrsVals();
			
			ArrayList<Integer> changedFRSNums = new ArrayList<Integer>();//list of all FRS numbers that are changed in previous event
			
			//determining changed FRS parameters
			for(int i=0; i<prevFrsChanges.size(); i++){
				double frsChg = prevFrsChanges.get(i);//current change value
				if(Math.abs(frsChg) >= frsChgThreshPred)
					changedFRSNums.add(prevFrsNums.get(i));//add current FRS parameter to the list of changed FRS numbers
			}//for(i)
			
			//if no ALSFRS param is changed
//			if(changedFRSNums.isEmpty()) {
//				for(int i=1; i<CreslaUtils.numOfDynamicParamsCresla; i++) {
//					int effectFrsNum = prevFrsNums.get(i);//current FRS parameter
//					currPredFrsNums.add(effectFrsNum);
//					if(effectFrsNum == 2 || effectFrsNum == 1 ||
//							effectFrsNum == 7 || effectFrsNum == 8 || effectFrsNum == 12) {
//						int effectIdx = -1;
//						for(int k=0; k<prevFrsNums.size(); k++) {
//							if(prevFrsNums.get(k) == effectFrsNum) {
//								effectIdx = k;
//								break;
//							}//if
//						}//for(k)
//						currPredFrsChanges.add(-1.0);
//						currPredFrsVals.add(prevFrsVals.get(effectIdx) - 1);
//					}//if
//					else {
//					int effectIdx = -1;
//					//find index of effect number in previous event
//					for(int k=0; k<prevFrsNums.size(); k++) {
//						if(prevFrsNums.get(k) == effectFrsNum) {
//							effectIdx = k;
//							break;
//						}//if
//					}//for(k)
//					currPredFrsChanges.add(0.0);
//					currPredFrsVals.add(prevFrsVals.get(effectIdx));
//				}//else
//				}//for(i)
//				currPredEvent.setFrsChanges(currPredFrsChanges);
//				currPredEvent.setFrsVals(currPredFrsVals);
//				currPredEvent.setFrsNums(currPredFrsNums);
//				
//				//calculate predicted ALSFRS total value
//				double currPredALSFRS = 0;
//				for(int i=0; i<currPredFrsVals.size(); i++) {
//					currPredALSFRS = currPredALSFRS + currPredFrsVals.get(i);
//				}//for(i)
//				currPredEvent.setPredictedALSFRS(Math.floor(currPredALSFRS));
//			
//			return currPredEvent;
//				
//			}//if
			
		//predict all FRS parameters and ALSFRS-R value in current event			
			
				for(int i=0; i<prevFrsNums.size(); i++){
					int effectFrsNum = prevFrsNums.get(i);//current FRS parameter
						
					//selected PGM edge for predicting next value of frsNum based on in-degree
					PGMEdge mostProbPGMEdgeForPredFrsParams = null;
					for(int j=0; j<pgmEdgesIn.size(); j++){
						if(pgmEdgesIn.get(j).getEffectNum() == effectFrsNum) {
							mostProbPGMEdgeForPredFrsParams = pgmEdgesIn.get(j);
							break;
						}//if
					}//for(j)
					
					int causeFrsNum = mostProbPGMEdgeForPredFrsParams.getCauseNum();
					
					//current cause feature does not changed in previous event, so apply out-degree
					if(!changedFRSNums.contains(causeFrsNum)) {
						PGMEdge selectedEdge = null;
						//get causal edge with max CTER based on out-degree method
						for(int k=0; k<pgmEdgesOut.size(); k++) {
							double maxCTER = Double.MIN_VALUE;
							PGMEdge edge = pgmEdgesOut.get(k);//current edge
							if(edge.getEffectNum() == effectFrsNum && edge.getCTER() > maxCTER 
									&& changedFRSNums.contains(edge.getCauseNum())) {
									selectedEdge = edge;
									maxCTER = edge.getCTER();
							}//if
						}//for(k)
						
						//it does not exist any edge for prediction based on out-degree
						if(selectedEdge == null) {
							currPredFrsNums.add(effectFrsNum);
							currPredFrsChanges.add(0.0);
							int effectIdx = -1;
							//find index of effect number in previous event
							for(int k=0; k<prevFrsNums.size(); k++) {
								if(prevFrsNums.get(k) == effectFrsNum) {
									effectIdx = k;
									break;
								}//if
							}//for(k)
							currPredFrsVals.add(prevFrsVals.get(effectIdx));
							continue;
						}//if
						
						//causal edge exists based on out-degree
						if(selectedEdge != null) {
							int effectIdx = -1;
							//find index of effect number in previous event
							for(int k=0; k<prevFrsNums.size(); k++) {
								if(prevFrsNums.get(k) == effectFrsNum) {
									effectIdx = k;
									break;
								}//if
							}//for(k)
							double prevEffectVal = prevFrsVals.get(effectIdx);
							double currEffectVal = 0;
							
							//predict current value of FRS parameters
							if(frsParamsPredMaxAvgType.equalsIgnoreCase("MAX"))
								currEffectVal = Math.floor(prevEffectVal + selectedEdge.getMaxEffect());
							if(frsParamsPredMaxAvgType.equalsIgnoreCase("AVG"))
								currEffectVal = Math.floor(prevEffectVal + selectedEdge.getAvgEffect());
							if(currEffectVal < 0)	//ALSFRS parameter should not be negative
								currEffectVal = 0;
							double currEffectChg = currEffectVal - prevEffectVal;//change value of FRS parameter
							currPredFrsNums.add(effectFrsNum);
							currPredFrsVals.add(currEffectVal);
							currPredFrsChanges.add(currEffectChg);
							continue;
						}//end						
						
					}//if
					
					//changed the effect feature based on in-degree
					int effectIdx = -1;
					//find index of effect number in previous event
					for(int k=0; k<prevFrsNums.size(); k++) {
						if(prevFrsNums.get(k) == effectFrsNum) {
							effectIdx = k;
							break;
						}//if
					}//for(k)
					
					double prevEffectVal = prevFrsVals.get(effectIdx);
					double currEffectVal = 0;
					
					//predict current value of FRS parameters
					if(frsParamsPredMaxAvgType.equalsIgnoreCase("MAX"))
						currEffectVal = Math.floor(prevEffectVal + mostProbPGMEdgeForPredFrsParams.getMaxEffect());
					if(frsParamsPredMaxAvgType.equalsIgnoreCase("AVG"))
						currEffectVal = Math.floor(prevEffectVal + mostProbPGMEdgeForPredFrsParams.getAvgEffect());
					if(currEffectVal < 0)	//ALSFRS parameter should not be negative
						currEffectVal = 0;
					double currEffectChg = currEffectVal - prevEffectVal;//change value of FRS parameter
					currPredFrsNums.add(effectFrsNum);
					currPredFrsVals.add(currEffectVal);
					currPredFrsChanges.add(currEffectChg);
				}//for
				currPredEvent.setFrsChanges(currPredFrsChanges);
				currPredEvent.setFrsVals(currPredFrsVals);
				currPredEvent.setFrsNums(currPredFrsNums);
				
				//calculate predicted ALSFRS total value
				double currPredALSFRS = 0;
				for(int i=0; i<currPredFrsVals.size(); i++) {
					currPredALSFRS = currPredALSFRS + currPredFrsVals.get(i);
				}//for(i)
				currPredEvent.setPredictedALSFRS(Math.floor(currPredALSFRS));
			
			return currPredEvent;
			
		}//predictCurrentEventInOut
	
	//predict FRS values for current event based on previous event
	private CreslaPredictedEvent predictCurrentEvent(CreslaPredictedEvent prevPredEvent){
		
		//current predicted object
		CreslaPredictedEvent currPredEvent = new CreslaPredictedEvent();
		ArrayList<Integer> currPredFrsNums = new ArrayList<Integer>();
		ArrayList<Double> currPredFrsVals = new ArrayList<Double>();
		ArrayList<Double> currPredFrsChanges = new ArrayList<Double>();
		double currPredALSFRS = 0;
		
		ArrayList<Integer> prevFrsNums = prevPredEvent.getFrsNums();
		ArrayList<Double> prevFrsChanges = prevPredEvent.getFrsChanges();
		ArrayList<Double> prevFrsVals = prevPredEvent.getFrsVals();
		double prevALSFRS = prevPredEvent.getPredictedALSFRS();//ALSFRS total for previous predict event
		ArrayList<Integer> changedFRSNums = new ArrayList<Integer>();//list of all FRS numbers that are changed in previous event
		
		//determining changed FRS parameters
		for(int i=0; i<prevFrsChanges.size(); i++){
			double frsChg = prevFrsChanges.get(i);//current change value
			if(Math.abs(frsChg) >= frsChgThreshPred)
				changedFRSNums.add(prevFrsNums.get(i));//add current FRS parameter to the list of changed FRS numbers
		}//for(i)
		
		//predict all FRS parameters in current event
		for(int i=0; i<prevFrsNums.size(); i++){
			int frsNum = prevFrsNums.get(i);//current FRS parameter
			double prevFrsVal = prevFrsVals.get(i);
			double currFrsVal = 0;
			//selected PGM edge for predicting next value of frsNum
			PGMEdge mostProbPGMEdgeForPredFrsParams = getMostProbPGMEdgeForPredFrsParams(frsNum, changedFRSNums);
			if(mostProbPGMEdgeForPredFrsParams == null){//PGM edge is not found for prediction, so skip to another FRS parameter
				currPredFrsNums.add(frsNum);
				currPredFrsVals.add(prevFrsVal);
				currPredFrsChanges.add(0.0);
				continue;
			}//if
			
			//predict current value of FRS parameters
			if(frsParamsPredMaxAvgType.equalsIgnoreCase("MAX"))
				currFrsVal = prevFrsVal + mostProbPGMEdgeForPredFrsParams.getMaxEffect();
			if(frsParamsPredMaxAvgType.equalsIgnoreCase("AVG"))
				currFrsVal = prevFrsVal + mostProbPGMEdgeForPredFrsParams.getAvgEffect();
			double currFrsChg = currFrsVal - prevFrsVal;//change value of FRS parameter
			currPredFrsNums.add(frsNum);
			currPredFrsVals.add(currFrsVal);
			currPredFrsChanges.add(currFrsChg);
		}//for
		currPredEvent.setFrsChanges(currPredFrsChanges);
		currPredEvent.setFrsVals(currPredFrsVals);
		currPredEvent.setFrsNums(currPredFrsNums);
		
		//most probable PGM edge for predicting ALSFRS total
		PGMEdge mostProbPGMEdgeForPredALSFRS = getMostProbPGMEdgeForPredALSFRS(changedFRSNums);
		
		if(mostProbPGMEdgeForPredALSFRS != null){
		
		//predicting ALSFRS total for current event
		if(ALSFRSPredAbsRelType.equalsIgnoreCase("ABS") && ALSFRSPredMaxAvgType.equalsIgnoreCase("MAX"))
			currPredALSFRS = prevALSFRS + mostProbPGMEdgeForPredALSFRS.getMaxALSFRSAbsChange();
		if(ALSFRSPredAbsRelType.equalsIgnoreCase("ABS") && ALSFRSPredMaxAvgType.equalsIgnoreCase("AVG"))
			currPredALSFRS = prevALSFRS + mostProbPGMEdgeForPredALSFRS.getAvgALSFRSAbsChange();
		if(ALSFRSPredAbsRelType.equalsIgnoreCase("REL") && ALSFRSPredMaxAvgType.equalsIgnoreCase("MAX"))
			currPredALSFRS = prevALSFRS + (prevALSFRS*mostProbPGMEdgeForPredALSFRS.getMaxALSFRSRelativeChange());
		if(ALSFRSPredAbsRelType.equalsIgnoreCase("REL") && ALSFRSPredMaxAvgType.equalsIgnoreCase("AVG"))
			currPredALSFRS = prevALSFRS + (prevALSFRS*mostProbPGMEdgeForPredALSFRS.getAvgALSFRSRelativeChange());
		
		currPredEvent.setPredictedALSFRS(currPredALSFRS);
		}//if
		
		//when selected PGM edge is null
		else{
			PGMEdge minALSFRSEntropyPGMEdge = getMinALSFRSEntropyPGMEdge();
			
			//predicting ALSFRS total for current event based on PGM edge with minimum value of ALSFRS entropy
			if(ALSFRSPredAbsRelType.equalsIgnoreCase("ABS") && ALSFRSPredMaxAvgType.equalsIgnoreCase("MAX"))
				currPredALSFRS = prevALSFRS + minALSFRSEntropyPGMEdge.getMaxALSFRSAbsChange();
			if(ALSFRSPredAbsRelType.equalsIgnoreCase("ABS") && ALSFRSPredMaxAvgType.equalsIgnoreCase("AVG"))
				currPredALSFRS = prevALSFRS + minALSFRSEntropyPGMEdge.getAvgALSFRSAbsChange();
			if(ALSFRSPredAbsRelType.equalsIgnoreCase("REL") && ALSFRSPredMaxAvgType.equalsIgnoreCase("MAX"))
				currPredALSFRS = prevALSFRS + (prevALSFRS*minALSFRSEntropyPGMEdge.getMaxALSFRSRelativeChange());
			if(ALSFRSPredAbsRelType.equalsIgnoreCase("REL") && ALSFRSPredMaxAvgType.equalsIgnoreCase("AVG"))
				currPredALSFRS = prevALSFRS + (prevALSFRS*minALSFRSEntropyPGMEdge.getAvgALSFRSRelativeChange());
			
			currPredEvent.setPredictedALSFRS(currPredALSFRS);
			
		}//else
		
		return currPredEvent;
		
	}//predictCurrentEvent
	
	//select PGM edge with minimum value of ALSFRS entropy (when non of PGM edges are selected for prediction of ALSFRS total)
	private PGMEdge getMinALSFRSEntropyPGMEdge(){
		PGMEdge selectedEdge = pgmEdges.get(0);
		double minALSFRSEntropy = selectedEdge.getALSFRSAbsEntropy();
		for(int i=1; i<pgmEdges.size(); i++){
			PGMEdge currEdge = pgmEdges.get(i);//current PGM edge
			double currEntropy = currEdge.getALSFRSAbsEntropy();//current entropy
			if(currEntropy < minALSFRSEntropy)
				selectedEdge = currEdge;
		}//for
		return selectedEdge;
	}//getMinALSFRSEntropyPGMEdge
	
	//determine most probable PGM edge for predicting ALSFRS total
	//based on edge with minimum value of ALSFRS entropy
	private PGMEdge getMostProbPGMEdgeForPredALSFRS(ArrayList<Integer> changedFRSNums){
		double minALSFRSEntropy = Double.MAX_VALUE;
		PGMEdge mostProbPGMEdge = null;
		for(int i=0; i<pgmEdges.size(); i++){
			PGMEdge pgmEdge = pgmEdges.get(i);//current PGM edge
			int cause = pgmEdge.getCauseNum();
			if(!changedFRSNums.contains(cause))
				continue;//skip this edge if cause parameter did not change
			if(pgmEdge.getALSFRSAbsEntropy() < minALSFRSEntropy)
				mostProbPGMEdge = pgmEdge;
		}//for(i)
		return mostProbPGMEdge;
	}//getMostProbPGMEdgeForPredALSFRS
	
	//determine most probable PGM edge for predicting frsNum (effect)
	//with highest value of CTER among all input edges to frsNum
	private PGMEdge getMostProbPGMEdgeForPredFrsParams(int frsNum, ArrayList<Integer> changedFRSNums){
		double maxCTER = Double.MIN_VALUE;
		PGMEdge mostProbPGMEdge = null;
		for(int i=0; i<pgmEdges.size(); i++){
			PGMEdge pgmEdge = pgmEdges.get(i);//current PGM edge
			int cause = pgmEdge.getCauseNum();
			int effect = pgmEdge.getEffectNum();
			//skip this edge if effect feature is not frsNum or cause feature did not change
			if(!changedFRSNums.contains(cause) || effect != frsNum)
				continue;
			if(pgmEdge.getCTER() > maxCTER) {
				mostProbPGMEdge = pgmEdge;
				maxCTER = pgmEdge.getCTER();
			}//if
		}//for(i)
		return mostProbPGMEdge;
	}//getMostProbFrsChangeForPredFrsParam
	
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
	
	private CreslaPredictedEvent setSecondPredEvent(CreslaALSFRS firstFRS, 
			CreslaALSFRS secondFRS){
		
		CreslaPredictedEvent pe = new CreslaPredictedEvent();
		pe.setRealFrs(secondFRS);
		pe.setPredictedALSFRS(secondFRS.getFrsTotal());
		ArrayList<Integer> frsNums = new ArrayList<Integer>();
		ArrayList<Double> frsVals = new ArrayList<Double>();
		ArrayList<Double> frsChanges = new ArrayList<Double>();
		
		//set all FRS parameters values if exist in the PGM
		for(int i=1; i <= CreslaUtils.numOfDynamicParamsCresla; i++){
			if(!pgmNodes.contains(i))//current FRS parameter does not exist in the PGM
				continue;
			frsNums.add(i);//set FRS number
			
			//set FRS values and changes values
			if(i==1){
				frsVals.add(secondFRS.getFrs1Speech());
				frsChanges.add(secondFRS.getFrs1Speech() - firstFRS.getFrs1Speech());
				continue;
			}
			if(i==2){
				frsVals.add(secondFRS.getFrs2Salivation());
				frsChanges.add(secondFRS.getFrs2Salivation() - firstFRS.getFrs2Salivation());
				continue;
			}
			if(i==3){
				frsVals.add(secondFRS.getFrs3Swallowing());
				frsChanges.add(secondFRS.getFrs3Swallowing() - firstFRS.getFrs3Swallowing());
				continue;
			}
			if(i==4){
				frsVals.add(secondFRS.getFrs4Handwriting());
				frsChanges.add(secondFRS.getFrs4Handwriting() - firstFRS.getFrs4Handwriting());
				continue;
			}
			if(i==5){
				frsVals.add(secondFRS.getFrs5CuttingFood());
				frsChanges.add(secondFRS.getFrs5CuttingFood() - firstFRS.getFrs5CuttingFood());
				continue;
			}
			if(i==6){
				frsVals.add(secondFRS.getFrs6DressingHygiene());
				frsChanges.add(secondFRS.getFrs6DressingHygiene() - firstFRS.getFrs5CuttingFood());
				continue;
			}
			if(i==7){
				frsVals.add(secondFRS.getFrs7TurningBed());
				frsChanges.add(secondFRS.getFrs7TurningBed() - firstFRS.getFrs7TurningBed());
				continue;
			}
			if(i==8){
				frsVals.add(secondFRS.getFrs8Walking());
				frsChanges.add(secondFRS.getFrs8Walking() - firstFRS.getFrs8Walking());
				continue;
			}
			if(i==9){
				frsVals.add(secondFRS.getFrs9ClimbingStairs());
				frsChanges.add(secondFRS.getFrs9ClimbingStairs() - firstFRS.getFrs9ClimbingStairs());
				continue;
			}
			if(i==10){
				frsVals.add(secondFRS.getFrs10Dyspnea());
				frsChanges.add(secondFRS.getFrs10Dyspnea() - firstFRS.getFrs10Dyspnea());
				continue;
			}
			if(i==11){
				frsVals.add(secondFRS.getFrs11Orthopnea());
				frsChanges.add(secondFRS.getFrs11Orthopnea() - firstFRS.getFrs11Orthopnea());
				continue;
			}
			if(i==12){
				frsVals.add(secondFRS.getFrs12RespiratoryInsufficiency());
				frsChanges.add(secondFRS.getFrs12RespiratoryInsufficiency() - firstFRS.getFrs12RespiratoryInsufficiency());
				continue;
			}
		}//for(i)
		pe.setFrsChanges(frsChanges);
		pe.setFrsNums(frsNums);
		pe.setFrsVals(frsVals);
		
		return pe;
	}//setSecondPredEvent
	
	private CreslaPredictedEvent setFirstPredEvent(CreslaALSFRS frs){
		CreslaPredictedEvent pe = new CreslaPredictedEvent();
		pe.setRealFrs(frs);
		pe.setPredictedALSFRS(frs.getFrsTotal());
		ArrayList<Integer> frsNums = new ArrayList<Integer>();
		ArrayList<Double> frsVals = new ArrayList<Double>();
		
		//set all FRS parameters values if exist in the PGM
		for(int i=1; i <= CreslaUtils.numOfDynamicParamsCresla; i++){
			if(!pgmNodes.contains(i))//current FRS parameter does not exist in the PGM
				continue;
			frsNums.add(i);//set FRS number
			
			//set FRS value
			if(i==1){
				frsVals.add(frs.getFrs1Speech());
				continue;
			}
			if(i==2){
				frsVals.add(frs.getFrs2Salivation());
				continue;
			}
			if(i==3){
				frsVals.add(frs.getFrs3Swallowing());
				continue;
			}
			if(i==4){
				frsVals.add(frs.getFrs4Handwriting());
				continue;
			}
			if(i==5){
				frsVals.add(frs.getFrs5CuttingFood());
				continue;
			}
			if(i==6){
				frsVals.add(frs.getFrs6DressingHygiene());
				continue;
			}
			if(i==7){
				frsVals.add(frs.getFrs7TurningBed());
				continue;
			}
			if(i==8){
				frsVals.add(frs.getFrs8Walking());
				continue;
			}
			if(i==9){
				frsVals.add(frs.getFrs9ClimbingStairs());
				continue;
			}
			if(i==10){
				frsVals.add(frs.getFrs10Dyspnea());
				continue;
			}
			if(i==11){
				frsVals.add(frs.getFrs11Orthopnea());
				continue;
			}
			if(i==12){
				frsVals.add(frs.getFrs12RespiratoryInsufficiency());
				continue;
			}
		}//for(i)

		pe.setFrsNums(frsNums);
		pe.setFrsVals(frsVals);
		return pe;
	}//setFirstPredEvent
	
	class CreslaPredictedEvent{
		ArrayList<Integer> frsNums = new ArrayList<Integer>();//ALSFRS number
		ArrayList<Double> frsVals = new ArrayList<Double>();//ALSFRS values
		ArrayList<Double> frsChanges = new ArrayList<Double>();//ALSFRS changes values
		double predictedALSFRS;
		CreslaALSFRS realFrs;
		
		
		CreslaALSFRS getRealFrs() {
			return realFrs;
		}
		void setRealFrs(CreslaALSFRS realFrs) {
			this.realFrs = realFrs;
		}
		ArrayList<Double> getFrsChanges() {
			return frsChanges;
		}
		void setFrsChanges(ArrayList<Double> frsChanges) {
			this.frsChanges = frsChanges;
		}
		ArrayList<Integer> getFrsNums() {
			return frsNums;
		}
		void setFrsNums(ArrayList<Integer> frsNums) {
			this.frsNums = frsNums;
		}
		ArrayList<Double> getFrsVals() {
			return frsVals;
		}
		void setFrsVals(ArrayList<Double> frsVals) {
			this.frsVals = frsVals;
		}
		double getPredictedALSFRS() {
			return predictedALSFRS;
		}
		void setPredictedALSFRS(double predictedALSFRS) {
			this.predictedALSFRS = predictedALSFRS;
		}
		
	}//class CreslaPredictedEvent
	
}//CreslaPGMPrediction
