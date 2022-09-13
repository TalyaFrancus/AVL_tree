package src;

/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info.
 *
 */

public class AVLTree {

	private IAVLNode root;

	public AVLTree() {	// constructor - create new empty AVLTree as external node + minimum and maximum are null
		this.root = new AVLNode(-1, null, null, null, null, -1, false);
	}


	/**
	 * public boolean empty()
	 * <p>
	 * Returns true if and only if the tree is empty.
	 */

	public boolean empty() {
		return !root.isRealNode();
	}

	/**
	 * public String search(int k)
	 * <p>
	 * Returns the info of an item with key k if it exists in the tree.
	 * otherwise, returns null.
	 */
	public String search(int k) {
		IAVLNode x = lastVisitNodeSearch(k);	// get last node visited in tree
		if (x.getKey() == k) {	// if founded - return value (if k = -1 still return value of external tree - null)
			return x.getValue();
		} else {	// not founded
			return null;
		}
	}


	/**
	 * public int insert(int k, String i)
	 * <p>
	 * Inserts an item with key k and info i to the AVL tree.
	 * The tree must remain valid, i.e. keep its invariants.
	 * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
	 * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
	 * Returns -1 if an item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {
		if (this.empty()) {		// if empty, set the root real node with given key-value 
			IAVLNode leftExternalLeaf = new AVLNode(-1, null, null, null, null, -1, false);
			IAVLNode rightExternalLeaf = new AVLNode(-1, null, null, null, null, -1, false);
			this.root = new AVLNode(k, i, leftExternalLeaf, rightExternalLeaf, null, 0, true);
			leftExternalLeaf.setParent(this.root);
			rightExternalLeaf.setParent(this.root);
			return 0;
		}
		IAVLNode x = lastVisitNodeSearch(k);	// get last node in path for node with key k - if real, return -1
		if (x.isRealNode()) {
			return -1;
		}	// create and connect new node in correct place and rebalance
		IAVLNode leftExternalLeaf = new AVLNode(-1, null, null, null, null, -1, false);
		IAVLNode rightExternalLeaf = new AVLNode(-1, null, null, null, null, -1, false);
		IAVLNode son = new AVLNode(k, i, leftExternalLeaf, rightExternalLeaf, x.getParent(), 0, true);
		leftExternalLeaf.setParent(son);
		rightExternalLeaf.setParent(son);
		return setSonAndRebalance(x.getParent(), son);
	}

	/**
	 * public int delete(int k)
	 * <p>
	 * Deletes an item with key k from the binary tree, if it is there.
	 * The tree must remain valid, i.e. keep its invariants.
	 * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
	 * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
	 * Returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k) {
		IAVLNode x = lastVisitNodeSearch(k);
		if (!x.isRealNode()) {
			return -1;
		}
		return deleteRebalance(x);
	}

		/**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    */
   public int size()
   {
	   return this.root.getSize();
   }
   
   /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    */
   public IAVLNode getRoot()
   {
	   return this.root;
   }
   
