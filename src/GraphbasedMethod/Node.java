package GraphbasedMethod;

import java.util.*;

public class Node {
	public String word;
    public HashMap<Integer, Integer> outlinks;
    public HashMap<Integer, Integer> inlinks;
    public int inlinkCount;
    public int outlinkCount;
    public Node(String w)
    {
        word = w;
        outlinks = new HashMap<Integer, Integer>();
        inlinks = new HashMap<Integer, Integer>();
    }
    public void getTopLinks(int threshold){    	
    	List<Map.Entry<Integer, Integer>> sortOutlinks = new ArrayList<Map.Entry<Integer,Integer>>(outlinks.entrySet());
    	Collections.sort(sortOutlinks, new Comparator<Map.Entry<Integer, Integer>> (){
    		public int compare (Map.Entry<Integer, Integer> entry1, Map.Entry<Integer,Integer> entry2){
    			return entry2.getValue() - entry1.getValue();
    		}
    	});
    	
    	List<Map.Entry<Integer,Integer>> sortInlinks = new ArrayList<Map.Entry<Integer,Integer>>(inlinks.entrySet());
    	Collections.sort(sortInlinks, new Comparator<Map.Entry<Integer, Integer>>(){
    		public int compare (Map.Entry<Integer, Integer> entry1, Map.Entry<Integer,Integer> entry2){
    			return entry2.getValue() - entry1.getValue();
    		}
    	});
    	inlinkCount = outlinkCount = 0;
    	int inCount = sortInlinks.size();
    	int outCount = sortOutlinks.size();
    	inlinks.clear();
    	outlinks.clear();
    	for (int i=0; i<threshold; i++){
    		if (i < inCount){
    			inlinks.put(sortInlinks.get(i).getKey(), sortInlinks.get(i).getValue());
    			inlinkCount+=sortInlinks.get(i).getValue();
    		}
    		if (i < outCount){
    			outlinks.put(sortOutlinks.get(i).getKey(), sortOutlinks.get(i).getValue());
    			outlinkCount+=sortOutlinks.get(i).getValue();
    		}
    	}
    }
}
