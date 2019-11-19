package org.xidian.test;

public class TestStatic {
	public static String str;
	
	public static void main(String[] args) {

		int[][] a = new int[20000][20000]; 
		for(int i = 0; i <a.length ; i++ ) {
			for(int j = 0; j <a.length ; j++ ) {
				a[i][j]=1;
			}
		}
		for(int i = 0; i <a.length ; i++ ) {
			for(int j = 0; j <a.length ; j++ ) {
				System.out.println(a[i][j]);
			}
		}
		
	}

}
