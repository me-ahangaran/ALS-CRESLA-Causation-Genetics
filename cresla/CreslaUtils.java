package cresla;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;

import als.ALSUtils;

public class CreslaUtils {
	
	public static double frsChgThresh = 1; //threshold value for change of FRS parameters
	public static int numOfDynamicParamsCresla = 12; //number of FRS parameters
	public static final int frs1 = 1;
	public static final int frs2 = 2;
	public static final int frs3 = 3;
	public static final int frs4 = 4;
	public static final int frs5 = 5;
	public static final int frs6 = 6;
	public static final int frs7 = 7;
	public static final int frs8 = 8;
	public static final int frs9 = 9;
	public static final int frs10 = 10;
	public static final int frs11 = 11;
	public static final int frs12 = 12;
	
	public static final double numOfCreslaPatients = 1114;//number of patients in CRESLA dataset
	public String getFrsName(int frsNum){
		String frsName = null;
		switch (frsNum) {
		case 1:
			frsName = "1-Speech";
			break;
			
		case 2:
			frsName = "2-Salivation";
			break;
			
		case 3:
			frsName = "3-Swallowing";
			break;
			
		case 4:
			frsName = "4-Handwriting";
			break;	
			
		case 5:
			frsName = "5-CuttingFood";
			break;
			
		case 6:
			frsName = "6-DressingHygiene";
			break;
			
		case 7:
			frsName = "7-TurningBed";
			break;
			
		case 8:
			frsName = "8-Walking";
			break;
			
		case 9:
			frsName = "9-ClimbingStairs";
			break;
			
		case 10:
			frsName = "10-Dyspnea";
			break;
			
		case 11:
			frsName = "11-Orthopnea";
			break;
			
		case 12:
			frsName = "12-RespiratoryInsufficiency";
			break;

		default:
			break;
		}
		
		return frsName;
	}//getFrsName
	
	public int getDays(int firstDate, int firstMonth, int firstYear,
			int lastDate, int lastMonth, int lastYear){
		int days = 0;
		if(lastYear > firstYear){
			if(lastMonth > firstMonth){
				if(lastDate > firstDate)
					days = (lastDate - firstDate) + (lastMonth - firstMonth)*30 + (lastYear - firstYear)*365;
				else
					days = (30 + lastDate - firstDate) + (lastMonth - firstMonth - 1)*30 + (lastYear - firstYear)*365;
				
			}//if (lastMonth)
			else{//lastMonth <= firstMonth
				if(lastDate > firstDate)
					days = (lastDate - firstDate) + (12 + lastMonth - firstMonth)*30 + (lastYear - firstYear - 1)*365;
				else
					days = (30 + lastDate - firstDate) + (12 + lastMonth - firstMonth + 1)*30 + (lastYear - firstYear - 1)*365;
			}//else
		}//if (lastYear)
		if(lastYear == firstYear){
			if(lastMonth > firstMonth){
				if(lastDate > firstDate)
					days = (lastDate - firstDate) + (lastMonth - firstMonth)*30;
				else
					days = (30 + lastDate - firstDate) + (lastMonth - firstMonth - 1)*30;
			}//if (lastMonth)
			else//lastMonth = firstMonth
					days = lastDate - firstDate;
		}//if (lastYear)
		
		return days;
	}//getDays
	
	public double getMinVal(ArrayList<Double> list){
		double min = list.get(0);
		for(int i=1; i<list.size(); i++){
			if(list.get(i) < min)
				min = list.get(i);
		}//for
		return min;
	}//getMinVal
	
	public double getMaxVal(ArrayList<Double> list){
		double max = list.get(0);
		for(int i=1; i<list.size(); i++){
			if(list.get(i) > max)
				max = list.get(i);
		}//for
		return max;
	}//getMaxVal
	
	//convert a comma separated CSV file to a matrix (number of skip rows should be given)  
	public String[][] CSVFileToMatrix(String fileName, int numOfColumns){
		int numOfRows = getNumOfRowsFile(fileName);
		String[][] matrix = new String[numOfRows][numOfColumns];
		BufferedReader reader;
		ALSUtils util = new ALSUtils();
		String str;
		String[] line;
		int lineCtr = 0;//counter for lines
		try {
			reader = new BufferedReader(new FileReader(fileName));
			
			while((str = reader.readLine()) != null){
				line = util.getStrArray(str, numOfColumns);//read a line
				for(int i=0; i<numOfColumns; i++){
					matrix[lineCtr][i] = line[i];
				}//for
				lineCtr++;//go to next row of matrix
			}//while
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return matrix;
	}//CSVFileToMatrix
	
	private int getNumOfRowsFile(String filePath){
		int rows = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String str;
			while((str = reader.readLine()) != null)
				rows++;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rows;
	}//getNumOfRowsFile

}//class CreslaUtils
