public class TicTacToe {
	public static void main(String[] args) {
		draw();

	}

	public static void draw() {
		 final int ROW = 3;
		 final int COL = 3;
		
		char[] symbols = {'1','2','3','4','5','6','7','8','9'};
		int count = 0;
		
		for(int row = 0; row < ROW; ++row) {
			for(int col = 0; col < COL; ++col) {
				if(col < COL-1) { 
					System.out.print(symbols[count] + "|");
				} else { 
					System.out.print(symbols[count]);
				}
				count++;
			}

			if(row < ROW-1) {
				System.out.println("\n------");
			}
		}
	
		System.out.println();
	}
}

