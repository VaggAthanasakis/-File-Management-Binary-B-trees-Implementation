package Search;

import Counter.MoultiCounter;

public class BinarySearch {

	private int data[];

	/**
	 * Constructor. Given newData must be sorted!
	 * @param newData
	 */
	public BinarySearch(int newData[]) {
		this.data = newData;
	}
	/**
	 * Searches data array for given key. Returns the key if found, otherwise Integer.MIN_VALUE
	 * @param key
	 * @return
	 */
	public int search(int key) {
		if (MoultiCounter.increaseCounter(9) && data == null) {
			return Integer.MIN_VALUE;
		}
		return doSearch(0, data.length - 1, key);
	}	
	
	/* Method That Searches A Range Of Values Into An Array(Starting From Min Till Max)
	 * At First It Finds The Position Of The min And max Into The Array 
	 * Then, Since The Array Is Sorted It Prints All The In Between Values*/
	public void searchRange(int min, int max) {
		if (MoultiCounter.increaseCounter(9) && max < min) {
			System.out.println("SearchRange: Wrong Input!");
			return;
		}
		int minPos=doSearch(0,data.length-1,min);
		MoultiCounter.increaseCounter(9);
		int tmpMin=min;
		MoultiCounter.increaseCounter(9);
		while(MoultiCounter.increaseCounter(9) && minPos==Integer.MIN_VALUE) {  // Finding The Position Of The min Value By Searching It Till It Found At the Array
			min+=1;																// If Not Found, It Takes As min The Next Bigger Value Than min That Exists 
			minPos=doSearch(0,data.length-1,min);
			MoultiCounter.increaseCounter(9,2);
		}
		int tmpMax=max;
		MoultiCounter.increaseCounter(9);
		int maxPos=doSearch(minPos,data.length-1,max);                          // Since The data Array Is Sorted, maxPos > minPos. So We Search maxPos At Positions minPos+1 Till The End Of The Array
		MoultiCounter.increaseCounter(9);
		while(MoultiCounter.increaseCounter(9) && maxPos==Integer.MIN_VALUE) {  // Finding The Position Of The Max Value By Searching It Till It Found At the Array
			max-=1;																// If Not Found, It Takes As max The Next Smaller Value Than max That Exists 
			maxPos=doSearch(minPos,data.length-1,max);
			MoultiCounter.increaseCounter(9,2);
		}
		System.out.println("Printing Range Between "+tmpMin+" And "+tmpMax);
		MoultiCounter.increaseCounter(9);
		for(int k=minPos; (MoultiCounter.increaseCounter(9)) && (k<=maxPos); k++) {
			MoultiCounter.increaseCounter(9);
			System.out.println(data[k]);
		}	
		System.out.println("Max "+max);
	}
	
	/**
	 * Searches data array for given key. Returns the key if found, otherwise Integer.MIN_VALUE
	 * @param leftIndex
	 * @param rightIndex
	 * @param key
	 * @return key if found or Integer.MIN_VALUE otherwise
	 */
    private int doSearch(int leftIndex, int rightIndex, int key) 
    { 
        if (MoultiCounter.increaseCounter(9) && rightIndex >= leftIndex) { 
            int mid = leftIndex + (rightIndex - leftIndex) / 2; 
            MoultiCounter.increaseCounter(9);
  
            // If the element is present at the 
            // middle itself 
            if (MoultiCounter.increaseCounter(9) && data[mid] == key) {
            	//System.out.println(key);
                return mid; }
  
            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            if (MoultiCounter.increaseCounter(9) && data[mid] > key) 
                return doSearch(leftIndex, mid - 1, key); 
  
            // Else the element can only be present 
            // in right subarray 
            return doSearch(mid + 1, rightIndex, key); 
        } 
  
        // We reach here when element is not present in array. 
        // We return Integer.MIN_VALUE in this case, so the data array can not contain this value!
      //  System.out.println("Not Found");
        return Integer.MIN_VALUE; 
    } 
	
}
