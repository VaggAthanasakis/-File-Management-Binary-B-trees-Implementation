package ConsolePackage;

import SearchingPackage.*;
import ElementInsertionPackage.*;
import FileProcessPackage.*;


import Counter.*;

public class Console {

	public static void main(String[] args) {
		//Path DataFileName=Paths.get("C:\\Users\\vagga\\OneDrive\\Desktop\\03 - demodata_1h_askhsh\\demodata");
	   // final String TestDataFileName="C:\\Users\\vagga\\OneDrive\\Desktop\\03 - demodata_1h_askhsh\\demodata\\randomNodes.bin";
	   // final String TestKeyFileName="C:\\Users\\vagga\\OneDrive\\Desktop\\03 - demodata_1h_askhsh\\demodata\\randomNodesSortedIndex.bin";
	     
		
	    FileGenerator generator=new FileGenerator();
	    Searching search =new Searching();
	    
	    
	    /* Generate A File With Random Data And A File With The Keys And The Page Position Of The DataFile */
		
	    System.out.println("Starting Generating Files...");
	    
	    FileManager DataFile=generator.DataFileGenerator("DataFile");
	    FileManager KeyFile=generator.KeyFileGenerator("KeyFile",generator.getArrayKeys());
	    ExternalSort externalSort= new ExternalSort();
	    
		/* Search To DataFile With The 'A' Way Of File Organization */	
	     System.out.println("Starting A Way Of File Organization...");
	     search.SerialSearchingData(DataFile,generator.getArrayKeys());
	  
	    /* Search To DataFile With The 'B' Way Of File Organization */
	     System.out.println("Starting B Way Of File Organization...");
	     search.SerialSearchingKeys(KeyFile,DataFile,generator.getArrayKeys());
	    
	    /* Search To DataFile With The 'C' Way Of File Organization */
	     System.out.println("Starting C Way Of File Organization...");
	     FileManager SortedDataFile=externalSort.externalSort(DataFile,"DataFile");
	     search.BinarySearchingData(SortedDataFile, generator.getArrayKeys());
	  
	    /* Search To DataFile With The 'D' Way Of File Organization */
	     System.out.println("Starting D Way Of File Organization...");
	     FileManager SortedKeyFile=externalSort.externalSort(KeyFile,"KeyFile");
	     search.BinarySearchingKeys(SortedKeyFile, DataFile, generator.getArrayKeys());
	    
	    System.out.println(" Counter A: "+ MultiCounter.getCount(1)/20+" Counter B: "+MultiCounter.getCount(2)/20+" Counter C:"+MultiCounter.getCount(3)/20+" Counter D:"+MultiCounter.getCount(4)/20);
	    System.out.println("Counter For Sorting At C: "+MultiCounter.getCount(5)+" Counter For Sorting At D: "+MultiCounter.getCount(6));
	}

}