   /**
    * public AVLTree[] split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
    * 
	* precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
    */   
   public AVLTree[] split(int x)
   {
	   IAVLNode node = lastVisitNodeSearch(x);	// get node with key x
	   AVLTree t1 = new AVLTree();	// create first AVLTree - smaller then x
	   t1.root = node.getLeft();
	   t1.root.setParent(null);
	   AVLTree addt1 = new AVLTree();	// helper tree for smaller values
	   AVLTree t2 = new AVLTree();	// create second AVLTree - greater then x
	   t2.root = node.getRight();
	   t2.root.setParent(null);
	   AVLTree addt2 = new AVLTree();	// helper tree for greater values
	   IAVLNode y = node.getParent();
	   IAVLNode t = y;
	   while (y != null){	// while y not null - path from node to root
		   t = y.getParent();
		   if (y.getLeft() == node){	// if current node is left son of father, Join t2 and y.getRight()
			   node = y;
			   addt2.root = y.getRight();
			   addt2.root.setParent(null);
			   t2.join(y, addt2);
		   }
		   else {	// if current node is right son of father, Join t1 and y.getLeft()
			   node = y;
			   addt1.root = y.getLeft();
			   addt1.root.setParent(null);
			   t1.join(y, addt1);
		   }
		   y = t;
	    }
	   
	   AVLTree[] lst = new AVLTree[2];	// return the splited trees
	   lst[0] = t1;
	   lst[1] = t2;

	   return lst;
   }
   

   
   /**
    * public int join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	*
	* precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    */   
   public int join(IAVLNode x, AVLTree t)
   {
	   if(t.empty() && this.empty()) {		// if both trees empty, make x the root
		   this.root = x;
		   this.root.setLeft(new AVLNode(-1, null, null, null, this.root, -1, false));
		   this.root.setRight(new AVLNode(-1, null, null, null, this.root, -1, false));
		   this.root.updateMax();
		   this.root.updateMin();
		   this.root.setSize(1);
		   return 1;
	   }
	   else if(t.empty()) { 	// if t empty, join x, this according to which is the bigger
		   if(this.root.getKey() < x.getKey())
			   return this.join(x, this, t);
		   return this.join(x, t, this);
	   }
	   else if(this.empty()) {		// if this empty, join x, t according to which is the bigger
		   if(t.root.getKey() < x.getKey())
			   return this.join(x, t, this);
		   return this.join(x, this, t);
	   }
	   if(this.root.getKey() < x.getKey())	// join x, this, t according to which is the bigger and which is smaller
		   return this.join(x, this, t);
	   return this.join(x, t, this);
   }
   
