package GraphbasedMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import GraphbasedMethod.Node;

public class Graph {
	List<Node> nodes;
    HashMap<String, Integer> nodeIndex;
	HashMap<String, Integer> wordFreq;
	int distance = 1;
	int freqThreshold = 0;
    boolean choped; //whether the edges have been cut
    
    private void updateEdgeInfor(int index, int preWordIndex, String word, String preWord) {
    	if (index == preWordIndex || word == preWord)
    		return;
        if (!nodes.get(index).inlinks.containsKey(preWordIndex))
            nodes.get(index).inlinks.put(preWordIndex, 1);
        else {
        	int vOld = nodes.get(index).inlinks.get(preWordIndex);
        	nodes.get(index).inlinks.put(preWordIndex, vOld + 1);
        }
        if (!nodes.get(preWordIndex).outlinks.containsKey(index))
            nodes.get(preWordIndex).outlinks.put(index, 1);
        else {
        	int vOld = nodes.get(preWordIndex).outlinks.get(index);
        	nodes.get(preWordIndex).outlinks.put(index, vOld + 1);
        }
        nodes.get(index).inlinkCount++;
        nodes.get(preWordIndex).outlinkCount++;
    }
    
    private void getTopLinks (int threshold) {
    	int count = nodes.size();
    	for (int i=0; i<count; i++)
    		nodes.get(i).getTopLinks(threshold);
    }
    
    private void constructGraphFromDirectory (String folderName, int distance, String delimiter) throws Exception {
    	File dir = new File(folderName); 
        File[] files = dir.listFiles(); 
        if (files == null) 
            return; 
        for (int i = 0; i < files.length; i++) { 
            if (files[i].isDirectory()) { 
                constructGraphFromDirectory(files[i].getAbsolutePath(), distance, delimiter); 
            } else { 
                conGraphForIndividualFile(files[i].getAbsolutePath(), distance, delimiter);                    
            } 
        } 
    }
    
    private void conGraphForIndividualFile(String filename, int distance, String delimiter) throws Exception {
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        String [] preWord = new String [distance];
        String line = null;
        while ((line = br.readLine()) != null) {
        	line = line.trim();
            if (line.isEmpty())            
                continue;
            String [] sp = line.split(delimiter);
            for (int i=0;i<sp.length;i++){
            	if (sp[i].isEmpty())
            		continue;
	            int index;
	            if (!nodeIndex.containsKey(sp[i]))
					continue;
	            else
	                index = nodeIndex.get(sp[i]);
				if (preWord[0] != null) {
	                int preWordIndex = nodeIndex.get(preWord[0]);
	                updateEdgeInfor(index, preWordIndex, line, preWord[0]);
	            }          
	            for (int j=0; j<distance - 1; j++)
	            	preWord[j] = preWord[j+1];
	            preWord[distance - 1] = sp[i];
            }
            for (int i=0; i<distance; i++)
            	preWord[i] = null;
        }
        br.close();
    }

	private void getWordFreq(String filename, String delimiter) {
		try {
			File file = new File(filename);
			if (file.isDirectory()) {
				File[] subFiles = file.listFiles();
				if (subFiles == null)
					return;
				for (int i = 0; i < subFiles.length; i++) {
					getWordFreq(subFiles[i].getAbsolutePath(), delimiter);
				}
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
				String line = null;
				while ((line = br.readLine()) != null) {
					line = line.trim();
            		if (line.isEmpty())            
                		continue;
            		String [] sp = line.split(delimiter);
            		for (int i=0;i<sp.length;i++) {
            			if (sp[i].isEmpty())
            				continue;
	            		int index;
	            		if (!wordFreq.containsKey(sp[i])){
							wordFreq.put(sp[i], 1);
						} else {
							wordFreq.put(sp[i], wordFreq.get(sp[i]) + 1);
						}
					}
				}
				br.close();
			}
		} catch (Exception e) {
        	System.out.println(e.getMessage());
        }  
	}

