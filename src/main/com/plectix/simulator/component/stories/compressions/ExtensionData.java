package com.plectix.simulator.component.stories.compressions;

import com.plectix.simulator.component.stories.storage.WireHashKey;

/**
 * left1 and right1 has same siteId left2 and right2 has same siteId
 * a_1(x!1),b_1(x!1) <-> a_2(x!1),b_2(x!2) left1 - linkstate a_1(x) on wk1 left2
 * - linkstate b_1(x) on wk2 right1 - linkstate a_2(x) on wk3 right2 - linkstate
 * b_2(x) on wk4
 */

public final class ExtensionData {
	public WireHashKey wk1;
	public WireHashKey wk2;
	public WireHashKey wk3;
	public WireHashKey wk4;
}