   /**
    * precondition: keys(t1) < x < keys(t2)
    * */
   private int join(IAVLNode x, AVLTree t1, AVLTree t2) {
	   IAVLNode leftExternalLeaf = new AVLNode(-1, null, null, null, x, -1, false);
	   IAVLNode rightExternalLeaf = new AVLNode(-1, null, null, null, x, -1, false);
	   if(t1.empty()) {		// add x as minimum of t2
		   x.setHeight(0);
		   int ret = t2.getRoot().getHeight() + 1;
		   x.setSize(1);
		   x.setLeft(leftExternalLeaf);
		   x.setRight(rightExternalLeaf);
		   t2.setSonAndRebalance(t2.root.getMin(), x);
		   this.root = t2.getRoot();
		   this.root.updateMax();
		   this.root.updateMin();
		   return ret;
	   }
	   else if(t2.empty()) {	// add x as maximum of t1
		   x.setHeight(0);
		   int ret = t1.getRoot().getHeight() + 1;
		   x.setSize(1);
		   x.setLeft(leftExternalLeaf);
		   x.setRight(rightExternalLeaf);
		   t1.setSonAndRebalance(t1.root.getMax(), x);
		   this.root = t1.getRoot();
		   this.root.updateMax();
		   this.root.updateMin();
		   return ret;
	   }
	   int ret = Math.abs(t1.getRoot().getHeight() - t2.getRoot().getHeight()) + 1;
	   if(t2.getRoot().getHeight() >= t1.getRoot().getHeight()) {	// case t2 is higher
		   IAVLNode r = t2.getRoot();	// get first node in t2 s.t height of node <= height of t1 
		   while(r.getHeight() > t1.getRoot().getHeight()) {
			   r = r.getLeft();
		   }
		   x.setParent(r.getParent());	// set x as r, t1 parent and x as son of r.getParent() and update min, max, size, height of x.
		   if(r.getParent() != null)
			   r.getParent().setLeft(x);
		   x.setRight(r);
		   r.setParent(x);
		   x.setLeft(t1.getRoot());
		   t1.getRoot().setParent(x);
		   x.setHeight(t1.getRoot().getHeight() + 1);
		   x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
		   x.updateMax();
		   x.updateMin();
		   if(this.bf(x) == 0 && x.getParent() != null && x.getParent().getHeight() - x.getHeight() == 0) {		// if special case, do rotation right from class
			   this.rightRotation(x.getParent());
			   x.setHeight(x.getHeight() + 1);
		   }
		   if(x.getParent() == null) { 	// case x is now the root
			   this.root = x;
			   this.root.updateMax();
			   this.root.updateMin();
			   return ret;
		   }
		   t2.rebalance(x);		// if x not the root, rebalance from x and make this the joined tree
		   this.root = t2.getRoot();
		   this.root.updateMax();
		   this.root.updateMin();
	   }
	   else {		// if t1 is higher - exactly same as before, but switch t1, t2 and right, left
		   IAVLNode r = t1.getRoot();
		   while(r.getHeight() > t2.getRoot().getHeight()) {
			   r = r.getRight();
		   }
		   x.setParent(r.getParent());
		   if(r.getParent() != null)
			   r.getParent().setRight(x);
		   x.setLeft(r);
		   r.setParent(x);
		   x.setRight(t2.getRoot());
		   t2.getRoot().setParent(x);
		   x.setHeight(t2.getRoot().getHeight() + 1);
		   x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
		   x.updateMax();
		   x.updateMin();
		   if(this.bf(x) == 0 && x.getParent() != null && x.getParent().getHeight() - x.getHeight() == 0) {
			   this.leftRotation(x.getParent());
			   x.setHeight(x.getHeight() + 1);
		   }
		   if(x.getParent() == null) {
			   this.root = x;
			   this.root.updateMax();
			   this.root.updateMin();
			   return ret;
		   }
		   t1.rebalance(x);
		   this.root = t1.getRoot();
		   this.root.updateMax();
		   this.root.updateMin();
	   }
	   return ret;
   }

   
   private IAVLNode[] keySortedNodesArr() {		// returns array of IAVLNode, sorted by their keys
	  IAVLNode[] nodesArr = new IAVLNode[this.root.getSize()];
	  int i = 0;
	  IAVLNode x = this.root;
	  while(x != null && x.isRealNode()) {	// perform an in-order walk
		  if(x.getLeft().isRealNode() && (i == 0 || x.getLeft().getKey() > nodesArr[i-1].getKey())) {  // needs to go left
			  x = x.getLeft();
			  continue;
		  }
		  if(i == 0 || x.getKey() > nodesArr[i-1].getKey()) {	// if didn't go left, add x to array and if x has right son, go right
			  nodesArr[i] = x;
			  i++;
			  if(x.getRight().isRealNode()) {
				  x = x.getRight();
				  continue;
			  }
		  }
		  x = x.getParent();	// else, go back to parent
	  }
	  return nodesArr;	// returns the array
   }

	/**
	 * public String min()
	 * <p>
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty.
	 */
	public String min() {
		if (this.empty())
			return null;
		return this.root.getMin().getValue();
	}

	/**
	 * public String max()
	 * <p>
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty.
	 */
	public String max() {
		if (this.empty())
			return null;
		return this.root.getMax().getValue();
	}

	/**
	 * public int[] keysToArray()
	 * <p>
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 */
	public int[] keysToArray() {
		IAVLNode[] nodes = this.keySortedNodesArr();	// get nodes sorted by keys
		int[] keys = new int[this.root.getSize()];
		for (int i = 0; i < this.root.getSize(); i++) {		// create array of keys - sorted because nodes are sorted
			keys[i] = nodes[i].getKey();
		}
		return keys;
	}

