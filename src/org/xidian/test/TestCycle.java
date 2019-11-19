package org.xidian.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.xidian.utils.FileUtil;
/**
 * 我们的程序，是在这个基础上去修改，除了环以外，还需要所有的点的出度为一
 * @author HanChun
 * 实际上，这个是可以写成非递归显示的，递归太容易奔溃了
 */
public class TestCycle {
     private int n;
     private int[] visited;//节点状态,值为0的是未访问的
     private int[][] e;//有向图的邻接矩阵
     private ArrayList<Integer> trace=new ArrayList<Integer>();//从出发节点到当前节点的轨迹 ；实际上，保存的是个探索路径
     private boolean hasCycle=false;
     
     private int count = 0;

    //邻接矩阵，表示有向图
     public TestCycle(int n,int[][] e){
         this.n=n;
         visited=new int[n];
         Arrays.fill(visited,0);
         this.e=e;
     }
     
     public TestCycle(){
        
     }
    
     void findCycle(int v)   //递归DFS
    {
    	//为1说明找到一个环
        if(visited[v]==1)
        {
            int j;
            if((j=trace.indexOf(v))!=-1)
            {
                hasCycle=true;
                System.out.print("["+ (++count) +"]");
                while(j<trace.size())
                {
                    System.out.print((trace.get(j))+1+" ");
                    j++;
                }
                System.out.print("\n");
                return;
            }
            return;
        }
        //标记为1，
        visited[v]=1;
        trace.add(v);
        
        for(int i=0;i<n;i++)
        {
            if(e[v][i]==1)
                findCycle(i);
        }
        trace.remove(trace.size()-1);
    }
  
  public boolean getHasCycle(){
      return hasCycle;
  }

   public static void main(String[] args) {
	   
	   	String resource = FileUtil.read(System.getProperty("user.dir") + File.separator  + "resources"+File.separator  + "Graph.txt",null);
		Pattern pattern = Pattern.compile("\r|\n");
	   	String[] strs = pattern.split(resource);
	   	String[] temp;
	   	int[][] e = new int[8][8];
	   	for(int i = 0; i < strs.length; i++) {
	   		temp = strs[i].split(" ");
	   		for(int j = 1; j < temp.length; j++) {
	   			//graphModel.costMatrix.getMatrix()[Integer.parseInt(temp[0])][Integer.parseInt(temp[j])] = 1;
	   			e[Integer.parseInt(temp[0])][Integer.parseInt(temp[j])] = 1;
	   		}
	   	}
	   
        int n=8;
//      int[][] e={
//                    {0,1,1,0,0,0,0},
//                    {0,0,0,1,0,0,0},
//                    {0,0,0,0,0,1,0},
//                    {0,0,0,0,1,0,0},
//                    {0,0,1,0,0,0,0},
//                    {0,0,0,0,1,0,1},
//                    {1,0,1,0,0,0,0}};//有向图的邻接矩阵,值大家任意改.
        TestCycle tc=new TestCycle(n,e);
        tc.findCycle(1);
        if(!tc.hasCycle) 
            System.out.println("No Cycle.");
    }
   
   
   public void testCycle(int[][] e, int n) {
	   
//	   int n=8;
//     int[][] e={
//                 {0,1,1,0,0,0,0},
//                 {0,0,0,1,0,0,0},
//                 {0,0,0,0,0,1,0},
//                 {0,0,0,0,1,0,0},
//                 {0,0,1,0,0,0,0},
//                 {0,0,0,0,1,0,1},
//                 {1,0,1,0,0,0,0}}; //有向图的邻接矩阵,值大家任意改.
     TestCycle tc=new TestCycle(n,e);
     tc.findCycle(0);
     if(!tc.hasCycle) System.out.println("No Cycle.");
   	}
	   
	   
   
}
