package cresla;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


//This class create CFD matrix for ALS based on CRESLA dataset
public class CreslaCFDMatrix {
		
		//main CFD matrix (matrix index [0,0] is null)
		private CreslaCFDMatrixIdx[][] cfdMatrix = new 
				CreslaCFDMatrixIdx[CreslaUtils.numOfDynamicParamsCresla+1][CreslaUtils.numOfDynamicParamsCresla+1];
		private ArrayList<CreslaEvent[]> allPatientsEvents = new ArrayList<CreslaCFDMatrix.CreslaEvent[]>();//all events of all patients
		
		public void createCFDMatrix(ArrayList<CreslaALSPatient> patientsList){
			
			int numberOfPatients = patientsList.size();
			CreslaUtils util = new CreslaUtils();
			//create an empty list for each index of CFD matrix
			for(int i=0; i<CreslaUtils.numOfDynamicParamsCresla+1; i++)
				for(int j=0; j<CreslaUtils.numOfDynamicParamsCresla+1; j++){
					CreslaCFDMatrixIdx cfdMatIdx = new CreslaCFDMatrixIdx();
					cfdMatrix[i][j] = cfdMatIdx;
				}//for(j)
			for(int patientIdx = 0; patientIdx<numberOfPatients; patientIdx++){
				CreslaALSPatient patient = patientsList.get(patientIdx);//currentPatient
				ArrayList<CreslaALSFRS> patientSequence = patient.getPatientSequence();//current patient sequence (ALSFRS parameters)
				int patientSeqSize = patientSequence.size();
				String sex = patient.getSex();//M or F
				int age = 0;
				int birthDate = patient.getBirthDate();
				int birthMonth = patient.getBirthMonth();
				int birthYear = patient.getBirthYear();
				String genetics = patient.getGenetics();
				String death = null;
				if(patient.getDeathDate() == 0)//zero means alive, else means dead
					death = "alive";
				else
					death = "dead";
				CreslaEvent[] eventsArray = new CreslaEvent[patientSeqSize];//array of events for current patient
				
				//first cresla event
				CreslaEvent ceFirst = new CreslaEvent();//first cresla event object
				ceFirst.setTime(0);
				ceFirst.setFrsTotal(patient.getPatientSequence().get(0).getFrsTotal());
				ceFirst.setFrsTotalAbsChange(0);
				ceFirst.setFrsTotalRelativeChange(0);
				ceFirst.setFrsTotal(patient.getPatientSequence().get(0).getFrsTotal());
				eventsArray[0] = ceFirst;
				
				for(int patientSeqIdx = 1; patientSeqIdx < patientSeqSize; patientSeqIdx++){
					CreslaALSFRS prevEvent = patientSequence.get(patientSeqIdx-1);
					CreslaALSFRS currEvent = patientSequence.get(patientSeqIdx);
					CreslaEvent ce = new CreslaEvent();
					ArrayList<Integer> frsNums = new ArrayList<Integer>();
					ArrayList<Double> frsChanges = new ArrayList<Double>();
					double currFrsTotal = currEvent.getFrsTotal();
					double prevFrsTotal = prevEvent.getFrsTotal();
					double frsTotalAbsChange = currFrsTotal - prevFrsTotal;
					double frsTotalRelativeChange = 0;
					if(prevFrsTotal != 0)	//if prevFrsTotal == 0 then relativeChange = 0
						frsTotalRelativeChange = (currFrsTotal - prevFrsTotal) / prevFrsTotal;
					ce.setFrsTotal(currFrsTotal);
					ce.setFrsTotalAbsChange(frsTotalAbsChange);
					ce.setFrsTotalRelativeChange(frsTotalRelativeChange);
					int prevDate = prevEvent.getVisitDate();
					int prevMonth = prevEvent.getVisitMonth();
					int prevYear = prevEvent.getVisitYear();
					int currDate = currEvent.getVisitDate();
					int currMonth = currEvent.getVisitMonth();
					int currYear = currEvent.getVisitYear();
					int time = util.getDays(prevDate, prevMonth, prevYear, currDate, currMonth, currYear) 
							+ eventsArray[patientSeqIdx-1].getTime();
					ce.setTime(time);
					//Frs1
					double frs1Prev = prevEvent.getFrs1Speech();
					double frs1Curr = currEvent.getFrs1Speech();
					if(Math.abs(frs1Curr-frs1Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(1);
						double frs1Chg = frs1Curr-frs1Prev;
						frsChanges.add(frs1Chg);
					}//if
					
					//Frs2
					double frs2Prev = prevEvent.getFrs2Salivation();
					double frs2Curr = currEvent.getFrs2Salivation();
					if(Math.abs(frs2Curr-frs2Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(2);
						double frs2Chg = frs2Curr-frs2Prev;
						frsChanges.add(frs2Chg);
					}//if
					
					//Frs3
					double frs3Prev = prevEvent.getFrs3Swallowing();
					double frs3Curr = currEvent.getFrs3Swallowing();
					if(Math.abs(frs3Curr-frs3Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(3);
						double frs3Chg = frs3Curr-frs3Prev;
						frsChanges.add(frs3Chg);
					}//if
					
					//Frs4
					double frs4Prev = prevEvent.getFrs4Handwriting();
					double frs4Curr = currEvent.getFrs4Handwriting();
					if(Math.abs(frs4Curr-frs4Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(4);
						double frs4Chg = frs4Curr-frs4Prev;
						frsChanges.add(frs4Chg);
					}//if
					
					//Frs5
					double frs5Prev = prevEvent.getFrs5CuttingFood();
					double frs5Curr = currEvent.getFrs5CuttingFood();
					if(Math.abs(frs5Curr-frs5Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(5);
						double frs5Chg = frs5Curr-frs5Prev;
						frsChanges.add(frs5Chg);
					}//if
					
					//Frs6
					double frs6Prev = prevEvent.getFrs6DressingHygiene();
					double frs6Curr = currEvent.getFrs6DressingHygiene();
					if(Math.abs(frs6Curr-frs6Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(6);
						double frs6Chg = frs6Curr-frs6Prev;
						frsChanges.add(frs6Chg);
					}//if
					
					//Frs7
					double frs7Prev = prevEvent.getFrs7TurningBed();
					double frs7Curr = currEvent.getFrs7TurningBed();
					if(Math.abs(frs7Curr-frs7Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(7);
						double frs7Chg = frs7Curr-frs7Prev;
						frsChanges.add(frs7Chg);
					}//if
					
					//Frs8
					double frs8Prev = prevEvent.getFrs8Walking();
					double frs8Curr = currEvent.getFrs8Walking();
					if(Math.abs(frs8Curr-frs8Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(8);
						double frs8Chg = frs8Curr-frs8Prev;
						frsChanges.add(frs8Chg);
					}//if
					
					//Frs9
					double frs9Prev = prevEvent.getFrs9ClimbingStairs();
					double frs9Curr = currEvent.getFrs9ClimbingStairs();
					if(Math.abs(frs9Curr-frs9Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(9);
						double frs9Chg = frs9Curr-frs9Prev;
						frsChanges.add(frs9Chg);
					}//if
					
					//Frs10
					double frs10Prev = prevEvent.getFrs10Dyspnea();
					double frs10Curr = currEvent.getFrs10Dyspnea();
					if(Math.abs(frs10Curr-frs10Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(10);
						double frs10Chg = frs10Curr-frs10Prev;
						frsChanges.add(frs10Chg);
					}//if
					
					//Frs11
					double frs11Prev = prevEvent.getFrs11Orthopnea();
					double frs11Curr = currEvent.getFrs11Orthopnea();
					if(Math.abs(frs11Curr-frs11Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(11);
						double frs11Chg = frs11Curr-frs11Prev;
						frsChanges.add(frs11Chg);
					}//if
					
					//Frs12
					double frs12Prev = prevEvent.getFrs12RespiratoryInsufficiency();
					double frs12Curr = currEvent.getFrs12RespiratoryInsufficiency();
					if(Math.abs(frs12Curr-frs12Prev) >= CreslaUtils.frsChgThresh){
						frsNums.add(12);
						double frs12Chg = frs12Curr-frs12Prev;
						frsChanges.add(frs12Chg);
					}//if
					
					ce.setFrsNums(frsNums);
					ce.setFrsChanges(frsChanges);
					eventsArray[patientSeqIdx] = ce;//current event sequence
					
					//calculate the age of patient 
					if(patientSeqIdx == patientSeqSize - 1){
						age = util.getDays(birthDate, birthMonth, birthYear, 
								currEvent.getVisitDate(), currEvent.getVisitMonth(), currEvent.getVisitYear()) / 365;
					}//if
					
				}//for(patientSeqIdx)
				
				allPatientsEvents.add(eventsArray);//add list of events for current patient
				
//				int age = util.getDays(currEvent, firstMonth, firstYear, lastDate, lastMonth, lastYear)
				updateCFDMatrix(eventsArray, sex, age, death, genetics);	//set each index of dependency matrix
				
			}//for patientList
			
			normalizeCFDMatrix();
			
			//create density files
			createCFDMatIdxsFiles();
			createCFDMatCauseEffectFiles();
			createCFDMatTimeDistFiles();
			createCFDMatTimePosFiles();
			createCFDMatAgeFiles();
			createCFDMatFrsTotalAbsFiles();
			createCFDMatFrsTotalRelativeFiles();
			createCFDMatGeneticsFiles();
			createCFDCausalTendFile();
			
			//create average files
			createCFDAvgAgeFile();
			createCFDAvgCauseEffectFile();
			createCFDAvgDeathFile();
			createCFDAvgFrsAbsRelativeFile();
			createCFDAvgMaleFemaleFile();
			createCFDAvgTimeDistFile();
			createCFDAvgTimePosFile();
			createCFDAveragesFile();
			createCTERFile();
			
			System.out.println("CRESLA CFD matrix files are created sucessfully.");
			
		}//createCFDMatrix
		
		
		private void normalizeCFDMatrix(){

			// TODO Auto-generated method stub
			double causalTendency, age, death, male, female, cause, effect, frsAbs, frsRelative, timePos, timeDist;
			CreslaCFDMatrixIdx cfdIdx = null;
			ArrayList<Double> CTERList = getCTERList();
			ArrayList<Double> entropyList = getEntropyList();
			double CTER, entropy;
			int ctr = 0;
			for(int i=1; i<=CreslaUtils.numOfDynamicParamsCresla; i++){
				for(int j=1; j<=CreslaUtils.numOfDynamicParamsCresla; j++){
					cfdIdx = cfdMatrix[i][j];
					age = getAvgAge(cfdIdx);
					male = getAvgMalePercent(cfdIdx);
					female = getAvgFemalePercent(cfdIdx);
					death = getAvgDeath(cfdIdx);
					cause = getAvgCause(cfdIdx);
					effect = getAvgEffect(cfdIdx);
					frsAbs = getAvgALSFRSAbsChange(cfdIdx);
					frsRelative = getAvgALSFRSRelativeChange(cfdIdx);
					timePos = getAvgTimePos(cfdIdx);
					timeDist = getAvgTimeDist(cfdIdx);
					causalTendency = getCausalTendency(i,j);
					CTER = CTERList.get(ctr);
					entropy = entropyList.get(ctr);
					
					cfdIdx.setCausalTendency(causalTendency);
					cfdIdx.setAvgAge(age);
					cfdIdx.setAvgMalePercent(male);
					cfdIdx.setAvgFemalePercent(female);
					cfdIdx.setAvgDeath(death);
					cfdIdx.setAvgCause(cause);
					cfdIdx.setAvgEffect(effect);
					cfdIdx.setAvgALSFRSAbsChange(frsAbs);
					cfdIdx.setAvgALSFRSRelativeChange(frsRelative);
					cfdIdx.setAvgTimeDist(timeDist);
					cfdIdx.setAvgTimePos(timePos);
					cfdIdx.setCTER(CTER);
					cfdIdx.setEntropy(entropy);
					
					ctr++;
				}//for(j)
			}//for(i)
		
		}//normalizeCFDMatrix
		
		private double getAvgTimeDist(CreslaCFDMatrixIdx cfdIdx) {
			// TODO Auto-generated method stub
			ArrayList<CausalEdge> causalEdges = cfdIdx.getCausalEdges();
			CausalEdge causalEdge = null;
			double timeDistTotal = 0;
			double timeDist;
			for(int i=0; i<causalEdges.size(); i++){
				causalEdge = causalEdges.get(i);
				timeDist = causalEdge.getTimeDist();
				timeDistTotal = timeDistTotal + timeDist;
			}
			if(causalEdges.size() == 0)
				return 0;
			double size = causalEdges.size();
			double timeDistAvg = timeDistTotal / size;
			return timeDistAvg;
		}


		private double getAvgTimePos(CreslaCFDMatrixIdx cfdIdx) {
			// TODO Auto-generated method stub
			ArrayList<CausalEdge> causalEdges = cfdIdx.getCausalEdges();
			CausalEdge causalEdge = null;
			double timePosTotal = 0;
			double timePos;
			for(int i=0; i<causalEdges.size(); i++){
				causalEdge = causalEdges.get(i);
				timePos = causalEdge.getTimePos();
				timePosTotal = timePosTotal + timePos;
			}
			if(causalEdges.size() == 0)
				return 0;
			double size = causalEdges.size();
			double timePosAvg = timePosTotal / size;
			return timePosAvg;
		}


		private double getAvgALSFRSRelativeChange(CreslaCFDMatrixIdx cfdIdx) {
			// TODO Auto-generated method stub
			ArrayList<CausalEdge> causalEdges = cfdIdx.getCausalEdges();
			CausalEdge causalEdge = null;
			double frsTotal = 0;
			double frs;
			for(int i=0; i<causalEdges.size(); i++){
				causalEdge = causalEdges.get(i);
				frs = causalEdge.getFrsTotalRelativeChange();
				frsTotal = frsTotal + frs;
			}
			if(causalEdges.size() == 0)
				return 0;
			double size = causalEdges.size();
			double frsAvg = frsTotal / size;
			return frsAvg;
		}


		private double getAvgALSFRSAbsChange(CreslaCFDMatrixIdx cfdIdx) {
			// TODO Auto-generated method stub
			ArrayList<CausalEdge> causalEdges = cfdIdx.getCausalEdges();
			CausalEdge causalEdge = null;
			double frsTotal = 0;
			double frs;
			for(int i=0; i<causalEdges.size(); i++){
				causalEdge = causalEdges.get(i);
				frs = causalEdge.getFrsTotalAbsChange();
				frsTotal = frsTotal + frs;
			}
			if(causalEdges.size() == 0)
				return 0;
			double size = causalEdges.size();
			double frsAvg = frsTotal / size;
			return frsAvg;
		}


		private double getAvgCause(CreslaCFDMatrixIdx cfdIdx) {
			// TODO Auto-generated method stub
			ArrayList<CausalEdge> causalEdges = cfdIdx.getCausalEdges();
			CausalEdge causalEdge = null;
			double causeChangeTotal = 0;
			double causeChange;
			for(int i=0; i<causalEdges.size(); i++){
				causalEdge = causalEdges.get(i);
				causeChange = causalEdge.getCauseChange();
				causeChangeTotal = causeChangeTotal + causeChange;
			}
			if(causalEdges.size() == 0)
				return 0;
			double size = causalEdges.size();
			double causeChangeAvg = causeChangeTotal / size;
			return causeChangeAvg;
		}


		private double getAvgEffect(CreslaCFDMatrixIdx cfdIdx) {
			// TODO Auto-generated method stub
			ArrayList<CausalEdge> causalEdges = cfdIdx.getCausalEdges();
			CausalEdge causalEdge = null;
			double effectChangeTotal = 0;
			double effectChange;
			for(int i=0; i<causalEdges.size(); i++){
				causalEdge = causalEdges.get(i);
				effectChange = causalEdge.getEffectChange();
				effectChangeTotal = effectChangeTotal + effectChange;
			}
			if(causalEdges.size() == 0)
				return 0;
			double size = causalEdges.size();
			double effectChangeAvg = effectChangeTotal / size;
			return effectChangeAvg;
		}


		private double getAvgDeath(CreslaCFDMatrixIdx cfdIdx) {
			// TODO Auto-generated method stub
			ArrayList<CausalEdge> causalEdges = cfdIdx.getCausalEdges();
			int ctrDeath = 0;
			CausalEdge causalEdge = null;
			String death = null;
			for(int i=0; i<causalEdges.size(); i++){
				causalEdge = causalEdges.get(i);
				death = causalEdge.getDeath();
				if(death.equalsIgnoreCase("dead"))
					ctrDeath++;
			}
			if(causalEdges.size() == 0)
				return 0;
			double size = causalEdges.size();
			double deathPercent = ctrDeath / size;
			return deathPercent;
		}


		private double getAvgFemalePercent(CreslaCFDMatrixIdx cfdIdx) {
			// TODO Auto-generated method stub
			ArrayList<CausalEdge> causalEdges = cfdIdx.getCausalEdges();
			int ctrFemale = 0;
			CausalEdge causalEdge = null;
			String sex = null;
			for(int i=0; i<causalEdges.size(); i++){
				causalEdge = causalEdges.get(i);
				sex = causalEdge.getSex();
				if(sex.equalsIgnoreCase("\"F\""))
					ctrFemale++;
			}
			if(causalEdges.size() == 0)
				return 0;
			double size = causalEdges.size();
			double femalePercent = ctrFemale / size;
			return femalePercent;
		}


		private double getAvgMalePercent(CreslaCFDMatrixIdx cfdIdx) {
			// TODO Auto-generated method stub
			ArrayList<CausalEdge> causalEdges = cfdIdx.getCausalEdges();
			int ctrMale = 0;
			CausalEdge causalEdge = null;
			String sex = null;
			for(int i=0; i<causalEdges.size(); i++){
				causalEdge = causalEdges.get(i);
				sex = causalEdge.getSex();
				if(sex.equalsIgnoreCase("\"M\""))
					ctrMale++;
			}
			if(causalEdges.size() == 0)
				return 0;
			double size = causalEdges.size();
			double malePercent = ctrMale / size;
			return malePercent;
		}


		private double getAvgAge(CreslaCFDMatrixIdx cfdIdx) {
			// TODO Auto-generated method stub
			ArrayList<CausalEdge> causalEdges = cfdIdx.getCausalEdges();
			CausalEdge causalEdge = null;
			double ageTotal = 0;
			double age;
			for(int i=0; i<causalEdges.size(); i++){
				causalEdge = causalEdges.get(i);
				age = causalEdge.getAge();
				ageTotal = ageTotal + age;
			}
			if(causalEdges.size() == 0)
				return 0;
			double ageAvg = ageTotal / causalEdges.size();
			return ageAvg;
		}
		
		private void createCTERFile(){
			ArrayList<Double> CTERList = getCTERList();
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CTER.csv");
			double CTER;
			try {
				PrintWriter pw = new PrintWriter(file);
				for(int i=0; i<CTERList.size(); i++){
					CTER = CTERList.get(i);
					pw.printf("%.3f", CTER);
					pw.println();
				}
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}//createCTERFile
		
		//get entropy list for all causal links
				private ArrayList<Double> getEntropyList(){
					String strEntropy;
					ArrayList<Double> entropyList = new ArrayList<Double>();
					double entropy;
					try {
						BufferedReader inputEntropy = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\EntropyCausalLinks.csv"));
						try {
							while ((strEntropy = inputEntropy.readLine()) != null){
								entropy = Double.parseDouble(strEntropy);
								entropyList.add(entropy);
							}//while
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return entropyList;
				}//getEntropyList

		//get CTER for all causal links
		private ArrayList<Double> getCTERList(){
			String strEntropy, strCausalTend;
			ArrayList<Double> entropyList = new ArrayList<Double>();
			ArrayList<Double> causalTendList = new ArrayList<Double>();
			ArrayList<Double> CTERList = new ArrayList<Double>();
			double entropy, causalTend, CTER;
			try {
				BufferedReader inputEntropy = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\EntropyCausalLinks.csv"));
				BufferedReader inputCausalTend = new BufferedReader(new FileReader("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CausalTendency.csv"));
				try {
					while ((strEntropy = inputEntropy.readLine()) != null){
						entropy = Double.parseDouble(strEntropy);
						entropyList.add(entropy);
					}//while
					while ((strCausalTend = inputCausalTend.readLine()) != null){
						causalTend = Double.parseDouble(strCausalTend);
						causalTendList.add(causalTend);
					}//while
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(int i=0; i<entropyList.size(); i++){
				causalTend = causalTendList.get(i);
				entropy = entropyList.get(i);				
				CTER = causalTend / entropy;
				CTERList.add(CTER);
			}//for
			return CTERList;
		}//getCTERList
				
		private double getCausalTendency(int row, int col){
			//calculate causal tendency of row-->col based on log [p(T-->T)/p(T-->F)]
			CreslaEvent[] events;
			int ctrSorat = 0;
			int ctrMakhraj = 0;
			double probSorat = 0;
			double probMakhraj = 0;
			ArrayList<Integer> rowArr, colArr;
			
			//soorat
			for(int i=0; i<allPatientsEvents.size(); i++){
				events = allPatientsEvents.get(i);
				if(events == null)
					continue;
				for(int j=1; j<events.length-1; j++){
					rowArr = events[j].getFrsNums();
					colArr = events[j+1].getFrsNums();
					if(isExistIdx(colArr, col)){
						ctrSorat++;
						if(isExistIdx(rowArr, row))
							probSorat++;
					}//if(!isExistIdx)
				}//for(j)
			}//for(i)
			
			if(ctrSorat==0)
				probSorat = 0;
			else
				probSorat = probSorat/ctrSorat;//P(T-->T)
			
			//makhraj
			for(int i=0; i<allPatientsEvents.size(); i++){
				events = allPatientsEvents.get(i);
				if(events == null)
					continue;
				for(int j=1; j<events.length-1; j++){
					rowArr = events[j].getFrsNums();
					colArr = events[j+1].getFrsNums();
					if(!isExistIdx(colArr, col)){
						ctrMakhraj++;
						if(isExistIdx(rowArr, row))
							probMakhraj++;
					}//if(!isExistIdx)
				}//for(j)
			}//for(i)
			
			if(ctrMakhraj == 0)
				probMakhraj = 0;
			else	
				probMakhraj = probMakhraj/ctrMakhraj;
			
			if(probMakhraj == 0)
				return 0;
			double answer = Math.log10(probSorat/probMakhraj);
			
			if(answer<0)
				return 0;
			
			return answer;
			
		}//getCausalTendency
		
		private boolean isExistIdx(ArrayList<Integer> list, int idx){
			for(int i=0; i<list.size(); i++)
				if(list.get(i) == idx)
					return true;
			return false;
		}//isExistIdx 
		
		//call this method for each CFD matrix index and add a causal link to the related index
		private void updateCFDMatrix(CreslaEvent[] eventsArray, String sex,
				int age, String death, String genetics) {

			CreslaEvent currEvent, nextEvent;
			ArrayList<Integer> currFrsNums = new ArrayList<Integer>();
			ArrayList<Integer> nextFrsNums = new ArrayList<Integer>();
			ArrayList<Double> currFrsChanges = new ArrayList<Double>();
			ArrayList<Double> nextFrsChanges = new ArrayList<Double>();
			CausalEdge causalEdge;//causal edge object for each index
			
			for(int eventsArrIdx = 0; eventsArrIdx < eventsArray.length-1; eventsArrIdx++){
				currEvent = eventsArray[eventsArrIdx];//current event
				nextEvent = eventsArray[eventsArrIdx+1];//next event
				currFrsNums = currEvent.getFrsNums();
				currFrsChanges = currEvent.getFrsChanges();
				nextFrsNums = nextEvent.getFrsNums();
				nextFrsChanges = nextEvent.getFrsChanges();
				CreslaEvent lastEvent = eventsArray[eventsArray.length-1];
				
				for(int currIdx = 0; currIdx < currFrsNums.size(); currIdx++){
					for(int nextIdx = 0; nextIdx < nextFrsNums.size(); nextIdx++){
						int row = currFrsNums.get(currIdx);
						int col = nextFrsNums.get(nextIdx);
						double causeChange = currFrsChanges.get(currIdx);
						double effectChange = nextFrsChanges.get(nextIdx);
						int timeDist = nextEvent.getTime() - currEvent.getTime();
						double currTime = currEvent.getTime();
						double lastTime = lastEvent.getTime();
						double timePos = currTime / lastTime;
						double frsTotalAbsChange = currEvent.getFrsTotalAbsChange();
						double frsTotalRelativeChange = currEvent.getFrsTotalRelativeChange();
						causalEdge = new CausalEdge();
						causalEdge.setCauseChange(causeChange);
						causalEdge.setEffectChange(effectChange);
						causalEdge.setSex(sex);
						causalEdge.setAge(age);
						causalEdge.setDeath(death);
						causalEdge.setGenetics(genetics);
						causalEdge.setTimeDist(timeDist);
						causalEdge.setTimePos(timePos);
						causalEdge.setFrsTotalAbsChange(frsTotalAbsChange);
						causalEdge.setFrsTotalRelativeChange(frsTotalRelativeChange);
						cfdMatrix[row][col].getCausalEdges().add(causalEdge);//add causal edge to the related index
					}//for(nextIdx)
				}//for(currIdx)
			}//for(eventsArrIdx)
		}//updateCFDMatrix
		
		//create CFDMatrix indexes files
		private void createCFDMatIdxsFiles(){
			File file = new File("D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CFDMatIdxCresla.csv");
			try {
				PrintWriter pw = new PrintWriter(file);
				for(int i=1; i <= CreslaUtils.numOfDynamicParamsCresla; i++){
					for(int j=1; j <= CreslaUtils.numOfDynamicParamsCresla; j++){
						pw.println(i+","+j);
					}//for(j)
				}//for(i)
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//createCFDMatIdxs
		
		//create CFDMatrix cause effect changes files
		private void createCFDMatCauseEffectFiles(){
			File cfdFile;
			ArrayList<CausalEdge> causalEdgesList;
			CausalEdge causalEdge;
			int causalEdgeListSize;
			String fileName;
			PrintWriter pw = null;
			double causeChg, effectChg;
			
			for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
				for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
					fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CauseEffectChanges\\"+row+"-"+col+".csv";
					cfdFile = new File(fileName);
					try {
						pw = new PrintWriter(cfdFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					causalEdgesList = cfdMatrix[row][col].getCausalEdges();
					causalEdgeListSize = causalEdgesList.size();
					for(int ce=0; ce < causalEdgeListSize; ce++){
						causalEdge = causalEdgesList.get(ce);
						causeChg = causalEdge.getCauseChange();
						effectChg = causalEdge.getEffectChange();
						pw.printf("%.2f", causeChg);
						pw.print(",");
						pw.printf("%.2f", effectChg);
						pw.println();
					}//for(ce)
					pw.close();
				}//for(col)
			}//for(row)
		}//createCFDMatCauseEffectFiles
		
		//create CFDMatrix genetics files
		private void createCFDMatGeneticsFiles(){
			File cfdFile;
			ArrayList<CausalEdge> causalEdgesList;
			CausalEdge causalEdge;
			int causalEdgeListSize;
			String fileName;
			PrintWriter pw = null;
			String genetics;
			
			for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
				for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
					fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\Genetics\\"+row+"-"+col+".csv";
					cfdFile = new File(fileName);
					try {
						pw = new PrintWriter(cfdFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					causalEdgesList = cfdMatrix[row][col].getCausalEdges();
					causalEdgeListSize = causalEdgesList.size();
					for(int ce=0; ce < causalEdgeListSize; ce++){
						causalEdge = causalEdgesList.get(ce);
						genetics = causalEdge.getGenetics();
						if(genetics.contains("c9orf72"))
							pw.println("1,c9orf72");
						else if(genetics.contains("sod1"))
								pw.println("2,sod1");
						else if(genetics.contains("tardbp"))
								pw.println("3,tardbp");
						else if(genetics.contains("fus"))
								pw.println("4,fus");
						else if(genetics.contains("wt"))
								pw.println("5,wt");
						else if(genetics.contains("NA"))
								pw.println("6,NA");
					}//for(ce)
					pw.close();
				}//for(col)
			}//for(row)
		}//createCFDMatGeneticsFiles
		
				//create CFDMatrix time distances files
				private void createCFDMatTimeDistFiles(){
					File cfdFile;
					ArrayList<CausalEdge> causalEdgesList;
					CausalEdge causalEdge;
					int causalEdgeListSize;
					String fileName;
					PrintWriter pw = null;
					double timeDist;
					
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\TimeDistances\\"+row+"-"+col+".csv";
							cfdFile = new File(fileName);
							try {
								pw = new PrintWriter(cfdFile);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							causalEdgesList = cfdMatrix[row][col].getCausalEdges();
							causalEdgeListSize = causalEdgesList.size();
							for(int ce=0; ce < causalEdgeListSize; ce++){
								causalEdge = causalEdgesList.get(ce);
								timeDist = causalEdge.getTimeDist();
								pw.printf("%.2f", timeDist);
								pw.println();
							}//for(ce)
							pw.close();
						}//for(col)
					}//for(row)
				}//createCFDMatTimeDistFiles
				
				//create CFDMatrix time positions files
				private void createCFDMatTimePosFiles(){
					File cfdFile;
					ArrayList<CausalEdge> causalEdgesList;
					CausalEdge causalEdge;
					int causalEdgeListSize;
					String fileName;
					PrintWriter pw = null;
					double timePos;
					
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\TimePositions\\"+row+"-"+col+".csv";
							cfdFile = new File(fileName);
							try {
								pw = new PrintWriter(cfdFile);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							causalEdgesList = cfdMatrix[row][col].getCausalEdges();
							causalEdgeListSize = causalEdgesList.size();
							for(int ce=0; ce < causalEdgeListSize; ce++){
								causalEdge = causalEdgesList.get(ce);
								timePos = causalEdge.getTimePos();
								pw.printf("%.2f", timePos);
								pw.println();
							}//for(ce)
							pw.close();
						}//for(col)
					}//for(row)
				}//createCFDMatTimePosFiles
				
				//create CFDMatrix average time distances file
				private void createCFDAvgTimeDistFile(){
					String fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\AverageTimeDistances.csv";
					File cfdFile = new File(fileName);
					PrintWriter pw = null;
					try {
						pw = new PrintWriter(cfdFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					double timeDist;
					
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							
							
							timeDist = cfdMatrix[row][col].getAvgTimeDist();
							pw.printf("%.2f", timeDist);
							pw.println();
						}//for(col)
					}//for(row)
					pw.close();
				}//createCFDAvgTimeDistFile
				
				//create CFDMatrix average time positions file
				private void createCFDAvgTimePosFile(){
					String fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\AverageTimePositions.csv";
					File cfdFile = new File(fileName);
					PrintWriter pw = null;
					double timePos;
					try {
						pw = new PrintWriter(cfdFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							
							
							timePos = cfdMatrix[row][col].getAvgTimePos();
							pw.printf("%.2f", timePos);
							pw.println();
						}//for(col)
					}//for(row)
					pw.close();
				}//createCFDAvgTimePosFile
				
				//create CFDMatrix average ALSFRS absolute and relative values file
				private void createCFDAvgFrsAbsRelativeFile(){
					String fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\AverageFrsAbsRelative.csv";
					File cfdFile = new File(fileName);
					PrintWriter pw = null;
					double frsAbs, frsRelative;
					try {
						pw = new PrintWriter(cfdFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							
							
							frsAbs = cfdMatrix[row][col].getAvgALSFRSAbsChange();
							frsRelative = cfdMatrix[row][col].getAvgALSFRSRelativeChange();
							pw.printf("%.2f", frsAbs);
							pw.print(",");
							pw.printf("%.2f", frsRelative);
							pw.println();
						}//for(col)
					}//for(row)
					pw.close();
				}//createCFDAvgFrsAbsRelativeFile
				
				//create CFDMatrix average cause and effect file
				private void createCFDAvgCauseEffectFile(){
					String fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\AverageCauseEffect.csv";
					File cfdFile = new File(fileName);
					PrintWriter pw = null;
					double cause, effect;
					try {
						pw = new PrintWriter(cfdFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							
							
							cause = cfdMatrix[row][col].getAvgCause();
							effect = cfdMatrix[row][col].getAvgEffect();
							pw.printf("%.2f", cause);
							pw.print(",");
							pw.printf("%.2f", effect);
							pw.println();
						}//for(col)
					}//for(row)
					pw.close();
				}//createCFDAvgCauseEffectFile
				
				//create CFDMatrix average male and female file
				private void createCFDAvgMaleFemaleFile(){
					String fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\AverageMaleFemale.csv";
					File cfdFile = new File(fileName);
					PrintWriter pw = null;
					double male, female;
					try {
						pw = new PrintWriter(cfdFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							
							
							male = cfdMatrix[row][col].getAvgMalePercent();
							female = cfdMatrix[row][col].getAvgFemalePercent();
							pw.printf("%.2f", male);
							pw.print(",");
							pw.printf("%.2f", female);
							pw.println();
						}//for(col)
					}//for(row)
					pw.close();
				}//createCFDAvgDeathFile
				
				//create CFDMatrix average death file
				private void createCFDAvgDeathFile(){
					String fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\AverageDeath.csv";
					File cfdFile = new File(fileName);
					PrintWriter pw = null;
					double death;
					try {
						pw = new PrintWriter(cfdFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							
							
							death = cfdMatrix[row][col].getAvgDeath();
							pw.printf("%.2f", death);
							pw.println();
						}//for(col)
					}//for(row)
					pw.close();
				}//createCFDAvgDeathFile
				
				//create CFDMatrix average ages file
				private void createCFDAvgAgeFile(){
					String fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\AverageAge.csv";
					File cfdFile = new File(fileName);
					PrintWriter pw = null;
					try {
						pw = new PrintWriter(cfdFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					double age;
					
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							age = cfdMatrix[row][col].getAvgAge();
							pw.printf("%.2f", age);	
							pw.println();
						}//for(col)
					}//for(row)
					pw.close();
				}//createCFDAvgAgeFile
				
				//create CFDMatrix causal tendency file
				private void createCFDCausalTendFile(){
					
					String fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CausalTendency.csv";
					File cfdFile = new File(fileName);
					PrintWriter pw = null;
					double causalTend;
					try {
						pw = new PrintWriter(cfdFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							
							causalTend = cfdMatrix[row][col].getCausalTendency();
							pw.printf("%.2f", causalTend);	
							pw.println();
						}//for(col)
					}//for(row)
					pw.close();
				}//createCFDCausalTendFile
				
				//create CFDMatrix averages file
				private void createCFDAveragesFile(){
					
					String fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\CFDMatrixAverages.csv";;
					File cfdFile = new File(fileName);
					PrintWriter pw = null;
					double causalTendency, entropy, CTER, avgAge, avgDeath, avgMale, avgFemale,
					avgCause, avgEffect, avgFrsAbs, avgFrsRelative, avgTimePos, avgTimeDist;
					try {
						pw = new PrintWriter(cfdFile);
						pw.println("Row, Column, CausalTendency, Entropy, CTER, Age, Death, Male, Female, Cause, Effect, FrsAbs, FrsRelative, TimePosition, TimeDistance");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){							
							causalTendency = cfdMatrix[row][col].getCausalTendency();
							entropy = cfdMatrix[row][col].getEntropy();
							CTER = cfdMatrix[row][col].getCTER();
							avgAge = cfdMatrix[row][col].getAvgAge();
							avgDeath = cfdMatrix[row][col].getAvgDeath();
							avgMale = cfdMatrix[row][col].getAvgMalePercent();
							avgFemale = cfdMatrix[row][col].getAvgFemalePercent();
							avgCause = cfdMatrix[row][col].getAvgCause();
							avgEffect = cfdMatrix[row][col].getAvgEffect();
							avgFrsAbs = cfdMatrix[row][col].getAvgALSFRSAbsChange();
							avgFrsRelative = cfdMatrix[row][col].getAvgALSFRSRelativeChange();
							avgTimePos = cfdMatrix[row][col].getAvgTimePos();
							avgTimeDist = cfdMatrix[row][col].getAvgTimeDist();
							pw.print(row+","+col+",");
							pw.printf("%.2f", causalTendency);
							pw.print(",");
							pw.printf("%.2f", entropy);
							pw.print(",");
							pw.printf("%.3f", CTER);
							pw.print(",");
							pw.printf("%.2f", avgAge);
							pw.print(",");
							pw.printf("%.2f", avgDeath);
							pw.print(",");
							pw.printf("%.2f", avgMale);
							pw.print(",");
							pw.printf("%.2f", avgFemale);
							pw.print(",");
							pw.printf("%.2f", avgCause);
							pw.print(",");
							pw.printf("%.2f", avgEffect);
							pw.print(",");
							pw.printf("%.2f", avgFrsAbs);
							pw.print(",");
							pw.printf("%.2f", avgFrsRelative);
							pw.print(",");
							pw.printf("%.2f", avgTimePos);
							pw.print(",");
							pw.printf("%.2f", avgTimeDist);
							pw.println();
						}//for(col)
					}//for(row)
					pw.close();
				}//createCFDAveragesFile
				
				//create CFDMatrix ages files
				private void createCFDMatAgeFiles(){
					File cfdFile;
					ArrayList<CausalEdge> causalEdgesList;
					CausalEdge causalEdge;
					int causalEdgeListSize;
					String fileName;
					PrintWriter pw = null;
					double age;
					
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\Ages\\"+row+"-"+col+".csv";
							cfdFile = new File(fileName);
							try {
								pw = new PrintWriter(cfdFile);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							causalEdgesList = cfdMatrix[row][col].getCausalEdges();
							causalEdgeListSize = causalEdgesList.size();
							for(int ce=0; ce < causalEdgeListSize; ce++){
								causalEdge = causalEdgesList.get(ce);
								age = causalEdge.getAge();
								pw.println(age);
							}//for(ce)
							pw.close();
						}//for(col)
					}//for(row)
				}//createCFDMatAgesFiles
				
				//create CFDMatrix FRS total absolute change files
				private void createCFDMatFrsTotalAbsFiles(){
					File cfdFile;
					ArrayList<CausalEdge> causalEdgesList;
					CausalEdge causalEdge;
					int causalEdgeListSize;
					String fileName;
					PrintWriter pw = null;
					double frsTotAbsChg;
					
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\FrsTotalAbsChanges\\"+row+"-"+col+".csv";
							cfdFile = new File(fileName);
							try {
								pw = new PrintWriter(cfdFile);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							causalEdgesList = cfdMatrix[row][col].getCausalEdges();
							causalEdgeListSize = causalEdgesList.size();
							for(int ce=0; ce < causalEdgeListSize; ce++){
								causalEdge = causalEdgesList.get(ce);
								frsTotAbsChg = causalEdge.getFrsTotalAbsChange();
								pw.printf("%.2f",frsTotAbsChg);
								pw.println();
							}//for(ce)
							pw.close();
						}//for(col)
					}//for(row)
				}//createCFDMatFrsTotalAbsFiles
				
				//create CFDMatrix FRS total relative change files
				private void createCFDMatFrsTotalRelativeFiles(){
					File cfdFile;
					ArrayList<CausalEdge> causalEdgesList;
					CausalEdge causalEdge;
					int causalEdgeListSize;
					String fileName;
					PrintWriter pw = null;
					double frsTotRelativeChg;
					
					for(int row=1; row <= CreslaUtils.numOfDynamicParamsCresla; row++){
						for(int col=1; col <= CreslaUtils.numOfDynamicParamsCresla; col++){
							fileName = "D:\\PHD\\Thesis\\Implementation\\ALS-Matlab\\Cresla\\CFDMatrix\\FrsTotalRelativeChanges\\"+row+"-"+col+".csv";
							cfdFile = new File(fileName);
							try {
								pw = new PrintWriter(cfdFile);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							causalEdgesList = cfdMatrix[row][col].getCausalEdges();
							causalEdgeListSize = causalEdgesList.size();
							for(int ce=0; ce < causalEdgeListSize; ce++){
								causalEdge = causalEdgesList.get(ce);
								frsTotRelativeChg = causalEdge.getFrsTotalRelativeChange();
								pw.printf("%.2f",frsTotRelativeChg);
								pw.println();
							}//for(ce)
							pw.close();
						}//for(col)
					}//for(row)
				}//createCFDMatFrsTotalRelativeFiles


		private class CreslaEvent{
			ArrayList<Integer> frsNums;//all FRS nums
			ArrayList<Double> frsChanges;//all FRS changes values
			double frsTotal = 0;	//ALSFRS total value
			double frsTotalAbsChange = 0;	//Absolute change value of ALSFRS total
			double frsTotalRelativeChange = 0;//Relative change value of ALSFRS total
			int time = -1;	//time of visit in days
			
			CreslaEvent() {
				// TODO Auto-generated constructor stub
				frsNums = new ArrayList<Integer>();
				frsChanges = new ArrayList<Double>();
			}
			
			double getFrsTotalRelativeChange() {
				return frsTotalRelativeChange;
			}

			void setFrsTotalRelativeChange(double frsTotalRelativeChange) {
				this.frsTotalRelativeChange = frsTotalRelativeChange;
			}

			double getFrsTotalAbsChange() {
				return frsTotalAbsChange;
			}

			void setFrsTotalAbsChange(double frsTotalAbsChange) {
				this.frsTotalAbsChange = frsTotalAbsChange;
			}

			ArrayList<Integer> getFrsNums() {
				return frsNums;
			}
			void setFrsNums(ArrayList<Integer> frsNums) {
				this.frsNums = frsNums;
			}
			ArrayList<Double> getFrsChanges() {
				return frsChanges;
			}
			void setFrsChanges(ArrayList<Double> frsChanges) {
				this.frsChanges = frsChanges;
			}
			double getFrsTotal() {
				return frsTotal;
			}
			void setFrsTotal(double frsTotal) {
				this.frsTotal = frsTotal;
			}
			int getTime() {
				return time;
			}
			void setTime(int time) {
				this.time = time;
			}
			
		}//inner class CreslaEvent
	
}//CreslaCFDMatrix
