package com.plectix.simulator.probability.avl;

public class BalanceRecoveryUnit extends AncestorsVisitor {
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
		
	}
}
