package psl.memento.pervasive.ontology;

import java.io.*;
import java.util.*;

public class OntologyTest {
	public static void main(String[] args) {
		try {
			String choice = args[0];

			Properties settings = new Properties();
			settings.load(new FileInputStream("config.ini"));

			Ontology ontology = new Ontology(settings);
			ontology.connect();
			ontology.loadOntology("test_termExtraction");

			///////////////////
			// Delete all node
			if (choice.equals("clear")) {
				Iterator itr2 = ontology.getNodes();
				while (itr2.hasNext()) {
					OntologyNode node = (OntologyNode) itr2.next();
					ontology.removeNode(node);
				}
			}

			/////////////////////
			// Perform extraction

			if (choice.equals("extract")) {
				try {
					OntologyTermExtractor tExtractor = new OntologyTermExtractor(settings, ontology);
					OntologyRelationsExtractor rExtractor = new OntologyRelationsExtractor(settings, ontology);

					File file = new File(args[1]);
					File[] files = new File[0];

					if (file.isDirectory()) {
						files = file.listFiles();
						for (int i=0; i < files.length; i++) {
							files[i].setReadOnly();
							tExtractor.addDocument(files[i].getAbsolutePath());
						}
					} else {
						tExtractor.addDocument(args[1]);
					}

					tExtractor.performExtraction();
					tExtractor.addToOntology();
					tExtractor.clearCorpus();

					//Relations extraction

					if (file.isDirectory()) {
						for (int i=0; i < files.length; i++) {
							rExtractor.addDocument(files[i].getAbsolutePath());
							rExtractor.performExtraction();
							rExtractor.addToOntology();
							rExtractor.clearCorpus();
						}
					} else {
						tExtractor.addDocument(args[1]);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			///////////
			// Testing

			if (choice.equals("print_all")) {
				System.out.println("==================");
				System.out.println("Nodes");
				System.out.println("==================");

				Iterator itr = ontology.getNodes();
				while (itr.hasNext()) {
					OntologyNode node = (OntologyNode) itr.next();
					System.out.println(node.getLabel());
					System.out.println("---------------");
					System.out.println("ConnectedNodes:");

					Iterator cnnItr = node.getConnectedNodes();
					while (cnnItr.hasNext()) {
						OntologyNode connected = (OntologyNode) cnnItr.next();
						System.out.println("\t"+connected.getLabel()+"\t"+node.getWeight(connected));
					}

					System.out.println("---------------");
				}
			}

			if (choice.equals("connect")) {
				System.out.println("=========================");
				System.out.println("Testing Connection");
				System.out.println("=========================");
				System.out.println("Premise:\t" + args[1]);
				System.out.println("Conclusion:\t" + args[2]);
				System.out.println("Depth:\t" + args[3]);

				OntologyInquirer inq = new OntologyInquirer(ontology);
				List list = inq.areRelated(args[1], args[2], Integer.parseInt(args[3]));

				if (list == null) {
					System.out.println("Terms Not Related");
				}
				else {
					Object[] objs = list.toArray();
					OntologyNode start = (OntologyNode)objs[0];

					System.out.print("Path: " + start.getLabel());
					for (int i=1; i < objs.length; i++) {
						OntologyNode next = (OntologyNode)objs[i];
						long weight = start.getWeight(next);

						System.out.print(" --"+weight+"--> "+next.getLabel());
						start = next;
					}

					System.out.println("");
				}
			}

			System.out.println("=========================");
			System.out.println("CACHE STATISTICS");
			System.out.println("=========================");

			long misses = ontology.getCacheMisses();
			long hits = ontology.getCacheHits();

			double dMisses = (double)misses;
			double dHits = (double)hits;
			double total = dHits + dMisses;
			double percentage = (dHits/total) * (double)100;

			System.out.println("Cache Hits: " + hits);
			System.out.println("Cache Misses: " + misses);
			System.out.println("Cache Hit Percentage: " + (int)percentage +"%");
			System.out.println("=========================");
			System.out.println("METHOD TIME STATISTICS");
			System.out.println("=========================");
			ontology.printStatistics();

			///////////

			ontology.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}