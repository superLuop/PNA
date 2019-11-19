package org.xidian.test;

import java.util.Scanner;
import java.util.TreeSet;

public class Test6 {
        public static void main(String[] args){
                //输入一个字符串
                System.out.print("请输入一个字符串：");
                @SuppressWarnings("resource")
				Scanner sc=new Scanner(System.in);
                String str=sc.nextLine();
                //判断输入的字符串中是否有重复字符
                if(judge(str)){
                        //调用输出方法
                        show(str);                
                }else{
                        System.out.println("您输入的字符串有重复字符");
                }
                
        }
        //从str中取出字符，如果取出的字符在TreeSet中存在说明有重复字符，如果不存在放入
        public static boolean judge(String str) {
                TreeSet<Character> tst=new TreeSet<Character>();
                for(int i=0;i<str.length();i++){
                        char ch=str.charAt(i);
                        if(tst.contains(ch)){
                                return false;
                        }else{
                                tst.add(ch);
                        }
                }
                return true;
        }

        public static void show(String str) {
                //调用递归方法
                //参数有字符串，当前操作数的位置，临时存放字符串的buf，存放组合情况的TreeSet
                TreeSet<String> ts=getString(new StringBuffer(str),0,new StringBuffer(),new TreeSet<String>());
                //打印所有组合
                for(String s:ts){
                        System.out.println(s);
                }
                
        }

        public static TreeSet<String> getString(StringBuffer str, int count,StringBuffer buf, TreeSet<String> set) {
                //以每个字母为开头循环
                for(int i=0;i<str.length();i++){
                        //取出字母
                        char c=str.charAt(i);
                        //先把字母删除，以免重复
                        str.deleteCharAt(i);
                        //将当前可能放入缓存
                        buf.append(c);
                        //把组合放入set
                        set.add(buf.toString());
                        //如果str长度不为零，表示还有字符可以取，继续递归
                        if(str.length()!=0){
                                //count用于记录目前在进行排列组合的第count位
                                count++;
                                //递归
                                getString(str,count,buf,set);
                                //第n位递归结束后，需要继续对n-1位排列，位数-1
                                count--;
                        }
                        //删除添加的字符，还原到之前的字符情况
                        buf.deleteCharAt(count);
                        //把删除的字母再放回去，还原
                        str.insert(i, c);
                }
                return set;
        }

}




