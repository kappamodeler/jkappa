package com.plectix.simulator.probability.avl.processors;

import com.plectix.simulator.probability.WeightedItem;
import com.plectix.simulator.probability.avl.WeightedNode;

public class ElementAdder {
	private static ElementAdder instance = new ElementAdder();
	
	private ElementAdder() {
	}
	
	public static ElementAdder getInstance() {
		return instance;
	}
	
	public <E extends WeightedItem> WeightedNode<E> addElement(WeightedNode<E> root, 
			E element) {
		if (element.getWeight() >= root.getWeight()) {
			if (root.getRightChild() == null) {
				WeightedNode<E> added = new WeightedNode<E>(element, root.getTree());
				root.setRightChild(added);
				return added;
			} else {
				return addElement(root.getRightChild(), element);
			}
		} else {
			if (root.getLeftChild() == null) {
				WeightedNode<E> added = new WeightedNode<E>(element, root.getTree());
				root.setLeftChild(added);
				return added;
			} else {
				return addElement(root.getLeftChild(), element);
			}
		}
	}
}
