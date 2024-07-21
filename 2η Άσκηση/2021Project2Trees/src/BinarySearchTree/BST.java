package BinarySearchTree;
import Counter.MoultiCounter;

/* This Class Is Used For The Implementation Of The Binary Search Tree Using Array  */

public class BST implements BinarySearchTree {

	private int rootIndex=-1; // The root of the tree
    private int data[][];
    private int avail;
  
    /* Constructor Of BST  */
	public BST(int N) {
	   setup(N); 
	  //initialize tree
	} 
	
	/* Method That Initializes BST
	 * Sets Next Available Position At Zero
	 * Initializes Stack To Have The Next Available Positions For Insertion */
	private void setup(int N) {
		avail=0;
		data=new int[N][3];
		for(int i=0; i<N; i++) {
			data[i][2]=i+1;              // Initialization Of Stack
			data[i][0]=data[i][1]=-1;    // Childs Set To Null
		}
		data[N-1][2]=-1;
	}
	
	/* This Method Is Used In Order To Return The Next Available 
	 * Position In Witch The Next Insertion Will Be Done */
	private int get_Node() {
		if(MoultiCounter.increaseCounter(1) && avail==-1) {
			System.err.print("BinarySearchTreee OverFlow!");
		    return -1; 
		    }
		else {
			int pos=avail;
			avail=data[avail][2];                           // Renew The Avail To Point At The Next Available Potition Of The Stack
			MoultiCounter.increaseCounter(1,2);
			return pos;
		}
	}

    /* Method That Calls inserthelp To Perform An Insertion At The Tree Of The Value 'val' */
    @Override
	public void insert(int val) {
    	inserthelp(rootIndex,val);
	}

    /* Method That Calls findhelp To Perform A Search Of The Given parameter 'key' At The Tree */
	public int find(int key) {
		return findhelp(rootIndex, key);
	}
	
	/* This Method Calls printrangehelp For Printing A Given Range Of Keys Starting From 'low' till 'high' */
	public void printRange(int low, int high) {
		System.out.print("\nPrint keys between " + low + " and " + high +": ");
		printrangehelp(rootIndex, low, high);
		System.out.println();
	}
   
	/* Method That Searches 'key' @param At The Tree
	 * If 'key' Is Found It Returns The Key
	 * Otherwise It Returns -1 */
	private int findhelp(int rt, int key) {
		if (MoultiCounter.increaseCounter(3) && rt == -1) {       // If Node Is Empty, Returns -1
			System.out.println("BST Find: Key Not Found");
			return -1;}
		int it = data[rt][0];
		MoultiCounter.increaseCounter(3);
		if (MoultiCounter.increaseCounter(3) && it > key)         // Else If Node(info) > 'key' Calls It Self(Recursion) With 'rt' Being The Left Child Of The Node
			return findhelp(data[rt][1], key);
		else if (MoultiCounter.increaseCounter(3) && it == key) { // Else If Node(info) == 'key'-> Key Found, Returns The Key
			System.out.println("BST Find: Key Found! "+ it);
			return it; }
		else                                                      // Else Node(Info) < 'key' -> Calls It Self(Recursion) With 'rt' Being The Right Child Of The Node 
			return findhelp(data[rt][2], key);
	}

	/* Method That Inserts 'val' At The Tree */
	private void inserthelp(int rt, int val) {
		if ( MoultiCounter.increaseCounter(1) && rt == -1) {         // If Tree Is Empty
			int position=get_Node();							     // Get The Next Available Positions For Insertion
			data[position][0]=val;								     // Store 'val' At The Available Position At Info Section 
			data[position][1]=-1;								     // Left And Right Child Are Empty -> null
			data[position][2]=-1; 
			this.rootIndex=position;          					     // Since This Is The First Node Of The Tree, This Node Becomes The Root
			MoultiCounter.increaseCounter(1,5);
			return;
	    }
		int it = data[rt][0];								         // 'it' Has The Key Of The Root
		MoultiCounter.increaseCounter(1);
		if (MoultiCounter.increaseCounter(1) && val < it) {          // If 'val' > 'it'
			if(MoultiCounter.increaseCounter(1) && data[rt][1]==-1) {// And If Node(rt).left == null -> Left Child Of Root Does Not Exist
				data[rt][1]=avail;								     // Then Node(rt).left Becomes The Available Position For Insertion 
				int tmpAvail=data[avail][2];						 // Renew The Next Available Position To Point At The Next Position Of The Stack
				data[avail][0]=val;									 // The New Node Takes As Info The Value Of 'val'
				data[avail][1]=data[avail][2]=-1;					 // Both Childs Of The New Node Are null -> Empty
				avail=tmpAvail;
				MoultiCounter.increaseCounter(1,5);
				return;
			}
			else {													 // Else If Root's Child Exist, The Function Calls It Self(Recursion) 
				inserthelp(data[rt][1],val);						 // With 'rt' @param Now Being The Left Child Of The Root
				}
		}
		else {														 // Else  
			if(MoultiCounter.increaseCounter(1) && data[rt][2]==-1) {// If Node(rt).right == null -> Right Child Of Root Does Not Exist
				data[rt][2]=avail;								     // Then Node(rt).right Becomes The Available Position For Insertion 
				int tmpAvail=data[avail][2];						 // Renew The Next Available Position To Point At The Next Position Of The Stack
				data[avail][0]=val;									 // The New Node Takes As Info The Value Of 'val'
				data[avail][2]=data[avail][1]=-1;					 // Both Childs Of The New Node Are null -> Empty
				avail=tmpAvail;
				MoultiCounter.increaseCounter(1,5);
				return;
			}
			else {												     // Else If Root's Child Exist, The Function Calls It Self(Recursion)
				inserthelp(data[rt][2],val);					     // With 'rt' @param Now Being The Right Child Of The Root
			}		
	 }
	
  }
   
	/* Method That Prints A Given Range Of The Tree Starting From 'low' Till 'high' */
	private void printrangehelp(int root, int low, int high) {
		if (MoultiCounter.increaseCounter(5) && root == -1)   // If The Tree Is Empty Then Return
		return;
		int it = data[root][0];                               // It Has The Info Of The Root Node
		MoultiCounter.increaseCounter(5);
		if (MoultiCounter.increaseCounter(5) && high < it)    // If 'high' < 'it' Calls It Self With @param 'root' Now Being Node(rt).left
			printrangehelp(data[root][1], low, high);
		else if (MoultiCounter.increaseCounter(5) && low > it)// If 'low' > 'it' Calls It Self With @param 'root' Now Being Node(rt).right
			printrangehelp(data[root][2], low, high);
		else {                                                // Else Must Process Both Children                  
			printrangehelp(data[root][1], low, high);
		System.out.print(" " + it);                         
		printrangehelp(data[root][2], low, high);
		}
	}
	

}
