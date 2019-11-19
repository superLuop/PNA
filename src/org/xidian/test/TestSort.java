package org.xidian.test;

import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;

public class TestSort {

	public static void main(String[] args) {

		int [] t = new int[]{68, 63 ,58, 72, 57, 76 ,47 ,24 ,56 ,82, 71, 50 ,40 ,39, 52 ,35, 66 ,32, 78 ,64 ,48};
	    
		 Arrays.sort(t);
		 for(int i : t) {
			 System.out.print(i + " ");
		 }
	}

}
