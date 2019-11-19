package org.xidian.test;

import java.util.ArrayList;
import java.util.List;

public class test11 {
public static void main(String[] args) {
	List<String> list = new ArrayList<String>();
	list.add("张三");
	list.add("李四");
	
	System.out.println(list.contains(list.get(0)));
}
}
