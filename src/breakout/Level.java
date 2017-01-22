package breakout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sprites.Brick;

/**
 * A wrapper class to store the levels configuration.
 * @author keping
 *
 */
public class Level {
	
	private int nBricksHorizontal = -1;
	private int nBricksVertical = 15;
	private List<int[][]> list;
	
	public Level(String fileName) {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			list = new ArrayList<int[][]>();
			while ((line = br.readLine()) != null) {
				if (nBricksHorizontal == -1) {
					nBricksHorizontal = Integer.parseInt(line.trim()); 
				} else {
					if (line.trim().isEmpty()) { continue; }
					String[] s = line.trim().split("\\s+");
					int[][] arr = new int[s.length][2];
					for (int i = 0; i < arr.length; i++) {
						if (s[i].indexOf('p') == -1) {
							arr[i][0] = Integer.parseInt(s[i]);
							arr[i][1] = -1;
						} else {
							arr[i][0] = Integer.parseInt(s[i].split("p")[0]);
							arr[i][1] = Integer.parseInt(s[i].split("p")[1]);
						}
					}
					list.add(arr);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void layoutBricks(List<Brick> bricks, GameWorld world) {
		double w = GameApp.WIDTH / nBricksHorizontal;
		double h = GameApp.HEIGHT / nBricksVertical;
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).length; j++) {
				if (list.get(i)[j][0] > 0) {
					int lives = list.get(i)[j][0];
					int powerUp = list.get(i)[j][1];
					bricks.add(new Brick(world, w*j+w/2, h*i+h/2, w, h, lives, powerUp));
				}
			}
		}
	}
	
}
