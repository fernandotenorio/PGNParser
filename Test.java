import java.util.*;
import java.io.*;


public class Test{

	static final int OPEN_LIMIT = 8;
	static final int MID_LIMIT = OPEN_LIMIT + 20;

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
		PrintWriter writer = new PrintWriter("doubled_pawns.csv", "UTF-8");
		writer.print("wd_open,wd_mid,wd_end,bd_open,bd_mid,bd_end");
		int i = 0;
		for (String pgn : games){
			writer.println();
			String[] moves = pgn.split(" ");
			PGNStats stats = new PGNStats(moves);
			int[][] data = stats.doubledPawns();

			float wd_open_avg = 0.0f;
			float bd_open_avg = 0.0f;

			float wd_mid_avg = 0.0f;
			float bd_mid_avg = 0.0f;

			float wd_end_avg = 0.0f;
			float bd_end_avg = 0.0f;

			wd_open_avg = mean(Arrays.copyOfRange(data[0], 0, OPEN_LIMIT));
			bd_open_avg = mean(Arrays.copyOfRange(data[1], 0, OPEN_LIMIT));

			if (data[0].length > OPEN_LIMIT){
				wd_mid_avg = mean(Arrays.copyOfRange(data[0], OPEN_LIMIT, MID_LIMIT));
				bd_mid_avg = mean(Arrays.copyOfRange(data[1], OPEN_LIMIT, MID_LIMIT));
			}
			if (data[0].length > MID_LIMIT){
				wd_end_avg = mean(Arrays.copyOfRange(data[0], MID_LIMIT, data[0].length));
				bd_end_avg = mean(Arrays.copyOfRange(data[1], MID_LIMIT, data[1].length));
			}
			
			writer.print(wd_open_avg + "," + wd_mid_avg + "," + wd_end_avg + "," + bd_open_avg + "," + bd_mid_avg + "," + bd_end_avg);
			i++;
			if (i % 5000 == 0)
				System.out.format("Done %.1f%s%n", i*100.0f/games.length, "%");
		}
		writer.close();

		System.out.println("end");
	}	//end main

}