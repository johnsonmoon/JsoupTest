package xuyihao.JsoupTest.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     用来追踪代码执行情况的工具类
 * </pre>
 * 
 * Created by Xuyh on 2016/12/6.
 */
public class StackTraceUtil {
	/**
	 * 信息编码-->行号
	 * <pre>
	 *     用来获取Map中的值
	 * </pre>
	 */
	public static final String INFO_CODE_LINE_NUMBER = "LINE_NUMBER";
	/**
	 * 信息编码-->方法名
	 * <pre>
	 *     用来获取Map中的值
	 * </pre>
	 */
	public static final String INFO_CODE_METHOD_NAME = "METHOD_NAME";
	/**
	 * 信息编码-->类名
	 * <pre>
	 *     用来获取Map中的值
	 * </pre>
	 */
	public static final String INFO_CODE_CLASS_NAME = "CLASS_NAME";
	/**
	 * 信息编码-->文件名
	 * <pre>
	 *     用来获取Map中的值
	 * </pre>
	 */
	public static final String INFO_CODE_FILE_NAME = "FILE_NAME";

	/**
	 * 获取调用方法在方法所属文件中的行信息
	 * <pre>
	 *     包括： 
	 *     LINE_NUMBER 行号
	 *     METHOD_NAME 方法名
	 *     CLASS_NAME 类名
	 *     FILE_NAME 文件名
	 * </pre>
	 * 
	 * @return
	   */
	public static Map<String, Object> getCurrentSourceLineInfoMap() {
		Map<String, Object> info = new HashMap<>();
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		if (stackTraceElements.length > 2) {
			info.put("LINE_NUMBER", stackTraceElements[2].getLineNumber());
			info.put("METHOD_NAME", stackTraceElements[2].getMethodName());
			info.put("CLASS_NAME", stackTraceElements[2].getClassName());
			info.put("FILE_NAME", stackTraceElements[2].getFileName());
		}
		return info;
	}

	/**
	 * 获取调用方法在方法所属文件中的行信息，以字串形式返回
	 *
	 * @return
	   */
	public static String getCurrentSourceLineInfoString() {
		String info = "";
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		if (stackTraceElements.length > 2) {
			info += ("[Line: " + stackTraceElements[2].getLineNumber());
			info += ("], [Method: " + stackTraceElements[2].getMethodName());
			info += ("], [Class: " + stackTraceElements[2].getClassName());
			info += ("], [File: " + stackTraceElements[2].getFileName() + "]");
		}
		return info;
	}

	/**
	 * 获取调用方法在方法所属文件中的行号
	 *
	 * @return
	 */
	public static int getCurrentSourceLineNumber() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		int invokerLineNumber = 0;
		if (stackTraceElements.length > 2) {
			invokerLineNumber = stackTraceElements[2].getLineNumber();
		}
		return invokerLineNumber;
	}

	/**
	 * 获取调用方法的堆栈情况
	 *
	 * @return
	 */
	public static StackTraceElement[] getStacTraceElements() {
		return Thread.currentThread().getStackTrace();
	}
}
