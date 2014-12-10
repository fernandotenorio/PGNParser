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
		PrintWriter writer = new PrintWriter("piece_mobility.csv", "UTF-8");
		writer.print("wc_open,wc_mid,wc_end,wm_open,wm_mid,wm_end,bc_open,bc_mid,bc_end,bm_open,bm_mid,bm_end");
		int i = 0;
		for (String pgn : games){
			writer.println();
			String[] moves = pgn.split(" ");
			PGNStats stats = new PGNStats(moves);
			//int[][] data = stats.bishopMov();
			int[][] data = stats.pieceMobility();

			float wc_open_avg = 0.0f;
			float wm_open_avg = 0.0f;
			float bc_open_avg = 0.0f;
			float bm_open_avg = 0.0f;

			float wc_mid_avg = 0.0f;
			float wm_mid_avg = 0.0f;
			float bc_mid_avg = 0.0f;
			float bm_mid_avg = 0.0f;

			float wc_end_avg = 0.0f;
			float wm_end_avg = 0.0f;
			float bc_end_avg = 0.0f;
			float bm_end_avg = 0.0f;

			wc_open_avg = mean(Arrays.copyOfRange(data[0], 0, OPEN_LIMIT));
			wm_open_avg = mean(Arrays.copyOfRange(data[1], 0, OPEN_LIMIT));
			bc_open_avg = mean(Arrays.copyOfRange(data[2], 0, OPEN_LIMIT));
			bm_open_avg = mean(Arrays.copyOfRange(data[3], 0, OPEN_LIMIT));

			if (data[0].length > OPEN_LIMIT){
				wc_mid_avg = mean(Arrays.copyOfRange(data[0], OPEN_LIMIT, MID_LIMIT));
				wm_mid_avg = mean(Arrays.copyOfRange(data[1], OPEN_LIMIT, MID_LIMIT));
				bc_mid_avg = mean(Arrays.copyOfRange(data[2], OPEN_LIMIT, MID_LIMIT));
				bm_mid_avg = mean(Arrays.copyOfRange(data[3], OPEN_LIMIT, MID_LIMIT));
			}
			if (data[0].length > MID_LIMIT){
				wc_end_avg = mean(Arrays.copyOfRange(data[0], MID_LIMIT, data[0].length));
				wm_end_avg = mean(Arrays.copyOfRange(data[1], MID_LIMIT, data[0].length));
				bc_end_avg = mean(Arrays.copyOfRange(data[2], MID_LIMIT, data[0].length));
				bm_end_avg = mean(Arrays.copyOfRange(data[3], MID_LIMIT, data[0].length));
			}
			
			writer.print(wc_open_avg + "," + wc_mid_avg + "," + wc_end_avg + "," + wm_open_avg + "," + wm_mid_avg + "," + wm_end_avg + "," +
				bc_open_avg + "," + bc_mid_avg + "," + bc_end_avg + "," + bm_open_avg + "," + bm_mid_avg + "," + bm_end_avg);
			i++;
			if (i % 5000 == 0)
				System.out.format("Done %.1f%s%n", i*100.0f/games.length, "%");
		}
		writer.close();

		System.out.println("end");
	}	//end main

}