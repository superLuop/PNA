package org.xidian.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.xidian.alg.BaseData;
import org.xidian.alg.ReachabilityGraphAlgorithm;
import org.xidian.model.Marking;
import org.xidian.model.Matrix;
import org.xidian.model.PetriModel;
import org.xidian.model.StateNode;
import org.xidian.model.Transition;

/**
 * 加载 PN model
 * @author HanChun
 * @version 1.0 2016-5-16
 */
public class LoadModelUtil {
	
	
	public static Map<Integer,String> ifcontrollable = new HashMap<Integer, String>();
	public static Map<Integer,String> ifobservable = new HashMap<Integer, String>();
	public static Map<Integer,String> ifreliable = new HashMap<Integer, String>();
	
	//资源是否可靠
 	public static LinkedList<Integer> up = new LinkedList<Integer>();  //up用于存放不可靠资源库所
 	static LinkedList<Integer> rp = new LinkedList<Integer>();  //rp用于存放资源库所

	static BaseData baseData;

	static int[][] preMatrix;	//前置矩阵
	static int[][] posMatrix;	//后置矩阵
	static List<Integer> iniMarking;  //初始marking
	
	static int defaultTranCount = 1000; //默认最大变迁数量为1000
	static int trueMaxTran = 0; //实际最大的变迁编号
	

	public static Set<Integer> badTrans = null;
	public static Map<Integer, List<Integer>> resourceWeightMap = null;
	static List<Integer> weight = null;
	static Map<String, List<Integer>> Trans = new LinkedHashMap<String, List<Integer>>();
	static String filePath; //文件位置
	
	 
	/**
	 * 加载xxx.pnt文件
	 * @param filePath 文件具体路径
	 */
	public static void loadResource(String filePath) {
		//1.读文件
		String resource = FileUtil.read(filePath, null);

//		System.out.println("文件名："+resource);

		//wss
		String[] split = resource.split("end");
		
		
		Pattern pattern = Pattern.compile("\r|\n");       //正则表达式，以换行间隔的模式
    	String[] strs = pattern.split(split[0]);
    	
    	
    	//wss
   /* 	System.out.println("============");
    	System.out.println(split[1].trim());
    	System.out.println("===========");*/
    	
    	Pattern pattern2 = Pattern.compile("\\s{1,}");    //正则表达式，以空格间隔的模式
    	
    	String[] split1 = split[1].split("div");
    	
    	String[] split2 = pattern.split(split1[0].trim());
    	String[] split3 = pattern.split(split1[1].trim());

    	for(int i=1;i<split2.length;i++){
    		if(split2[i].equals("@")){
    			break;
    		}else{
    			String[] split4 = pattern2.split(split2[i]);
				ifcontrollable.put(Integer.parseInt(split4[0].trim()), split4[1].trim());
				ifobservable.put(Integer.parseInt(split4[0].trim()), split4[2].trim());
    			/*System.out.println("***************");
    			System.out.println(split3[0].trim());
    			System.out.println(split3[1].trim());
    			System.out.println("***************");*/
    		}
    	}

    	for(int j = 1;j<split3.length;j++){
    		if(split3[j].equals("@")){
    			break;
    		}else{
    			String[] split5 = pattern2.split(split3[j]);
				ifreliable.put(Integer.parseInt(split5[0].trim()), split5[1].trim());
			}
    	}
    	
    	preMatrix = new int[strs.length-2][defaultTranCount];
    	posMatrix = new int[strs.length-2][defaultTranCount];
    	iniMarking = new LinkedList<Integer>();

	 	int num = ifreliable.hashCode();
	 	for(int h=1;h<num;h++){
	 		if("u".equals(ifreliable.get(h))){
	 			up.add(h);
	 		}
	 	}
	 	for(int i = 1;i < num;i++) {
    		if("u".equals(ifreliable.get(i)) || "r".equals(ifreliable.get(i))){
	 			rp.add(i);
	 		}
    	}
//	 	System.out.println(up);
//	   	System.out.println(rp);
        //2.解析
    	for(int i = 1; i < strs.length; i++) {
    		if(strs[i].equals("@")) break;
    		parseModelLine(strs[i]);
    	}

    	//计算含有不可靠资源时的坏变迁
		calculateBadTrans(strs);

		//3.得到模型
    	preMatrix = Matrix.copyMatrix(0, 0, strs.length-2, 
    			trueMaxTran, preMatrix);
    	posMatrix = Matrix.copyMatrix(0, 0, strs.length-2, 
    			trueMaxTran, posMatrix);
    	
    	int[] transition = new int[trueMaxTran];
    	for(int i = 0; i<transition.length;i++) {
    		transition[i] = i;
    	}                                               //变迁
    	int[] marking = new int[iniMarking.size()];
    	for(int i = 0; i<iniMarking.size();i++) {
    		marking[i] = iniMarking.get(i); 
    	}                                               //初始标识

    	//4.清空已存在基础模型数据
		if(baseData != null) {
			clearBaseData();
            BaseData.rootState = new StateNode(PetriModel.ininmarking.getMarking(), 1, 1);
		}

    	//初始化变量
    	new PetriModel(new Matrix(preMatrix, "preMatrix"), 
    			new Matrix(posMatrix, "posMatrix"), 
    			new Transition(transition), new Marking(marking));
	}