	/**
	 * public String[] infoToArray()
	 * <p>
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		IAVLNode[] nodes = this.keySortedNodesArr();	// get nodes sorted by keys
		String[] values = new String[this.root.getSize()];
		for (int i = 0; i < this.root.getSize(); i++) {		// create array of values - sorted because nodes are sorted
			values[i] = nodes[i].getValue();
		}
		return values;
	}


	private void rightRotation(IAVLNode y) {	// Perform a right rotation on the nodes y, y.getLeft
		IAVLNode x = y.getLeft();
		if (y.getParent() != null) {	// case y is not the root
			if (y.getParent().getLeft() == y)
				y.getParent().setLeft(x);
			else
				y.getParent().setRight(x);
		}
		x.setParent(y.getParent());	// change pointers
		y.setParent(x);
		y.setLeft(x.getRight());
		y.getLeft().setParent(y);
		x.setRight(y);
		if (this.root == y)	 // case y is root, now change root to new root - x
			this.root = x;
		y.setSize(y.getLeft().getSize() + y.getRight().getSize() + 1);	// correct the sizes
		x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
		y.updateMax();
		y.updateMin();
		x.updateMax();
		x.updateMin();

	}

	private void leftRotation(IAVLNode x) {		// Perform a left rotation on the nodes x, x.getRight
		IAVLNode y = x.getRight();
		if (x.getParent() != null) {	// case x is not the root
			if (x.getParent().getRight() == x)
				x.getParent().setRight(y);
			else
				x.getParent().setLeft(y);
		}
		y.setParent(x.getParent());		// change pointers
		x.setParent(y);
		x.setRight(y.getLeft());
		x.getRight().setParent(x);
		y.setLeft(x);
		if (this.root == x)		// case x is root, now change root to new root - y
			this.root = y;
		x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);	// correct the sizes
		y.setSize(y.getLeft().getSize() + y.getRight().getSize() + 1);
		x.updateMax();
		x.updateMin();
	}

	private int insertRotation(IAVLNode x, IAVLNode y, int cnt) {	// Performs the rotations in insert/Join according to possible cases
		if (y.getLeft() == x) {
			if (x.getHeight() - x.getLeft().getHeight() == 1) {		// single right rotation and height correction
				this.rightRotation(y);
				y.setHeight(y.getHeight() - 1);
				cnt++;
			} else {	// LR double rotation and height correction
				this.leftRotation(x);
				x.setHeight(x.getHeight() - 1);
				IAVLNode w = y.getLeft();
				this.rightRotation(y);
				w.setHeight(w.getHeight() + 1);
				y.setHeight(y.getHeight() - 1);
				cnt += 2;
			}
		} else {
			if (x.getHeight() - x.getRight().getHeight() == 1) {	// single left rotation and height correction
				this.leftRotation(y);
				y.setHeight(y.getHeight() - 1);
				cnt++;
			} else {	// RL double rotation and height correction
				this.rightRotation(x);
				x.setHeight(x.getHeight() - 1);
				IAVLNode w = y.getRight();
				this.leftRotation(y);
				w.setHeight(w.getHeight() + 1);
				y.setHeight(y.getHeight() - 1);
				cnt += 2;
			}
		}
		return cnt;
	}

	private int rebalance(IAVLNode x) {		// rebalance the tree starting from node x 
		IAVLNode y = x.getParent();
		if(y == null)	// if x is root, do nothing
			return 0;	
		int cnt = 0;
		IAVLNode z = y.getRight() == x ? y.getLeft() : y.getRight();	// get the second son of x
		boolean rankDiff1 = (y.getHeight() - x.getHeight() == 0 && y.getHeight() - z.getHeight() == 1);
		boolean rankDiff2 = (y.getHeight() - x.getHeight() == 1 && y.getHeight() - z.getHeight() == 0);
		while(( rankDiff1 || rankDiff2) && y != this.root && y.getParent() != null) {	// while not root and need to promote
			y.setHeight(y.getHeight() + 1);		// promote y, correct sizes and update nodes and conditions
			y.setSize(y.getRight().getSize() + y.getLeft().getSize() + 1);
			y.updateMax();
			y.updateMin();
			x = y;
			y = y.getParent();
			z = y.getRight() == x ? y.getLeft() : y.getRight();
			rankDiff1 = (y.getHeight() - x.getHeight() == 0 && y.getHeight() - z.getHeight() == 1);
			rankDiff2 = (y.getHeight() - x.getHeight() == 1 && y.getHeight() - z.getHeight() == 0);
			cnt++;
		}
		if (y.getHeight() - x.getHeight() == 0 && y.getHeight() - z.getHeight() == 2)	// if problem with x, rotate x, y
			cnt = this.insertRotation(x, y, cnt);
		else if (y.getHeight() - z.getHeight() == 0 && y.getHeight() - x.getHeight() == 2)	// if problem with z, rotate z, y
			cnt = this.insertRotation(z, y, cnt);
		else if (rankDiff1 || rankDiff2) {	// if there is problem not from above cases (and in root) - promote root
			this.root.setHeight(this.root.getHeight() + 1);
			cnt++;
		}
		while (y != null) {		// while not in root, correct all sizes in remain path from the starting node to root
			y.setSize(y.getRight().getSize() + y.getLeft().getSize() + 1);
			y.updateMax();
			y.updateMin();
			y = y.getParent();
		}
		return cnt;
	}

	private int insertRebalance(IAVLNode x) {	// in case if insert, rebalance correctly
		IAVLNode y = x.getParent();
		if (y.getRight().isRealNode() && y.getLeft().isRealNode())	// if no need for rebalance
			return 0;
		return this.rebalance(x);	// rebalance from added node
	}

	private IAVLNode lastVisitNodeSearch(int k) { //return the last node we were in search.
		IAVLNode x = this.root;
		while (x.isRealNode()) {
			if (x.getKey() > k) {
				x = x.getLeft();
			} else if (x.getKey() < k) {
				x = x.getRight();
			} else {
				return x;
			}
		}
		return x;
	}

	private int setSonAndRebalance(IAVLNode parent, IAVLNode son){	// addes node son to node parent, and rebalance the tree
		son.setParent(parent);
		if (parent.getKey() < son.getKey()){
			parent.setRight(son);
		} else {
			parent.setLeft(son);
		}
		return this.insertRebalance(son);	// rebalance
	}

	private IAVLNode findSuccessor(IAVLNode node) { //find the successor of the given node.
		IAVLNode successor = node;
		if (node.getRight().isRealNode()) {
			successor = node.getRight();
			while (successor.getLeft().isRealNode()) {
				successor = successor.getLeft();
			}
		} else if (node != this.root) {
			if (successor.getParent() == this.root) {
				return successor.getParent();
			}
			while (successor.getParent().getLeft() != successor) {
				successor = successor.getParent();
			}
		}
		return successor;
	}

	private void deleteNode(IAVLNode node) { //delete given node from the tree.
		if (node == this.root && !node.getRight().isRealNode() && !node.getLeft().isRealNode()) {
			this.root = new AVLNode(-1, null, null, null, null, -1, false);
		}
		
		IAVLNode y = node.getParent();
		IAVLNode ExternalLeaf = new AVLNode(-1, null, null, null, this.root, -1, false);
		if (!node.getLeft().isRealNode() && !node.getRight().isRealNode()) {
			if (y != null) {
				if (y.getLeft() == node) {
					y.setLeft(ExternalLeaf);
					y.updateMin();
				} else {
					y.setRight(ExternalLeaf);
					y.updateMax();
				}
			}
		}
		else if (node.getLeft().isRealNode() && !node.getRight().isRealNode()) {
			if (node == this.root) {
				this.root = node.getLeft();
				node.getLeft().setParent(null);
				this.root.updateMax();
				this.root.updateMin();
			} else if (y.getLeft() == node) {
				y.setLeft(node.getLeft());
				y.getLeft().setParent(y);
				y.updateMax();
				y.updateMin();
			} else {
				y.setRight(node.getLeft());
				y.getRight().setParent(y);
				y.updateMax();
				y.updateMin();
			}

		} else if (!node.getLeft().isRealNode() && node.getRight().isRealNode()) {
			if (this.root == node) {
				this.root = node.getRight();
				node.getRight().setParent(null);
				this.root.updateMax();
				this.root.updateMin();
			} else if (y.getLeft() == node) {
				y.setLeft(node.getRight());
				y.getLeft().setParent(y);
				y.updateMax();
				y.updateMin();
			} else {
				y.setRight(node.getRight());
				y.getRight().setParent(y);
				y.updateMax();
				y.updateMin();
			}
		} else{
			
			IAVLNode nodeSuccessor = findSuccessor(node);
			IAVLNode successorParent = nodeSuccessor.getParent();
			
			if(successorParent == node) {
				if(node.getParent() != null) {
					if(node.getParent().getRight() == node)
						node.getParent().setRight(nodeSuccessor);
					else
						node.getParent().setLeft(nodeSuccessor);
				}
				node.getLeft().setParent(nodeSuccessor);
				nodeSuccessor.setLeft(node.getLeft());
				nodeSuccessor.setParent(node.getParent());
				nodeSuccessor.setHeight(Math.max(nodeSuccessor.getRight().getHeight(), nodeSuccessor.getLeft().getHeight()) + 1);
				successorParent = nodeSuccessor.getParent();
				successorParent.setHeight(1 + Math.max(successorParent.getLeft().getHeight(), successorParent.getRight().getHeight()));
				setSize(nodeSuccessor);
				return;
			}
			
			successorParent.setLeft(nodeSuccessor.getRight());
			if (this.root == node) {
				this.root = nodeSuccessor;
				nodeSuccessor.setLeft(node.getLeft());
				nodeSuccessor.setRight(node.getRight());
				nodeSuccessor.setHeight(Math.max(nodeSuccessor.getLeft().getHeight(), nodeSuccessor.getRight().getHeight()) + 1);


			} else if (y.getRight() == node) {
				y.setRight(nodeSuccessor);
				nodeSuccessor.setRight(node.getRight());
				nodeSuccessor.setLeft(node.getLeft());


			} else {
				y.setLeft(nodeSuccessor);
				nodeSuccessor.setLeft(node.getLeft());
				nodeSuccessor.setRight(node.getRight());

			}
			nodeSuccessor.getRight().setParent(nodeSuccessor);
			nodeSuccessor.getLeft().setParent(nodeSuccessor);
			nodeSuccessor.setParent(node.getParent());
			nodeSuccessor.setHeight(Math.max(nodeSuccessor.getRight().getHeight(), nodeSuccessor.getLeft().getHeight()) + 1);
			successorParent.setHeight(1 + Math.max(successorParent.getLeft().getHeight(), successorParent.getRight().getHeight()));
			setSize(nodeSuccessor);
			node.setParent(successorParent);

		}

	}







	private int deleteRotation(IAVLNode x, IAVLNode y, int cnt) {  //find and do the correct rotation.
		if (bf(x) == -2) {
			if (bf(x.getRight()) == -1 || bf(x.getRight()) == 0) {
				this.leftRotation(x);
				cnt++;
				x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
				x.setSize(1 + x.getRight().getSize() + x.getLeft().getSize());
				y.setHeight(Math.max(y.getLeft().getHeight(), y.getRight().getHeight()) + 1);
			} else {
				this.rightRotation(y);
				y.setHeight(Math.max(y.getLeft().getHeight(), y.getRight().getHeight()) + 1);
				y.setSize(1 + y.getRight().getSize() + y.getLeft().getSize());
				this.leftRotation(x);
				x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
				x.getParent().setHeight(x.getParent().getHeight() + 1);
				x.setSize(1 + x.getRight().getSize() + x.getLeft().getSize());
				cnt += 2;
			}
		} else {
			if (bf(x.getLeft()) == 1 || bf(x.getLeft()) == 0) {
				this.rightRotation(x);
				x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
				x.setSize(1 + x.getRight().getSize() + x.getLeft().getSize());
				y.setHeight(Math.max(y.getLeft().getHeight(), y.getRight().getHeight()) + 1);
				y.setSize(1 + y.getRight().getSize() + y.getLeft().getSize());
				cnt++;
			} else {
				this.leftRotation(y);
				y.setHeight(Math.max(y.getLeft().getHeight(), y.getRight().getHeight() + 1));
				y.setSize(1 + y.getRight().getSize() + y.getLeft().getSize());
				this.rightRotation(x);
				x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
				x.getParent().setHeight(x.getParent().getHeight() - 1);
				x.setSize(1 + x.getRight().getSize() + x.getLeft().getSize());
				x.getParent().setSize(1 + x.getParent().getRight().getSize() + x.getParent().getLeft().getSize());
				x.getParent().updateMax();
				x.getParent().updateMin();
				cnt += 2;
			}
		}
		return cnt;
	}

	private int deleteRebalance(IAVLNode node) { //delete a given node and rebalance the tree.
		int cnt = 0;
		deleteNode(node);
		IAVLNode y = node.getParent();
		int preHeight = -1;
		if (y == null) {
			y = this.root;
			preHeight = y.getHeight();
		} else {
			preHeight = y.getHeight();
			y.setHeight(1 + Math.max(y.getLeft().getHeight(), y.getRight().getHeight()));
			y.setSize(1 + y.getRight().getSize() + y.getLeft().getSize());
			y.updateMax();
			y.updateMin();
			cnt++;
		}

		node = new AVLNode(-1, null, null, null, null, -1, false);


		while (y != null) {
			if (y.getParent() == null && y.getLeft() == null && y.getRight() == null) {
				return cnt;
			}
			if (Math.abs(bf(y)) < 2) {
				if (y.getHeight() == preHeight) {
					while(y != null) {
						y.updateMax();
						y.updateMin();
						y.setSize(1 + y.getLeft().getSize() + y.getRight().getSize());
						y.setHeight(1 + Math.max(y.getLeft().getHeight(), y.getRight().getHeight()));
						y = y.getParent();
					}
					return cnt;
				} else if (y.getParent() == null) {
					y = y.getParent();
				} else {
					preHeight = y.getParent().getHeight();
					y = y.getParent();
					y.setHeight(1 + Math.max(y.getLeft().getHeight(), y.getRight().getHeight()));
					y.setSize(1 + y.getRight().getSize() + y.getLeft().getSize());
					y.updateMax();
					y.updateMin();
					cnt++;
				}
			} else if (Math.abs(bf(y)) == 2) {
				if (bf(y) == -2) {
					cnt += deleteRotation(y, y.getRight(), 0);
					y = y.getParent();
					if (y != null) {
						preHeight = y.getHeight();
						y.setHeight(1 + Math.max(y.getLeft().getHeight(), y.getRight().getHeight()));
						y.setSize(1 + y.getRight().getSize() + y.getLeft().getSize());
						y.updateMax();
						y.updateMin();
						cnt++;
					}
				} else {
					cnt += deleteRotation(y, y.getLeft(), 0);
					y = y.getParent();
					if (y != null) {
						preHeight = y.getHeight();
						y.setHeight(1 + Math.max(y.getLeft().getHeight(), y.getRight().getHeight()));
						y.setSize(1 + y.getRight().getSize() + y.getLeft().getSize());
						y.updateMax();
						y.updateMin();
						cnt++;
					}
				}
			}
		}
		return cnt;
	}

	private int bf(IAVLNode node) {
		return node.getLeft().getHeight() - node.getRight().getHeight();
	} //return the difference between the height of the sons of the given node


	private void setSize(IAVLNode node){ //set the size of all the node from the given node to the root
		while (node != null){
			node.setSize(node.getLeft().getSize() + node.getRight().getSize() +1);
			node.updateMax();
			node.updateMin();
			node = node.getParent();
		}
	}
	
	/** 
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */

