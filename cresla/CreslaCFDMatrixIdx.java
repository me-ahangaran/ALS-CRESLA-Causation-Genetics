package cresla;

import java.util.ArrayList;

public class CreslaCFDMatrixIdx {
	
	private ArrayList<CausalEdge> causalEdges = new ArrayList<CausalEdge>();//list of causal edges changes for current index;
	private double avgAge = 0;
	private double avgMalePercent = 0;
	private double avgFemalePercent = 0;
	private double causalTendency = 0;
	private double avgALSFRSAbsChange = 0;
	private double avgALSFRSRelativeChange = 0;
	private double avgTimeDist = 0;
	private double avgTimePos = 0;
	private double avgDeath = 0;
	private double avgCause = 0;
	private double avgEffect = 0;
	private double CTER = 0;
	private double entropy = 0;
		
	
	
	public double getEntropy() {
		return entropy;
	}



	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}



	public double getCTER() {
		return CTER;
	}



	public void setCTER(double cTER) {
		CTER = cTER;
	}



	public double getAvgCause() {
		return this.avgCause;
	}



	public void setAvgCause(double avgCause) {
		this.avgCause = avgCause;
	}



	public double getAvgEffect() {
		return this.avgEffect;
	}



	public void setAvgEffect(double avgEffect) {
		this.avgEffect = avgEffect;
	}



	public double getAvgFemalePercent() {
		
		return this.avgFemalePercent;
	}



	public void setAvgFemalePercent(double avgFemalePercent) {
		this.avgFemalePercent = avgFemalePercent;
	}



	public double getAvgALSFRSAbsChange() {
		return this.avgALSFRSAbsChange;
	}



	public void setAvgALSFRSAbsChange(double avgALSFRSAbsChange) {
		this.avgALSFRSAbsChange = avgALSFRSAbsChange;
	}



	public double getAvgALSFRSRelativeChange() {
		return this.avgALSFRSRelativeChange;
	}



	public void setAvgALSFRSRelativeChange(double avgALSFRSRelativeChange) {
		this.avgALSFRSRelativeChange = avgALSFRSRelativeChange;
	}



	public double getAvgTimeDist() {
		return this.avgTimeDist;
	}



	public void setAvgTimeDist(double avgTimeDist) {
		this.avgTimeDist = avgTimeDist;
	}



	public double getAvgTimePos() {
		return this.avgTimePos;
	}



	public void setAvgTimePos(double avgTimePos) {
		this.avgTimePos = avgTimePos;
	}



	public double getAvgDeath() {
		
		return this.avgDeath;
	}



	public void setAvgDeath(double avgDeath) {
		this.avgDeath = avgDeath;
	}



	public double getCausalTendency() {
		return causalTendency;
	}



	public void setCausalTendency(double causalTendency) {
		this.causalTendency = causalTendency;
	}



	public ArrayList<CausalEdge> getCausalEdges() {
		return causalEdges;
	}



	public void setCausalEdges(ArrayList<CausalEdge> causalEdges) {
		this.causalEdges = causalEdges;
	}



	public double getAvgAge() {
		
		return this.avgAge;
	}



	public void setAvgAge(double avgAge) {
		this.avgAge = avgAge;
	}



	public double getAvgMalePercent() {
		
		return this.avgMalePercent;
	}
	


	public void setAvgMalePercent(double avgMalePercent) {
		this.avgMalePercent = avgMalePercent;
	}

}//CreslaCFDMatrixIdx
