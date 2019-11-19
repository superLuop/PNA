package org.xidian.test;

import java.util.HashMap;
import java.util.Map;

public class TestMap {

	public static void main(String[] args) {
		
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(1, 2);
		map.put(1, 3);
		System.out.println(map.get(1));
		

	}

}
