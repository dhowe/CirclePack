package jcp;

import java.util.ArrayList;

import jcp.util.Rect;


public class Mer {

	public static char occuppied = '#', vacant = '-', mark = 'A';

	public static void main(String[] args) {
		int[][] r = {{ 2,3,2,2 }, {5,5,2,6}};
		r = MER(7, 11, r);
		for (int j = 0; j < r.length; j++) {
			System.out.println("width = " + r[j][3] + ", height = " + r[j][2]+
					", area = "+ (r[j][3]*r[j][2]) + " at "+r[j][1]+","+ r[j][1]);	
		}
	}
	
	public static Rect[] merToRects(int[][] mer) {
		Rect[] r = new Rect[mer.length];
		for (int i = 0; i < mer.length; i++) {
			r[i] = new Rect();
			r[i].x =  mer[i][1];
			r[i].y =  mer[i][0];
			r[i].width =  mer[i][3];
			r[i].height =  mer[i][2];
		}
		return r;
	}
	
	public static int[][] rectsToMer(Rect[] f, int tx, int ty) {

		int[][] mer = new int[f.length][];
		for (int i = 0; i < f.length; i++) {
			mer[i] = new int[4];
			mer[i][0] = f[i].y + ty;
			mer[i][1] = f[i].x + tx;
			mer[i][2] = f[i].height;
			mer[i][3] = f[i].width;
		}
		return mer;
	}
	
	public static int[][] rectsToMer(Rect[] r) {
		return rectsToMer(r, 0, 0);
	}

	public static int[][] MER(int rows, int cols, int[][] r) {
		return MER(rows, cols, r, false);
	}
	
	/*
	 * Code adapted from Amarghosh
	 * http://stackoverflow.com/questions/1859719/what-is-an-algorithm-to-return-free-space-in-blocks-of-largest-possible-rectangle
	 */
	public static int[][] MER(int rows, int cols, int[][] r, boolean print) {

		ArrayList<int[]> results = new ArrayList<int[]>(); 
		char[][] matrix = new char[rows][cols];
		setRect(matrix, vacant, 0, 0, rows, cols);

		for (int i = 0; i < r.length; i++) {
			setRect(matrix, occuppied, r[i][0], r[i][1], r[i][2], r[i][3]);
		}

		if (print) print(matrix);
		for (int i = 0; i < rows; i++) {
			int colstart = 0;
			while ((colstart = nextEmptyCol(matrix[i], colstart)) != -1) {
				int width = 0;
				for (int j = colstart; j < cols; j++) {
					if (matrix[i][j] == vacant)
						width++;
					else
						break;
				}
				if (width == 0)
					continue;
				int height = 1;
				outer: for (; height + i < rows; height++)
					for (int n = 0; n < width; n++) {
						if (matrix[i + height][colstart + n] == occuppied)
							break outer;
					}
				//System.out.println("width = " + width + ", height = " + height+", area = "+ (width*height) + " at "+i+","+ colstart);
				setRect(matrix, mark, i, colstart, height, width);
				int[] result = { i, colstart, height, width };
				results.add(result);
				if (print) print(matrix);
				mark++;
			}
		}
		return results.toArray(new int[0][]);
	}

	public static void setRect(char[][] matrix, char c, int startrow, int startcol,
			int numrows, int numcols) 
	{
		for (int i = 0; i < numrows; i++)
			for (int j = 0; j < numcols; j++)
				matrix[startrow + i][startcol + j] = c;
	}

	public static void print(char[][] matrix) {
		int rows = matrix.length;
		int cols = matrix[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++)
				System.out.print(matrix[i][j] + " ");
			System.out.println();
		}
		for (int i = 0; i < cols; i++)
			System.out.print("==");
		System.out.println();
	}

	public static int nextEmptyCol(char[] row, int start) {
		for (int i = start; i < row.length; i++)
			if (row[i] == vacant)
				return i;
		return -1;
	}
}
