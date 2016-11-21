import java.util.Date;

public class Test {	
	public static void main(String[] args) {
		Date date1 = new Date();
		int[][] tt = new int[4][4];
		b(tt, 0, 0);
		Date date2 = new Date();
		System.out.println(date2.getTime()-date1.getTime());
		
		date1 = new Date();
		int[] t = new int[16];
		a(t, 0);
		date2 = new Date();
		System.out.println(date2.getTime()-date1.getTime());
	}
	
	private static void b(int[][] t, int x, int y) {
		if (x < t[0].length && y < t.length) {
			t[y][x] = 1;
//			print(t);
			b(t, x+1, y);
			b(t, x, y+1);
			b(t, x+1, y+1);
			
			t[y][x] = 0;
			b(t, x+1, y);
			b(t, x, y+1);
			b(t, x+1, y+1);
		}
	}
	
	public static void print(int[][] t) {
		for (int i = 0; i < t.length; i++) {
			for (int k = 0; k < t[i].length; k++) {
				System.out.print(t[i][k] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	private static void a(int[] t, int pos) {
		if (pos < t.length) {
			t[pos] = 1;
//			print(t);
			a(t, pos+1);
			
			t[pos] = 0;
			a(t, pos+1);
		}
	}

	public static void print(int[] t) {
		for (int k = 0; k < t.length; k++) {
			System.out.print(t[k] + " ");
		}
		System.out.println();
	}
}
