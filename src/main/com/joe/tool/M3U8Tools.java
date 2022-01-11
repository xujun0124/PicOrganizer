package com.joe.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class M3U8Tools {
	private static boolean m3u8 = false;
	private static boolean key = false;
	private static boolean mp4 = false;
	private static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello!");
		String path = "/Users/i530994/Downloads/kkb1/237312/250174K8S （提前预习）";
		checkFolder(path);
	}

	public static void checkFolder(String path) {
		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (null != files) {
				for (File sub : files) {
					if (sub.isDirectory()) {
						System.out.println("文件夹:" + sub.getAbsolutePath());
						m3u8 = false;
						key = false;
						mp4 = false;
						checkFolder(sub.getAbsolutePath());
					} else {
						// System.out.println("Get File: " + sub.getAbsolutePath());
						if (sub.getName().endsWith(".m3u8")) {
							m3u8 = true;
						} else if (sub.getName().equalsIgnoreCase("key.txt")) {
							key = true;
						} else if (sub.getName().endsWith(".mp4")) {
							mp4 = true;
						}
					}
				}
				if (m3u8 && key && !mp4) {
					String name = path.substring(path.lastIndexOf("/") + 7);
					String cmString = " -allowed_extensions ALL -protocol_whitelist file,http,https,crypto,tcp,tls -i ./a.m3u8 '"
							+ name + ".mp4'";
					try {
						runCmd2(path, "/usr/local/bin/ffmpeg "  + cmString);
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.err.println("===== ===== ===== ===== 跳过mp4文件生成步骤!!!!!!... ===== ===== ===== ===== ");
				}
			}
		} else {
			System.out.println("文件不存在!");
		}
	}

	private static void runCmd(String dir, String cmd) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder();

		if (isWindows) {
			builder.command("cmd.exe", "/c", cmd);
		} else {
			builder.command("/bin/sh", "-c", cmd);
		}
		boolean rerun = true;
		// while (!rerun) {
		builder.directory(new File(dir));
		Process process = builder.start();

		StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), (line) -> {
			if (line.contains("Unable to open resource")) {
				System.err.println(line);
				// rerun = true;
			} else {
				System.out.println(line);
			}
		});
		Executors.newSingleThreadExecutor().submit(streamGobbler);

		int exitCode = process.waitFor();
		assert exitCode == 0;
		System.err.println("Finished: " + exitCode);
		// }
	}

	private static void runCmd2(String dir, String command) throws IOException, InterruptedException {
		String[] cmd = { "/bin/sh", "-c", command };
		try {
			Process p0 = Runtime.getRuntime().exec(cmd, null, new File(dir));
			// 读取标准输出流
			StringBuffer osb = new StringBuffer();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p0.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				osb.append(line);
			}
			// 读取标准错误流
			StringBuffer esb = new StringBuffer();
			BufferedReader brError = new BufferedReader(new InputStreamReader(p0.getErrorStream()));
			String errline = null;
			while ((errline = brError.readLine()) != null) {
				System.out.println(errline);
				esb.append(errline);
			}
			// waitFor()判断Process进程是否终止，通过返回值判断是否正常终止。0代表正常终止
			int returnkey = p0.waitFor();
			System.err.println("Finished with: " + returnkey);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private static class StreamGobbler implements Runnable {
		private InputStream inputStream;
		private Consumer<String> consumer;

		public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}

		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
		}
	}

}
