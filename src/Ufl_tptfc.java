import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ufl.UflBruteForce;
import ufl.UflHeuristic;
import ufl.UflHeuristicOptimal;
import ufl.UflResult;

public class Ufl_tptfc {
	public static void main(String[] args) throws IOException {
		int seek = 1;
		int[] facilities = null;
		int[] consumers = null;
		int[][] distance = null;
		
		double totalBruteForce = 0;
		double totalHeuristic = 0;
		double totalHeuristicOptimal = 0;
		
		int totalExecution = 0;
		
		final int numberOfFacilities = Integer.parseInt(args[0]);
		final int numberOfClients = Integer.parseInt(args[1]);
		final String fileNameIn = String.format("Projeto_UFL_tptfc_%sx%s_inputs.txt", numberOfFacilities, numberOfClients);
		final String fileNameOut = String.format("Projeto_UFL_tptfc_%sx%s_out.txt", numberOfFacilities, numberOfClients);
		
		List<String> list = new ArrayList<>();
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileNameIn))) {

			//br returns as stream and convert it into a List
			list = br.lines().collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<String> lines = new LinkedList<>();
		
		for (String line : list) {
			// new graph
			if (line.isEmpty()) {
				long date1 = new Date().getTime();
				totalExecution++;
				lines.add("-----------");
				final UflResult resultBruteForce = new UflBruteForce(facilities, consumers, distance).exec();
				final UflResult resultHeuristic = new UflHeuristic(facilities, consumers, distance).exec();
				final UflResult resultHeuristicOptimal = new UflHeuristicOptimal(facilities, consumers, distance).exec();
				
				
				lines.add("brute force:");
				lines.add(resultBruteForce.toString());
				lines.add("heuristic:");
				lines.add(resultHeuristic.toString());
				lines.add("heuristic opt:");
				lines.add(resultHeuristicOptimal.toString());
				
				totalBruteForce += resultBruteForce.getScore();
				totalHeuristic += resultHeuristic.getScore();
				totalHeuristicOptimal += resultHeuristicOptimal.getScore();
				
				seek = 1;
				long date2 = new Date().getTime();
				System.out.println(totalExecution);
				System.out.println(date2-date1);
				continue;
			}
			
			// read header number of facilities / consumers
			if (seek == 1) {
				String[] header = line.trim().split(" ");
				distance = new int[Integer.parseInt(header[0])][];
			} else if (seek == 2) {
				facilities = buildArray(line);
			} else if (seek == 3) {
				consumers = buildArray(line);
			} else {
				distance[seek - 4] = buildArray(line);
			}
			
			seek++;
		}
		
		lines.add("########################################################");
		lines.add("brute force:  " + (totalBruteForce));
		lines.add("heuristic:    " + (totalHeuristic));
		lines.add("heuristic opt:" + (totalHeuristicOptimal));
		lines.add("");
		lines.add("media brute force:  " + (totalBruteForce/totalExecution));
		lines.add("media heuristic:    " + (totalHeuristic/totalExecution));
		lines.add("media heuristic opt:" + (totalHeuristicOptimal/totalExecution));
		lines.add("");
		lines.add("score heuristic:    " + (totalHeuristic/totalBruteForce));
		lines.add("score heuristic opt:" + (totalHeuristicOptimal/totalBruteForce));
		
		new File("." + File.separator + fileNameOut).createNewFile();
		Files.write(Paths.get(fileNameOut), lines);
	}

	private static int[] buildArray(String line) {
		final String[] strValues = line.trim().split(" ");
		final int[] array = new int[strValues.length];
		
		for (int i = 0; i < strValues.length; i++) {
			final String strValue = strValues[i];
			array[i] = Integer.parseInt(strValue);
		}
		
		return array;
	} 
}
