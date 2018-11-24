class SelectionSort {
	static public void main(String[] args) {
		int [] myArray = {5,3,1,0,2,4};
		
		for(int i : myArray) {
			System.out.print(myArray[i] + " ");
		}
		
		selSort(myArray);
		System.out.println();
		
		for(int i : myArray) {
			System.out.print(myArray[i] + "  ");
		}
	}

	public static void selSort(int[] array) {
		int rh, i_max, i;
		for(rh = array.length-1; rh>0; rh--) {
			i_max = 0;
			for(i=1; i<=rh; i++) {
				if(array[i] > array[i_max]) { i_max = i;}
			}
			int temp = array[rh]; array[rh] = array[i_max]; array[i_max] = temp;
		}
	}

}