	/**
	 * 解析模型
	 * @param str .pnt文件中一行
	 */
	public static void parseModelLine(String str){
		if(str==""||str==null) return;
		Trans.put(str, new ArrayList<Integer>());
		String[] strArr = str.split(","); 
		//前置矩阵
		String preStr = strArr[0].replaceAll("\\s{1,}", " ").trim();
		String[] preArr = preStr.split(" ");
		iniMarking.add(Integer.parseInt(preArr[1]));
		for(int i = 2; i < preArr.length; i++) {
			if(preArr[i] == null) {
				continue;
			} 
			if(preArr[i].contains(":")) {
				String[] tem = preArr[i].split(":");
				if((Integer.parseInt(tem[0]))>trueMaxTran){
					trueMaxTran = Integer.parseInt(tem[0]);
				}
				Trans.get(str).add(Integer.parseInt(tem[0]));
				preMatrix[Integer.parseInt(preArr[0])-1]
						[Integer.parseInt(tem[0])-1] = Integer.parseInt(tem[1]);
			} else {
				if(Integer.parseInt(preArr[i])>trueMaxTran) {
					trueMaxTran = Integer.parseInt(preArr[i]);
				}
				Trans.get(str).add(Integer.parseInt(preArr[i]));
				preMatrix[Integer.parseInt(preArr[0])-1]
						[Integer.parseInt(preArr[i])-1] = 1;
			}
		}
		//后置矩阵
		String posStr = strArr[1].replaceAll("\\s{1,}", " ").trim();
		String[] posArr = posStr.split(" ");
		for(int i = 0; i < posArr.length; i++) {
			if(posArr[i] == null) {
				continue;
			} 
			if(posArr[i].contains(":")) {
				String[] tem2 = posArr[i].split(":");
				if((Integer.parseInt(tem2[0]))>trueMaxTran) trueMaxTran =
						Integer.parseInt(tem2[0]);
				Trans.get(str).add(Integer.parseInt(tem2[0]));
				posMatrix[Integer.parseInt(preArr[0])-1]
						[Integer.parseInt(tem2[0])-1] = Integer.parseInt(tem2[1]);	
				
			}else{
				if(Integer.parseInt(posArr[i])>trueMaxTran) trueMaxTran = 
						Integer.parseInt(posArr[i]);
				Trans.get(str).add(Integer.parseInt(posArr[i]));
				posMatrix[Integer.parseInt(preArr[0])-1]
						[Integer.parseInt(posArr[i])-1] = 1;
			}
		}

	}
	
	/**
	 * 为了解决编号超过1位数情况
	 * @param charArray
	 * @return
	 */
	public static String[] parseCharToStringArr(char[] charArray){
		List<String> temList =  new LinkedList<String>();
		
		for(int i = 0; i < charArray.length;) {
			if(i+1 > charArray.length-1) break;
			//有连续数字情况
			if(48 <= charArray[i+1] && charArray[i+1] <= 57) {
				StringBuffer sb = null;
				while(48 <= charArray[i+1] && charArray[i+1] <= 57) {
					sb = new StringBuffer();
					sb.append(String.valueOf(charArray[i]));
					i++;
				}
				temList.add(sb.toString());
			}else{
				temList.add(String.valueOf(charArray[i]));
				i++;
			}
		}
		return (String[]) temList.toArray();
	}
	
