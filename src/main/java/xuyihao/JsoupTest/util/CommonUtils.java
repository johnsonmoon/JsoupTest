package xuyihao.JsoupTest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * @Author Xuyh created at 2016年11月10日 上午10:32:22
 *
 */
public class CommonUtils {
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * 控制台输出一行
	 * 
	 * @param message
	 */
	public static void output(String message) {
		System.out.println(message);
	}

	/**
	 * 控制台输入
	 * 
	 * @return
	 */
	public static String input() {
		String message = "";
		try {
			message = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}
}