	public void getVocab() {
		for (Iterator<Map.Entry<String, Integer>> itr = wordFreq.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<String, Integer> entry = itr.next();
			if (entry.getValue() < freqThreshold)
				continue;
			String word = entry.getKey();	
			if (!nodeIndex.containsKey(word)) {
				Node node = new Node(word);
	        	int index = nodes.size();
	            nodes.add(node);
	            nodeIndex.put(word, index);
	            if (index % 100000 == 0)
	            	System.out.println(index + " nodes has been added");
			}
		}
	}

    
    public Graph(String filename, String delimiter) {
		//format: each line is a word, documents <DOC +ID> body </DOC>
        nodes = new ArrayList<Node>();
        nodeIndex = new HashMap<String, Integer>();
		wordFreq = new HashMap<String, Integer>();
        choped = false;
        System.out.println("Constructing the graph");
		getWordFreq(filename, delimiter);
		getVocab();
        try {
        	File file = new File(filename);
        	if (file.isDirectory())
        		constructGraphFromDirectory(filename, this.distance, delimiter);
        	else
        		conGraphForIndividualFile(filename, this.distance, delimiter);
        } catch(Exception e) {
        	System.out.println(e.getMessage());
        }    
        System.out.println("Construction done");
        System.out.println(nodes.size() + " nodes in all");
    } 
    
	public Graph(String filename, String delimiter, int dis) {
		//format: each line is a word, documents <DOC +ID> body </DOC>
        nodes = new ArrayList<Node>();
        nodeIndex = new HashMap<String, Integer>();
		wordFreq = new HashMap<String, Integer>();
        choped = false;
		this.distance = dis;
        System.out.println("Constructing the graph");
		getWordFreq(filename, delimiter);
		getVocab();
        try {
        	File file = new File(filename);
        	if (file.isDirectory())
        		constructGraphFromDirectory(filename, dis, delimiter);
        	else
        		conGraphForIndividualFile(filename, dis, delimiter);
        } catch(Exception e) {
        	System.out.println(e.getMessage());
        }     
        System.out.println("Construction done");
        System.out.println(nodes.size() + " nodes in all");
    } 

	public Graph(String filename, String delimiter, int dis, int freqThreshold) {
		//format: each line is a word, documents <DOC +ID> body </DOC>
        nodes = new ArrayList<Node>();
        nodeIndex = new HashMap<String, Integer>();
		wordFreq = new HashMap<String, Integer>();
        choped = false;
		this.distance = dis;
		this.freqThreshold = freqThreshold;
        System.out.println("Constructing the graph");
		getWordFreq(filename, delimiter);
		getVocab();
        try {
        	File file = new File(filename);
        	if (file.isDirectory())
        		constructGraphFromDirectory(filename, dis, delimiter);
        	else
        		conGraphForIndividualFile(filename, dis, delimiter);
        } catch(Exception e) {
        	System.out.println(e.getMessage());
        }     
        System.out.println("Construction done");
        System.out.println(nodes.size() + " nodes in all");
    }

	public Graph(String filename, String delimiter, int dis, int freqThreshold, int topEdgeThreshold) {
		//format: each line is a word, documents <DOC +ID> body </DOC>
        nodes = new ArrayList<Node>();
        nodeIndex = new HashMap<String, Integer>();
		wordFreq = new HashMap<String, Integer>();
        choped = true;
		this.distance = dis;
		this.freqThreshold = freqThreshold;
        System.out.println("Constructing the graph");
		getWordFreq(filename, delimiter);
		getVocab();
        try {
        	File file = new File(filename);
        	if (file.isDirectory())
        		constructGraphFromDirectory(filename, this.distance, delimiter);
        	else
        		conGraphForIndividualFile(filename, this.distance, delimiter);
        	
        	getTopLinks(topEdgeThreshold);
        } catch(Exception e) {
        	System.out.println(e.getMessage());
        }        
        System.out.println("Construction done");
        System.out.println(nodes.size() + " nodes in all");
    } 
	
	public int getIndex(String word) {
		if (!nodeIndex.containsKey(word))
			return -1;
		return nodeIndex.get(word);
	}
	
