package FileProcessPackage;

import java.io.*;

import NodePackage.*;

public class BlockToNodeArray {

    private final int DataPageSize=128;
    private final int NodeSize=32;
    private final int IndexNodeSize=8;
    Node[] ArrayOfNodes= new Node[4];
    IndexNode[] ArrayOfIndexNodes= new IndexNode[16];
    
    
    public BlockToNodeArray() {
    	
    }
	
	/* This Method Is Used To Take a Byte Array Of 128 Bytes(DataPage) 
     * And To Return A Node Array */	
    public Node[] BlockToNodeArrayProccess(byte[] DataPage) throws Exception {
    	if(DataPage == null || DataPage.length != DataPageSize) {
    		throw new Exception("Invalid Size");
    	}
    	DataInputStream dis= new DataInputStream(new ByteArrayInputStream(DataPage));
    	byte[] Nodebytes= new byte[NodeSize];
    	for(int i=0; i<4; i++) {
    		dis.readNBytes(Nodebytes, 0, NodeSize);
    		Node n= new Node(Nodebytes);
    		ArrayOfNodes[i]=n;
    	}
    	return ArrayOfNodes;
    }
    
    /* This Method Is Used To Take a Byte Array Of 128 Bytes 
     * And To Return A IndexNode Array */
    public IndexNode[] BlockToIndexNodeArrayProccess(byte[] DataPage) throws Exception {
    	if(DataPage == null || DataPage.length != DataPageSize) {
    		throw new Exception("Invalid Size");
    	}
    	DataInputStream dis= new DataInputStream(new ByteArrayInputStream(DataPage));
    	byte[] IndexNodebytes= new byte[IndexNodeSize];
    	for(int i=0; i<16; i++) {
    		dis.readNBytes(IndexNodebytes, 0, IndexNodeSize);
    		IndexNode in= new IndexNode(IndexNodebytes);
    		ArrayOfIndexNodes[i]=in;
    	}
    	return ArrayOfIndexNodes;
    }
	
}