	public static void clearBaseData() {
		BaseData.deadStates = null;
		BaseData.graphModel = null;
		BaseData.grapht = null;
//		BaseData.rootState = new StateNode(PetriModel.ininmarking.getMarking(), 1, 1);
	}

	public static void calculateBadTrans(String[] strs){
		badTrans = new HashSet<Integer>();
		resourceWeightMap = new LinkedHashMap<Integer, List<Integer>>();
		List<Integer> list = new LinkedList<>();
		for(int j:up){
			//static Map<String, List<Integer>> Trans = new LinkedHashMap<>();
			if(Trans.containsKey(strs[j])){
				String[] posTran = strs[j].split(",");
				String[] strTrans = posTran[1].trim().split(" ");

				for(int t = 0;t < strTrans.length;t++) {
					if(strTrans[t] == null || "".equals(strTrans[t])) {
						continue;
					}
//    				System.out.println(strTrans[t]);
					if(strTrans[t].contains(":")) {
						String[] tem = strTrans[t].split(":");
						list.add(Integer.parseInt(tem[0]));
					} else {
						list.add(Integer.parseInt(strTrans[t]));
					}

				}

			}else{
				break;
			}

		}

//    	System.out.println(Trans);
		for(int m = 0;m < list.size();m++)
			badTrans.add(list.get(m));//不可靠资源的后置变迁集

		for (String keys : Trans.keySet()) {
			String[] key = keys.trim().split("\\s{1,}"); //以空格分隔
			int resourcePlace = Integer.parseInt(key[0]);
			if(rp.contains(resourcePlace)){   //判断是否为资源库所
//				List<Integer> tempList = Trans.get(keys);
				String[] key1 = keys.trim().split(","); //以逗号分隔
				String[] preKey = key1[0].split("\\s{1,}");
				for(int i = 2;i < preKey.length;i++) {
					if(preKey[i] == null) {
						continue;
					}
					if(preKey[i].contains(":")) {
						String[] temp = preKey[i].split(":");
						if(badTrans.contains(Integer.parseInt(temp[0]))) {
							resourceWeightMap.put(resourcePlace, new ArrayList<Integer>());
							String[] posTrans = key1[1].trim().split("\\s{1,}");
							for(int j = 0;j < posTrans.length;j++) {
								if(posTrans[j] == null) {
									continue;
								}
								if (posTrans[j].contains(":")) {
									String[] posTran = posTrans[j].split(":");
									//Integer.parseInt(key[0]:拿到不可靠资源库所的后置集的后置集，即P··

									resourceWeightMap.get(resourcePlace).add(Integer.parseInt(posTran[1]));
								}else {

									resourceWeightMap.get(resourcePlace).add(1);
								}
							}

//			    			System.out.println(resourcePlace);

						}
					}else {

						if(badTrans.contains(Integer.parseInt(preKey[i]))) {
							resourceWeightMap.put(resourcePlace, new ArrayList<Integer>());
//		    				System.out.println(resourcePlace);
							String[] posTrans = key1[1].trim().split("\\s{1,}");
							for(int j = 0;j < posTrans.length;j++) {
								if(posTrans[j] == null) {
									continue;
								}
								if (posTrans[j].contains(":")) {
									String[] posTran = posTrans[j].split(":");
									//Integer.parseInt(key[0]:拿到不可靠资源库所的后置集的后置集，即P··

									resourceWeightMap.get(resourcePlace).add(Integer.parseInt(posTran[1]));
								}else {

									resourceWeightMap.get(resourcePlace).add(1);
								}
							}
						}
					}
				}
			}
		}

		System.out.println(badTrans);
//		for (Integer key : resourceWeightMap.keySet()) {
//			System.out.println(key + ":" + resourceWeightMap.get(key));
//		}
	}
}
