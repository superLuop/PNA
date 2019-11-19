package org.xidian.test;

import java.util.Set;
import java.util.TreeSet;

public class TestTreeSet {

	public static void main(String[] args) {

		Set<Integer> set = new TreeSet<Integer>();
		set.add(1);
		set.add(2);
		set.add(3);
		set.add(4);
		set.add(5);
		set.add(6);
		Object[] array = set.toArray();
		for(int i = 0; i < array.length; i++) {
			System.out.println((int)array[i]);
		}
		
	}

}
