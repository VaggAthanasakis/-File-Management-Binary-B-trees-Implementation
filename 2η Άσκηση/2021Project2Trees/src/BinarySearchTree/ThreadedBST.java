package BinarySearchTree;

import Counter.MoultiCounter;

/* This Class Implements Threaded BST Using Array */

public class ThreadedBST implements BinarySearchTree{

	private int data[][];
	private final int IsTrue=1;
	private final int IsFalse=0;
	private int avail;
	private int root=-1;
	
	/* Constructor Of Threaded BST  */
	public ThreadedBST(int N) {
		setup(N);
	}
	
	/* Method That Initializes Threaded BST
	 * Sets Next Available Position At Zero
	 * Initializes Stack To Have The Next Available Positions For Insertion */
	private void setup(int N) {
		avail=0;
		data=new int[N][5];
		for(int i=0; i<N; i++) {
			data[i][0]=data[i][1]=-1;     // Childs Set Null
			data[i][2]=i+1;               // Initialization Of Stack
			data[i][3]=data[i][4]=IsFalse;
		}
		data[N-1][2]=-1;
	}
	
	/* Method That Calls insertHelp For Insertion */
	public void insert(int val) {
		insertHelp(this.root,val);	
	}
	
	/* Method That Calls printRangeHelp For Printing A Specific Range Of Values */
	public void printRange(int min, int max) {
		printRangeHelp(min,max);
	}

	/* Method That Calls findHelp To Find A Key */
	public int find(int key) {
		int position=findHelp(key);
		MoultiCounter.increaseCounter(4);
		return position;
	}
	
	/* Method That Prints Threaded BST Inorder -> Sorted Array */
	public void inorder(int[] sortedArray) {
		int i=0;
		if(root==-1) {
			System.out.println("Threaded Binary Search Tree Is Empty!");
			return;
		}
		int ptr=root;
		while(data[ptr][3]==IsFalse) {               // At First It Finds The Most Left Node That Has A Thread 
			ptr=data[ptr][1];
		}
		while(ptr!=-1) {
			System.out.println(data[ptr][0]+" ");    // It Prints The Node Or It Inserts It Into An Array
			sortedArray[i]=data[ptr][0]; 
			ptr=inorderSuccessor(ptr);               // Then Calls inorderSuccessor For ptr Definition
			i+=1;
		}
	}
	
	private int inorderSuccessor(int ptr) {
		if(data[ptr][4]==IsTrue) {                   // If Node(ptr).right Is A Thread Then Returns Node(ptr).right
			return data[ptr][2]; 
		}
		ptr=data[ptr][2];                            // Else if Node(ptr).right Is A Child, Sets ptr= Node(ptr).right And Returns Left Most Node Of This SubTree
		while(data[ptr][3]==IsFalse) {
			ptr=data[ptr][1];
		}
		return ptr;	
	}
	
	/* Method That Inserts A New Key At The Tree */
	private void insertHelp(int root, int value) {
		int ptr=root;
		int par=-1;
		MoultiCounter.increaseCounter(2,2);
		
		/* This Loop Defines The Parent Of The New Key By Crossing Down To The Childs Till It Finds A Node Whose Child Is Thread */
		while(MoultiCounter.increaseCounter(2) && ptr != -1) {                
			if(MoultiCounter.increaseCounter(2) && value==data[ptr][0]) {
				System.out.println("Duplicate Key!");
				return;
			}
			par=ptr;
			MoultiCounter.increaseCounter(2);
			if(MoultiCounter.increaseCounter(2) && value < data[ptr][0]) {
				if(MoultiCounter.increaseCounter(2) && data[ptr][3]==IsFalse) { // If Left Node Is Child Then Keep Crossing Down Else Stop
					ptr=data[ptr][1];
					MoultiCounter.increaseCounter(2);
				}
				else
					break;		
			}
			else {
				if(MoultiCounter.increaseCounter(2) && data[ptr][4]==IsFalse) {
					ptr=data[ptr][2];
					MoultiCounter.increaseCounter(2);
				}
				else
					break;
			}
		}
		/* Now par Is The Parent Of The New Node (key) */
		data[avail][0]=value;                             // Setting Nodes Info. Avail Is The Positions That The New Node Is Inserted. 
		data[avail][3]=data[avail][4]=IsTrue;             // The New Node Has Threads, Not Childs
		MoultiCounter.increaseCounter(2,2);
		
		if(MoultiCounter.increaseCounter(2) && par==-1) { 				    // If par==-1 The Tree Is Empty
			this.root=avail;                             				    // Setting root At The Positions Of The Node That Is Inserted
			data[avail][1]=-1;                           				    // Left Child=null
			int TmpAvail=data[avail][2];                  					// Renew Avail's Value From The Stack Of Next Available Positions
			data[avail][2]=-1;							  					// Right Child=null
			data[avail][3]=data[avail][4]=IsFalse;       				    // New Node-Root Has Not Threads
			avail=TmpAvail;
			MoultiCounter.increaseCounter(2,6);
		}
		else if(MoultiCounter.increaseCounter(2) && value < data[par][0]) {	// Else If Tree Is Not Empty And value < Node(par).info	
			    int TmpAvail=data[avail][2]; 								// Renew Avail's Value From The Stack Of Next Available Positions
				data[avail][1]=data[par][1];								// Node(avail).left = Node(avail)
				data[par][1]=avail;											// Node(par).left=avail
				data[avail][2]=par;											// Node(avail)
				data[par][3]=IsFalse;										// Node(par).left Is A Child
				avail=TmpAvail;	
				MoultiCounter.increaseCounter(2,6);
		}
		else { 															    // Else
			int TmpAvail=data[avail][2];									// Renew Avail's Value From The Stack Of Next Availiable Positions
			data[avail][1]=par;												// Node(avail).left =par
			data[avail][2]=data[par][2];									// Node(avail).right = Node(par).right
			data[par][2]=avail;												// Node(par).right = avail
			data[par][4]=IsFalse;											// Node(par).right Is A Child
			avail=TmpAvail;
			MoultiCounter.increaseCounter(2,6);
		}
		return;
	}
	
