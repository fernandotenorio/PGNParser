import java.util.*;
import java.io.*;


public class Test{

	static final int OPEN_LIMIT = 8;
	static final int MID_LIMIT = OPEN_LIMIT + 20;
	//lims = [0, min(GameFeatures.OPEN_LIMIT, self.plys), min(GameFeatures.MID_LIMIT, self.plys), self.plys]

	public static float mean(int[] x){

		float s = 0;
		for (int i = 0; i < x.length; i++)
			s += x[i];
		return s * 1.0f/x.length;
	}

	public static void main(String[] args) throws Exception {
		
		BufferedReader in = new BufferedReader(new FileReader("data.pgn"));
		int event = -1;
		String[] games = new String[50000];
		
		while (in.ready()) {
		  String line = in.readLine();

		  if (line.contains("Event")) {
		  	event = Integer.parseInt(line.replaceAll("\\D+","")) - 1;		  	
		  }
		  else if (line.startsWith("1. ")){
		  	games[event] = line.trim();
		  }
		  else if ((! line.contains("[")) && (! line.isEmpty())) {
		  	games[event] += " " + line.trim();
		  }

		}
		in.close();
		System.out.println("Done reading PGN file");

		for (int i = 0; i < games.length; i++)
			games[i] = games[i].replaceAll("\\d+\\.\\s*", "").replaceAll("\\s*\\d-\\d", "").replaceAll("\\d\\/\\/\\d", "");

		System.out.println("Done spliting moves");
		//Parse
		PrintWriter writer = new PrintWriter("mobility_control.csv", "UTF-8");
		writer.print("white_central,white_mobility,black_central,black_mobility");
		int i = 0;
		for (String pgn : games){
			writer.println();
			String[] moves = pgn.split(" ");
			PGNStats stats = new PGNStats(moves);
			int[][] data = stats.centralDominance();

			writer.print(mean(data[0]) + "," + mean(data[1]) + "," + mean(data[2]) + "," + mean(data[3]));
			i++;
			if (i % 1000 == 0)
				System.out.format("Done %.1f%s%n", i*100.0f/games.length, "%");
		}
		writer.close();

		System.out.println("end");
	}	//end main

}