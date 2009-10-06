package com.plectix.simulator.component.perturbations;

/**
 * This class implements Perturbation type:<br>
 * <li><b>TIME</b> - means time conditional.<br>
 * **CONDITION**:<br> 
 * $T > n<br>
 * where $T is the symbol for time and n is any floating number.<br>
 * or <br>
 * ['specie_j'] (> or <) a_1*['specie_1'] + ... + a_n*['specie_n'] + K<br> 
 * where 'specie_i' must be any named kappa observable which must appear in the kappa file as<br>
 * %obs: 'specie_i' kappa_expression and a_i, K are any floating number. <br>
 * <br>
 * For example:<br>
 * 'intro a' -> a(x) @ 0.0<br> 
 * <br>
 * %init: 10*(a(x),b(x))<br> 
 * %mod: $T>0.5 do 'intro a':=100000<br>
 * </li>
 * 
 * <br><br>
 * <li><b>NUMBER</b> - means modification.<br>
 * **MODIFICATION**:<br>
 * 'rule_i':=a_1*'rule_1' + a_n*'rule_n' + K<br> 
 * where 'rule_j' is any named rule which must appear in the kappa file as<br> 
 * 'rule_j' kappa_expression -> kappa_expression [rate] and a_i,K are any floating number.<br>
 * <br>
 * For example:<br>
 * 'deg a' a(x?) -> @ 0.0 <br> 
 * <br>
 * %init: 10*(a(x),b(x))<br> 
 * %mod: ['a'] > 100.0 do 'deg a':= 1.0<br>
 * </li>
 * <br><br>
 * 
 * <li><b>ONCE</b> - means "once" modification.<br>
 * In general we have kappa file line like<br>
 * **ONCE MODIFICATION**:<br>
 * Does same with <b>TIME</b>.<br>
 * <br>
 * For example:<br>
 * to add 1000 A()<br>
 * %mod: $T>10.0 do $ADDONCE 1000 * A()<br>
 * <br> 
 * to delete 1000 A (give warning if there is less than 1000 A()s)<br> 
 * %mod: $T>10.0 do $DELETEONCE 1000 * A()<br>
 * <br>
 * to remove all A()<br> 
 * %mod: $T>10.0 do $DELETEONCE $INF * A()<br> 
 * </li>
 * 
 * @author avokhmin
 *
 */
public enum PerturbationType {
	TIME,
	NUMBER,
	ONCE;
}