	public interface IAVLNode{	
		public int getKey(); // Returns node's key (for virtual node return -1).
		public String getValue(); // Returns node's value [info], for virtual node returns null.
		public void setLeft(IAVLNode node); // Sets left child.
		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
		public void setRight(IAVLNode node); // Sets right child.
		public IAVLNode getRight(); // Returns right child, if there is no right child return null.
		public void setParent(IAVLNode node); // Sets parent.
		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
    	public void setHeight(int height); // Sets the height of the node.
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
    	public int getSize();
    	public void setSize(int size);
    	public IAVLNode getMax();
    	public IAVLNode getMin();
    	public void updateMin();
    	public void updateMax();
	}

   /** 
    * public class AVLNode
    *
    * If you wish to implement classes other than AVLTree
    * (for example AVLNode), do it in this file, not in another file. 
    * 
    * This class can and MUST be modified (It must implement IAVLNode).
    */
  public class AVLNode implements IAVLNode{

	  	private int key;
	  	private String value;
	  	private IAVLNode left;
		  private IAVLNode right;
		  private IAVLNode parent;
		  private int height;
		  private boolean isRealNode;
		  private int size;
		  private IAVLNode min;
		  private IAVLNode max;

		  public AVLNode(int key, String value, IAVLNode left, IAVLNode right, IAVLNode parent, int height, boolean isRealNode){
			  this.key = key;
			  this.value = value;
			  this.left = left;
			  this.right = right;
			  this.parent = parent;
			  this.height = height;
			  this.isRealNode = isRealNode;
			  if(!this.isRealNode)
				  this.size = 0;
			  else
				  this.size = this.getLeft().getSize() + this.getRight().getSize() + 1;
			  updateMin();
			  updateMax();
		}
			  
