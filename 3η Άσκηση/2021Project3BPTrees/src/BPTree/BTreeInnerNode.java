package BPTree;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class BTreeInnerNode<TKey extends Comparable<TKey>> extends BTreeNode<TKey> {
	protected final static int INNERORDER = 29;

	protected Integer[] children;
	private final int DataPageSize=256;
	public int Type = 0;      // 0 Refers to InnerNode

	public BTreeInnerNode() {
		this.keys = new Object[INNERORDER];
		this.children = new Integer[INNERORDER + 1];
	}

	@SuppressWarnings("unchecked")
	public BTreeNode<TKey> getChild(int index) {
		if(this.children[index] != null)
			return (BTreeNode<TKey>)StorageCache.getInstance().retrieveNode(this.children[index]);
		else
			return null;
	}

	public void setChild(int index, BTreeNode<TKey> child) {
		if(child == null) {
			this.children[index]=null;
		}
		else
			this.children[index] = child.getStorageDataPage();

		if (child != null)
			child.setParent(this);
		setDirty();
	}

	@Override
	public TreeNodeType getNodeType() {
		return TreeNodeType.InnerNode;
	}
	
	@Override
	public int search(TKey key) {
		int index = 0;
		for (index = 0; index < this.getKeyCount(); ++index) {
			int cmp = this.getKey(index).compareTo(key);
			if (cmp == 0) {
				return index + 1;
			}
			else if (cmp > 0) {
				return index;
			}
		}	
		return index;
	}
	
	
	/* The codes below are used to support insertion operation */
	
	private void insertAt(int index, TKey key, BTreeNode<TKey> leftChild, BTreeNode<TKey> rightChild) {
		// move space for the new key
		for (int i = this.getKeyCount()+1 ; i > index; --i) {// Was this.getKeyCount()+1
			//System.out.println(this.keyCount);
			this.setChild(i, this.getChild(i - 1));
		}
		for (int i = this.getKeyCount(); i > index; --i) {
			this.setKey(i, this.getKey(i - 1));
		}
		
		// insert the new key
		this.setKey(index, key);
		this.setChild(index, leftChild);
		this.setChild(index + 1, rightChild);
		this.keyCount += 1;

	}

	/**
	 * When splits a internal node, the middle key is kicked out and be pushed to parent node.
	 */
	@Override
	protected BTreeNode<TKey> split() {
		int midIndex = this.getKeyCount() / 2;
		
		BTreeInnerNode<TKey> newRNode = StorageCache.getInstance().newInnerNode();
		
		for (int i = midIndex + 1; i < this.getKeyCount(); ++i) {
			newRNode.setKey(i - midIndex - 1, this.getKey(i));
			this.setKey(i, null);
		}
		for (int i = midIndex + 1; i <= this.getKeyCount(); ++i) {
			newRNode.setChild(i - midIndex - 1, this.getChild(i));
			newRNode.getChild(i - midIndex - 1).setParent(newRNode);
			this.setChild(i, null);
		}
		this.setKey(midIndex, null);
		newRNode.keyCount = this.getKeyCount() - midIndex - 1;
		this.keyCount = midIndex;

		setDirty();
		return newRNode;
	}

	@Override
	protected BTreeNode<TKey> pushUpKey(TKey key, BTreeNode<TKey> leftChild, BTreeNode<TKey> rightNode) {
		// find the target position of the new key
		int index = this.search(key);
		
		// insert the new key
		this.insertAt(index, key, leftChild, rightNode);
		// check whether current node need to be split
		if (this.isOverflow()) {
			return this.dealOverflow();
		}
		else {
			return this.getParent() == null ? this : null;
		}
	}
	
	/* The codes below are used to support delete operation */
	
	private void deleteAt(int index) {
		int i = 0;
		for (i = index; i < this.getKeyCount() - 1; ++i) {
			this.setKey(i, this.getKey(i + 1));
			this.setChild(i + 1, this.getChild(i + 2));
		}
		this.setKey(i, null);
		this.setChild(i + 1, null);
		--this.keyCount;
		setDirty();
	}


	@Override
	protected void processChildrenTransfer(BTreeNode<TKey> borrower, BTreeNode<TKey> lender, int borrowIndex) {
		int borrowerChildIndex = 0;
		while (borrowerChildIndex < this.getKeyCount() + 1 && this.getChild(borrowerChildIndex) != borrower)
			++borrowerChildIndex;
		
		if (borrowIndex == 0) {
			// borrow a key from right sibling
			TKey upKey = borrower.transferFromSibling(this.getKey(borrowerChildIndex), lender, borrowIndex);
			this.setKey(borrowerChildIndex, upKey);
		}
		else {
			// borrow a key from left sibling
			TKey upKey = borrower.transferFromSibling(this.getKey(borrowerChildIndex - 1), lender, borrowIndex);
			this.setKey(borrowerChildIndex - 1, upKey);
		}
	}
	
	@Override
	protected BTreeNode<TKey> processChildrenFusion(BTreeNode<TKey> leftChild, BTreeNode<TKey> rightChild) {
		int index = 0;
		while (index < this.getKeyCount() && this.getChild(index) != leftChild)
			++index;
		TKey sinkKey = this.getKey(index);
		
		// merge two children and the sink key into the left child node
		leftChild.fusionWithSibling(sinkKey, rightChild);
		
		// remove the sink key, keep the left child and abandon the right child
		this.deleteAt(index);
		
		// check whether need to propagate borrow or fusion to parent
		if (this.isUnderflow()) {
			if (this.getParent() == null) {
				// current node is root, only remove keys or delete the whole root node
				if (this.getKeyCount() == 0) {
					leftChild.setParent(null);
					return leftChild;
				}
				else {
					return null;
				}
			}
			return this.dealUnderflow();
		}	
		return null;
	}
	
	
	@Override
	protected void fusionWithSibling(TKey sinkKey, BTreeNode<TKey> rightSibling) {
		BTreeInnerNode<TKey> rightSiblingNode = (BTreeInnerNode<TKey>)rightSibling;
		
		int j = this.getKeyCount();
		this.setKey(j++, sinkKey);
		
		for (int i = 0; i < rightSiblingNode.getKeyCount(); ++i) {
			this.setKey(j + i, rightSiblingNode.getKey(i));
		}
		for (int i = 0; i < rightSiblingNode.getKeyCount() + 1; ++i) {
			this.setChild(j + i, rightSiblingNode.getChild(i));
		}
		this.keyCount += 1 + rightSiblingNode.getKeyCount();
		
		this.setRightSibling(rightSiblingNode.getRightSibling());
		if (rightSiblingNode.rightSibling != null ||rightSiblingNode.rightSibling != -1)
			rightSiblingNode.getRightSibling().setLeftSibling(this);
	}
	
	@Override
	protected TKey transferFromSibling(TKey sinkKey, BTreeNode<TKey> sibling, int borrowIndex) {
		BTreeInnerNode<TKey> siblingNode = (BTreeInnerNode<TKey>)sibling;
		
		TKey upKey = null;
		if (borrowIndex == 0) {
			// borrow the first key from right sibling, append it to tail
			int index = this.getKeyCount();
			this.setKey(index, sinkKey);
			this.setChild(index + 1, siblingNode.getChild(borrowIndex));			
			this.keyCount += 1;
			upKey = siblingNode.getKey(0);
			siblingNode.deleteAt(borrowIndex);
		}
		else {
			// borrow the last key from left sibling, insert it to head
			this.insertAt(0, sinkKey, siblingNode.getChild(borrowIndex + 1), this.getChild(0));
			upKey = siblingNode.getKey(borrowIndex);
			siblingNode.deleteAt(borrowIndex);
		}

		setDirty();
		return upKey;
	}

	protected byte[] toByteArray() {
		/* This Method Converts A BTreeInnerNode Into A Byte Array For Later Storing In The File */
		
		java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(DataPageSize);
		bb.putInt(Type);				     // 4 bytes for marking this as a inner node (e.g. an int with value = 0 for inner node and 1 for leaf node)
		if(this.leftSibling != null)
			bb.putInt(this.leftSibling);     // 4 bytes for left sibling
		else
			bb.putInt(-1);                   // -1 since we cannot put <null> at the bytebuffer
		if(this.rightSibling != null)
			bb.putInt(this.rightSibling);
		else
		    bb.putInt(-1);    				 // 4 bytes for right sibling
		if(this.parentNode != null)
			bb.putInt(this.parentNode);      // 4 bytes for parent
		else
			bb.putInt(-1);
		bb.putInt(this.keyCount);            // 4 bytes for the number of keys
		
		// The rest in our data page are for the list of pointers to children, and the keys. Depending on the size of our data page
		// we can calculate the order our tree
		for(int i=0; i<INNERORDER; i++) {	 // INNERORDER*SizeOfInt() bytes for the keys
			if(this.keys[i] != null)
				bb.putInt((int) this.keys[i]);
			else
				bb.putInt(-1);
		}
		for(int i=0; i<INNERORDER+1; i++) { // (INNERORDER+1)*SizeOfInt() bytes for the children
			if(this.children[i] != null)
				bb.putInt(this.children[i]);
			else
				bb.putInt(-1);
		}
		byte[] byteArray = new byte[DataPageSize]; // 256: demo size of our data page
		byteArray=bb.array();
		
		return byteArray;						   // Returns The ByteArray

	}
	public BTreeInnerNode<TKey> fromByteArray(byte[] byteArray, int dataPageOffset) {
		// this takes a byte array of fixed size, and transforms it to a BTreeInnerNode
		// it takes the format we store our node (as specified in BTreeInnerNode.toByteArray()) and constructs the BTreeInnerNode
		// We need as parameter the dataPageOffset in order to set it
		BTreeInnerNode<TKey> result = new BTreeInnerNode<TKey>();
		result.setStorageDataPage(dataPageOffset);

		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(byteArray));
	    try {
	        int type=dis.readInt();			// Reads the type of the node -> must be BtreeInnerNode
	        int InnerType=0;
			if(type != InnerType)
				System.err.println("Wrong Type At BTreeInnerNode.fromByteArray!!");
	        result.Type=type;
	        int left=dis.readInt();		   // Reads the datapage of the left sibling
	        if(left == -1)
	        	result.leftSibling=null;
	        else
	        	result.leftSibling=left;
	        int right=dis.readInt();	   // Reads the datapage of the right sibling
			if(right == -1)
				result.rightSibling=null;
			else
				result.parentNode=right;
			int par=dis.readInt();		   // Reads the datapage of the parent
			if(par == -1)
				result.parentNode=null;
			else
				result.parentNode=par;
		    result.keyCount=dis.readInt();// Reads the amount of keys
		 
		    for(int i=0; i<INNERORDER; i++) {// Reads all the keys
		    	Integer buff=dis.readInt();
		    	if(buff != -1)
		    		result.keys[i]=buff;
		    	else
		    		result.keys[i]=null;
		    }
		    for(int i=0; i<INNERORDER+1; i++) {// Reads all the children
		    	Integer buff=dis.readInt();
		    	if(buff != -1)
		    		result.children[i]=buff;
		    	else
		    		result.children[i]=null;
	
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;				         // Returns the node that result
	}
}