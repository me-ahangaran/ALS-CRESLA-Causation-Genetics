package cresla;

import java.util.ArrayList;
import java.util.List;

import cresla.CreslaPGM.PGMEdge;


public class CreslaMainALS {

	public static void main(String[] args) {
		
		//create CRESLA dataset (number of CRESLA patients = 1114)
		CreslaDataset cd = new CreslaDataset(); 
		ArrayList<CreslaALSPatient> allPatientsList = cd.createDataset();
		
		//create genetics file for all patients
		CreslaGenetics genetics = new CreslaGenetics();
		genetics.createGeneticsFile(allPatientsList);
		
		//create train dataset
		List<CreslaALSPatient> trainList = allPatientsList.subList(0, 1000);
		ArrayList<CreslaALSPatient> trainPatients = new ArrayList<CreslaALSPatient>(trainList);//train dataset
		
		//create CFD matrix of CRESAL dataset (train phase)
//		CreslaCFDMatrix cfd = new CreslaCFDMatrix();
//		cfd.createCFDMatrix(trainPatients);//create CFD matrix files
		
		//create PGM of CRESLA dataset
		CreslaPGM pgm = new CreslaPGM();
		ArrayList<PGMEdge> pgmEdges = pgm.createPGMInOut();//create PGM file and genetics file (out or in version)
		
		//create test dataset
		List<CreslaALSPatient> testList = allPatientsList.subList(900, 1100);
		ArrayList<CreslaALSPatient> testPatients = new ArrayList<CreslaALSPatient>(testList);//test dataset
		
		//prediction of CRESLA test dataset based on PGM
		CreslaPGMPrediction pgmPredict = new CreslaPGMPrediction();
		pgmPredict.predictInOut(testPatients, pgmEdges);	

	}//main
	
}//class CreslaMainALS
