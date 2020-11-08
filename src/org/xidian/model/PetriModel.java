package org.xidian.model;

/**
 * petri net 数学模型
 * @author luopeng
 * @version 1.0 2018-6-16
 */
public class PetriModel {
	
	public static Matrix preMatrix; //前置矩阵
	public static Matrix posMatrix; //后置矩阵
	public static Transition transition; //变迁
	public static Marking ininmarking; //初始marking
	public static int transCount, placesCount;
	
	public PetriModel() {
		
	}
	
	/**
	 * @param preMatrix
	 * @param posMatrix
	 * @param transition
	 * @param ininmarking
	 */
	public PetriModel(Matrix preMatrix, Matrix posMatrix, Transition transition, Marking ininmarking) {
		PetriModel.preMatrix = preMatrix;
		PetriModel.posMatrix = posMatrix;
		PetriModel.transition = transition;
		PetriModel.ininmarking = ininmarking;
		transCount = transition.getTransition().length;
		placesCount = ininmarking.getMarking().length;
		
	}
	
}
