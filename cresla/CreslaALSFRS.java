package cresla;

public class CreslaALSFRS {
	
		//visit date
		private int visitDate = 0;
		private int visitMonth = 0;
		private int visitYear = 0;

		//all dynamic features (ALSFRS parameters)
		private double frs1Speech = -1;
		private double frs2Salivation = -1;
		private double frs3Swallowing = -1;
		private double frs4Handwriting = -1;
		private double frs5CuttingFood = -1;
		private double frs6DressingHygiene = -1;
		private double frs7TurningBed = -1;
		private double frs8Walking = -1;
		private double frs9ClimbingStairs = -1;
		private double frs10Dyspnea = -1;
		private double frs11Orthopnea = -1;
		private double frs12RespiratoryInsufficiency = -1;		
		private double frsTotal = 0;	//the total value of ALSFRS parameters in the interval [0,48]
		
		public CreslaALSFRS(int visitDate, int visitMonth, int visitYear) {
			super();
			this.visitDate = visitDate;
			this.visitMonth = visitMonth;
			this.visitYear = visitYear;
		}
		public int getVisitDate() {
			return visitDate;
		}
		public void setVisitDate(int visitDate) {
			this.visitDate = visitDate;
		}
		public int getVisitMonth() {
			return visitMonth;
		}
		public void setVisitMonth(int visitMonth) {
			this.visitMonth = visitMonth;
		}
		public int getVisitYear() {
			return visitYear;
		}
		public void setVisitYear(int visitYear) {
			this.visitYear = visitYear;
		}
		
		
		public double getFrs1Speech() {
			return frs1Speech;
		}
		public void setFrs1Speech(double frs1Speech) {
			this.frs1Speech = frs1Speech;
		}
		public double getFrs2Salivation() {
			return frs2Salivation;
		}
		public void setFrs2Salivation(int frs2Salivation) {
			this.frs2Salivation = frs2Salivation;
		}
		public double getFrs3Swallowing() {
			return frs3Swallowing;
		}
		public void setFrs3Swallowing(int frs3Swallowing) {
			this.frs3Swallowing = frs3Swallowing;
		}
		public double getFrs4Handwriting() {
			return frs4Handwriting;
		}
		public void setFrs4Handwriting(int frs4Handwriting) {
			this.frs4Handwriting = frs4Handwriting;
		}
		public double getFrs5CuttingFood() {
			return frs5CuttingFood;
		}
		public void setFrs5CuttingFood(int frs5CuttingFood) {
			this.frs5CuttingFood = frs5CuttingFood;
		}
		public double getFrs6DressingHygiene() {
			return frs6DressingHygiene;
		}
		public void setFrs6DressingHygiene(int frs6DressingHygiene) {
			this.frs6DressingHygiene = frs6DressingHygiene;
		}
		public double getFrs7TurningBed() {
			return frs7TurningBed;
		}
		public void setFrs7TurningBed(int frs7TurningBed) {
			this.frs7TurningBed = frs7TurningBed;
		}
		public double getFrs8Walking() {
			return frs8Walking;
		}
		public void setFrs8Walking(int frs8Walking) {
			this.frs8Walking = frs8Walking;
		}
		public double getFrs9ClimbingStairs() {
			return frs9ClimbingStairs;
		}
		public void setFrs9ClimbingStairs(int frs9ClimbingStairs) {
			this.frs9ClimbingStairs = frs9ClimbingStairs;
		}
		public double getFrs10Dyspnea() {
			return frs10Dyspnea;
		}
		public void setFrs10Dyspnea(int frs10Dyspnea) {
			this.frs10Dyspnea = frs10Dyspnea;
		}
		public double getFrs11Orthopnea() {
			return frs11Orthopnea;
		}
		public void setFrs11Orthopnea(int frs11Orthopnea) {
			this.frs11Orthopnea = frs11Orthopnea;
		}
		public double getFrs12RespiratoryInsufficiency() {
			return frs12RespiratoryInsufficiency;
		}
		public void setFrs12RespiratoryInsufficiency(double frs12RespiratoryInsufficiency) {
			this.frs12RespiratoryInsufficiency = frs12RespiratoryInsufficiency;
		}
		public double getFrsTotal() {
			return frsTotal;
		}
		public void setFrsTotal(double frsTotal) {
			this.frsTotal = frsTotal;
		}
		
		
}//class ALSFRSCresla
