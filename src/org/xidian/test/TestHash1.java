package org.xidian.test;

import java.util.LinkedList;
import java.util.List;

public class TestHash1 {

	@Override
	public int hashCode() {
		return 123;
	}

//	@Override  
//	 public boolean equals(Object o) {
//	    if(this.hashCode()==o.hashCode()){
//	    	return true;    
//	    }
//	    return false;
//	 }
	
	public static void main(String[] args) {
		List<Integer> trace = new LinkedList<Integer>();
		trace.add(1);
		trace.add(5);
		trace.remove((Integer)5);
		System.out.println(trace.size());
	}
	
}
