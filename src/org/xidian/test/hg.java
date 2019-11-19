package org.xidian.test;

//import java.util.ArrayList;
import java.util.HashSet;
//import java.util.List;
import java.util.Set;

public class hg {
	
	public static void main(String[] args) {
		Set<String> kk = new HashSet<String>();
		kk.add("a");
		kk.add("b");
		kk.add("c");
		for(String k:kk){
			if(k.equals("a")){
				kk.add("u");
			}
			System.out.println(k);
		}
	}
     
}
