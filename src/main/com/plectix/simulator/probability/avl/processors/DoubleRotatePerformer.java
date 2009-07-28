package com.plectix.simulator.probability.avl.processors;

import com.plectix.simulator.probability.WeightedItem;
import com.plectix.simulator.probability.avl.Orientation;
import com.plectix.simulator.probability.avl.WeightedNode;

public final class DoubleRotatePerformer implements RotatePerformer {
	private static DoubleRotatePerformer instance = new DoubleRotatePerformer();
	
	private DoubleRotatePerformer() {
	}
	
	public static DoubleRotatePerformer getInstance() {
		return instance;
	}
	
	public <E extends WeightedItem> WeightedNode<E> perform(WeightedNode<E> root, 
			Orientation o) {
		// LR
		Orientation m = o.reflect();
		WeightedNode<E> rootsParent = root.getParent();
		WeightedNode<E> pivot = root.getChild(o);
		WeightedNode<E> bottom = pivot.getChild(m);
		WeightedNode<E> betha1 = bottom.getChild(o);
		WeightedNode<E> betha2 = bottom.getChild(m);
		
		if (rootsParent != null) {
			root.getParent().setChild(root.whichChild(), bottom);
		} else {
			bottom.forgetParent();
			root.getTree().setRoot(bottom);
		}
		
		pivot.setChild(m, betha1);
		bottom.setChild(o, pivot);
		root.setChild(o, betha2);
		bottom.setChild(m, root);
		if (o == Orientation.LEFT) {
			setBalancesOnLeftRotation(bottom.getBalance(), root, pivot);
		} else {
			setBalancesOnRightRotation(bottom.getBalance(), root, pivot);
		}
		bottom.setBalance(0);
		return bottom;
	}
	
	private void setBalancesOnLeftRotation(int oldBottomBalance, 
			WeightedNode<?> root, WeightedNode<?> pivot) {
		if (oldBottomBalance == 0) {
			root.setBalance(0);
			pivot.setBalance(0);
		} else if (oldBottomBalance == 1) {
			root.setBalance(0);
			pivot.setBalance(-1);
		} else if (oldBottomBalance == -1) {
			root.setBalance(1);
			pivot.setBalance(0);
		}
	}
	
	private void setBalancesOnRightRotation(int oldBottomBalance, 
			WeightedNode<?> root, WeightedNode<?> pivot) {
		if (oldBottomBalance == 0) {
			root.setBalance(0);
			pivot.setBalance(0);
		} else if (oldBottomBalance == 1) {
			root.setBalance(-1);
			pivot.setBalance(0);
		} else if (oldBottomBalance == -1) {
			root.setBalance(0);
			pivot.setBalance(1);
		}
	}
}
