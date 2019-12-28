package com.joe.tool;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PicOrganizer {

	public static boolean createFile(String destFileName) {
		File file = new File(destFileName);
		if (file.exists()) {
			System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
			return false;
		}
		if (destFileName.endsWith(File.separator)) {
			System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
			return false;
		}
		// 判断目标文件所在的目录是否存在
		if (!file.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			System.out.println("目标文件所在目录不存在，准备创建它！");
			if (!file.getParentFile().mkdirs()) {
				System.out.println("创建目标文件所在目录失败！");
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {
				System.out.println("创建单个文件" + destFileName + "成功！");
				return true;
			} else {
				System.out.println("创建单个文件" + destFileName + "失败！");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
			return false;
		}
	}

	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		// 创建目录
		if (dir.mkdirs()) {
			System.out.println("创建目录" + destDirName + "成功！");
			return true;
		} else {
			System.out.println("创建目录" + destDirName + "失败！");
			return false;
		}
	}

	public static String createTempFile(String prefix, String suffix, String dirName) {
		File tempFile = null;
		if (dirName == null) {
			try {
				// 在默认文件夹下创建临时文件
				tempFile = File.createTempFile(prefix, suffix);
				// 返回临时文件的路径
				return tempFile.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("创建临时文件失败！" + e.getMessage());
				return null;
			}
		} else {
			File dir = new File(dirName);
			// 如果临时文件所在目录不存在，首先创建
			if (!dir.exists()) {
				if (!createDir(dirName)) {
					System.out.println("创建临时文件失败，不能创建临时文件所在的目录！");
					return null;
				}
			}
			try {
				// 在指定目录下创建临时文件
				tempFile = File.createTempFile(prefix, suffix, dir);
				return tempFile.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("创建临时文件失败！" + e.getMessage());
				return null;
			}
		}
	}

	/**
	 * 获得某个月最大天数
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份 (1-12)
	 * @return 某个月最大天数
	 */
	public static int getMaxDayByYearMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		return calendar.getActualMaximum(Calendar.DATE);
	}

	public static String getFolder(int month, int day) {
		return String.format("%02d", month) + String.format("%02d", day);
	}

	public static void moveFile(String source, String target) {
		try {
			File startFile = new File(source);
			File tmpFile = new File(target);// 获取文件夹路径
			if (!tmpFile.exists()) {// 判断文件夹是否创建，没有创建则创建新文件夹
				tmpFile.mkdirs();
			}
			System.out.println(target + startFile.getName());
			if (startFile.renameTo(new File(target + startFile.getName()))) {
				System.out.println("File is moved successful!");
				System.out.println(String.format("文件移动成功！源：%s，目标路径：%s", source, target));
			} else {
				System.out.println("File is failed to move!");
				System.out.println(String.format("文件移动失败！源：%s", source));
			}
		} catch (Exception e) {
			System.err.println(String.format("文件移动异常！源：%s 至目标路径：%s", source, target));

		}
	}

	public static void main(String[] args) throws IOException {

		String home = "";
		if(args.length != 1){
			System.err.println("Parameters Number: " + args.length);
			System.err.println("Parameter Pattern is not correct: only accept one paramenter, please enter your pics folder path!");
			return;
		} else {
			home = args[0];
		}
		
		if(!home.endsWith(File.separator)){
			home += File.separator;
		}
		StringBuilder target = new StringBuilder();
		/*
		 * int year = 2017; int month = 8; 
		 * int days = getMaxDayByYearMonth(year,month); 
		 * for(int i = 1; i<days+1; i++){ 
		 * 	target = home + File.separator + getFolder(month, i);
		 *  createDir(target); 
		 * }
		 */

		Pattern pattern = Pattern.compile("^(IMG|VID|MTXX|PANO|Screenshot|faceu)([_-]?)((19|20)\\d\\d((0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])))\\2?\\w*\\.(jpg|mp4)$");
		File file = new File(home);
		Matcher matcher = pattern.matcher(home);
		if (file.exists()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					System.out.println("--- Processing " + f.getName());
					matcher.reset(f.getName());
					if (matcher.find()) {
						String date = matcher.group(5);
						System.out.println(date);
						target.setLength(0);
						target.append(home).append(date).append(File.separator);
						createDir(target.toString());
						moveFile(f.getAbsolutePath(), target.toString());
					}
				}
			}
		} else {
			System.err.println("--- Target doesn't exist: " + file.getName());
        }

	}

}
