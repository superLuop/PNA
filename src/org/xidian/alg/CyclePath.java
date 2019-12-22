package org.xidian.alg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.xidian.model.GraphModel;
import org.xidian.model.Matrix;

/**
 * 活锁检测
 * @author HanChun
 * @since 2016-6-7
 * @version 1.0
 * 实际上，这个是可以写成非递归显示的，递归太容易奔溃了
 */
public class CyclePath {
	
	@SuppressWarnings("unused")
	private GraphModel graphModel; 
	
    private int statesCount;
    private int[] visited;//节点状态,值为0的是未访问的
    private int[][] matrix;//有向图的邻接矩阵
    private ArrayList<Integer> trace = new ArrayList<Integer>();//从出发节点到当前节点的轨迹 ；实际上，保存的是个探索路径
    private boolean hasCycle = false;
    private int temCount = 0;
    
    private List<Integer> pathList;
    private List<List<Integer>> allPathList;
     
    public CyclePath(GraphModel graphModel) {
    	pathList = new LinkedList<Integer>();
    	allPathList = new LinkedList<List<Integer>>();
		this.graphModel = graphModel;
		this.statesCount = graphModel.getStatesAmount();;
		visited=new int[statesCount];
        Arrays.fill(visited,0);
        this.matrix = graphModel.getCostMatrix().getMatrix();
	    findCycle(0); // 初始节点开始遍历
	    
	    if(!hasCycle) {
	    	//System.out.println("没有环");
	    }
	    
//	    System.out.println(allPathList.size());
	    validatePath();
	 }

     public void findCycle(int v) {
    	//为1说明找到一个环
        if(visited[v]==1) {
            int j;
            if((j=trace.indexOf(v))!=-1) {
                hasCycle=true;
                System.out.print("["+ (++temCount) +"]");
                while(j < trace.size()) {
                	pathList.add((trace.get(j))+1);
                    System.out.print((trace.get(j))+1+" ");
                    j++;
                }
                allPathList.add((new LinkedList<Integer>(pathList)));
                pathList.clear();
                System.out.print("\n");
                return;
            }
            return;
        }
        //标记为1，
        visited[v]=1;
        trace.add(v);
        
        for(int i=0;i<statesCount;i++) {
            if(matrix[v][i]==1)
                findCycle(i);
        }
        trace.remove(trace.size()-1);
    }
     
     /**
      * 验证是否为活锁路径
      * 算法：除开始点外，其余点，只可以有一个变迁可以发射
      */
     public void validatePath() {

    	 boolean flag = true;
    	 int flagNum = 0;  
    	 int [] tem = null;
    	 
    	 for(int i = 0; i < allPathList.size(); i++) {
    		 //分析每条路径
    		 for(int j = 0; j < allPathList.get(i).size(); j++) {
    			 //得到路径中第j个点的能发射信息，如果超过两个能发射，则不满足要求
    			 tem = Matrix.getMatrixRow(allPathList.get(i).get(j)-1, matrix);
    			 for(int k = 0; k < tem.length - 1; k++ ) {
    				if(tem[k] != 0){
    					flagNum ++;
    					if(flagNum >=2){
    						flag = false;
    						flagNum = 0;
        					break;
    					}
    				}
    			 }
    		 }
    		 //找到一条满足要求的路径
    		 if(flag) System.out.println(allPathList.get(i)); 
    		 flagNum = 0;
    		 flag = true;
    	 } 
     }
     
}
