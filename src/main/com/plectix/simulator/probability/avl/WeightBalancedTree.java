package com.plectix.simulator.probability.avl;

import java.util.Collection;

import com.plectix.simulator.probability.WeightedItem;
import com.plectix.simulator.probability.avl.processors.BalanceRecoveryUnit;
import com.plectix.simulator.probability.avl.processors.ElementAdder;
import com.plectix.simulator.probability.avl.processors.ElementFinder;
import com.plectix.simulator.probability.avl.processors.ElementRemover;

public class WeightBalancedTree<E extends WeightedItem> {
	private WeightedNode<E> myRoot = null;
	
	public void add(E element) {
		if (myRoot == null) {
			myRoot = new WeightedNode<E>(element, this);
		} else {
			WeightedNode<E> newNode = ElementAdder.getInstance().
				addElement(myRoot, element);
			BalanceRecoveryUnit.getInstance().restoreBalanceAfterAdding(newNode);
		}
	}
	
	public void setRoot(WeightedNode<E> newRoot) {
		myRoot = newRoot;
	}
	
	public WeightedNode<E> search(E element) {
		return ElementFinder.getInstance().search(myRoot, element);
	}
	
	public void remove(E element) {
		WeightedNode<E> nodeToRemove = search(element);
		if (nodeToRemove == null) {
			return;
		}
		if (nodeToRemove == myRoot) {
			myRoot = null;
		} else {
			WeightedNode<E> startFrom = ElementRemover.getInstance().
				removeElement(nodeToRemove);
			BalanceRecoveryUnit.getInstance().
				restoreBalanceAfterDeleting(startFrom);
		}
	}
	
	public WeightedNode<E> getRoot() {
		return myRoot;
	}
	
	public E select() {
		// TODO Auto-generated method stub
		return null;
	}

	public void updatedItems(Collection<E> changedWeightedItemList) {
		// TODO Auto-generated method stub
		
	}
}
