import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CreateOutput {
	private final static int MAX_VALUE = 200;
	private final static int MAX_PERCENT = 60;
	private final static int PERCENT_DECREASE = 20;
	private final static int PERCENT = 100;
	private final static int NO_PATH = 9999;
	
	public static void main(String[] args) throws IOException {
		final int numberOfExamples = 100;
		final int numberOfFacilities = Integer.parseInt(args[0]);
		final int numberOfClients = Integer.parseInt(args[1]);
		final String fileName = String.format("Projeto_UFL_tptfc_%sx%s_inputs.txt", numberOfFacilities, numberOfClients);
		
		final List<String> lines = new LinkedList<>();
		for (int i = 0; i < numberOfExamples; i++) {
			lines.add(String.format("%s %s", numberOfFacilities, numberOfClients));
			
			// create the facilities price
			genValues(numberOfFacilities, lines);
			// create the clients demand
			genValues(numberOfClients, lines);
			// create the client weights
			lines.addAll(genWeights(numberOfFacilities, numberOfClients));			
			
			lines.add("");
		}
		lines.add("");
		
		new File("." + File.separator + fileName).createNewFile();
		Files.write(Paths.get(fileName), lines);
	}
	
	private static List<String> genWeights(int numberOfFacilities, int numberOfClients) {
		List<String> lines = null;
		
		while (true) {
			lines = new LinkedList<>();
			int[][] metrics = new int[2][numberOfClients];
			for (int j = 0; j < numberOfClients; j++) {
				metrics[0][j] = MAX_PERCENT;
			}
			
			for (int j = 0; j < numberOfFacilities; j++) {
				genValues(numberOfClients, lines, metrics);
			}
			int total = 0;
			for (int j = 0; j < numberOfClients; j++) {
				total += metrics[1][j];
			}
			
			if (total == numberOfClients) {
				break;
			}
		}
		
		return lines;
	}
	
	private static void genValues(int qtd, List<String> lines) {
		final StringBuilder builder = new StringBuilder();
		int value = genValue();
		builder.append(value);
		for (int j = 1; j < qtd; j++) {
			int valueJ = genValue();
			builder.append(" ").append(valueJ);
		}
		lines.add(builder.toString());
	}
	
	private static void genValues(int qtd, List<String> lines, int[][]metrics) {
		final StringBuilder builder = new StringBuilder();
		for (int j = 0; j < qtd; j++) {
			int percent = new Random().nextInt(PERCENT);
			if (percent > PERCENT - metrics[0][j]) {
				metrics[0][j] = metrics[0][j] - PERCENT_DECREASE;
				builder.append(NO_PATH);
			} else {
				int valueJ = genValue();
				builder.append(valueJ);
				metrics[1][j] = 1;
			}
			
			if (j < qtd-1) {
				builder.append(" ");
			}
		}
		lines.add(builder.toString());
	}

	private static int genValue() {
		int value = new Random().nextInt(MAX_VALUE);
		return value > 0 ? value : genValue();
	}
}
