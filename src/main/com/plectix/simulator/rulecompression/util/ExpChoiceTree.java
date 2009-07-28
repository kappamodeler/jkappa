package com.plectix.simulator.rulecompression.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ExpChoiceTree<E> {
	private final E value;
	private final ExpChoiceTree<E> parent;
	private final ExpChoiceTree<E> root;
	private final List<ExpChoiceTree<E>> children = new ArrayList<ExpChoiceTree<E>>();
	private final List<ExpChoiceTree<E>> allLeaves = new ArrayList<ExpChoiceTree<E>>(); 
	
	private ExpChoiceTree(E value, ExpChoiceTree<E> parent, ExpChoiceTree<E> root) {
		this.value = value;
		this.parent= parent;
		this.root = root;
	}
	
	public ExpChoiceTree() {
		root = this;
		value = null;
		parent = null;
	}

	public void addNextFloor(List<E> elements, boolean isLastFloor) {
		if (children.isEmpty()) {
			for (E element : elements) {
				ExpChoiceTree<E> newNode = new ExpChoiceTree<E>(element, this, root);
				if (isLastFloor) {
					root.allLeaves.add(newNode);
				}
				children.add(newNode);	
			}
		} else {
			for (ExpChoiceTree<E> child : children) {
				child.addNextFloor(elements, isLastFloor);
			}
		}
	}
	
	public Collection<Collection<E>> getAllPaths() {
		List<Collection<E>> result = new ArrayList<Collection<E>>();
		for (ExpChoiceTree<E> leave : allLeaves) {
			ExpChoiceTree<E> currentVertex = leave;
			LinkedList<E> path = new LinkedList<E>();
			while (currentVertex.parent != null) {
				path.addFirst(currentVertex.value);
				currentVertex = currentVertex.parent;
			}
			result.add(path);
		}
		return result;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (ExpChoiceTree<E> leave : allLeaves) {
			sb.append(" " + leave.value);
		}
		return sb + "";
	}
	
	public static final void main(String[] args) {
		ExpChoiceTree<Integer> root = new ExpChoiceTree<Integer>();
		ArrayList<Integer> secondFloor = new ArrayList<Integer>();
		secondFloor.add(5);
		secondFloor.add(6);
		root.addNextFloor(secondFloor, false);
		ArrayList<Integer> thirdFloor = new ArrayList<Integer>();
		thirdFloor.add(0);
		thirdFloor.add(1);
		root.addNextFloor(thirdFloor, false);
		ArrayList<Integer> fourthFloor = new ArrayList<Integer>();
		fourthFloor.add(7);
		fourthFloor.add(8);
		root.addNextFloor(fourthFloor, true);
		System.out.println(root);
		System.out.println(root.getAllPaths());
	}
}
