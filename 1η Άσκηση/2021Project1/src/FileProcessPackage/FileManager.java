package FileProcessPackage;

import java.io.*;
public class FileManager {
	
	final int DataPageSize = 128;
	byte[] DataPage = new byte[DataPageSize];
	
	private int filePos,numOfPages;
	private String fileName;
	private RandomAccessFile myfile;
	
	public FileManager() {
		
	}
	
	/******Getters And Setters******/
	
	
	public int getFilePos() {
		return filePos;
	}


	public RandomAccessFile getMyfile() {
		return myfile;
	}

	public void setMyfile(RandomAccessFile myfile) {
		this.myfile = myfile;
	}

	public void setFilePos(int filePos) {
		this.filePos = filePos;
	}


	public int getNumOfPages() {
		return numOfPages;
	}


	public void setNumOfPages(int numOfPages) {
		this.numOfPages = numOfPages;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileInfo(String fileInfo) {
		this.fileName = fileInfo;
	}

	
	/**********************************************************Methods*********************************************************/
    /******File Handle******/
	
	public void FileHandle() {
		System.out.println("FileName: "+this.fileName+" Number OF Pages: "+this.numOfPages+" File Position: "+this.filePos);
	}
	
	
	/*****Create new file*****/
	public int CreateFile(String name)  {    //Creating new file
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			
			this.filePos=0;
			this.numOfPages=1;
			this.fileName=name;
			
			dos.writeInt(filePos);
			dos.writeInt(numOfPages);
			dos.writeChars(fileName);
			dos.close();
			
			byte[] writeBuffer = new byte[DataPageSize];
			writeBuffer=bos.toByteArray();
			
			this.myfile = new RandomAccessFile(name, "w");
			myfile.seek(0);
			myfile.write(writeBuffer);
			myfile.close();
			return 1;
		
		}
		catch(FileNotFoundException e) {
			System.err.println("File Not Found!");
			return 0;
		}
		catch(IOException e) {
			System.err.println("Write Failed!");
			return 0;
		}
	}
	
	/*********Open File***********/
	
	public int OpenFile(String name)  {
		try{
			this.myfile = new RandomAccessFile(name, "rw");
			this.filePos=0;
			return this.numOfPages;
		}
		catch(FileNotFoundException e) {
			System.err.println("File Not Found!");
			return 0;
		}	
	}
	
	/********* Read Block ********/
	public int ReadBlock(int pagePos,byte[] DataPage) throws IOException {
		try { 
			this.filePos=pagePos;
			this.myfile.seek(this.filePos*128);
			this.myfile.read(DataPage,0, DataPageSize);
			return 1;
			}
		catch(FileNotFoundException e){
			System.err.println("File Not Found!");
			return 0;
		}
		catch(IOException e) {
			System.err.println("ReadBlock Failed!");
			return 0;
		}
       }
	/********Read Next Block*******/
	
	public int ReadNextBlock(byte[] DataPage) throws IOException{
		try {
			this.filePos+=1; //Go to the next block
			this.myfile.seek(this.filePos*128); 			
			this.myfile.read(DataPage);
			return 1;
		}
		catch(IOException e) {
			System.err.println("ReadNextBlock Failed!");
			return 0;
		}
		
	}
	/********Write Block*******/
	public int WriteBlock(int position, byte[] DataPage) throws IOException {
	   try {
		   this.filePos=position;
		   this.myfile.seek(this.filePos*128);
		   this.myfile.write(DataPage,0,DataPageSize);
		   
		   this.numOfPages+=1;
		 
		   return 1;
	   }
	   catch(IOException e) {
			System.err.println("WriteBlock Failed!");
			return 0;
		}
	}
	
	/********Write Next Block*******/
    public int WriteNextBlock(byte[] DataPage) throws IOException{
		try {
			this.myfile.seek(this.filePos*128);
			this.filePos+=1;
			this.myfile.write(DataPage);
	        this.numOfPages+=1;
		
			return 1;
		}
		 catch(IOException e) {
				System.err.println("WriteNextBlock Failed!");
				return 0;
			}
    	
	}
    
    /********Append Block*******/
    public int AppendBlock(byte[] DataPage) throws IOException {
		try {
			this.myfile.seek(this.myfile.length());
			this.myfile.write(DataPage);
			this.numOfPages+=1;
			return 1;
		}
		 catch(IOException e) {
				System.err.println("AppendBlock Failed!");
				return 0;
			}
		
	}
	
    /********Delete Block*******/
    public int DeleteBlock(int deletePosition) throws IOException {
		try {
			byte[] tmp = new byte[DataPageSize];
			int LastPosition=((int)(this.myfile.length()))/128;
		    ReadBlock(LastPosition,tmp);
		    WriteBlock(deletePosition,tmp);
		    this.numOfPages-=1;
		    this.filePos=deletePosition;
		    return 1;
	    }
		catch(IOException e) {
			System.err.println("DeleteBlock Failed!");
			return 0;
		  }		
		}
   
    /********Close File*******/
    public int CloseFile() throws IOException {
		try {
			this.myfile.close();
			return 1;
		}
		catch(IOException e) {
			System.err.println("CloseFile Failed!");
			return 0;
		  }		
		
	}
	
}
