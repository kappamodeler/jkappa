package com.plectix.simulator.probability.avl.processors;

import com.plectix.simulator.probability.avl.WeightedNode;

public class BalanceRecoveryUnit extends AncestorsVisitor {
	private static BalanceRecoveryUnit instance = new BalanceRecoveryUnit();
	
	private BalanceRecoveryUnit() {
		
	}
	
	public static BalanceRecoveryUnit getInstance() {
		return instance;
	}
	/**
	 * After new vertex addition, this method restores it's ancestors subtrees balances  
	 * @param vertex addedVertex
	 */
	public void restoreBalanceAfterAdding(WeightedNode<?> vertex) {
		this.visit(vertex, new AddedVertexAncestorVisitor());
	}
	
	/**
	 * After one vertex removal, this method restores it's ancestors subtrees balances  
	 * @param vertex removedVertex
	 */
	public void restoreBalanceAfterDeleting(WeightedNode<?> vertex) {
		this.visit(vertex, new DeletedVertexAncestorVisitor());
	}
}
