package FileProcessPackage;


import Counter.MultiCounter;
import ElementInsertionPackage.FileGenerator;
import NodePackage.*;


public class ExternalSort{
 

 private final int NumberOfKeys=1000000;
 private final int DataPageSize=128;
 byte[] DataPage= new byte[DataPageSize];
 Node[] TmpArrayOfNodes=new Node[4];
 Node[] ArrayOfNodes=new Node[NumberOfKeys];
 IndexNode[] ArrayOfIndexNodes= new IndexNode[NumberOfKeys];
 IndexNode[] TmpArrayOfIndexNodes= new IndexNode[16];
 
 /*This Method Takes As Input A FileManager(fileName) And A String(FileType)
  *It Reads The FileManager Info From The Disk Into The Main Memory
  *Then This Method Sorts The File Of Nodes(By Key) At The Main Memory 
  *And Creates A New Sorted File Back At The Disk*/
 public FileManager externalSort(FileManager fileName,String FileType){
	
   int NumberOfPages=fileName.getNumOfPages();
   BlockToNodeArray btna=new BlockToNodeArray();
   QuickNodeSort qns=new QuickNodeSort();
   QuickIndexNodeSort qins=new QuickIndexNodeSort();
   FileGenerator fileGenerator=new FileGenerator();
   
   
   for(int i=0; i<NumberOfPages; i++) {                                 // Read All The File From The Disk
	   int counter=0;
	   int counter2=0;
		try {   
		   if(FileType=="DataFile") {  
			   fileName.ReadBlock(i, DataPage);	
			   MultiCounter.increaseCounter(5);
			   TmpArrayOfNodes=btna.BlockToNodeArrayProccess(DataPage);	// Converts The Byte Array(DataPage) Into A Node Array		  
			   for(int k=0; k<4; k++) { 
				   ArrayOfNodes[4*i+counter]=TmpArrayOfNodes[k];        // Put The 4 Nodes Array Info Into The ArrayOfNodes That Contains All Files Nodes
				   counter+=1;
			   }
			   TmpArrayOfNodes=null;
			   
		   }
		   else if(FileType=="KeyFile") {
		       fileName.ReadBlock(i, DataPage);
		       MultiCounter.increaseCounter(6);
		       TmpArrayOfIndexNodes=btna.BlockToIndexNodeArrayProccess(DataPage);
		       
		       for(int l=0; l<16; l++) {
		    	   ArrayOfIndexNodes[16*i+counter2]=TmpArrayOfIndexNodes[l];
		    	   counter2+=1;
		       }
		       TmpArrayOfIndexNodes=null;  
		   }else {
		   System.err.println("Error At externalSorting.Wrong Filetype!");
		   }
	   }catch(Exception e) {
		   e.printStackTrace();
	   }
   }
   
   if(FileType=="DataFile") {
	   System.out.println("Will Start Data Sorting..");
	    qns.sort(ArrayOfNodes,0,NumberOfKeys-5);                        // Then Sorts ArrayOfNodes(By Key) And Creates A New File With The Sorted Data
	    FileManager manager=fileGenerator.SortedDataFileGeneretor(ArrayOfNodes);
	    return manager;
   }
   else {
	   System.out.println("Will Start Key Sorting..");
	   qins.sort(ArrayOfIndexNodes,0,NumberOfKeys-1);
	   FileManager f=fileGenerator.SortedKeyFileGenerator(ArrayOfIndexNodes);
       return f;
   }
   
 }
}
   
   
  
   
 
   
   
   
   
 