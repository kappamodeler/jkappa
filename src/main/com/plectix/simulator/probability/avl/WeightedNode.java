package com.plectix.simulator.probability.avl;

import com.plectix.simulator.probability.WeightedItem;

public class WeightedNode<E extends WeightedItem> {
	private E myElement;
	private WeightedNode<E> myLeftChild;
	private WeightedNode<E> myRightChild;
	private WeightedNode<E> myParent = null;
	private int myBalance = 0;
	private final WeightBalancedTree<E> myTree;
	
	public WeightedNode(E element, WeightBalancedTree<E> tree) {
		myElement = element;
		myTree = tree;
	}
	
	//---------------GETTERS AND SETTERS------------------------
	
	public double getWeight() {
		return myElement.getWeight();
	}
	
	public WeightedNode<E> getLeftChild() {
		return myLeftChild;
	}
	
	public WeightedNode<E> getRightChild() {
		return myRightChild;
	}
	
	public void setLeftChild(WeightedNode<E> child) {
		if (child != null) {
			child.myParent = this;
		}
		myLeftChild = child;
	}
	
	public void setRightChild(WeightedNode<E> child) {
		if (child != null) {
			child.myParent = this;
		}
		myRightChild = child;
	}
	
	public WeightedNode<E> getParent() {
		return myParent;
	}
	
	public boolean isRightChild() {
		if (myParent == null) {
			return false;
		}
		return myParent.myRightChild == this;
	}
	
	public boolean isLeftChild() {
		if (myParent == null) {
			return false;
		}
		return myParent.myLeftChild == this;
	}
	
	public Orientation whichChild() {
		if (myParent == null) {
			return Orientation.UNKNOWN;
		}
		if (myParent.myRightChild == this) {
			return Orientation.RIGHT;
		} else if (myParent.myLeftChild == this) {
			return Orientation.LEFT;
		} else {
			// should be impossible
			return Orientation.UNKNOWN;
		}
	}
	
	public void setChild(Orientation orientation, WeightedNode<E> child) {
		if (orientation == Orientation.LEFT) {
			this.setLeftChild(child);
		} else if (orientation == Orientation.RIGHT) {
			this.setRightChild(child);
		}
	}
	                               
	public int getBalance() {
		return myBalance ;
	}
	
	public void incBalance() {
		myBalance++;
	}

	public void decBalance() {
		myBalance--;
	}
	
	public void forgetParent() {
		myParent = null;
	}

	public WeightBalancedTree<E> getTree() {
		return myTree;
	}

	public void setBalance(int i) {
		myBalance = i;
	}

	public WeightedNode<E> getChild(Orientation orientation) {
		if (orientation == Orientation.LEFT) {
			return myLeftChild;
		} else if (orientation == Orientation.RIGHT) {
			return myRightChild;
		}
		// impossible
		return null;
	}

	public E getElement() {
		return myElement;
	}

	public boolean isLeave() {
		return myLeftChild == null && myRightChild == null;
	}

	public void forgetChild(Orientation orientation) {
		if (orientation == Orientation.LEFT) {
			myLeftChild = null;
		} else if (orientation == Orientation.RIGHT) {
			myRightChild = null;
		}
	}
	
	public void swapElements(WeightedNode<E> otherNode) {
		E thisElement = myElement;
		myElement = otherNode.getElement();
		otherNode.myElement = thisElement;
	}
}
