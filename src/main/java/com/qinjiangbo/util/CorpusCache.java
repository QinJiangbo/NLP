package com.qinjiangbo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorpusCache {
	
	private static Map<String, Integer> senMap = new HashMap<String, Integer>();
	private static boolean loaded = false;
	
	/**
	 * 从语料库文件读取数据
	 * @param dirPath
	 */
	private static String readData(String dirPath) throws Exception {
		String content = "";
		File file = new File(dirPath);
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String tmpLine = null;
				while((tmpLine = reader.readLine()) != null) {
					content += tmpLine;
				}
				reader.close();
			}
		}else{
			throw new FileNotFoundException("无效的目录路径!");
		}
		return content;
	}
	
	/**
	 * 将文件系统读取的文件数据加载到Map中
	 * @param dirPath
	 */
	private static void loadDataFromFS(String dirPath) {
		try {
			String content = readData(dirPath);
			List<String> sentences = splitText(content);
			for(String sentence : sentences) {
				String[] words = sentence.split(",| |;");
				senMap.put(sentence, words.length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将数据库的数据加载到Map中
	 */
	@SuppressWarnings("unused")
	private static void loadDataFromDb() {
		
	}
	
	/**
	 * 加载缓存信息
	 * @return
	 */
	public static Map<String, Integer> loadCache() {
		long startTime = System.currentTimeMillis();
		if(!loaded) {
			loadDataFromFS(FilePath.CORPUS);
			loaded = true;
		}
		long endTime = System.currentTimeMillis();
		System.out.println("cache loaded in " + (endTime - startTime) + "ms!");
		return senMap;
	}
	
	/**
	 * 将文章切分成每一个句子
	 * @param text 学生文章
	 * @return 句子的集合
	 */
	public static List<String> splitText(String text) {
		text = replaceChSymbol(text);
		List<String> list = new ArrayList<String>();
		boolean inQuote = false;
		boolean patched = false; // 是否补引号，用于将“”内的句子拆分
		int oriPos = 0;
		char[] s = null;
		char[] c = text.toCharArray();
		// 句尾为!、?、...、.
		for (int i = 0; i < c.length; i++) {

			if (c[i] == '"' && !inQuote) {
				inQuote = true;
			} else if (c[i] == '"' && inQuote) {
				// 前朔去掉前面的空格
				int j = i;
				for (; j >= 0 && c[j - 1] == ' '; j--) {
				}
				// 引号结束，根据引号前面的前面标点符号，进行下一步处理
				char m = c[j - 1];
				if (m == '.' || m == '!' || m == '?') {// 引号前的标点是结束
					if (patched) {
						s = new char[i + 2 - oriPos];
						System.arraycopy(c, oriPos, s, 1, i + 1 - oriPos);
						s[0] = '"';
					} else {
						s = new char[i + 1 - oriPos];
						System.arraycopy(c, oriPos, s, 0, i + 1 - oriPos);
					}
					list.add(new String(s));
					oriPos = i + 1;
				}
				inQuote = false;
				patched = false;
				continue;
			}
			/*
			 * if( inQuote ) //引号作为一个整体，里面内容不做判断 continue ;
			 */
			if (c[i] == '!' || c[i] == '?' || c[i] == ';') { // 直接分句
				// 判断后一个字符是否引号，是引号，跳过
				if (i < c.length - 1 && c[i + 1] == '"') {
					continue;
				}
				if (i - oriPos > 0) {
					if (inQuote) {
						s = new char[i + 2 - oriPos];
						patched = true;
						s[i + 1 - oriPos] = '"';
					} else
						s = new char[i + 1 - oriPos];
					System.arraycopy(c, oriPos, s, 0, i + 1 - oriPos);
					list.add(new String(s));
					oriPos = i + 1;
				}
				continue;
			} else if (c[i] == '.') { // 前行判断
				// 跳过后面的空格
				for (; i < c.length && c[i] == ' '; i++) {

				}
				String str = null;
				if (c.length - i > 2) {
					str = text.substring(i + 1, i + 3);
					if (str.compareTo("..") == 0) {// 为省略号
						// 判断后一个字符是否引号，是引号，跳过
						if (i < c.length - 1
								&& c[i + 1] == '"') {
							continue;
						}

						if (inQuote) {
							s = new char[i + 4 - oriPos];
							patched = true;
							s[i + 3 - oriPos] = '"';
						} else
							s = new char[i + 3 - oriPos];
						System.arraycopy(c, oriPos, s, 0, i + 3 - oriPos);
						list.add(new String(s));
						i = i + 3; // 跳过省略号
						oriPos = i;
						continue;
					}

					if (str.indexOf('.') != -1) { // 剔除W.E.B.这类的缩写
						i = i + 3;
						continue;
					}

				}
				// 后朔两个，判断是否Mr、Ms、Mz、Dr
				if (i - 2 > 0) {
					str = text.substring(i - 2, i);
					if (str.compareTo("Mr") == 0
							|| str.compareTo("Ms") == 0
							|| str.compareTo("Mz") == 0
							|| str.compareTo("Dr") == 0) {
						continue;
					}
				}
				if (i - 3 > 0) {
					str = text.substring(i - 3, i);
					// 后朔3个，判断是否是Mrs、Mme、Hon、
					if (str.compareTo("Mrs") == 0
							|| str.compareTo("Mme") == 0
							|| str.compareTo("Hon") == 0
							|| str.compareTo("Rev") == 0) {
						continue;
					}
				}

				if (c.length - i > 1
						&& !Character.isLowerCase(c[i + 1])) {// 后一个字母不是小写字母
					// 判断后一个字符是否引号，是引号，跳过
					if (i < c.length - 1 && c[i + 1] == '"') {
						continue;
					}
					if (inQuote) {
						s = new char[i + 2 - oriPos];
						patched = true;
						s[i + 1 - oriPos] = '"';
					} else {
						s = new char[i + 1 - oriPos];
					}
					System.arraycopy(c, oriPos, s, 0, i + 1 - oriPos);
					list.add(new String(s));
					oriPos = i + 1;
					continue;
				}
			}
		}
		if (c.length - oriPos > 0) {
			s = new char[c.length - oriPos];
			System.arraycopy(c, oriPos, s, 0, c.length - oriPos);
			String str = new String(s);
			if (str.trim().length() > 0)
				list.add(str);
		}
		return list;
	}
	
	/**
	 * 替换中文符号
	 * @param text 文本
	 * @return 处理后的文本
	 */
	private static String replaceChSymbol(String text) {
		text = text.replaceAll("[\r\n]", " "); //回车符和换行符变为空格
		text = text.replaceAll("“", "\"");
		text = text.replaceAll("”", "\"");
		text = text.replaceAll("，", ",");
		text = text.replaceAll("。", ".");
		text = text.replaceAll("？", "?");
		text = text.replaceAll("！", "!");
		text = text.replaceAll(" +", " ");
		text = text.replaceAll("’", "'");
		text = text.replaceAll("‘", "'");
		text = text.replaceAll("- ", "");
		return text;
	}
}
