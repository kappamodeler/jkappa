package com.plectix.simulator.probability.avl.processors;

import com.plectix.simulator.probability.WeightedItem;
import com.plectix.simulator.probability.avl.WeightedNode;

public class ElementFinder {
	private static ElementFinder instance = new ElementFinder();
	
	private ElementFinder() {
	}
	
	public static ElementFinder getInstance() {
		return instance;
	}
	
	public <E extends WeightedItem> WeightedNode<E> search(WeightedNode<E> root, 
			E element) {
		if (root == null) {
			return null;
		} else {
			if (root.getElement() == element) {
				return root;
			} else {
				if (element.getWeight() >= root.getWeight()) {
					return search(root.getRightChild(), element);
				} else {
					return search(root.getLeftChild(), element);
				}
			}
		}
	}
}
