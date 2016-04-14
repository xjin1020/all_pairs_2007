package all_pairs_2007;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class AllPairs {
	private ArrayList<ArrayList<Double>> V;
	private ArrayList<ArrayList<Double>> V_prime;
	int n;
	int featureNum;
	ArrayList<Double> maxWeight;
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
	
	public class CountType{
		public int count;
		public int index;
		public CountType(int c, int i){count = c; index = i;}
	}
	
	public class CustomComparator implements Comparator<CountType> {
	    @Override
	    public int compare(CountType o1, CountType o2) {
	        return o2.count - o1.count;
	    }
	}
	
	public void reorder()
	{
		ArrayList<ArrayList<Double>> V_old = new ArrayList<ArrayList<Double>>();
		for (ArrayList<Double> arr: V)
			V_old.add((ArrayList<Double>)arr.clone());
		int [] count = new int[featureNum];
		// find non-zero
		for (ArrayList<Double> arr: V_old)
			for (int i=0; i<featureNum; i++)
				if (i < arr.size() && Math.abs(arr.get(i)) > 1e-9) // non zero entry
					count[i]++;
					
		ArrayList<CountType> countUse = new ArrayList<CountType>();
		for (int i=0; i<featureNum; i++)
			countUse.add(new CountType(count[i], i));
		Collections.sort(countUse, new CustomComparator());
		// create V
		V = new ArrayList<ArrayList<Double>>();
		for (ArrayList<Double> arr: V_old) {
			ArrayList<Double> new_arr = new ArrayList<Double>(featureNum);
			for (int i=0; i<featureNum; i++)
				new_arr.add(0.0);
			for (int i=0; i<featureNum; i++) {
				int k = countUse.get(i).index; // current index
				if (k < arr.size())
					new_arr.set(k, arr.get(k));
			}
			V.add(new_arr);
		}
	}
	
	public ArrayList<ResultType> findMatches1(int w, ArrayList<ArrayList<Integer>> I, double t) {
		ArrayList<Double> x = V.get(w);
		HashMap<Integer, Double> A; // empty map from vector id to weight
		ArrayList<ResultType> M;
		A = new HashMap<Integer, Double>();
		M = new ArrayList<ResultType>();
		for (int i=0; i<featureNum; i++) {
			if (x.get(i) > 0) {
				for (int y: I.get(i)) {
					if (A.containsKey(y))
						A.put(y, A.get(y) + x.get(i)*V.get(y).get(i));
					else
						A.put(y, x.get(i)*V.get(y).get(i));
				}
			}
		}
		for (int y: A.keySet()) {
			double dotProduct = 0.0;
			for (int i=0; i<featureNum; i++)
				dotProduct += x.get(i) * V_prime.get(y).get(i);
			double s = A.get(y) + dotProduct;
			if (s >= t){
				M.add(new ResultType(w, y, s));
			}
		}
		return M;
	}
	
	public ArrayList<ResultType> all_pairs_1(double t) {
		// reorder the dimensions 1..featureNum in V
		reorder();
		// calculate maxWeight
		maxWeight = new ArrayList<Double>(featureNum);
		for (int i=0; i<featureNum; i++)
			maxWeight.add(0.0);
		for (ArrayList<Double> arr: V)
			for (int i=0; i<featureNum; i++)
				if (maxWeight.get(i) < arr.get(i))
					maxWeight.set(i, arr.get(i));
		// algorithm begins
		ArrayList<ArrayList<Integer>> I; // inverted list
		ArrayList<ResultType> O; // result
		I = new ArrayList<ArrayList<Integer>>(featureNum);
		O = new ArrayList<ResultType>();
		for (int i=0; i<featureNum; i++)
			I.add(new ArrayList<Integer>());
		V_prime = new ArrayList<ArrayList<Double>>();
		for (ArrayList<Double> arr: V)
			V_prime.add((ArrayList<Double>)arr.clone());
		
		for (int w=0; w<V.size(); w++) {
			ArrayList<Double> x = V.get(w);
			ArrayList<Double> x_prime = V_prime.get(w);
			// find matches
			ArrayList<ResultType> temp = findMatches1(w, I, t);
			// add results
			for (ResultType r: temp)
				O.add(r);
			// add x to I
			double b = 0.0;
			for (int i=0; i<featureNum; i++)
				if (x.get(i) > 1e-9){
					b += maxWeight.get(i) * x.get(i);
					if (b >= t){
						I.get(i).add(w);
						x_prime.set(i, 0.0);
					}
				}
		}
		return O;
	}
	
	public void calculateMaxweight() {
		for (ArrayList<Double> arr: V) {
			double maxweight=-99999.0;
			arr.add(0.0);
			for (int i=0; i<featureNum; i++) {
				if (maxweight < arr.get(i))
					maxweight = arr.get(i);
			}
			arr.set(featureNum, maxweight); // use the (featureNum+1)'th dimention as maxweight;
		}
	}
	
	public class CustomComparator2 implements Comparator<ArrayList<Double>> {
	    @Override
	    public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
	        if (o2.get(featureNum) - o1.get(featureNum) > 0)
	        		return 1;
	        else if (o2.get(featureNum) - o1.get(featureNum) == 0)
	        		return 0;
	        else
	        		return -1;
	    }
	}
	
	public ArrayList<ResultType> findMatches2(int w, ArrayList<ArrayList<Integer>> I, double t) {
		ArrayList<Double> x = V.get(w);
		HashMap<Integer, Double> A; // empty map from vector id to weight
		ArrayList<ResultType> M;
		A = new HashMap<Integer, Double>();
		M = new ArrayList<ResultType>();
		double remscore = 0.0;
		for (int i=0; i<featureNum; i++)
			remscore += x.get(i)*maxWeight.get(i);
		double minsize = t/x.get(featureNum);
		for (int i=0; i<featureNum; i++) {
			if (x.get(i) > 0) {
				// Iteratively remove (y,w) from the front of I_i while |y|<minsize.
				while(I.get(i).size() > 0){
					int y = I.get(i).get(0);
					int leny=0;
					for (int j=0; j<featureNum; j++)
						if (V.get(y).get(j) > 0)
							leny++;
					if (leny < minsize)
						I.get(i).remove(0);
					else
						break;
				}
				
				for (int y: I.get(i)) {
					double unindexed = 0.0;
					for (int j=0; j<i; j++)
						unindexed += x.get(j) * V_prime.get(y).get(j);
					if ((remscore+unindexed >= t) || (A.containsKey(y) && A.get(y) > 0.0)){ // this is the bug of the paper, missed unindexed part
						if (A.containsKey(y))
							A.put(y, A.get(y) + x.get(i)*V.get(y).get(i));
						else
							A.put(y, x.get(i)*V.get(y).get(i));
					}
				} // endfor y
				remscore = remscore - x.get(i) * maxWeight.get(i);
			} // endif
		}// endfor i
		for (int y: A.keySet()) {
			double c=0.0, maxweighty_prime=0.0;
			int lenx=0, leny_prime=0;
			for (int i=0; i<featureNum; i++){
				if (x.get(i) > 0)
					lenx++;
				if (V_prime.get(y).get(i) > 0) {
					leny_prime++;
					if (maxweighty_prime < V_prime.get(y).get(i))
						maxweighty_prime = V_prime.get(y).get(i);
				}
			}
			c = Math.min(leny_prime, lenx) * x.get(featureNum) * maxweighty_prime;
			if (A.get(y) + c >= t) {
				double dotProduct = 0.0;
				for (int i=0; i<featureNum; i++)
					dotProduct += x.get(i) * V_prime.get(y).get(i);
				double s = A.get(y) + dotProduct;
				if (s >= t){
					M.add(new ResultType(w, y, s));
				}
			} // endif
		}
		return M;
	}
	
	public ArrayList<ResultType> all_pairs_2(double t) {
		// reorder the dimensions 1..featureNum in V
		reorder();
		// calculate maxWeight_i(V)
		maxWeight = new ArrayList<Double>(featureNum);
		for (int i=0; i<featureNum; i++)
			maxWeight.add(0.0);
		for (ArrayList<Double> arr: V)
			for (int i=0; i<featureNum; i++)
				if (maxWeight.get(i) < arr.get(i))
					maxWeight.set(i, arr.get(i));
		// calculate maxweight(x)
		calculateMaxweight();
		// sort V in decreasing order of maxweight(x)
		Collections.sort(V, new CustomComparator2());
		// algorithm begins
		ArrayList<ArrayList<Integer>> I; // inverted list
		ArrayList<ResultType> O; // result
		I = new ArrayList<ArrayList<Integer>>(featureNum);
		O = new ArrayList<ResultType>();
		for (int i=0; i<featureNum; i++)
			I.add(new ArrayList<Integer>());
		V_prime = new ArrayList<ArrayList<Double>>();
		for (ArrayList<Double> arr: V)
			V_prime.add((ArrayList<Double>)arr.clone());
		
		for (int w=0; w<V.size(); w++) {
			ArrayList<Double> x = V.get(w);
			ArrayList<Double> x_prime = V_prime.get(w);
			// find matches
			ArrayList<ResultType> temp = findMatches2(w, I, t);
			// add results
			for (ResultType r: temp)
				O.add(r);
			// add x to I
			double b = 0.0;
			for (int i=0; i<featureNum; i++)
				if (x.get(i) > 1e-9){
					b += Math.min(maxWeight.get(i), x.get(featureNum)) * x.get(i);
					if (b >= t){
						I.get(i).add(w);
						x_prime.set(i, 0.0);
					}
				}
		}
		return O;
	}
	
	//2_new: do not actually remove elements from I_i, use an index I_begin
	public ArrayList<ResultType> findMatches2_new(int w, ArrayList<ArrayList<Integer>> I, double t, ArrayList<Integer> I_begin) {
		ArrayList<Double> x = V.get(w);
		HashMap<Integer, Double> A; // empty map from vector id to weight
		ArrayList<ResultType> M;
		A = new HashMap<Integer, Double>();
		M = new ArrayList<ResultType>();
		double remscore = 0.0;
		for (int i=0; i<featureNum; i++)
			remscore += x.get(i)*maxWeight.get(i);
		double minsize = t/x.get(featureNum);
		for (int i=0; i<featureNum; i++) {
			if (x.get(i) > 0) {
				// Iteratively remove (y,w) from the front of I_i while |y|<minsize.
				while(I.get(i).size() - I_begin.get(i) > 0){
					int index = I_begin.get(i);
					int y = I.get(i).get(index);
					int leny=0;
					for (int j=0; j<featureNum; j++)
						if (V.get(y).get(j) > 0)
							leny++;
					if (leny < minsize)
						I_begin.set(i, index+1);
					else
						break;
				}
				
				for (int k=I_begin.get(i); k<I.get(i).size(); k++) {
					int y = I.get(i).get(k);
					double unindexed = 0.0;
					for (int j=0; j<i; j++)
						unindexed += x.get(j) * V_prime.get(y).get(j);
					if ((remscore+unindexed >= t) || (A.containsKey(y) && A.get(y) > 0.0)){ // this is the bug of the paper, missed unindexed part
						if (A.containsKey(y))
							A.put(y, A.get(y) + x.get(i)*V.get(y).get(i));
						else
							A.put(y, x.get(i)*V.get(y).get(i));
					}
				} // endfor y
				remscore = remscore - x.get(i) * maxWeight.get(i);
			} // endif
		}// endfor i
		for (int y: A.keySet()) {
			double c=0.0, maxweighty_prime=0.0;
			int lenx=0, leny_prime=0;
			for (int i=0; i<featureNum; i++){
				if (x.get(i) > 0)
					lenx++;
				if (V_prime.get(y).get(i) > 0) {
					leny_prime++;
					if (maxweighty_prime < V_prime.get(y).get(i))
						maxweighty_prime = V_prime.get(y).get(i);
				}
			}
			c = Math.min(leny_prime, lenx) * x.get(featureNum) * maxweighty_prime;
			if (A.get(y) + c >= t) {
				double dotProduct = 0.0;
				for (int i=0; i<featureNum; i++)
					dotProduct += x.get(i) * V_prime.get(y).get(i);
				double s = A.get(y) + dotProduct;
				if (s >= t){
					M.add(new ResultType(w, y, s));
				}
			} // endif
		}
		return M;
	}
	
	public ArrayList<ResultType> all_pairs_2_new(double t) {
		// reorder the dimensions 1..featureNum in V
		reorder();
		// calculate maxWeight_i(V)
		maxWeight = new ArrayList<Double>(featureNum);
		for (int i=0; i<featureNum; i++)
			maxWeight.add(0.0);
		for (ArrayList<Double> arr: V)
			for (int i=0; i<featureNum; i++)
				if (maxWeight.get(i) < arr.get(i))
					maxWeight.set(i, arr.get(i));
		// calculate maxweight(x)
		calculateMaxweight();
		// sort V in decreasing order of maxweight(x)
		Collections.sort(V, new CustomComparator2());
		// algorithm begins
		ArrayList<ArrayList<Integer>> I; // inverted list
		ArrayList<ResultType> O; // result
		I = new ArrayList<ArrayList<Integer>>(featureNum);
		O = new ArrayList<ResultType>();
		ArrayList<Integer> I_begin = new ArrayList<Integer>(featureNum);
		for (int i=0; i<featureNum; i++)
			I_begin.add(0);
		for (int i=0; i<featureNum; i++)
			I.add(new ArrayList<Integer>());
		V_prime = new ArrayList<ArrayList<Double>>();
		for (ArrayList<Double> arr: V)
			V_prime.add((ArrayList<Double>)arr.clone());
		
		for (int w=0; w<V.size(); w++) {
			ArrayList<Double> x = V.get(w);
			ArrayList<Double> x_prime = V_prime.get(w);
			// find matches
			//ArrayList<ResultType> temp = findMatches2(w, I, t);
			ArrayList<ResultType> temp = findMatches2_new(w, I, t, I_begin);
			// add results
			for (ResultType r: temp)
				O.add(r);
			// add x to I
			double b = 0.0;
			for (int i=0; i<featureNum; i++)
				if (x.get(i) > 1e-9){
					b += Math.min(maxWeight.get(i), x.get(featureNum)) * x.get(i);
					if (b >= t){
						I.get(i).add(w);
						x_prime.set(i, 0.0);
					}
				}
		}
		return O;
	}
	public void printResult(ArrayList<ResultType> O){
		for (int i=0; i<O.size(); i++)
			System.out.println("vector " + (O.get(i).x+1) + " and vector " + (O.get(i).y+1) + " with score " + O.get(i).s);
		System.out.println("total number: "+O.size());
	}
	public static void main(String args[]){
		AllPairs allpairs = new AllPairs();
		if (args.length != 3)
		{
			System.out.println("Error, need 3 args.");
			return;
		}
		String filename = args[0];
		allpairs.n = Integer.parseInt(args[1]);
		// read in data
		allpairs.readData(filename);
		
		long startTime=System.currentTimeMillis();
		// normalize data
		allpairs.normalize();
		//allpairs.printV();
		//long startTime=System.currentTimeMillis();
		// calculate similar pairs
		//ArrayList<ResultType> O = allpairs.all_pairs_0(Double.parseDouble(args[2]));
		//ArrayList<ResultType> O = allpairs.all_pairs_1(Double.parseDouble(args[2]));
		//ArrayList<ResultType> O = allpairs.all_pairs_2(Double.parseDouble(args[2]));
		ArrayList<ResultType> O = allpairs.all_pairs_2_new(Double.parseDouble(args[2]));
		long endTime=System.currentTimeMillis();
		allpairs.printResult(O);
		System.out.println("time: "+(endTime-startTime)/1000.0+"s");    
	}
}
