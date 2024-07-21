package SearchingPackage;

import java.io.*;
import java.util.Random;

import FileProcessPackage.BlockToNodeArray;
import FileProcessPackage.FileManager;
import NodePackage.*;
import Counter.*;

/*This Class Searches A File With 4 Different Ways
 *It Makes 20 Searches For Each One Of The Ways*/
public class Searching {

	private final int DataPageSize=128;
	private final int numberOfKeys=10000;
	private final int TotalNumberOfSearches=20;
	
	
	byte[] DataPage= new byte[DataPageSize];
	Node[] TmpArrayOfNodes= new Node[4];
	Node[] ArrayOfNodes= new Node[numberOfKeys];
	IndexNode[] TmpArrayOfIndexNodes= new IndexNode[16];
	
	
	FileManager fileManager= new FileManager();	
	Node node= new Node();
	IndexNode indexNode= new IndexNode();
	Random random=new Random();
	
	
	/*This Method Takes As Input A FileManager(DataFile), A Block Position Integer And The Key To Search
	 *It Returns True If It Finds The Key And False If Not*/
	private boolean SearchBlock(FileManager DataFile, int BlockPosition, int key) {
		
		try{
			
			DataFile.ReadBlock(BlockPosition, DataPage);                              // Reads Block At 'BlockPosition' From The File On TheDisk 
			BlockToNodeArray btna=new BlockToNodeArray();
			TmpArrayOfNodes=btna.BlockToNodeArrayProccess(DataPage);                  // Converts The DataPage Into Nodes            
			
			for(int k=0; k<4; k++) {                                                  // Searches The Key At The Array Of Nodes
			    if(TmpArrayOfNodes[k].getKey()==key) {                                // Found The Key And Returns True
			    	return true;	
			    }
		    }
			TmpArrayOfNodes=null;
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;         														  // Did Not Find The Key And Returns False
		
	}
	
	/*This Method Performs 20 Serial Searches With 20 Different Existing Keys To The DataFile That Has The Nodes Of Key-Information*/
	public void SerialSearchingData(FileManager fileName,int[] Arraykeys) {
		
	    for(int j=0; j<TotalNumberOfSearches; j++) {
			int key=Arraykeys[random.nextInt(Arraykeys.length)+1];	                 // Takes 20 Different Existing Keys For Search
		try{	
			boolean k=false;
		    for(int i=0; i<fileName.getNumOfPages() && k==false; i++) {              // It Stops The Search When It Finds The Key Or When It Has Search The Whole File
		    	k=SearchBlock(fileName,i,key);
		    	MultiCounter.increaseCounter(1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}		
		}
	}
	
	/*This Method Performs 20 Serial Searches With 20 Different Existing Keys To The KeyFile That Has The IndexNodes Of Key-PagePosition
	 * When It Finds The Key At The KeyFile, It Takes The PagePosition Info And Searches The Specific Block At The DataFile*/
	public void SerialSearchingKeys(FileManager KeyFile,FileManager DataFile,int[] Arraykeys) { 
		
		for(int i=0; i<TotalNumberOfSearches; i++) {
			int key=Arraykeys[random.nextInt(Arraykeys.length)+1];                       // Takes 20 Different Existing Keys For Search
			try {
			    boolean l=false;
				for(int k=0; k<KeyFile.getNumOfPages() && l==false; k++) {
					KeyFile.ReadBlock(k, DataPage);                                    // Reads Block From The File On TheDisk 
					MultiCounter.increaseCounter(2);               	 
					BlockToNodeArray btna=new BlockToNodeArray();
					TmpArrayOfIndexNodes=btna.BlockToIndexNodeArrayProccess(DataPage); // Converts The DataPage Into IndexNodes  
									
					for(int k2=0; k2<16 && l==false; k2++) {                           // Searches The Key At The Array Of IndexNodes
						if(TmpArrayOfIndexNodes[k2].getKey()==key) {                   // If It Finds The Key, It Takes The PagePosition Info And Searches The DataFile At This Specific Page
							int position=TmpArrayOfIndexNodes[k2].getPagePos();
							l=SearchBlock(DataFile,position,key);
							MultiCounter.increaseCounter(2);								
						}
					}
                   TmpArrayOfIndexNodes=null;
				}  			
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}					
		}
	}
	
  /*This Method Takes As Input A DataFile, A left(FileStart), A Right(FileEnd) , And A Key
   *It Binary Searches The Proper Block, It Reads The Block From DataFile, Then Converts It Into A Array Of Nodes
   *And Then Searches The Key With Binary Search */
  private boolean BinarySearchNodeBlock(FileManager DataFile,int left,int right, int key) {
		
		   try {	
			   if (right >= left) { 
		            int mid = left + (right - left) / 2; 
			   
			    DataFile.ReadBlock(mid, DataPage);                      // Reads Block At Position 'Mid Position'
			    MultiCounter.increaseCounter(3);	
				BlockToNodeArray btna=new BlockToNodeArray();
				TmpArrayOfNodes=null;
				TmpArrayOfNodes=btna.BlockToNodeArrayProccess(DataPage);// Converts The Byte Array To Nodes
				
				 if(key<TmpArrayOfNodes[0].getKey()) {                  // if(key<TmpArrayOfNodes[0].getKey()) Then The Method Call It Self To Search To The Middle Page Of The Left Half File
					return BinarySearchNodeBlock(DataFile,left,mid-1,key);
				}
				else if(key>TmpArrayOfNodes[3].getKey()) {;            // if(key>TmpArrayOfNodes[3].getKey()) Then The Method Call It Self To Search To The Middle Page Of The Right Half File
					return BinarySearchNodeBlock(DataFile,mid+1,right,key);		
					}
				else {
					BinaryNodeSearch B=new BinaryNodeSearch(TmpArrayOfNodes);
					Node n=B.search(key);                                   // Searches The Key At the Array Of Nodes And Returns True If It Finds The Key
					if(n!=null) {  //We have found the key
						return true;
						}
				}
				}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		return false;
	   }
  /*This Method Takes As Input A KeyFile, A left(FileStart), A Right(FileEnd) , And A Key
   *It Binary Searches The Proper Block, It Reads The Block From keyFile, Then Converts It Into A Array Of IndexNodes
   *And Then Searches The Key With Binary Search 
   *When It Finds The IndexNode With The Key, It Returns IndexNodes.getPos()*/
  private int BinarySearchIndexNodeBlock(FileManager KeyFile,int left,int right,int key) {
	  
	  try {	
		   if (right >= left) { 
	            int mid = left + (right - left) / 2; 
		   
		    KeyFile.ReadBlock(mid, DataPage);                           	 // Reads Block At Position 'BlockPosition'
		    MultiCounter.increaseCounter(4);	
			BlockToNodeArray btna=new BlockToNodeArray();
			TmpArrayOfIndexNodes=null;
			TmpArrayOfIndexNodes=btna.BlockToIndexNodeArrayProccess(DataPage);// Converts The Byte Array To Nodes
			
			 if(key<TmpArrayOfIndexNodes[0].getKey()) {                  	  // if(key<TmpArrayOfIndexNodes[0].getKey()) Then The Method Call It Self To Search To The Middle Page Of The Left Half File
				return BinarySearchIndexNodeBlock(KeyFile,left,mid-1,key);
			}
			else if(key>TmpArrayOfIndexNodes[3].getKey()) {            	  // if(key>TmpArrayOfIndexNodes[3].getKey()) Then The Method Call It Self To Search To The Middle Page Of The Right Half File
				return BinarySearchIndexNodeBlock(KeyFile,mid+1,right,key);		
				}
			else {
				BinaryIndexNodeSearch B=new BinaryIndexNodeSearch(TmpArrayOfIndexNodes);
				IndexNode n=B.search(key);                                   // Searches The Key At the Array Of Nodes And Returns True If It Finds The Key
				if(n!=null) {  //We have found the key
					return n.getPagePos();
					}
			}
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	return Integer.MIN_VALUE;
  }
  
  /*This Method Performs 20 Binary Searches With 20 Different Existing Keys To The DataFile That Has The Nodes Of Key-Information*/
	public void BinarySearchingData(FileManager fileName,int[] Arraykeys) {
		
		for(int i=0; i<TotalNumberOfSearches; i++) {                       
			int key=Arraykeys[random.nextInt(Arraykeys.length)+1];	        // Takes 20 Different Existing Keys For Search
			try {
				int right=fileName.getNumOfPages();
				BinarySearchNodeBlock(fileName,0,right-1,key);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}	
    }
	/*This Method Performs 20 Binary Searches With 20 Different Existing Keys To The KeyFile That Has The IndexNodes Of Key-PagePosition
	 * When It Finds The Key At The KeyFile, It Takes The PagePosition Info And Searches The Specific Block At The DataFile*/
	public void BinarySearchingKeys(FileManager KeyFile,FileManager DataFile,int[] Arraykeys) {
		
		for(int i=0; i<TotalNumberOfSearches; i++) {
			int key=Arraykeys[random.nextInt(Arraykeys.length)+1];                 // Takes 20 Different Existing Keys For Search
			try {
				int right=KeyFile.getNumOfPages();
				int PagePos=BinarySearchIndexNodeBlock(KeyFile,0,right-1,key);
				if(PagePos!=Integer.MIN_VALUE) {
					DataFile.ReadBlock(PagePos, DataPage);
					MultiCounter.increaseCounter(4);
					BlockToNodeArray btna=new BlockToNodeArray();
					Node[] Array=btna.BlockToNodeArrayProccess(DataPage);
					BinaryNodeSearch B=new BinaryNodeSearch(Array);
					Node a=B.search(key);
					if(a!=null) {  //We have found the key
						}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}					
		}	
		
	}	
			
}



