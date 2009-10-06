package com.plectix.simulator.interfaces;

/**
 * Interface of observable rule component.<br>
 * <br>
 * Example:<br>
 * <code>
 * 'BND' B(x),A(x) -> B(x!1),A(x!1) @ 1.0<br> 
 * %obs: 'BND'<br>
 * </code><br>
 * where <code>"%obs: 'BND'"</code> means observable rule component.
 * 
 * @author avokhmin
 *
 */
/**
 * Observable rule component. Sometimes we want to watch statistics of some rule applications.
 * Then we should place this rule's name in observables. This entity is a rule placed
 * in observables.
 */
public interface ObservableRuleInterface extends ObservableInterface {
}
