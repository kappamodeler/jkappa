package com.plectix.simulator.probability.avl;

import com.plectix.simulator.probability.WeightedItem;

public class BinaryTreeNode<E extends WeightedItem> {
	private E myElement;
	private BinaryTree<E> myLeftSubTree = new BinaryTree<E>();
	private BinaryTree<E> myRightSubTree = new BinaryTree<E>();
	private final BinaryTreeNode<E> myParent;
	
	public BinaryTreeNode(E element, BinaryTreeNode<E> parent) {
		myParent = parent;
		myElement = element;
	}
	
	//---------------GETTERS AND SETTERS------------------------
	
	public void setLeftSubTree(BinaryTree<E> myLeftSubTree) {
		this.myLeftSubTree = myLeftSubTree;
	}

	public void setRightSubTree(BinaryTree<E> myRightSubTree) {
		this.myRightSubTree = myRightSubTree;
	}
	
	public double getWeight() {
		return myElement.getWeight();
	}
	
	public BinaryTree<E> getLeftSubTree() {
		return myLeftSubTree;
	}
	
	public BinaryTree<E> getRightSubTree() {
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

	public BinaryTreeNode<E> getParent() {
		return myParent;
	}

	public void forgetChild(BinaryTreeNode<E> childLink) {
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
}
