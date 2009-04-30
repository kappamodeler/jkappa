package com.plectix.simulator.probability.avl;

import com.plectix.simulator.probability.WeightedItem;

public class BinaryTree<E extends WeightedItem> {
	private BinaryTreeNode<E> myRoot = null;
	
	public void addElement(E element) {
		addElement(element, myRoot);
	}
	
	private void addElement(E element, BinaryTreeNode<E> parent) {
		if (myRoot == null) {
			myRoot = new BinaryTreeNode<E>(element, parent);
			return;
		} else {
			if (element.getWeight() >= myRoot.getWeight()) {
				myRoot.getRightSubTree().addElement(element, myRoot);
			} else {
				myRoot.getLeftSubTree().addElement(element, myRoot);
			}
		}
	}

	private BinaryTreeNode<E> getLeftVertex() {
		BinaryTreeNode<E> vertex = myRoot;
		while (vertex.getLeftSubTree() != null) {
			vertex = vertex.getLeftSubTree().getRoot();
		}
		return vertex;
	}
	
	public void removeElement(E element) {
		if (!isEmpty()) {
			if (element.getWeight() > myRoot.getWeight()) {
				myRoot.getRightSubTree().removeElement(element);
			} else if (element.getWeight() < myRoot.getWeight()) {
				myRoot.getLeftSubTree().removeElement(element);
			} else {
				// we have element.weight == myRoot.weight
				if (myRoot.getElement() != element) {
					myRoot.getRightSubTree().removeElement(element);
				} else {
					boolean noChildren = myRoot.hasNoLeftChild() && myRoot.hasNoRightChild();
					boolean hasOneChild = myRoot.hasNoLeftChild() || myRoot.hasNoRightChild();
					if (noChildren) {
						myRoot.getParent().forgetChild(myRoot);
					} else if (hasOneChild) {
						myRoot.erase();
					} else {
						BinaryTreeNode<E> leftVertexOfRightSubTree = myRoot.getRightSubTree().getLeftVertex();
						BinaryTreeNode<E> parentOfLeftVertex = leftVertexOfRightSubTree.getParent();
						myRoot.setElement(leftVertexOfRightSubTree.getElement());
						if (leftVertexOfRightSubTree.isLeftChild()) {
							parentOfLeftVertex.setLeftSubTree(leftVertexOfRightSubTree.getRightSubTree());
						} else {
							parentOfLeftVertex.setRightSubTree(leftVertexOfRightSubTree.getRightSubTree());
						}
					}
				}
			}
		}
	}
	
	public BinaryTreeNode<E> getRoot() {
		return myRoot;
	}
	
	public boolean isEmpty() {
		return myRoot == null;
	}
}
