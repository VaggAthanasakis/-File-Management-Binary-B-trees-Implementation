package Console;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import BPTree.BTree;
import BPTree.BTreeInnerNode;
import BPTree.BTreeLeafNode;
import BPTree.BTreeNode;
import BPTree.Data;
import Counter.MoultiCounter;
import FileManager.FileManager;

public class Test {

	public static void main(String[] args) throws IOException {
		int ÍO_OF_ELEMENTS=100000;
		int START_INT=1;
		int END_INT=1000000;
		int recordSize=0;
		int NUMBER_OF_TESTS=20;

		BTree<Integer,Data> tree = new BTree<Integer, Data>();

		
		java.util.Random randomGenerator = new java.util.Random();
		Data data= new Data(0,1,2,3);							 // create the data that will be stored at the datafile 
		int[] randomInts = randomGenerator.ints(START_INT, END_INT).distinct().limit(ÍO_OF_ELEMENTS).toArray();
		
		/*Initialize Our Tree With The Insertion Of NO_OF_ELEMENTS Elements*/
		for(int i=0; i < ÍO_OF_ELEMENTS; i++) {
			int key=randomInts[i];
			tree.insert(key,data);
		}
		MoultiCounter.resetCounter(1);
		//System.out.println("End Of Initialisation Inserts...");
		
		/********* Performing 20 Key Inserts *********/
		int intsForInsert[]=randomGenerator.ints(END_INT, END_INT+200).distinct().limit(NUMBER_OF_TESTS).toArray();
		for(int i=0; i<NUMBER_OF_TESTS; i++) {
			tree.insert(intsForInsert[i], data);
		}
		
		//System.out.println("End Of 20 Inserts...");
		System.out.println();
		System.out.println("InsertCounter: "+MoultiCounter.getCount(1)/NUMBER_OF_TESTS);
		MoultiCounter.resetCounter(1);
		
		/********* Performing 20 Key Searches *********/
		int[] IndexForSearch=randomGenerator.ints(START_INT, ÍO_OF_ELEMENTS).distinct().limit(NUMBER_OF_TESTS).toArray();
		for(int i=0; i<NUMBER_OF_TESTS; i++) {
			int keyForSearch=randomInts[IndexForSearch[i]];
			tree.search(keyForSearch);
		}
		
		//System.out.println("End Of 20 Searches...");
		System.out.println();
		System.out.println("SearchCounter: "+MoultiCounter.getCount(1)/NUMBER_OF_TESTS);
		MoultiCounter.resetCounter(1);
		
		/********* Performing 20 Key Deletes *********/
	
		for(int i=0; i<NUMBER_OF_TESTS; i++) {
			int keyForDeletion=randomInts[IndexForSearch[i]];
			tree.delete(keyForDeletion);
		}
		System.out.println();
		System.out.println("DeleteCounter: "+MoultiCounter.getCount(1)/NUMBER_OF_TESTS);
		MoultiCounter.resetCounter(1);
		
		
	}

}
