package NodePackage;

import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets ;

public class Node {
    
	/*Total Node Size: 32 bytes*/
	private int key; //4 bytes
	String StringArray;
	private int int1,int2; //8 bytes
	
	
	
	public String getStringArray() {
		return StringArray;
	}

	public void setStringArray(String stringArray) {
		StringArray = stringArray;
	}

	public int getInt1() {
		return int1;
	}

	public void setInt1(int int1) {
		this.int1 = int1;
	}

	public int getInt2() {
		return int2;
	}

	public void setInt2(int int2) {
		this.int2 = int2;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	
	public Node() {
		
	}
	
	
	/*This Method Takes As Input 3 Integers And A String And Returns A ByteArray Of Them*/
	public byte[] NodeByteArray(int key,String dataString,int int1, int int2) {
		java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(32); // allocate 32 bytes for the output
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putInt(key);
		bb.put(dataString.getBytes(StandardCharsets.US_ASCII));
		bb.putInt(int1);
		bb.putInt(int2);
		byte[] result =  bb.array();
		return result;
	}
	
	/*This Constructor Takes A ByteArray As Input And Creates A Node*/
	public Node(byte[] byteArray) throws Exception {
		
		if(byteArray.length != 32 || byteArray==null) {
			throw new Exception("Wrong Byte Array Size!");
		}
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(byteArray));
		this.key=dis.readInt();
		byte[] bytesForDataString = new byte[20];
		dis.read(bytesForDataString,0,20);
		this.StringArray=new String(bytesForDataString, StandardCharsets.US_ASCII);
		this.int1=dis.readInt();
		this.int2=dis.readInt();			
	}
	
}
