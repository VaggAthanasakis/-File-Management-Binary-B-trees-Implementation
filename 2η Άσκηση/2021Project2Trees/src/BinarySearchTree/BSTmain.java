package BinarySearchTree;

import Counter.MoultiCounter;
import Search.BinarySearch;

public class BSTmain {

	public static void main(String[] args) {
		int N= 100000;            		  //Number Of Keys
		int START_INT=1;
		int END_INT=1000000;
		int NO_OF_ELEMENTS=N;
		int NO_OF_SEARCHES=100;
		int[] SortedArray =new int[N];
	
		BST bst= new BST(N);
		ThreadedBST TBST= new ThreadedBST(N);
		java.util.Random randomGenerator = new java.util.Random();
		
		/* Insertion Of 10^5 Keys At Binary Search Tree And To The Threaded Binary Search Tree */
		int[] randomInts = randomGenerator.ints(START_INT, END_INT).distinct().limit(NO_OF_ELEMENTS).toArray();
		for(int i=0; i<NO_OF_ELEMENTS; i++) {
			bst.insert(randomInts[i]);
			TBST.insert(randomInts[i]);
		}
		int Counter1=MoultiCounter.getCount(1)/N;
		int Counter2=MoultiCounter.getCount(2)/N;
	
		/* Performing 100 Searches Of Random Keys For Each One Of The Trees And To The Sorted Array*/
		int[] randomInts1 = randomGenerator.ints(START_INT, END_INT).distinct().limit(NO_OF_SEARCHES).toArray();
		TBST.inorder(SortedArray);
		BinarySearch bs=new BinarySearch(SortedArray);
		
		for(int k=0; k<NO_OF_SEARCHES; k++) {
			bst.find(randomInts1[k]);
			TBST.find(randomInts1[k]);
			bs.search(randomInts1[k]);
		}
		
		int Counter3=MoultiCounter.getCount(3)/NO_OF_SEARCHES;
		int Counter4=MoultiCounter.getCount(4)/NO_OF_SEARCHES;
	    int Counter9=MoultiCounter.getCount(9)/NO_OF_SEARCHES;
		
		/* Performing 100 RangeSearches Of Range (K=100) At Each One Of The Trees And The Sorted Array */
		int[] randomInts2 = randomGenerator.ints(START_INT, END_INT).distinct().limit(NO_OF_SEARCHES).toArray();
		int range1=100;
		
		for(int j=0; j<NO_OF_SEARCHES; j++) { 
			bst.printRange(randomInts2[j],randomInts2[j]+range1);
			bs.searchRange(randomInts2[j],randomInts2[j]+range1);
		}
	    MoultiCounter.resetCounter(4);
	    
		for(int j=0; j<NO_OF_SEARCHES; j++) {
			TBST.printRange(randomInts2[j],randomInts2[j]+range1);
		}
		int Counter5=MoultiCounter.getCount(5)/NO_OF_SEARCHES;
		int Counter6=MoultiCounter.getCount(4)/NO_OF_SEARCHES;
		int Counter10=MoultiCounter.getCount(9)/NO_OF_SEARCHES;
		
		/* Performing 100 RangeSearches Of Range (K=1000) At Each One Of The Trees And The Sorted Array */
		int[] randomInts3 = randomGenerator.ints(START_INT, END_INT).distinct().limit(NO_OF_SEARCHES).toArray();
		int range2=1000;
		MoultiCounter.resetCounter(4);
		MoultiCounter.resetCounter(5);
		MoultiCounter.resetCounter(9);
		
		for(int j=0; j<NO_OF_SEARCHES; j++) {
			bst.printRange(randomInts3[j],randomInts3[j]+range2);
			TBST.printRange(randomInts3[j],randomInts3[j]+range2);	
			bs.searchRange(randomInts3[j], randomInts3[j]+range2);
		}
		
		System.out.println("Counter 1(Insertion At BST): "+Counter1);
		System.out.println("Counter 2(Insertion At Threaded BST): "+Counter2);
		System.out.println();
		
		System.out.println("Counter 3(Random Key Search BST): "+Counter3);
		System.out.println("Counter 4(Random Key Search  Threaded BST): "+Counter4);
		System.out.println("Counter 9(Binary Random Search): "+Counter9);
		System.out.println();
		
		System.out.println("Counter 5(Random Range(100) Search BST): "+Counter5);
		System.out.println("Counter 6(Random Range(100) Search TBST): "+Counter6);
		System.out.println("Counter 10(Binary Random Range(100) Search): "+Counter10);
		System.out.println();
		
		System.out.println("Counter 7(Random Range(1000) Search BST): "+MoultiCounter.getCount(5)/NO_OF_SEARCHES);
		System.out.println("Counter 8(Random Range(1000) Search TBST): "+MoultiCounter.getCount(4)/NO_OF_SEARCHES);
		System.out.println("Counter 11(Binary Random Range(1000) Search) "+MoultiCounter.getCount(9)/NO_OF_SEARCHES);
		
	}
}
