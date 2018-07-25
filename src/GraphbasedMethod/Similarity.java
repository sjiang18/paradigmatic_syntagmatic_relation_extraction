package GraphbasedMethod;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import GraphbasedMethod.Graph;
public class Similarity {

	private Graph g;

	public Similarity(String filename, String interval) {
		g = new Graph(filename, interval);
	}

	public Similarity(String filename, String interval, int dis) {
		g = new Graph(filename, interval, dis);
	}
	
	public Similarity(String filename, String interval, int dis, int freqThreshold) {
		g = new Graph(filename, interval, dis, freqThreshold);
	}

	public Similarity(String filename, String interval, int dis, int freqThreshold, int topEdgeThreshold) {
		g = new Graph(filename, interval, dis, freqThreshold, topEdgeThreshold);
	}

	public List<String> getVocab() {
		List<String> vcb = new ArrayList<String>();
		for (int i = 0; i < g.nodes.size(); i++) {
			vcb.add(g.nodes.get(i).word);
		}
		return vcb;
	}

	public boolean inVocab(String word) {
		return (g.getIndex(word) == -1);
	}

	public double getSimilarityForWordPair(String word1, String word2) {
		int index1 = g.getIndex(word1);
		if (index1 < 0){
			if (word1.compareTo(word2) == 0)
				return 1;
			else
				return 0;
		}
		int index2 = g.getIndex(word2);
		if (index2 < 0)
			return 0;
		return g.getParaSim(index1, index2);
	}

	public double getSimilarityForWordPair(int index1, int index2) {
		int wordCount = g.nodeIndex.size();
		if (index1 < 0 || index1 >= wordCount || index2 < 0 || index2 >= wordCount)
			return 0;
		return g.getParaSim(index1, index2);
	}

	public List<Map.Entry<String, Double>> getSimListForWord(String word) {
		int fromIndex = g.getIndex(word);
		if (fromIndex < 0)
			return null;
		int [] toIndex = getTopReachableW2(fromIndex);
		HashMap<String, Double> word_sim = new HashMap<String, Double>();
		for (int i=0; i<toIndex.length; i++)
			word_sim.put(g.nodes.get(toIndex[i]).word, g.getParaSim(fromIndex, toIndex[i]));
		List<Map.Entry<String, Double>> sorted = new ArrayList<Map.Entry<String,Double>>(word_sim.entrySet());
    	Collections.sort(sorted, new Comparator<Map.Entry<String, Double>> () {
    		public int compare (Map.Entry<String, Double> entry1, Map.Entry<String,Double> entry2) {
    			if (entry2.getValue() - entry1.getValue() > 0)
    				return 1;
    			else if (entry2.getValue() - entry1.getValue() < 0)
    				return -1;
    			return 0;
    		}
    	});
    	return sorted;
	}
	
	private int [] getTopReachableW2 (int index1){    	
    	HashSet<Integer> hsOutlink = new HashSet<Integer>();
    	for (Iterator<Map.Entry<Integer,Integer>> itr = g.nodes.get(index1).outlinks.entrySet().iterator(); itr.hasNext(); ) {
    		int u = itr.next().getKey();
    		if (g.choped && !g.nodes.get(u).inlinks.containsKey(index1))
    			continue;
    		for (Iterator<Map.Entry<Integer,Integer>> itr1 = g.nodes.get(u).inlinks.entrySet().iterator(); itr1.hasNext(); ) {
    			int index2 = itr1.next().getKey();
    			if (g.choped && !g.nodes.get(index2).outlinks.containsKey(u))
    				continue;
    			hsOutlink.add(index2);
    		}
    	}
    	HashSet<Integer> hs = new HashSet<Integer>();
    	for (Iterator<Map.Entry<Integer,Integer>> itr = g.nodes.get(index1).inlinks.entrySet().iterator(); itr.hasNext(); ) {
    		int v = itr.next().getKey();
    		if (g.choped && !g.nodes.get(v).outlinks.containsKey(index1))
    			continue;
    		for (Iterator<Map.Entry<Integer,Integer>> itr1 = g.nodes.get(v).outlinks.entrySet().iterator(); itr1.hasNext(); ) {
    			int index2 = itr1.next().getKey();
    			if (g.choped && !g.nodes.get(index2).inlinks.containsKey(v))
    				continue;
    			if (hsOutlink.contains(index2)){
    				hs.add(index2);
    			}
    		}
    	}
    	
    	int [] w2 = new int[hs.size()];
    	int index = 0;
    	for (Iterator<Integer> itr = hs.iterator(); itr.hasNext(); )
    		w2[index++] = itr.next();
    	return w2;
    }

	public List<Map.Entry<String, Double>> getForwardSynSimListForWord(String word, int step) {
		int index = g.getIndex(word);
		if (index < 0)
			return null;
		HashMap<String, Double> word_sim = g.getForwardSynSim(index, step);
		List<Map.Entry<String, Double>> sorted = new ArrayList<Map.Entry<String,Double>>(word_sim.entrySet());
    	Collections.sort(sorted, new Comparator<Map.Entry<String, Double>> () {
    		public int compare (Map.Entry<String, Double> entry1, Map.Entry<String,Double> entry2) {
    			if (entry2.getValue() - entry1.getValue() > 0)
    				return 1;
    			else if (entry2.getValue() - entry1.getValue() < 0)
    				return -1;
    			return 0;
    		}
    	});
    	return sorted;
	}

	public List<Map.Entry<String, Double>> getBackwardSynSimListForWord(String word, int step) {
		int index = g.getIndex(word);
		if (index < 0)
			return null;
		HashMap<String, Double> word_sim = g.getBackwardSynSim(index, step);
		List<Map.Entry<String, Double>> sorted = new ArrayList<Map.Entry<String,Double>>(word_sim.entrySet());
    	Collections.sort(sorted, new Comparator<Map.Entry<String, Double>> () {
    		public int compare (Map.Entry<String, Double> entry1, Map.Entry<String,Double> entry2) {
    			if (entry2.getValue() - entry1.getValue() > 0)
    				return 1;
    			else if (entry2.getValue() - entry1.getValue() < 0)
    				return -1;
    			return 0;
    		}
    	});
    	return sorted;
	}
}
