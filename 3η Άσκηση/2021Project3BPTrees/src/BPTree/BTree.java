package BPTree;

/**
 * Original at https://github.com/linli2016/BPlusTree 
 * A B+ tree
 * Since the structures and behaviors between internal node and external node are different, 
 * so there are two different classes for each kind of node.
 * @param <TKey> the data type of the key
 * @param <TValue> the type of the value
 */
public class BTree<TKey extends Comparable<TKey>, TValue> {
	private BTreeNode<TKey> root;
	int LEAFORDER=29;


	private int nextFreeDatafileByteOffset = 0; // for this assignment, we only create new, empty files. We keep here the next free byteoffset in our file

	public BTree() {
		this.root = StorageCache.getInstance().newLeafNode(); // Creating A New Leaf Node That Stores The Root
		this.root.leftSibling=this.root.rightSibling=this.root.parentNode=null;
		this.root.keyCount=0;
		//this.root.keys=null;
		//Data rootData= new Data(1,2,3,4);
		//StorageCache.getInstance().newData(rootData, nextFreeDatafileByteOffset);
		StorageCache.getInstance().flush();
	}

	/**
	 * Insert a new key and its associated value into the B+ tree.
	 */
	public void insert(TKey key, TValue value) {
		
		this.root = StorageCache.getInstance().retrieveNode(this.root.getStorageDataPage());
	
		nextFreeDatafileByteOffset = StorageCache.getInstance().newData((Data)value, nextFreeDatafileByteOffset);
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key); // finds the leaf that should contain this specific key
		
		leaf.insertKey(key, value);			   // insert the key to this leaf with it's value		
		
		if (leaf.isOverflow()) {
			BTreeNode<TKey> n = leaf.dealOverflow();
			if (n != null)
				this.root = n; 
		}
		StorageCache.getInstance().flush();
	}

	/**
	 * Search a key value on the tree and return its associated value.
	 */
	public TValue search(TKey key) {
		//System.out.println("");
		this.root = StorageCache.getInstance().retrieveNode(this.root.getStorageDataPage());
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);

		int index = leaf.search(key);
		/*if(index != -1)
			System.out.println("BTree.Search Found The Key: "+leaf.getValue(index));
		else
			System.out.println("BTree.Search Key Not Found");*/
		return  ((index == -1) ? null : leaf.getValue(index));
	}
	
	/* This Method Searches A Range Of Keys Between keyMin And keyMax */
	public void searchRange(TKey keyMin, TKey keyMax) {
		this.root = StorageCache.getInstance().retrieveNode(this.root.getStorageDataPage());
		System.out.println("Finding Range Between "+keyMin+" And "+keyMax);
		int index=-1;
		int counter1=0;
		Integer tmpMin=(Integer) keyMin;
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(keyMin);
		
		
		do {
			index=leaf.search((TKey)tmpMin);
			++tmpMin;
		}
		while(index == -1 || counter1 <= LEAFORDER); 
		
		if(index != -1) { // It Found The Key Inside The Leaf
			int i=index;
			while(i<LEAFORDER || (Integer)leaf.getKey(i) <= (Integer)keyMax) {
				System.out.println("At Print Range:Key "+leaf.getKey(i)+"With Data "+leaf.getValue(i));
				++i;	
			}
			if(i >= LEAFORDER) { // Must go to the right sibling
				if(leaf.getRightSibling() != null) {
					BTreeNode rightSibling=StorageCache.getInstance().retrieveNode(leaf.getRightSibling().getStorageDataPage());
					
					//
				}
				else
					return;	   // leaf has not right sibling     
			}
			else if(counter1 > LEAFORDER) {
				BTreeNode rightSibling=StorageCache.getInstance().retrieveNode(leaf.getRightSibling().getStorageDataPage());
				//
			}
			
		}
		

	}
	
	/**
	 * Delete a key and its associated value from the tree.
	 */
	public void delete(TKey key) {
		this.root = StorageCache.getInstance().retrieveNode(this.root.getStorageDataPage());
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		
		if (leaf.delete(key) && leaf.isUnderflow()) {
			BTreeNode<TKey> n = leaf.dealUnderflow();
			if (n != null)
				this.root = n; 
		}
		// CHANGE FOR STORING ON FILE
		StorageCache.getInstance().flush();
	}
	
	

	/**
	 * Search the leaf node which should contain the specified key
	 */
	@SuppressWarnings("unchecked")
	private BTreeLeafNode<TKey, TValue> findLeafNodeShouldContainKey(TKey key) {
		this.root = StorageCache.getInstance().retrieveNode(this.root.getStorageDataPage());
		BTreeNode<TKey> node = this.root;	
		while (node.getNodeType() == TreeNodeType.InnerNode) {
			node = ((BTreeInnerNode<TKey>)node).getChild( node.search(key) );	
		}
		return (BTreeLeafNode<TKey, TValue>) node;
	}		
	
}	
		