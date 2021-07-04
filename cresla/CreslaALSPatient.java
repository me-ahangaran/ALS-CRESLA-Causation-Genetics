package cresla;

import java.util.ArrayList;

public class CreslaALSPatient {

	private String id = null;	//unique identifier for each patient in the CRESLA dataset
	
	//all static features of CRESLA dataset
	private String sex = null;	//M or F
	private int birthDate = 0;
	private int birthMonth = 0;
	private int birthYear = 0;
	private int heigh = 0;
	private int weightPremorbid = 0;	//weight before diagnosis (KG)
	private int weightDiagnosis = 0;	//weight after diagnosis (KG)
	private int fvc = 0;	//FVC value at the diagnosis time. Percentage of normal value, 
	//higher values are better
	private String familiarity = null;	//family history of motor neurons diseases (Y/N)
	private String genetics = null;	//genetic analysis of C9ORF72, SOD1, TARDBP, FUS. 
	//NA (Not Available), WT (negative), or name of the gene if positive.
	private String ftd = null;	//dementia, based on clinical suspicious or neuropsychological test.
	//Y(Yes), N(No), or NA(Not Available)
	private int onsetDate = 0;	//date of onset
	private int onsetMonth = 0;	//month of onset
	private int onsetYear = 0;	//year of onset
	private String onsetSite = null;	//S(spinal), B(bulbar), R(respiratory)
	private int diagnosisDate = 0;	//date of diagnosis
	private int diagnosisMonth = 0;	//month of diagnosis
	private int diagnosisYear = 0;	//year of diagnosis
	private int nivDate = 0;	//date of NIV (using mask) or "not adopted" 
	private int nivMonth = 0;	//month of NIV
	private int nivYear = 0;	//year of NIV
	private int pegDate =0;	//date of PEG (using gastrostomy) or "not adopted"
	private int pegMonth = 0;	//month of PEG
	private int pegYear = 0;	//year of PEG
	private int tracheoDate = 0;	//date of Tracheo (respiratory device) OR "not adopted"
	private int tracheoMonth = 0;	//month of Tracheo
	private int tracheoYear = 0;	//year of Tracheo
	private int deathDate = 0;	//date of death or "alive"
	private int deathMonth = 0;	//month of death
	private int deathYear = 0;	//year of death
	private double slope = 0; //slope value (score/month)
	private String progressionType = null;	//Slow, Medium, Fast
	
	private ArrayList<CreslaALSFRS> patientSequence = null;	//all ALSFRS parameters
	
	//constructor
	public CreslaALSPatient(String id, String sex) {
		super();
		this.id = id;
		this.sex = sex;
	}

	
	
	public String getProgressionType() {
		return progressionType;
	}



	public void setProgressionType(String progressionType) {
		this.progressionType = progressionType;
	}



	public double getSlope() {
		return slope;
	}



	public void setSlope(double slope) {
		this.slope = slope;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(int birthDate) {
		this.birthDate = birthDate;
	}

	public int getBirthMonth() {
		return birthMonth;
	}

	public void setBirthMonth(int birthMonth) {
		this.birthMonth = birthMonth;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	public int getHeigh() {
		return heigh;
	}

	public void setHeigh(int heigh) {
		this.heigh = heigh;
	}

	public int getWeightPremorbid() {
		return weightPremorbid;
	}

	public void setWeightPremorbid(int weightPremorbid) {
		this.weightPremorbid = weightPremorbid;
	}

	public int getWeightDiagnosis() {
		return weightDiagnosis;
	}

	public void setWeightDiagnosis(int weightDiagnosis) {
		this.weightDiagnosis = weightDiagnosis;
	}

	public int getFvc() {
		return fvc;
	}

	public void setFvc(int fvc) {
		this.fvc = fvc;
	}

	public String getFamiliarity() {
		return familiarity;
	}

	public void setFamiliarity(String familiarity) {
		this.familiarity = familiarity;
	}

	public String getGenetics() {
		return genetics;
	}

	public void setGenetics(String genetics) {
		this.genetics = genetics;
	}

	public String getFtd() {
		return ftd;
	}

	public void setFtd(String ftd) {
		this.ftd = ftd;
	}

	public int getOnsetDate() {
		return onsetDate;
	}

	public void setOnsetDate(int onsetDate) {
		this.onsetDate = onsetDate;
	}

	public int getOnsetMonth() {
		return onsetMonth;
	}

	public void setOnsetMonth(int onsetMonth) {
		this.onsetMonth = onsetMonth;
	}

	public int getOnsetYear() {
		return onsetYear;
	}

	public void setOnsetYear(int onsetYear) {
		this.onsetYear = onsetYear;
	}

	public String getOnsetSite() {
		return onsetSite;
	}

	public void setOnsetSite(String onsetSite) {
		this.onsetSite = onsetSite;
	}

	public int getDiagnosisDate() {
		return diagnosisDate;
	}

	public void setDiagnosisDate(int diagnosisDate) {
		this.diagnosisDate = diagnosisDate;
	}

	public int getDiagnosisMonth() {
		return diagnosisMonth;
	}

	public void setDiagnosisMonth(int diagnosisMonth) {
		this.diagnosisMonth = diagnosisMonth;
	}

	public int getDiagnosisYear() {
		return diagnosisYear;
	}

	public void setDiagnosisYear(int diagnosisYear) {
		this.diagnosisYear = diagnosisYear;
	}

	public int getNivDate() {
		return nivDate;
	}

	public void setNivDate(int nivDate) {
		this.nivDate = nivDate;
	}

	public int getNivMonth() {
		return nivMonth;
	}

	public void setNivMonth(int nivMonth) {
		this.nivMonth = nivMonth;
	}

	public int getNivYear() {
		return nivYear;
	}

	public void setNivYear(int nivYear) {
		this.nivYear = nivYear;
	}

	public int getPegDate() {
		return pegDate;
	}

	public void setPegDate(int pegDate) {
		this.pegDate = pegDate;
	}

	public int getPegMonth() {
		return pegMonth;
	}

	public void setPegMonth(int pegMonth) {
		this.pegMonth = pegMonth;
	}

	public int getPegYear() {
		return pegYear;
	}

	public void setPegYear(int pegYear) {
		this.pegYear = pegYear;
	}

	public int getTracheoDate() {
		return tracheoDate;
	}

	public void setTracheoDate(int tracheoDate) {
		this.tracheoDate = tracheoDate;
	}

	public int getTracheoMonth() {
		return tracheoMonth;
	}

	public void setTracheoMonth(int tracheoMonth) {
		this.tracheoMonth = tracheoMonth;
	}

	public int getTracheoYear() {
		return tracheoYear;
	}

	public void setTracheoYear(int tracheoYear) {
		this.tracheoYear = tracheoYear;
	}

	public int getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(int deathDate) {
		this.deathDate = deathDate;
	}

	public int getDeathMonth() {
		return deathMonth;
	}

	public void setDeathMonth(int deathMonth) {
		this.deathMonth = deathMonth;
	}

	public int getDeathYear() {
		return deathYear;
	}

	public void setDeathYear(int deathYear) {
		this.deathYear = deathYear;
	}

	public ArrayList<CreslaALSFRS> getPatientSequence() {
		return patientSequence;
	}

	public void setPatientSequence(ArrayList<CreslaALSFRS> patientSequence) {
		this.patientSequence = patientSequence;
	}

	
}//class ALSPatientCresla