	public double getParaSim (int index1, int index2) {
    	if (index1 < 0 || index1 >= nodes.size() || index2 < 0 || index2 >= nodes.size())
    		return 0;
    	ArrayList<Integer> uSet = new ArrayList<Integer>();
    	ArrayList<Integer> vSet = new ArrayList<Integer>();
    	for (Iterator<Map.Entry<Integer, Integer>> itr = nodes.get(index1).inlinks.entrySet().iterator(); itr.hasNext();) {
    		int v = itr.next().getKey();
    		if (choped) {
	    		if (!nodes.get(v).outlinks.containsKey(index1) || !nodes.get(v).outlinks.containsKey(index2))
	    			continue;
    		}
    		if (nodes.get(index2).inlinks.containsKey(v))
    			vSet.add(v);
    	}
    	if (vSet.size() == 0)
    		return 0;
    	for (Iterator<Map.Entry<Integer, Integer>> itr = nodes.get(index1).outlinks.entrySet().iterator(); itr.hasNext();) {
    		int u = itr.next().getKey();
    		if (choped) {
	    		if (!nodes.get(u).inlinks.containsKey(index1) || !nodes.get(u).inlinks.containsKey(index2))
	    			continue;
    		}
    		if (nodes.get(index2).outlinks.containsKey(u))
    			uSet.add(u);
    	}
    	if (uSet.size() == 0)
    		return 0;
    	
    	double sum1 = 0;
    	double sum2 = 0;
    	for (int i=0; i<vSet.size(); i++) {
    		int v = vSet.get(i);
    		double weight1 = Double.valueOf(nodes.get(v).outlinks.get(index1));
    		double weight2 = Double.valueOf(nodes.get(v).outlinks.get(index2));
    		double temp1 = 1;
    		double temp2 = 1;
    		temp1 *= weight1 / Double.valueOf(nodes.get(v).outlinkCount);
    		temp1 *= weight2 / Double.valueOf(nodes.get(index2).inlinkCount);
    		temp2 *= weight1 / Double.valueOf(nodes.get(index1).inlinkCount);    		
    		temp2 *= weight2 / Double.valueOf(nodes.get(v).outlinkCount);
    		sum1 += temp1;
    		sum2 += temp2;
    	}
    	
    	double sum3 = 0;
    	double sum4 = 0;
    	for (int i=0; i<uSet.size(); i++) {
    		int u = uSet.get(i);
    		double weight1 = Double.valueOf(nodes.get(index1).outlinks.get(u));
    		double weight2 = Double.valueOf(nodes.get(index2).outlinks.get(u));
    		double temp3 = 1;
    		double temp4 = 1;
    		temp3 *= weight1 / Double.valueOf(nodes.get(index1).outlinkCount);
    		temp3 *= weight2 / Double.valueOf(nodes.get(u).inlinkCount);
    		temp4 *= weight1 / Double.valueOf(nodes.get(u).inlinkCount);    		
    		temp4 *= weight2 / Double.valueOf(nodes.get(index2).outlinkCount);    		
    		sum3 += temp3;
    		sum4 += temp4;
    	}
    	return sum1*sum2*sum3*sum4 / Math.sqrt((double)(vSet.size() * uSet.size()));
    }

	private HashMap<String, Double> cumNormalizeSim(List<HashMap<Integer, Double>> simList, int step) {
		HashMap<String, Double> sim = new HashMap<String, Double>();
    	for (int i = 1; i <= step; i++) {
    		for (Iterator<Map.Entry<Integer, Double>> itr = simList.get(i).entrySet().iterator(); itr.hasNext();) {
    			Map.Entry<Integer, Double> entry = itr.next();
    			String to = nodes.get(entry.getKey()).word;
    			double s = entry.getValue();
    			if (!sim.containsKey(to))
    				sim.put(to, s);
    			else
    				sim.put(to, sim.get(to) + s);
    		}
    	}
		double simSum = 0;
		for (Iterator<Map.Entry<String, Double>> itr = sim.entrySet().iterator(); itr.hasNext();)
			simSum += itr.next().getValue();
		for (Iterator<String> itr = sim.keySet().iterator(); itr.hasNext();) {
			String to = itr.next();
			sim.put(to, sim.get(to) / simSum);
		}
    	return sim;
	}


