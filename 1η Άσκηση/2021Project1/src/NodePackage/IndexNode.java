package NodePackage;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.ByteOrder;

public class IndexNode {
	int key,pagePos;

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getPagePos() {
		return pagePos;
	}

	public void setPagePos(int pagePos) {
		this.pagePos = pagePos;
	}
	
	public IndexNode() {
		
	}
	
	
	/*This Method Takes As Input A Key And A Page Position Integer And Returns A Byte Array Of Them */
   	public byte[] IndexNodeByteArray(int key, int page) {
   		java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(8); // allocate 8 bytes for the output
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(key);
        bb.putInt(page);
        byte[] result=bb.array();
        return result;
   	}
	
	
	/*This Constructor Takes As Input A Byte Array And Returns An IndexNode With The Info From The Byte Array*/
    public IndexNode(byte[] byteArray) throws Exception {
		
		if(byteArray.length != 8 || byteArray==null) {
			throw new Exception("Wrong Byte Array Size!");
		}
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(byteArray));
		this.key=dis.readInt();
		this.pagePos=dis.readInt();	
	}
	
}
