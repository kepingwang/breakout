package breakout;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sprites.Brick;

public class Level {
	
	private int nBricksHorizontal = -1;
	private int nBricksVertical = 15;
	private List<int[]> list;
	
	public Level(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			list = new ArrayList<int[]>();
			while ((line = br.readLine()) != null) {
				if (nBricksHorizontal == -1) {
					nBricksHorizontal = Integer.parseInt(line.trim()); 
				} else {
					if (line.trim().isEmpty()) { continue; }
					String[] s = line.trim().split("\\s+");
					int[] arr = new int[s.length];
					for (int i = 0; i < arr.length; i++) {
						arr[i] = Integer.parseInt(s[i]);
					}
					list.add(arr);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void layoutBricks(LinkedList<Brick> bricks) {
		double w = World.width / nBricksHorizontal;
		double h = World.height / nBricksVertical;
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).length; j++) {
				if (list.get(i)[j] != 0) {
					bricks.add(new Brick(w*j+w/2, h*i+h/2, w, h, list.get(i)[j]));
				}
			}
		}
	}
	
}