	public HashMap<String, Double> getForwardSynSim(int fromIndex, int step) {
    	List<HashMap<Integer, Double>> simList = new ArrayList<HashMap<Integer,Double>>();
    	HashMap<Integer, Double> hashMap0 = new HashMap<Integer, Double>();
    	hashMap0.put(fromIndex, 1.0);
    	simList.add(hashMap0);
    	for (int simIndex = 1; simIndex <= step; simIndex++) {
    		HashMap<Integer, Double> hashMap = new HashMap<Integer, Double>();
    		HashMap<Integer, Integer> thoughNodesNum = new HashMap<Integer, Integer>();
    		for (Iterator<Map.Entry<Integer, Double>> itr = simList.get(simIndex - 1).entrySet().iterator(); itr.hasNext();) {
    			Map.Entry<Integer, Double> entry = itr.next();
    			int middle = entry.getKey();
    			double sim_from_middle = entry.getValue();
    			for (Iterator<Map.Entry<Integer, Integer>> itr1 = nodes.get(middle).outlinks.entrySet().iterator(); itr1.hasNext();) {
					Map.Entry<Integer, Integer> entry1 = itr1.next();
    				int to = entry1.getKey();
    				double outlinks = (double)entry1.getValue();
    				double sim_middle_to = outlinks / (double)nodes.get(middle).outlinkCount;
					sim_middle_to *= outlinks / (double)nodes.get(to).inlinkCount;
    				double sim_from_to = sim_from_middle * sim_middle_to;
    				if (!hashMap.containsKey(to)){
    					hashMap.put(to, sim_from_to);
    					thoughNodesNum.put(to, 1);
    				} else {
    					hashMap.put(to, hashMap.get(to) + sim_from_to);
    					thoughNodesNum.put(to, thoughNodesNum.get(to) + 1);
    				}
    			}
    		}
    		for (Iterator<Map.Entry<Integer, Double>> itr = hashMap.entrySet().iterator(); itr.hasNext();) {
    			Map.Entry<Integer, Double> entry = itr.next();
    			int key = entry.getKey();
    			double value = entry.getValue();
    			hashMap.put(key, value / (double)thoughNodesNum.get(key));
    		}
    		simList.add(hashMap);
    	}
    	return cumNormalizeSim(simList, step);
    }
    
    public HashMap<String, Double> getBackwardSynSim(int fromIndex, int step) {
    	List<HashMap<Integer, Double>> simList = new ArrayList<HashMap<Integer,Double>>();
    	HashMap<Integer, Double> hashMap0 = new HashMap<Integer, Double>();
    	hashMap0.put(fromIndex, 1.0);
    	simList.add(hashMap0);
    	for (int simIndex = 1; simIndex <= step; simIndex++) {
    		HashMap<Integer, Double> hashMap = new HashMap<Integer, Double>();
    		HashMap<Integer, Integer> thoughNodesNum = new HashMap<Integer, Integer>();
    		for (Iterator<Map.Entry<Integer, Double>> itr = simList.get(simIndex - 1).entrySet().iterator(); itr.hasNext();) {
    			Map.Entry<Integer, Double> entry = itr.next();
    			int middle = entry.getKey();
    			double sim_from_middle = entry.getValue();
    			for (Iterator<Map.Entry<Integer, Integer>> itr1 = nodes.get(middle).inlinks.entrySet().iterator(); itr1.hasNext();) {
					Map.Entry<Integer, Integer> entry1 = itr1.next();
    				int to = entry1.getKey();    				
    				double inlinks = (double)entry1.getValue();
    				double sim_middle_to = inlinks / (double)nodes.get(middle).inlinkCount;
    				sim_middle_to *= inlinks / (double)nodes.get(to).outlinkCount;
    				double sim_from_to = sim_from_middle * sim_middle_to;
    				if (!hashMap.containsKey(to)){
    					hashMap.put(to, sim_from_to);
    					thoughNodesNum.put(to, 1);
    				} else {
    					hashMap.put(to, hashMap.get(to) + sim_from_to);
    					thoughNodesNum.put(to, thoughNodesNum.get(to) + 1);
    				}
    			}
    		}
    		for (Iterator<Map.Entry<Integer, Double>> itr = hashMap.entrySet().iterator(); itr.hasNext();) {
    			Map.Entry<Integer, Double> entry = itr.next();
    			int key = entry.getKey();
    			double value = entry.getValue();
    			hashMap.put(key, value / thoughNodesNum.get(key));
    		}
    		simList.add(hashMap);
    	}
    	return cumNormalizeSim(simList, step);
    }
}
