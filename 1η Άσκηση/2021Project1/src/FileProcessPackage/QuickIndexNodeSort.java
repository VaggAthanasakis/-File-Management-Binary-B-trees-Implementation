package FileProcessPackage;

import NodePackage.IndexNode;

/*This Class Sorts A Array Of IndexNodes By Key*/
public class QuickIndexNodeSort {

	 int partition(IndexNode arr[], int low, int high)
	    {
	        int pivot = arr[high].getKey(); 
	        int i = (low-1); // index of smaller element
	        for (int j=low; j<high; j++)
	        {
	            // If current element is smaller than or
	            // equal to pivot
	            if (arr[j].getKey() <= pivot)
	            {
	                i++;
	  
	                // swap arr[i] and arr[j]
	                IndexNode temp = arr[i];
	                arr[i] = arr[j];
	                arr[j] = temp;
	            }
	        }
	  
	        // swap arr[i+1] and arr[high] (or pivot)
	        IndexNode temp = arr[i+1];
	        arr[i+1] = arr[high];
	        arr[high] = temp;
	  
	        return i+1;
	    }
	
 public	void sort(IndexNode arr[], int low, int high)
	    {
	        if (low < high)
	        {
	            /* pi is partitioning index, arr[pi] is 
	              now at right place */
	            int pi = partition(arr, low, high);
	  
	            // Recursively sort elements before
	            // partition and after partition
	            sort(arr, low, pi-1);
	            sort(arr, pi+1, high);
	        }
	    }
	
	
}
