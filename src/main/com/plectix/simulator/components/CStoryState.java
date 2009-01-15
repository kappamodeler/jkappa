package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.IStates;

public class CStoryState implements IStates {
	
		private int idInternalState = -1;
		private long idLinkAgent = -1;
		private int idLinkSite = -1;

		public CStoryState() {
		}

		public CStoryState(int idInternalState, long idLinkAgent, int idLinkSite) {
			this.idInternalState = idInternalState;
			this.idLinkAgent = idLinkAgent;
			this.idLinkSite = idLinkSite;
		}

		public CStoryState(int idInternalState) {
			this.idInternalState = idInternalState;
		}

		public CStoryState(long idLinkAgent, int idLinkSite) {
			this.idLinkAgent = idLinkAgent;
			this.idLinkSite = idLinkSite;
		}

		public int getIdInternalState() {
			return idInternalState;
		}

		public long getIdLinkAgent() {
			return idLinkAgent;
		}

		public int getIdLinkSite() {
			return idLinkSite;
		}

		public void addInformation(int idInternalState, long idLinkAgent,
				int idLinkSite) {
			if (this.idInternalState == -1)
				this.idInternalState = idInternalState;
			if (this.idLinkAgent == -1) {
				this.idLinkAgent = idLinkAgent;
				this.idLinkSite = idLinkSite;
			}
		}
	
}