	/* This Method Is Used For Searching A Key Inside The Tree 
	 * If It Finds The Key, It Returns The Position Of The Key
	 * Else Returns -1*/
	private int findHelp(int key) {
		int ptr=this.root;
		int par=-1;
		int found=IsFalse;
		MoultiCounter.increaseCounter(4,3);
		
		while(MoultiCounter.increaseCounter(4) && ptr!=-1) {                  // While ptr != null
			if(MoultiCounter.increaseCounter(4) && key == data[ptr][0]) {     // If It Finds The Key, Returns The Key Position
				found=IsTrue;
				System.out.println("Key "+key+" Found At Position "+ptr);
				MoultiCounter.increaseCounter(4);
				return ptr;
			}
			par=ptr;
			MoultiCounter.increaseCounter(4);
			if(MoultiCounter.increaseCounter(4) && key < data[ptr][0]) {      // Else If Key < Node(ptr).info It Moves At The Left Child And Repeats The Loop
				if(MoultiCounter.increaseCounter(4) && data[ptr][3]==IsFalse) {
					ptr=data[ptr][1];
					MoultiCounter.increaseCounter(4);
				}
				else
					break;
			}
			else {
				if(MoultiCounter.increaseCounter(4) && data[ptr][4]==IsFalse) {// Else It Moves At The Right Child And Repeats The Loop
					ptr=data[ptr][2];
					MoultiCounter.increaseCounter(4);
				}
				else
					break;
			}
		}
		if(MoultiCounter.increaseCounter(4) && found==IsFalse) {
			System.out.println("Key "+key+" Does Not Exist At The Threaded BST!");
		}
		return -1;	
	}
	/* This Method Is Used For Printing A Range Between min And max From The Tree
	 * It Finds The Position Of min And Prints All The Keys Till max */
	private void printRangeHelp(int min,int max) {
		int position=find(min);                                   
		MoultiCounter.increaseCounter(4);
		while(MoultiCounter.increaseCounter(4) && position==-1) {// Finding The Position Of The min Value By Searching It Till It Found At the Array
			min+=1;                                              // If Not Found, It Takes As min The Next Bigger Value Than min That Exists 
			position=find(min);
			MoultiCounter.increaseCounter(4,2);
		}
		int ptr=position;
		MoultiCounter.increaseCounter(4);
		while(MoultiCounter.increaseCounter(4) && ptr!=-1) {
			if((MoultiCounter.increaseCounter(4) && data[ptr][0]==-1) || (MoultiCounter.increaseCounter(4) && data[ptr][0]>max )) {// Then Prints All The Keys
				break;																											   // Till ptr == null -> End Of Tree
			}																													   // Or Before Finds max
			else {																												   // If max Does Not Exist, It Stops To
				System.out.println(data[ptr][0]);																				   // The Next Smaller Value Than max That Exists
				ptr=data[ptr][2];
				MoultiCounter.increaseCounter(4);
			}
		}
	}
}
