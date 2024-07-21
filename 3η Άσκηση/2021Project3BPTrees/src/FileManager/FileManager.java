package FileManager;

/*This Class Is Used For Creating, Opening  Reading And Storing Information To Files*/

import java.io.*;
public class FileManager {
	
	final int DataPageSize = 256;
	byte[] DataPage = new byte[DataPageSize];
	
	private int filePos;
	int numOfPages=0;
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
    
	/*********Open File Opens A File(Create) With Name 'name' ***********/
	
	public void OpenFile(String name)  {
		try{
			this.myfile = new RandomAccessFile(name, "rw");
			this.filePos=0;
		}
		catch(FileNotFoundException e) {
			System.err.println("File Not Found!");
		}	
	}
	
	/********* Read Block Takes As Parameter The PagePosition(PagePos) Of The File That It Will Read From
	 ********* And A ByteArray(DataPage) Where It Stores The File Bytes********/
	public int ReadBlock(int pagePos,byte[] DataPage) throws IOException {
		try { 
			this.filePos=pagePos;
			this.myfile.seek(this.filePos*DataPageSize);
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
		
	
	/********Write Block Takes As Parameter The Page Position(position) Of The File That Will Read From
	 ******** And The Byte Array(DataPage) With The Bytes That Will Be Stored In The File*******/
	public int WriteBlock(int position, byte[] DataPage) throws IOException {
	   try {
		   this.filePos=position;
		   this.myfile.seek(this.filePos*DataPageSize);
		   this.myfile.write(DataPage,0,DataPageSize);
		   //this.numOfPages+=1;		 
		   return 1;
	   }
	   catch(IOException e) {
			System.err.println("WriteBlock Failed!");
			return 0;
		}
	}
	
    	
}
