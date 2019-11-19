package org.xidian.test;

public class Test13 {
    public static String string = "abc";
    //递归实现
    public static void compute(int current_recur, String temp) {
        if(current_recur < string.length()) {
            for(int i = 0; i < string.length(); i++) {
                if(!( temp.contains(string.substring(i, i + 1)))){
                    System.out.println(temp + string.substring(i, i + 1));
                    compute(current_recur + 1, new String(temp + string.substring(i, i + 1)));
                }
            }
        }
    }
    
    public static void main(String[] args){
        compute(0, "");
    }
    
}