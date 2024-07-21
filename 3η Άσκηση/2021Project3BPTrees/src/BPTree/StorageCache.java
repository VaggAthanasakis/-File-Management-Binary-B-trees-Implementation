package BPTree;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import Counter.MoultiCounter;
import FileManager.FileManager;

/**
 * Basic singleton handling retrieving and storing BTree Nodes to node/index file and Data to data file.
 * @author sk
 */
public class StorageCache {
	private static final String NODE_STORAGE_FILENAME = "plh201_node.bin";
	private static final String DATA_STORAGE_FILENAME = "plh201_data.bin";

	private static StorageCache instance;

	private static HashMap retrievedNodes = null;
	private static HashMap retrievedDatas = null;
	
	private final int DataPageSize=256;
	private final int RecordSize=32;
	private final int InnerType=0;
	private final int LeafType=1;
	
	int NodeFileCounter=0;
	int DataFileCounter=0;

	// make this private so that noone can create instances of this class
	private StorageCache() {

	}

	private void cacheNode(int dataPageIndex, BTreeNode node) {
		if (StorageCache.retrievedNodes == null) {
			StorageCache.retrievedNodes = new HashMap();
		}
		StorageCache.retrievedNodes.put(dataPageIndex, node);
	}
	private void cacheData(int dataByteOffset, Data data) {
		if (StorageCache.retrievedDatas == null) {
			StorageCache.retrievedDatas = new HashMap();
		}
		StorageCache.retrievedDatas.put(dataByteOffset, data);
	}

	private BTreeNode getNodeFromCache(int dataPageIndex) {
		if (StorageCache.retrievedNodes == null) {
			return null;
		}

		return (BTreeNode)StorageCache.retrievedNodes.get(dataPageIndex);
	}
	private Data getDataFromCache(int dataByteOffset) {
		if (StorageCache.retrievedDatas == null) {
			return null;
		}

		return (Data)StorageCache.retrievedDatas.get(dataByteOffset);
	}	

	public static StorageCache getInstance() {
		if (StorageCache.instance == null) {
			StorageCache.instance = new StorageCache();
		}
		return StorageCache.instance;
	}

	public void flush() {
		flushNodes();
		flushData();
	}

