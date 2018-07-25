package Main;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Iterator;
import GraphbasedMethod.*;

public class Main {
	static public void getPara(String kg, int gap, int step, int freqThreshold) throws IOException {
		String kg_type = "yago";
		String sim_type = "yago";
		if (kg.equals("wiki")) {
			kg_type = "wiki";
			sim_type = "infobox";
		}
		else if (!kg.equals("yago")) {
			System.out.println("KG type should be yago or wiki");
			System.exit(0);
		}
		String data_filename = "/home/sjiang18/data/office_box_prediction/reviews_nlp_kg_stanford_ner_" + sim_type;
		Similarity sim = new Similarity(data_filename, " ", gap, freqThreshold);
		String vcb_filename = "/home/sjiang18/data/movie_classification/entity_pair_list." + kg_type;
		String sim_filename = "/home/sjiang18/data/movie_classification/para_sim_list." + kg_type + "." + gap;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(vcb_filename)));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sim_filename)));
		String line;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			String [] sp = line.split(" ");
			//double sim1 = sim.getSimilarityForWordPair(sp[0], sp[0]);
			//double sim2 = sim.getSimilarityForWordPair(sp[1], sp[1]);
			//List<Map.Entry<String, Double>> word_sim = sim.getSimListForWord(word);
			double sim0 = sim.getSimilarityForWordPair(sp[0], sp[1]);// / Math.min(sim1, sim2);
			if (sim0 > 0) {
				bw.write(line + " " + sim0);
				bw.newLine();
			}
		}
		br.close();
		bw.close();
	}

	static public void getSyntag(String kg, int gap, int step, int freqThreshold) throws IOException {
		String kg_type = "yago";
		String sim_type = "yago";
		if (kg.equals("wiki")) {
			kg_type = "wiki";
			sim_type = "infobox";
		}
		else if (!kg.equals("yago")) {
			System.out.println("KG type should be yago or wiki");
			System.exit(0);
		}
		String data_filename = "/home/sjiang18/data/office_box_prediction/reviews_nlp_kg_stanford_ner_" + sim_type;
		Similarity simCal  = new Similarity(data_filename, " ", gap, freqThreshold);
		String vcb_filename = "/home/sjiang18/data/movie_classification/entity_list." + kg_type;
		String forward_sim_filename = "/home/sjiang18/data/movie_classification/forward_syn_sim_list." + kg_type + "." + gap;
		String backward_sim_filename = "/home/sjiang18/data/movie_classification/backward_syn_sim_list." + kg_type + "." + gap;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(vcb_filename)));
		BufferedWriter bwForward = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(forward_sim_filename)));
		BufferedWriter bwBackward = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(backward_sim_filename)));
		String line;
		HashSet<String> words = new HashSet<String>();
		while ((line = br.readLine()) != null) {
			String word = line.trim();
			words.add(word);
		}
		br.close();
		Iterator<String> itr = words.iterator();
		while(itr.hasNext()) {
			String word = itr.next();
			if (word.length() <= 1)
				continue;
			List<Map.Entry<String, Double>> forwardSimList = simCal.getForwardSynSimListForWord(word, step);
			if (forwardSimList == null)
				continue;
			for (int i = 0; i < forwardSimList.size(); i++) {
				Map.Entry<String, Double> entry = forwardSimList.get(i);
				String word1 = entry.getKey();
				if (!words.contains(word1))
					continue;
				double sim = entry.getValue();
				if (sim > 0) {
					bwForward.write(word + " " + word1 + " " + sim);
					bwForward.newLine();
				}
			}
			List<Map.Entry<String, Double>> backwardSimList = simCal.getBackwardSynSimListForWord(word, step);
			if (backwardSimList == null)
				continue;
			for (int i = 0; i < backwardSimList.size(); i++) {
				Map.Entry<String, Double> entry = backwardSimList.get(i);
				String word1 = entry.getKey();
				if (!words.contains(word1))
					continue;
				double sim = entry.getValue();
				if (sim > 0) {
					bwBackward.write(word + " " + word1 + " " + sim);
					bwBackward.newLine();
				}
			}
		}
		bwForward.close();
		bwBackward.close();
	}

	static public void main(String [] args) throws IOException {
		int step = 1;
		int freqThreshold = 0;
		getPara("wiki", 1, step, freqThreshold);
		getPara("yago", 1, step, freqThreshold);
		getPara("wiki", 2, step, freqThreshold);
		getPara("yago", 2, step, freqThreshold);
		getPara("wiki", 3, step, freqThreshold);
		getPara("yago", 3, step, freqThreshold);
		getPara("wiki", 4, step, freqThreshold);
		getPara("yago", 4, step, freqThreshold);
		getPara("wiki", 5, step, freqThreshold);
		getPara("yago", 5, step, freqThreshold);
		step = 3;
		getSyntag("wiki", 1, step, freqThreshold);
		getSyntag("yago", 1, step, freqThreshold);
		getSyntag("wiki", 2, step, freqThreshold);
		getSyntag("yago", 2, step, freqThreshold);
		getSyntag("wiki", 3, step, freqThreshold);
		getSyntag("yago", 3, step, freqThreshold);
		getSyntag("wiki", 4, step, freqThreshold);
		getSyntag("yago", 4, step, freqThreshold);
		getSyntag("wiki", 5, step, freqThreshold);
		getSyntag("yago", 5, step, freqThreshold);
	}
}
