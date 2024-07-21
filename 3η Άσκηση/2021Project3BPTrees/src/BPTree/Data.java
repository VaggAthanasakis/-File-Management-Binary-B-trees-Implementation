package BPTree;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
	 * Contains our data. It is of fixed byte array size for writing to or reading to the data file
	 * @author sk
	 *
	 */
	public class Data {
		private int storageByteOffset; // this node is stored at byte index storageByteOffset in the data file. We must calculate the datapage this corresponds to in order to read or write it
        private final int DataSize=32;
        private final int DataPageSize=256;
		
		private int data1;
		private int data2;
		private int data3;
		private int data4;

		private boolean dirty;

		public Data() {
			this.data1 = 0;
			this.data2 = 0;
			this.data3 = 0;
			this.data4 = 0;
		}
		public Data(int data1, int data2, int data3, int data4) {
			this.data1 = data1;
			this.data2 = data2;
			this.data3 = data3;
			this.data4 = data4;
		}

		public boolean isDirty() {
			return this.dirty;
		}
		public void setDirty() {
			this.dirty = true;
		}
		public void setStorageByteOffset(int storageByteOffset) {
			this.storageByteOffset = storageByteOffset;
		}
		public int getStorageByteOffset() {
			return this.storageByteOffset;
		}

		@Override
		public String toString() {

			return "data1: "+data1+", data2: "+data2+", data3: "+data3+", data4: "+data4;
		}


		/* takes a Data class, and transforms it to an array of bytes 
		  we can't store it as is to the file. We must calculate the data page based on storageByteIndex, load the datapage, replace
		  the part starting from storageByteIndex, and then store the data page back to the file
		  */ 


		protected byte[] toByteArray() {
			
			int DataPage=storageByteOffset/DataPageSize; // Calculate The DataPage Based On StorageByteIndex
			byte[] byteArray = new byte[DataSize]; // 32: demo size of our data. This should be some constant

			java.nio.ByteBuffer bb=java.nio.ByteBuffer.allocate(DataSize);
			bb.order(ByteOrder.BIG_ENDIAN);
			bb.putInt(DataPage);
			bb.putInt(this.data1);
			bb.putInt(this.data2);
			bb.putInt(this.data3);
			bb.putInt(this.data4);
			byteArray=bb.array();
			return byteArray;

		}

		/* 
		 this takes a byte array of fixed size, and transforms it to a Data class instance
		 it takes the format we store our Data (as specified in toByteArray()) and constructs the Data
		 We need as parameter the storageByteIndex in order to set it
		 */
		protected Data fromByteArray(byte[] byteArray, int storageByteOffset) {
			
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(byteArray));
			try {
				int DataPage = dis.readInt();
				storageByteOffset=DataPage*DataPageSize;
				int data1=dis.readInt();
				int data2=dis.readInt();
				int data3=dis.readInt();
				int data4=dis.readInt();			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Data result = new Data(data1,data2,data3,data4);
			result.setStorageByteOffset(storageByteOffset);

			return result;
		}
	}
	

