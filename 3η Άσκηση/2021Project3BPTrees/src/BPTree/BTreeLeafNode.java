package BPTree;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class BTreeLeafNode<TKey extends Comparable<TKey>, TValue> extends BTreeNode<TKey> {
	protected final static int LEAFORDER = 29;
	
    public final int Type = 1; // 1 Refers To LeafNode
	private final int DataPageSize=256;
			
	private Integer[] values; // integers pointing to byte offset in data file

	public BTreeLeafNode() {
		this.keys = new Object[LEAFORDER + 0];
		this.values =new Integer[LEAFORDER + 0];
	}
	public TValue getValue(int index) {
		return (TValue)StorageCache.getInstance().retrieveData(this.values[index]);
	}

	public void setValue(int index, TValue value) {
		Data buff= (Data) value;
		if(buff != null)
			this.values[index] = (Integer)buff.getStorageByteOffset();
		else
			this.values[index]=null;
		setDirty(); // we changed a value, so this node is dirty and must be flushed to disk
	}

	@Override
	public TreeNodeType getNodeType() {
		return TreeNodeType.LeafNode;
	}
	
	@Override
	public int search(TKey key) {
		for (int i = 0; i < this.getKeyCount(); ++i) {
			 int cmp = this.getKey(i).compareTo(key);
			 if (cmp == 0) {
				 return i;
			 }
			 else if (cmp > 0) {
				 return -1;
			 }
		}	
		return -1;
	}
	
	/* The codes below are used to support insertion operation */
	
	public void insertKey(TKey key, TValue value) {
		int index = 0;
		while (index < this.getKeyCount() && this.getKey(index).compareTo(key) < 0)
			++index;
		this.insertAt(index, key, (TValue)value);
		//System.out.println("Inserting Key: "+key+" with value "+value+" at index "+index+" Of datapage: "+this.getStorageDataPage());
	}
	
	private void insertAt(int index, TKey key, TValue value) {
		// move space for the new key
		for (int i = this.getKeyCount() - 1; i >= index; --i) {
			this.setKey(i + 1, this.getKey(i));
			this.setValue(i + 1, (TValue)this.getValue(i));
		}
		// insert new key and value
		this.setKey(index, key);
		this.setValue(index, value);
		// setDirty() will be called in setKey/setValue
		++this.keyCount;
		
	}

	
	/**
	 * When splits a leaf node, the middle key is kept on new node and be pushed to parent node.
	 */
	@Override
	protected BTreeNode<TKey> split() {
		int midIndex = this.getKeyCount() / 2;
		
		//BTreeLeafNode<TKey, Integer> newRNode = new BTreeLeafNode<TKey, Integer>();
		BTreeLeafNode<TKey, TValue> newRNode = StorageCache.getInstance().newLeafNode();
		//System.out.println("Inside Split 1");
		for (int i = midIndex; i < this.getKeyCount(); ++i) {
			newRNode.setKey(i - midIndex, this.getKey(i));
			newRNode.setValue(i - midIndex, (TValue) this.getValue(i));
			this.setKey(i, null);
			this.setValue(i, null);
		}
		newRNode.keyCount = this.getKeyCount() - midIndex;
		this.keyCount = midIndex;

		setDirty();// just to make sure
		return newRNode;
	}

	@Override
	protected BTreeNode<TKey> pushUpKey(TKey key, BTreeNode<TKey> leftChild, BTreeNode<TKey> rightNode) {
		throw new UnsupportedOperationException();
	}
	
	/* The codes below are used to support deletion operation */
	
	public boolean delete(TKey key) {
		int index = this.search(key);
		if (index == -1)
			return false;
		
		this.deleteAt(index);
		return true;
	}
	
	private void deleteAt(int index) {
		int i = index;
		for (i = index; i < this.getKeyCount() - 1; ++i) {
			this.setKey(i, this.getKey(i + 1));
			this.setValue(i, (TValue) this.getValue(i + 1));
		}
		this.setKey(i, null);
		this.setValue(i, null);
		--this.keyCount;

		// setDirty will be called through setKey/setValue
	}

	@Override
	protected void processChildrenTransfer(BTreeNode<TKey> borrower, BTreeNode<TKey> lender, int borrowIndex) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected BTreeNode<TKey> processChildrenFusion(BTreeNode<TKey> leftChild, BTreeNode<TKey> rightChild) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Notice that the key sunk from parent is be abandoned. 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void fusionWithSibling(TKey sinkKey, BTreeNode<TKey> rightSibling) {
		BTreeLeafNode<TKey, TValue> siblingLeaf = (BTreeLeafNode<TKey, TValue>)rightSibling;
		
		int j = this.getKeyCount();
		for (int i = 0; i < siblingLeaf.getKeyCount(); ++i) {
			this.setKey(j + i, siblingLeaf.getKey(i));
			this.setValue(j + i, (TValue) siblingLeaf.getValue(i));
		}
		this.keyCount += siblingLeaf.getKeyCount();
		
		this.setRightSibling(siblingLeaf.getRightSibling());
		if (siblingLeaf.rightSibling != null || siblingLeaf.rightSibling != -1)
			siblingLeaf.getRightSibling().setLeftSibling(this);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected TKey transferFromSibling(TKey sinkKey, BTreeNode<TKey> sibling, int borrowIndex) {
		BTreeLeafNode<TKey,TValue > siblingNode = (BTreeLeafNode<TKey,TValue >)sibling;

		this.insertKey(siblingNode.getKey(borrowIndex), (TValue) siblingNode.getValue(borrowIndex));
		siblingNode.deleteAt(borrowIndex);

		// setDirty will be called through setKey/setValue in deleteAt
		return borrowIndex == 0 ? sibling.getKey(0) : this.getKey(0);
	}

	protected byte[] toByteArray() {
		/* This Method Converts A BTreeLeafNode Into A Byte Array For Later Storing In The File */
		
		java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(DataPageSize);
		
		bb.putInt(Type); 					// 4 bytes for marking this as a inner node (e.g. an int with value = 0 for inner node and 1 for leaf node)
		if(this.leftSibling != null)
			bb.putInt(this.leftSibling);	 // 4 bytes for left sibling
		else
			bb.putInt(-1);			
		if(this.rightSibling != null)
			bb.putInt(this.rightSibling);    // 4 bytes for right sibling
		else
			bb.putInt(-1);
		if(this.parentNode != null)
			bb.putInt(this.parentNode);      // 4 bytes for parent
		else
			bb.putInt(-1);
		bb.putInt(this.keyCount);       	 // 4 bytes for the number of keys
		
		for(int i=0; i<LEAFORDER; i++) {     // LEAFORDER*SizeOfInt() bytes for the keys
			if((Integer)this.keys[i] != null)
				bb.putInt((int) this.keys[i]);
			else
				bb.putInt(-1);
		}
		for(int i=0; i<LEAFORDER; i++) {	 // LEAFORDER*SizeOfInt() bytes for the values
			if(this.values[i] != null)
				bb.putInt(this.values[i]);
			else
				bb.putInt(-1);
		}
		
		byte[] byteArray = new byte[DataPageSize]; // DataPageSize: demo size of our data page(256)
        byteArray=bb.array();	
        
		return byteArray;						   // Returning The Byte Array 

	}
	
	public BTreeLeafNode<TKey, TValue> fromByteArray(byte[] byteArray, int dataPageOffset) {
		// this takes a byte array of fixed size, and transforms it to a BTreeLeafNode
		// it takes the format we store our node (as specified in toByteArray()) and constructs the BTreeLeafNode
		// We need as parameter the dataPageOffset in order to set it
		BTreeLeafNode<TKey, TValue> result = new BTreeLeafNode<TKey,TValue>();
		result.setStorageDataPage(dataPageOffset);
       
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(byteArray));
	    try {
	    	int Type=dis.readInt();			// Reads the type of the node -> must be BtreeLeafNode
	    	int LeafType=1;
			if(Type != LeafType)
				System.err.println("Wrong Leaf Type At BTreeLeafNode.fromByteArray!");
	    	
	    	int left=dis.readInt();			// Reads the datapage of the left sibling
	    	if(left == -1)
	    		result.leftSibling=null;
	    	else
	    		result.leftSibling=left;
			int right=dis.readInt();	    // Reads the datapage of the right sibling
			if(right == -1)
				result.rightSibling=null;
			else
				result.rightSibling=right;
			int par=dis.readInt();	        // Reads the datapage of the parent
			if(par == -1)
				result.parentNode=null;
			else
				result.parentNode=par;
		    result.keyCount=dis.readInt();  // Reads the amount of keys
		    for(int i=0; i<LEAFORDER; i++) {// Reads all the keys
		    	Integer buff=dis.readInt();
		    	if(buff != -1)
		    		result.keys[i]=buff;  
		    	else
		    		result.keys[i]=null;
		    }
		    for(int i=0; i<LEAFORDER; i++) {// Reads all the values
		    	Integer buff=dis.readInt();
		    	if(buff != -1)
		    		result.values[i]=buff;
		    	else
		    		result.values[i]=null;
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return result;					  // Returns the node that result
	}
}