	// checks each node in retrievedNodes whether it is dirty
	// If they are dirty, writes them to disk
	private void flushNodes() {
		BTreeNode node;
	    FileManager fm1 = new FileManager();
		for ( Object dataPageIndex : StorageCache.retrievedNodes.keySet() ) {
			node = (BTreeNode)StorageCache.retrievedNodes.get(dataPageIndex);  // Takes Node From Cache
			if (node.isDirty()) {	// if node is dirty writes it to the disk											
                try {
                	byte[] byteArray = node.toByteArray();               // converting node to a byte array
    			    fm1.OpenFile(NODE_STORAGE_FILENAME);   		         // opens the file with the nodes
    			    fm1.WriteBlock(node.getStorageDataPage(), byteArray);// writes the bytes of the node at its datapage
    			    MoultiCounter.increaseCounter(1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}               
			}
		}
		// reset it
		StorageCache.retrievedNodes = null;	
	}

	// checks each node in retrievedData whether it is dirty
	// If they are dirty, writes them to disk
	private void flushData() {
		Data data;
		int dataPageIndex;
		FileManager fm2 = new FileManager();

		if(StorageCache.retrievedDatas == null) 
			return;
		
		for ( Object storageByteOffset : StorageCache.retrievedDatas.keySet() ) {
			data = (Data)StorageCache.retrievedDatas.get(storageByteOffset);
			if (data.isDirty()) {
				// data.storageByteIndex tells us at which byte offset in the data file this data is stored
				// From this value, and knowing our data page size, we can calculate the dataPageIndex of the data page in the data file
				// This process may result in writing each data page multiple times if it contains multiple dirty Datas
				byte[] byteArray = data.toByteArray();
				byte[] pageBytes = new byte[DataPageSize];
				fm2.OpenFile(DATA_STORAGE_FILENAME);
			    
				int pagePos=data.getStorageByteOffset()/DataPageSize;
				try {
					fm2.ReadBlock(pagePos, pageBytes);	// read datapage given by calculated dataPageIndex from data file
					MoultiCounter.increaseCounter(1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int offset=data.getStorageByteOffset();	// Reads the offset that data is stored inside the datafile
				int posInsideDataPage;
				if(pagePos == 0)
					posInsideDataPage=offset;
				else
					posInsideDataPage=(offset-(pagePos)*DataPageSize); // find the correct position inside the datapage at which this record must be written
			    int k=0;

				for(int i=posInsideDataPage; i<posInsideDataPage+RecordSize-1; i++) {     // copy byteArray to correct position of read bytes
					pageBytes[i]=byteArray[k];
					k++;
				}
				try {
					fm2.WriteBlock(pagePos, pageBytes);		// store it again to file
					MoultiCounter.increaseCounter(1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			}
		}
		// reset it
		StorageCache.retrievedDatas = null;	
	}

   /* Takes As Input The DataPage Where The Node Is Stored At The Node File And Returns The Node */
	public BTreeNode retrieveNode(int dataPageIndex) {
		// if we have this dataPageIndex already in the cache, return it
		BTreeNode result = this.getNodeFromCache(dataPageIndex);
		if (result != null) {
			return result;	
		}
		
		if (StorageCache.retrievedNodes != null && StorageCache.retrievedNodes.keySet().size() > 200) { // we do not want to have more than 200 nodes in cache
			BTreeNode node;
			for ( Object key : StorageCache.retrievedNodes.keySet() ) {
				node = (BTreeNode)StorageCache.retrievedNodes.get(dataPageIndex);
				if (!node.isDirty()) {
					StorageCache.retrievedNodes.remove(key);
				}
			}
		}
		// we dont have the node in the cache so we read it from the node file
		// open our node/index file
	    FileManager fm = new FileManager();
	    fm.OpenFile(NODE_STORAGE_FILENAME);

		// seek to position DATA_PAGE_SIZE * dataPageIndex
	    // read DATA_PAGE_SIZE bytes 
	     byte[] pageBytes = new byte[DataPageSize];
	     DataInputStream dis = new DataInputStream(new ByteArrayInputStream(pageBytes));
         try {
			fm.ReadBlock(dataPageIndex,pageBytes);
			MoultiCounter.increaseCounter(1);
			int type=dis.readInt();            // a 4 byte int should tell us what kind of node this is. See toByteArray(). Is it a BTreeInnerNode or a BTreeLeafNode?
			if(type==InnerType){  			   // if type corresponds to inner node, we convert the bytes into a BTreeInnerNode
				 result = new BTreeInnerNode();
				 result = result.fromByteArray(pageBytes, dataPageIndex);
				// System.out.println("Key Count "+result.getKeyCount());
			}
			else if(type == LeafType){         // else this is a leaf node and we convert the bytes into a leafNode
				 result = new BTreeLeafNode();
				 result = result.fromByteArray(pageBytes, dataPageIndex);
			}  
			else {
				System.err.println("Error At Recognizing The Node Type!!");
			}
         }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		
		this.cacheNode(dataPageIndex, result);

		return result;
	}
	
	/* Takes As Input The DataByteOffset Where The Data Are Stored At The Data File And Returns The Data */
	public Data retrieveData(Integer dataByteOffset) {
		// if we have this dataPageIndex already in the cache, return it
		Data result = this.getDataFromCache(dataByteOffset);
		if (result != null) {
			return result;
		}
		if (StorageCache.retrievedDatas != null && StorageCache.retrievedDatas.keySet().size() > 100) { // we do not want to have more than 100 datas in cache
			Data data;
			for ( Object key : StorageCache.retrievedDatas.keySet() ) {
				data = (Data)StorageCache.retrievedDatas.get(dataByteOffset);
				if (!data.isDirty()) {
					StorageCache.retrievedDatas.remove(key);
				}
			}
		}
		// we don't have the data in the cache so we read it from the data file
		// open our data file
		FileManager fm = new FileManager();
	    fm.OpenFile(DATA_STORAGE_FILENAME);

		// seek to position of the data page that corresponds to dataByteOffset
        int DataPagePos=dataByteOffset/DataPageSize; // find the datapage that the dataByteOffset corresponds to
        byte[] pageBytes = new byte[DataPageSize];
	    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(pageBytes));
	    
	    try {
			fm.ReadBlock(DataPagePos,pageBytes);  // read DATA_PAGE_SIZE bytes
			MoultiCounter.increaseCounter(1);
         }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		// get the part of the bytes that corresponds to dataByteOffset (--> pageBytesData), and transform to a Data instance
		byte[] pageBytesData= new byte[RecordSize];
		int DataPageStartOfData=(dataByteOffset-DataPagePos*DataPageSize);
		int k=0;
		for(int i=DataPageStartOfData; i<DataPageStartOfData+32; i++) {
			pageBytesData[k]=pageBytes[i];
			k=k+1;

		}
	
	    result = new Data();
		result = result.fromByteArray(pageBytes, dataByteOffset);// Converting The Bytes Into A Data Instance
		// before returning it, cache it for future reference
		this.cacheData(dataByteOffset, result);
		return result;
	}

	public BTreeInnerNode newInnerNode() {
		//System.out.println("/////////////////////////////////////////////Creating NewInnerNode..");
		BTreeInnerNode result = new BTreeInnerNode();		// creates a new BTreeInnerNode
		this.aquireNodeStorage(result);						// finds the next available DataPage for Storing to the file
		result.setDirty();
		this.cacheNode(result.getStorageDataPage(), result);// Puts it to cache for future reference
		return result;
	}
	public BTreeLeafNode newLeafNode() {
		//System.out.println("///////////////////////////////////////////////Creating NewLeafNode..");
		BTreeLeafNode result = new BTreeLeafNode();			// creates a new BTreeLeafNode
		this.aquireNodeStorage(result);						// finds the next available DataPage for Storing to the file
		result.setDirty();
		this.cacheNode(result.getStorageDataPage(), result);// Puts it to cache for future reference
		return result;
	}

	// Finds the next available DataPage 
	// and sets it on given node
	private void aquireNodeStorage(BTreeNode node) {
		
		int dataPageIndex = 0;
		//FileManager fm= new FileManager();
		//fm.OpenFile(NODE_STORAGE_FILENAME);
		dataPageIndex=NodeFileCounter;
		this.NodeFileCounter=this.NodeFileCounter +1;
		node.setStorageDataPage(dataPageIndex);// Increase The Counter Of The DataPages
	}

	// Takes A Data InStance, Calculates Where It Will Be Stored In The 
	// Data File And Put The Instance To The Cache
	public int newData(Data result, int nextFreeDatafileByteOffset) {
		int NO_OF_DATA_BYTES = 32;
		result.setStorageByteOffset(nextFreeDatafileByteOffset);
		result.setDirty(); // so that it will written to disk at next flush
		this.cacheData(result.getStorageByteOffset(), result);
		return nextFreeDatafileByteOffset + NO_OF_DATA_BYTES;
	}

}