package com.plectix.simulator.probability.avl;

import com.plectix.simulator.probability.Ponderable;

/*package*/ class WeightedNode<E extends Ponderable> {
	private E myElement;
	private WeightBalancedTree<E> myLeftSubTree = new WeightBalancedTree<E>();
	private WeightBalancedTree<E> myRightSubTree = new WeightBalancedTree<E>();
	private WeightedNode<E> myParent;
	
	public WeightedNode(E element, WeightedNode<E> parent) {
		myParent = parent;
		myElement = element;
	}
	
	//---------------GETTERS AND SETTERS------------------------
	
	public void setLeftSubTree(WeightBalancedTree<E> leftSubTree) {
		myLeftSubTree = leftSubTree;
		if (!myLeftSubTree.isEmpty())
			myLeftSubTree.getRoot().myParent = this;
	}

	public void setRightSubTree(WeightBalancedTree<E> rightSubTree) {
		myRightSubTree = rightSubTree;
		if (!myRightSubTree.isEmpty())
			myRightSubTree.getRoot().myParent = this;
	}
	
	public int getWeight() {
		return myElement.getWeight();
	}
	
	public WeightBalancedTree<E> getLeftSubTree() {
		return myLeftSubTree;
	}
	
	public WeightBalancedTree<E> getRightSubTree() {
		return myRightSubTree;
	}

	public E getElement() {
		return myElement;
	}
	
	public void setElement(E element) {
		myElement = element;
	}

	public boolean hasNoLeftChild() {
		return myLeftSubTree.isEmpty();
	}
	
	public boolean hasNoRightChild() {
		return myRightSubTree.isEmpty();
	}

	public WeightedNode<E> getParent() {
		return myParent;
	}

	public void forgetChild(WeightedNode<E> childLink) {
		if (myRightSubTree.getRoot() == childLink) {
			myRightSubTree = null;
		} else if (myLeftSubTree.getRoot() == childLink) {
			myLeftSubTree = null;
		}
	}
	
	public boolean isRightChild() {
		boolean thisIsRightChild = false;
		if (myParent.myRightSubTree.getRoot() == this) {
			thisIsRightChild = true;
		}
		return thisIsRightChild;
	}
	
	public boolean isLeftChild() {
		boolean thisIsLeftChild = false;
		if (myParent.myLeftSubTree.getRoot() == this) {
			thisIsLeftChild = true;
		}
		return thisIsLeftChild;
	}
	                               
	public void erase() {
		boolean thisIsRightChild = isRightChild();
		if (!myRightSubTree.isEmpty()) {
			if (thisIsRightChild) {
				myParent.myRightSubTree = myRightSubTree;
			} else {
				myParent.myLeftSubTree = myRightSubTree;
			}
		} else if (!myLeftSubTree.isEmpty()) {
			if (thisIsRightChild) {
				myParent.myRightSubTree = myLeftSubTree;
			} else {
				myParent.myLeftSubTree = myLeftSubTree;
			}
		}
	}
	
	public int getBalance() {
		return Math.abs(myLeftSubTree.getHeight() - myRightSubTree.getHeight());
	}
	
	private void giveOtherSonToTheFather(WeightedNode<E> newSon) {
		if (this.isLeftChild()) {
			myParent.setLeftSubTree(new WeightBalancedTree<E>(newSon));
		} else {
			myParent.setRightSubTree(new WeightBalancedTree<E>(newSon));
		}
	}
	
	public void doLL() {
		WeightedNode<E> v = myLeftSubTree.getRoot();
		WeightBalancedTree<E> betha = v.myRightSubTree;
		// v becomes a new son of this's father
		this.giveOtherSonToTheFather(v);
		
		this.setLeftSubTree(betha);
		v.setRightSubTree(new WeightBalancedTree<E>(this));
	}
	
	public void doRR() {
		WeightedNode<E> v = myRightSubTree.getRoot();
		WeightBalancedTree<E> betha = v.myLeftSubTree;
		// v becomes a new son of this's father
		this.giveOtherSonToTheFather(v);
		
		this.setRightSubTree(betha);
		v.setLeftSubTree(new WeightBalancedTree<E>(this));
	}

	public void doLR() {
		WeightedNode<E> v = myLeftSubTree.getRoot();
		WeightedNode<E> w = v.myRightSubTree.getRoot();
		
		WeightBalancedTree<E> betha1 = w.myLeftSubTree;
		WeightBalancedTree<E> betha2 = w.myRightSubTree;
		
		this.giveOtherSonToTheFather(w);
		
		this.setLeftSubTree(betha2);
		v.setRightSubTree(betha1);
		w.setRightSubTree(new WeightBalancedTree<E>(this));
		w.setLeftSubTree(new WeightBalancedTree<E>(v));
	}

	public void doRL() {
		WeightedNode<E> v = myRightSubTree.getRoot();
		WeightedNode<E> w = v.myLeftSubTree.getRoot();
		
		WeightBalancedTree<E> betha1 = w.myLeftSubTree;
		WeightBalancedTree<E> betha2 = w.myRightSubTree;
		
		this.giveOtherSonToTheFather(w);
		
		this.setRightSubTree(betha1);
		v.setLeftSubTree(betha2);
		w.setLeftSubTree(new WeightBalancedTree<E>(this));
		w.setRightSubTree(new WeightBalancedTree<E>(v));
	}
}
