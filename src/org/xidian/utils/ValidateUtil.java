package org.xidian.utils;

import org.xidian.model.PetriModel;

/**
 * 验证工具类
 * @author HanChun
 *
 */
public class ValidateUtil {
	
	/**
	 * 验证文件是否导入
	 * @return true 已经导入
	 */
	public static boolean FileImport() {
		return PetriModel.ininmarking != null;
	}
	
	//TODO 各种输入是否规范验证

}