		public int getKey()
		{
			return this.key; // to be replaced by student code
		}
		public String getValue()
		{
			return this.value; // to be replaced by student code
		}
		public void setLeft(IAVLNode node)
		{
			this.left = node;
			return; // to be replaced by student code
		}
		public IAVLNode getLeft()
		{
			return this.left; // to be replaced by student code
		}
		public void setRight(IAVLNode node)
		{
			this.right = node;
			return; // to be replaced by student code
		}
		public IAVLNode getRight()
		{
			return this.right; // to be replaced by student code
		}
		public void setParent(IAVLNode node)
		{
			this.parent = node;
			return; // to be replaced by student code
		}
		public IAVLNode getParent()
		{
			return this.parent; // to be replaced by student code
		}
		public boolean isRealNode()
		{
			return this.isRealNode; // to be replaced by student code
		}
	    public void setHeight(int height)
	    {
			this.height = height;
	      return; // to be replaced by student code
	    }
	    public int getHeight()
	    {
	      return this.height; // to be replaced by student code
	    }
	    public int getSize() {
	    	return this.size;
	    }
	    public void setSize(int size) {
	    	this.size = size;
	    }
		@Override
		public IAVLNode getMax() {
			return this.max;
		}
		@Override
		public IAVLNode getMin() {
			return this.min;
		}
		public void updateMin() {	// update minimum of subtree
			if(!this.isRealNode) {
				this.min =  this;
				return;
			}
			if(this.left.isRealNode())
				this.min = this.left.getMin();
			else
				this.min =  this;
		}
		
		public void updateMax() {	// update maximum of subtree
			if(!this.isRealNode) {
				this.max =  this;
				return;
			}
			if(this.right.isRealNode())
				this.max = this.right.getMax();
			else
				this.max =  this;
		}
	}

}
  
