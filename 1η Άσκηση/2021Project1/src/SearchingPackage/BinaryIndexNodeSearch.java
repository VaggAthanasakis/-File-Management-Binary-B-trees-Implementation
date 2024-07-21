package SearchingPackage;

import NodePackage.IndexNode;
/*This Class Searches A Key At An Array Of Index Nodes Using BinarySearch */
public class BinaryIndexNodeSearch  {

	private IndexNode data[];

	/**
	 * Constructor. Given newData must be sorted!
	 * @param newData
	 */
	public BinaryIndexNodeSearch(IndexNode newData[]) {
		this.data = newData;
	}
	
	/**
	 * Given newData must be sorted!
	 * @param newData
	 */
	public void setData(IndexNode newData[]) {
		this.data = newData;
	}
	
	
	public IndexNode search(int key) {
		if (data == null) {
			return null;
		}
		return doSearch(0, data.length - 1, key);
	}
	

	/**
	 * <p>
	 * Helper method that searches in data array, from index leftIndex to index rightIndex. 
	 * Uses recursion
	 * </p>
	 * 
	 * @param leftIndex   The left index of the data array
	 * @param rightIndex  The right index of the data array
	 * @param key         The key to search
	 * @return            The node containing the key if found
	 */
	private IndexNode doSearch(int leftIndex, int rightIndex, int key) {     
	
        if (rightIndex >= leftIndex) { 
            int mid = leftIndex + (rightIndex - leftIndex) / 2; 
  
            // If the element is present at the 
            // middle itself 
            if (data[mid].getKey() == key) 
                return data[mid]; 
  
            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            
            if (data[mid].getKey() > key) 
                return doSearch(leftIndex, mid - 1, key); 
  
            // Else the element can only be present 
            // in right subarray 
            return doSearch(mid + 1, rightIndex, key); 
        } 
  
        // We reach here when element is not present 
        // in array 
        return null; 
    }

}
