package ElementInsertionPackage;

import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import Counter.MultiCounter;
import FileProcessPackage.FileManager;
import NodePackage.IndexNode;
import NodePackage.Node;

public class FileGenerator {
    
	private final int numberOfKeys=1000000;
	private final int max=1000000;
	private final int DataPageSize=128;
	private final int Length=20;
	private byte[] DataPage= new byte[128];
	private byte[] buffer32= new byte[32];
	private byte[] buffer8= new byte[8];
	private int[] arrayKeys= new int[numberOfKeys];
	private int[] posArray=new int[numberOfKeys];
	int DataPagesCounter=0;
	
	
	
	Random random= new Random();
		
	
	public int[] getArrayKeys() {
		return arrayKeys;
	}

	public void setArrayKeys(int[] arrayKeys) {
		this.arrayKeys = arrayKeys;
	}
	
	public int[] getposArray() {
		return this.posArray;
	}

	
	
	/*Method that gives a random String of 'length' length*/
	public static String getAlphaNumericString(int length) 
	{ 
  
		// chose a Character random from this String 
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				            + "0123456789"
				            + "abcdefghijklmnopqrstuvxyz"; 

		// create StringBuffer size of AlphaNumericString 
		StringBuilder sb = new StringBuilder(length); 

		for (int i = 0; i < length; i++) { 

		    // generate a random number between 
		    // 0 to AlphaNumericString variable length 
		    int index = (int)(AlphaNumericString.length() * Math.random()); 

		    // add Character one by one in end of sb 
		    sb.append(AlphaNumericString .charAt(index)); 
		} 

		return sb.toString(); 
	} 
	/*This Method Generates A File That Contains 10^6 Nodes
	 *Each Node Consists Of 32 Bytes That Include A Key, A 20 Chars String And 2 Integers*/
	public FileManager DataFileGenerator(String fileName)  {
		int int1=0;                    						// Storing 2 integers at the node
		int int2=1;
		
		FileManager fileManager= new FileManager();
		
		fileManager.OpenFile(fileName);
		fileManager.setNumOfPages(-1);
		
			
		for(int k=0; k<numberOfKeys/4; k++) {
			ByteBuffer bb= ByteBuffer.allocate(DataPageSize);
			bb.order(ByteOrder.BIG_ENDIAN);
			
			for(int i=0; i<4; i++) {
				int key=random.nextInt(max)+1; 			      // Generate the Node key
				this.arrayKeys[4*k+i]= key;                   // Storing each key at the arrayKeys int array
				this.posArray[4*k+i]= k;					  // Storing position of page 'k' at 'counter' position of the posArray array				
				
				String s=getAlphaNumericString(Length);       // Getting a random string of 20 characters
				Node n= new Node();
				buffer32=n.NodeByteArray(key, s, int1, int2);
				bb.put(buffer32);                             // Filling a node(record)
				buffer32=null;                                // Deleting buffer32 data in order to use him again at the next loop
			}
			DataPage =  bb.array();                           // One DataPage that is ready for storing at the disk
			try {
				fileManager.WriteNextBlock(DataPage);         // Write The Block At The DataFile
				this.DataPagesCounter+=1;
			} catch (IOException e) {
				e.printStackTrace();
			}
			DataPage=null;                   				 // Deleting DataPage data in order to use him again at the next loop
	}
		
		return fileManager;
	 }	
	
	/*This Method Generates A File That Contains 10^6 IndexNodes
	 *Each Node Consists Of 8 Bytes That Include A Key And An Integer That Has The Block Position Of The Key At The DataFile*/
	public FileManager KeyFileGenerator(String fname,int[] arrayKeys) {            
		
		FileManager fm= new FileManager();
		fm.OpenFile(fname);
		
		
		for(int y=0; y<this.DataPagesCounter/4; y++) {
			ByteBuffer bb= ByteBuffer.allocate(DataPageSize);
			bb.order(ByteOrder.BIG_ENDIAN);
			for(int i=0; i<DataPageSize/8; i++) {
				IndexNode indexNode= new IndexNode();
				buffer8=indexNode.IndexNodeByteArray(arrayKeys[i+y*16],this.posArray[i+y*16]);
				bb.put(buffer8);
				buffer8=null;
			}
			DataPage=bb.array();
			try {
				fm.WriteNextBlock(DataPage);
				MultiCounter.increaseCounter(5);
			} catch (IOException e) {
				e.printStackTrace();
			}
			DataPage=null; 
		   }	
		     return fm;
	  }
	
	
  /*This Method Takes A Node Array Sorted By Key And Generates A New File With These Nodes
   *The Method Converts Each Node Into A Byte Stream And Then Store Them At The Disc*/
  public FileManager SortedDataFileGeneretor(Node[] arrayNodes){
	  
	  FileManager fm=new FileManager();
	  fm.OpenFile("SortedDataFile");
	  
	  
	  for(int k=0; k<arrayNodes.length/4-1; k++) {                            // Taking Node's Info And Place Them To The Byte Stream
		  java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(DataPageSize);
		  bb.order(ByteOrder.BIG_ENDIAN);
		  for(int i=0; i<4; i++) {	  
			  bb.putInt(arrayNodes[4*k+i].getKey());
			  bb.put(arrayNodes[4*k+i].getStringArray().getBytes(StandardCharsets.US_ASCII));
		      bb.putInt(arrayNodes[4*k+i].getInt1());
		      bb.putInt(arrayNodes[4*k+i].getInt2());	
		      
		  }
		  byte[] block=bb.array();                                      
		  try {
			  fm.WriteNextBlock(block);                                      // Stores Bytes At The Disc
			  MultiCounter.increaseCounter(5);
		  }catch (IOException e) {
				e.printStackTrace();
			}
		  bb.clear();
		  block=null;
     } 
	  
	  return fm;
	  
  }
	
  /*This Method Takes An IndexNode Array Sorted By Key And Generates A New File With These IndexNodes
   * The Method Converts Each IndexNode Into A Byte Stream And Then Store Them At The Disc*/
  public FileManager SortedKeyFileGenerator(IndexNode[] Array) {
	  
	  FileManager fileManager=new FileManager();
	  fileManager.OpenFile("SortedKeyFile");
	  
	  for(int y=0; y<Array.length/16; y++) {
		  java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(DataPageSize);
		  bb.order(ByteOrder.BIG_ENDIAN);
		  for(int i=0; i<16; i++) {
			  
			  bb.putInt(Array[16*y+i].getKey());
			  bb.putInt(Array[16*y+i].getPagePos());
		  }
		  byte[] block=bb.array();
		  try {
			  fileManager.WriteNextBlock(block);
		  }catch (IOException e) {
				e.printStackTrace();
			}
		  bb.clear();
		  block=null;
	  }
	return fileManager;
  }
	
	
		
	}
	
	