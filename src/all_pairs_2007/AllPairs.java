package all_pairs_2007;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class AllPairs {
	private ArrayList<ArrayList<Double>> V;
	int n;
	int featureNum;
	// read data from filename
	public void readData(String filename) {
		V = new ArrayList<ArrayList<Double>>(n);
		for (int i=0; i<n; i++)
			V.add(new ArrayList<Double>());
		int counter = 0;
		try{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			while (line != null) {
				String [] tokens = line.split(" ");
				int length = Array.getLength(tokens);
				for (int i=1; i<length; i++){
					V.get(counter).add(Double.parseDouble(tokens[i])); // read features
				}
				line = br.readLine();
				counter++;
			}
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	// normalize data V
	public void normalize() {
		featureNum = 0;
		for (int i=0; i<n; i++){
			int m = V.get(i).size();
			if (featureNum < m)
				featureNum = m;
			double vectorLength = 0; 
			// calculate vector length
			for (int j=0; j<m; j++)
				vectorLength += Math.pow(V.get(i).get(j), 2);
			vectorLength = Math.sqrt(vectorLength);
			if (vectorLength > 1e-9) // if length is not 0
			{
				for (int j=0; j<m; j++)
					V.get(i).set(j, V.get(i).get(j) / vectorLength); // normalize by vector length 
			}
		}
	}
	
	public void printV() {
		for (int i=0; i<n; i++){
			for (int j=0; j<V.get(i).size(); j++)
				System.out.print(V.get(i).get(j) + "\t");
			System.out.println();
			System.out.println("feature number:"+featureNum);
		}
	}
	
	class ResultType {
		int x; // vector 1
		int y; // vector 2
		double s; // similarity score
		public ResultType(int a, int b, double c) {x=a; y=b; s=c;}
	}
	
	public ArrayList<ResultType> findMatches0(int w, ArrayList<ArrayList<Integer>> I, double t) {
		ArrayList<Double> x = V.get(w);
		HashMap<Integer, Double> A; // empty map from vector id to weight
		ArrayList<ResultType> M;
		A = new HashMap<Integer, Double>();
		M = new ArrayList<ResultType>();
		for (int i=0; i<x.size(); i++) {
			if (x.get(i) > 0) {
				for (int y: I.get(i)) {
					if (A.containsKey(y))
						A.put(y, A.get(y) + x.get(i)*V.get(y).get(i));
					else
						A.put(y, x.get(i)*V.get(y).get(i));
				}
			}
		}
		for (int y: A.keySet())
			if (A.get(y) >= t){
				M.add(new ResultType(w, y, A.get(y)));
			}
		return M;
	}
	// ALL-PAIRS-0(V,t), t: threshold
	public ArrayList<ResultType> all_pairs_0(double t) {
		ArrayList<ArrayList<Integer>> I; // inverted list
		ArrayList<ResultType> O; // result
		I = new ArrayList<ArrayList<Integer>>(featureNum);
		O = new ArrayList<ResultType>();
		for (int i=0; i<featureNum; i++)
			I.add(new ArrayList<Integer>());
		for (int w=0; w<V.size(); w++) {
			ArrayList<Double> x = V.get(w);
			// find matches
			ArrayList<ResultType> temp = findMatches0(w, I, t);
			// add results
			for (ResultType r: temp)
				O.add(r);
			// add x to I
			for (int i=0; i<featureNum; i++)
				if (i < x.size() && x.get(i) > 0)
					I.get(i).add(w);
		}
		return O;
	}
	
	public void printResult(ArrayList<ResultType> O){
		for (int i=0; i<O.size(); i++)
			System.out.println("vector " + (O.get(i).x+1) + " and vector " + (O.get(i).y+1) + " with score " + O.get(i).s);
	}
	public static void main(String args[]){
		AllPairs allpairs = new AllPairs();
		String filename = "data/1000";
		allpairs.n = 1000;
		// read in data
		allpairs.readData(filename);
		
		long startTime=System.currentTimeMillis();
		// normalize data
		allpairs.normalize();
		//allpairs.printV();
		// calculate similar pairs
		ArrayList<ResultType> O = allpairs.all_pairs_0(0.95);
		long endTime=System.currentTimeMillis();
		System.out.println("time: "+(endTime-startTime)/1000.0+" s");    
		
		//allpairs.printResult(O);
	}
}
