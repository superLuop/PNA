package org.xidian.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 矩阵相关操作
 * @author HanChun
 * @version 1.0 2016-5-16
 */
public class Matrix {
	
	private int[][] matrix;
	
	private String[][] strMatrix;
	
	private String matrixName;
	
	/**
	 * 
	 */
	public Matrix() {
		
	}
	
	/**
	 * @param matrix
	 * @param matrixName
	 */
	public Matrix(int[][] matrix, String matrixName) {
		//super();
		//matrix.hashCode();
		this.matrix = matrix;
		this.matrixName = matrixName;
	}

	/**
	 * @param i place个数
	 * @param j token个数
	 * @param matrixName 矩阵名称
	 */
	public Matrix(int rows, int cols, String matrixName) {
		matrix = new int[rows][cols];
		this.matrixName = matrixName;
	}
	
	/**
	 * 变迁矩阵（弧）
	 * @param i place个数
	 * @param j token个数
	 * @param matrixName 矩阵名称
	 */
	public Matrix(int rows, int cols, String matrixName, boolean flag) {
		if(flag) {
			strMatrix = new String[rows][cols];
			this.matrixName = matrixName;
		}
	}
	
	/**
	 * 矩阵复制
	 * @param startRow 
	 * @param startCol
	 * @param endRow
	 * @param endCol
	 * @param matrix
	 * @vturn
	 */
	public static int[][] copyMatrix(int startRow, int startCol, int endRow, int endCol, int[][] matrix) {
		int[][] temMatrix = new int[endRow-startRow][endCol-startCol];
		for(int i = startRow; i < endRow; i++) {
			for(int j = startCol; j < endCol; j++){
				temMatrix[i-startRow][j-startCol] = matrix[i][j];
			}
		}
		return temMatrix;
	}

	/**
	 * 指定某个元素值为value
	 * @param i
	 * @param j
	 * @param value
	 */
	public void setMatrix(int i, int j, int value) {
		if(matrix != null) {
			matrix[i][j] = value; 
		}
	}
	
	/**
	 * 得到矩阵中某个值
	 * @param i
	 * @param j
	 * @return
	 */
	public int getValue(int i, int j) {
		return this.matrix[i][j];
	}
	
	/**
	 * 修改矩阵中的值
	 * @param i
	 * @param j
	 * @return 总共修改个数
	 */
	public static int reviseValue(int[][] matrix, int source, int destination) {
		int count = 0;
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j< matrix[0].length; j++) {
				if(matrix[i][j] == source) {
					matrix[i][j] = destination;
					count++;
				}
			}
		}
		return count;
	}
	
	//for debug 
	public static void printMatrix(int[][] matrix) {
		for(int i = 0; i < matrix.length; i++) {
			System.out.print("["+i+"]");
			for(int j = 0; j< matrix[0].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	//for debug 
	public static String printMatrixInString(int[][] matrix) {
		String result = "";
		for(int i = 0; i < matrix[0].length; i++) {
			for(int j = 0; j< matrix.length; j++) {
				result += (matrix[j][i] + " ");
			}
			result += "\n";
		}
		return result;
	}
	
	//for debug 
	public static void printStrMatrix(String[][] matrix) {
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j< matrix[0].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	/**
	 * 得到矩阵的某一列
	 * @param j
	 * @param matrix
	 * @return
	 */
	public static int[] getMatrixCol(int j, int[][] matrix) {
		int[] tem = new int[matrix.length];
		for(int i = 0; i < matrix.length; i++) {
			tem[i] = matrix[i][j];
		}
		return tem;
	}
	
	/**
	 * 得到矩阵的某一行
	 * @param i
	 * @param matrix
	 * @return
	 */
	public static int[] getMatrixRow(int i, int[][] matrix) {
		int[] tem = new int[matrix.length];
		for(int j = 0; j < matrix.length; j++) {
			tem[j] = matrix[i][j];
		}
		return tem;
	}
	
	/**
	 * 清空矩阵的某一列
	 * @param j
	 * @param matrix
	 * @return
	 */
	public static void clearMatrixCol(int j, int[][] matrix) {
		for(int i = 0; i < matrix.length; i++) {
			 matrix[i][j] = 0;
		}
	}
	
	/**
	 * 清空矩阵的某一行
	 * @param i
	 * @param matrix
	 * @return
	 */
	public static void clearMatrixRow(int i, int[][] matrix) {
		for(int j = 0; j < matrix.length; j++) {
			matrix[i][j] = 0;
		}
	}
	
	/**
	 * 返回数组中非零元素下标+1，即实际状态编号
	 * @param i
	 * @param matrix
	 * @return
	 */
	public static List<Integer> getElementsExceptZero(int[] array) {
		if(array == null || array.length <= 0) {
			return null;
		}
		List<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < array.length; i++) {
			if(array[i] != 0) {
				result.add(i + 1);
			}
		}
		return result;
	}
	
	/**
	 * 返回数组中key:非零元素下标+1,即实际状态编号,value:具体值
	 * @param i
	 * @param matrix
	 * @return
	 */
	public static Map<Integer, Integer> getElementsIndexAndValueExceptZero(int[] array) {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for(int i = 0; i < array.length; i++) {
			if(array[i] != 0) {
				result.put(i + 1, array[i]);
			}
		}
		return result;
	}
	
	/**
	 * 返回矩阵中行或列不是全为0项的下标（+1），真实状态编号
	 * @param i
	 * @param matrix
	 * @return
	 */
	public static Set<Integer> getIndexOfAllZero(int[][] matrix) {
		
		Set<Integer> result = new HashSet<Integer>();
		boolean flag = false;
		//1.检查行
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j< matrix[0].length; j++) {
				if(matrix[i][j] != 0) {
					flag = true;
					break;
				}
			}
			if(!flag) {
				result.add(i + 1);
			}
			flag = false;
		}
		flag = false;
		//2.检查列
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j< matrix[0].length; j++) {
				if(matrix[j][i] != 0) {
					flag = true;
					break;
				}
			}
			if(!flag) {
				result.add(i + 1);
			}
			flag = false;
		}
		return result;
	}
	
	
	//for debug
	public static void printArr(int[] array) {
		for(int i = 0; i < array.length; i++) {
			System.out.print(array[i]+ "");
		}
	}
	
	public String[][] getStrMatrix() {
		return strMatrix;
	}

	public void setStrMatrix(String[][] strMatrix) {
		this.strMatrix = strMatrix;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
	}

	public String getMatrixName() {
		return matrixName;
	}

	public void setMatrixName(String matrixName) {
		this.matrixName = matrixName;
	}

}
