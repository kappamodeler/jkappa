package com.plectix.simulator.probability.avl.processors;

import com.plectix.simulator.probability.WeightedItem;
import com.plectix.simulator.probability.avl.Orientation;
import com.plectix.simulator.probability.avl.WeightedNode;

public final class SingleRotatePerformer implements RotatePerformer {
	private static SingleRotatePerformer instance = new SingleRotatePerformer();
	
	private SingleRotatePerformer() {
	}
	
	public static SingleRotatePerformer getInstance() {
		return instance;
	}
	
	public <E extends WeightedItem> WeightedNode<E> perform(WeightedNode<E> root, 
			Orientation o) {
		// LR
		Orientation m = o.reflect();
		WeightedNode<E> rootsParent = root.getParent();
		WeightedNode<E> pivot = root.getChild(m);
		WeightedNode<E> betha = pivot.getChild(o);
		// v becomes a new son of this's father
		
		if (rootsParent != null) {
			rootsParent.setChild(root.whichChild(), pivot);
		} else {
			pivot.forgetParent();
			root.getTree().setRoot(pivot);
		}
		root.setChild(m, betha);
		pivot.setChild(o, root);
		if (o == Orientation.LEFT) {
			setBalancesOnLeftRotation(pivot.getBalance(), root, pivot);
		} else {
			setBalancesOnRightRotation(pivot.getBalance(), root, pivot);
		}
		return pivot;
	}
	
	private void setBalancesOnLeftRotation(int oldPivotBalance, 
			WeightedNode<?> root, WeightedNode<?> pivot) {
		if (oldPivotBalance != 0) {
			pivot.setBalance(0);
			root.setBalance(0);
		} else {
			pivot.setBalance(-1);
			root.setBalance(1);
		}
	}
	
	private void setBalancesOnRightRotation(int oldPivotBalance, 
			WeightedNode<?> root, WeightedNode<?> pivot) {
		if (oldPivotBalance != 0) {
			pivot.setBalance(0);
			root.setBalance(0);
		} else {
			pivot.setBalance(1);
			root.setBalance(-1);
		}
	}
}
