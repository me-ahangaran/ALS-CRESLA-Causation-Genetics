package cresla;

public class CausalEdge {

	 private double causeChange = 0;	//change value for cause parameter relative to previous value
	 private double effectChange = 0; //change value for effect parameter relative to previous value
	 private double timeDist = 0; //distance between cause and effect changes in days
	 private double age = 0;	//age of patient related to causal link
	 private double timePos = 0; //time position of causal link in interval [0,1]
	 private double frsTotalAbsChange = 0; //absolute change of total FRS value
	 private double frsTotalRelativeChange = 0; //relative change of total FRS value(relative to previous value)
	 private String death = null;	//alive or dead
	 private String sex = null; //sex = 'M' or 'F'
	 private String genetics = null; //gene type for causal edge
	
	 
	public double getCauseChange() {
		return causeChange;
	}
	public void setCauseChange(double causeChange) {
		this.causeChange = causeChange;
	}
	public double getEffectChange() {
		return effectChange;
	}
	public void setEffectChange(double effectChange) {
		this.effectChange = effectChange;
	}
	public double getTimeDist() {
		return timeDist;
	}
	public void setTimeDist(double timeDist) {
		this.timeDist = timeDist;
	}
	public double getAge() {
		return age;
	}
	public void setAge(double age) {
		this.age = age;
	}
	public double getTimePos() {
		return timePos;
	}
	public void setTimePos(double timePos) {
		this.timePos = timePos;
	}
	public double getFrsTotalAbsChange() {
		return frsTotalAbsChange;
	}
	public void setFrsTotalAbsChange(double frsTotalAbsChange) {
		this.frsTotalAbsChange = frsTotalAbsChange;
	}
	public double getFrsTotalRelativeChange() {
		return frsTotalRelativeChange;
	}
	public void setFrsTotalRelativeChange(double frsTotalRelativeChange) {
		this.frsTotalRelativeChange = frsTotalRelativeChange;
	}
	public String getDeath() {
		return death;
	}
	public void setDeath(String death) {
		this.death = death;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getGenetics() {
		return genetics;
	}
	public void setGenetics(String genetics) {
		this.genetics = genetics;
	}
	 
	 
	
}//class CausalEdge
