package com.tqhy.client.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Yiheng
 * @create 4/2/2019
 * @since 1.0.0
 */
public class FileUtils {

    static Logger logger = LoggerFactory.getLogger(FileUtils.class);


    /**
     * 删除文件夹
     *
     * @param temp
     * @return
     */
    public static void deleteDir(File temp) {
        logger.info("into delete");
        try {
            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按行读取文件,返回一个由每行处理结果对象组成的集合
     *
     * @param file     待读行文件
     * @param function 对每一行内容处理的{@link Function <String,T> Function}
     * @param <T>      返回集合泛型
     * @return
     */
    public static <T> List<T> readLine(File file, Function<String, T> function) {
        ArrayList<T> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                T apply = function.apply(line);
                if (null != apply) {
                    list.add(apply);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 写文件
     *
     * @param file
     * @param info     待写入信息
     * @param function 处理写入内容,为null则不做任何处理
     * @param create   当文件不存在时是否创建新文件
     * @param append   是否追加写入
     */
    public static void writeFile(File file, String info, @Nullable Function<StringBuilder, StringBuilder> function,
                                 boolean create, boolean append) {

        if (create && !file.exists()) {
            createNewFile(file);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
            if (null == function) {
                writer.write(info);
            } else {
                String apply = function.apply(new StringBuilder(info))
                                       .toString();
                writer.write(apply);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不追加写入,重写文件,覆盖已有内容
     *
     * @param file
     * @param info     待写入信息
     * @param function 处理写入内容,为null则不做任何处理
     * @param create   当文件不存在时是否创建新文件
     */
    public static void writeFile(File file, String info, @Nullable Function<StringBuilder, StringBuilder> function,
                                 boolean create) {
        writeFile(file, info, function, create, false);
    }

    /**
     * 向文件追加内容,内部调用{@link FileUtils#writeFile(File, String, Function, boolean, boolean) writeFile} 方法
     *
     * @param file
     * @param info     待写入信息
     * @param consumer 处理写入内容,为null则不做任何处理
     * @param create   当文件不存在时是否创建新文件
     */
    public static void appendFile(File file, String info, @Nullable Function<StringBuilder, StringBuilder> consumer,
                                  boolean create) {
        writeFile(file, info, consumer, create, true);
    }


    /**
     * 创建新文件
     *
     * @param file
     * @return
     */
    public static boolean createNewFile(File file) {
        if (file.exists()) {
            file.delete();
        }

        try {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            boolean newFile = file.createNewFile();
            return newFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取项目所在路径
     *
     * @return
     */
    public static String getAppPath() {
        String jarPath = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        int end = jarPath.lastIndexOf("/");
        String appPath = jarPath.substring(1, end);
        //logger.info("appPath is: "+appPath);
        return appPath;
    }

    /**
     * 获取相对jar包所在文件夹路径的{@link File File}对象
     *
     * @param relativePath 相对jar包所在文件夹相对路径.以<em>"/"<em/>开头
     * @param fileName     文件名
     * @return
     */
    public static File getLocalFile(String relativePath, String fileName) {
        String rootPath = getAppPath();
        String localFilePath = rootPath + relativePath;

        logger.info("localFilePath is: " + localFilePath);
        File localFile = new File(localFilePath, fileName);
        return localFile;
    }

    /**
     * 如果目标文件存在则替换之
     *
     * @param sourceFile
     * @param destFile
     * @return
     */
    public static boolean copyFile(File sourceFile, File destFile) {

        logger.info("source file: " + sourceFile.getAbsolutePath() + ", dest file: " + destFile.getAbsolutePath());
        if (!sourceFile.exists()) {
            return false;
        }
        if (destFile.exists()) {
            boolean delete = destFile.delete();
            if (!delete) {
                return false;
            }
        }

        if (createNewFile(destFile)) {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile))) {
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile))) {
                    byte[] buffer = new byte[1024 * 8];
                    int i = 0;
                    while ((i = bis.read(buffer)) != -1) {
                        bos.write(buffer);
                    }
                    bos.flush();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 复制jar包内资源文件到jar包外
     *
     * @param resourceName
     * @param destination
     * @return
     */
    public static boolean copyResource(String resourceName, String destination) {
        boolean succeess = true;
        InputStream resourceStream = FileUtils.class.getResourceAsStream(resourceName);
        logger.info("Copying ->" + resourceName + " to ->" + destination);

        try {
            Files.copy(resourceStream, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            logger.error("copy resource error!!", ex);
            succeess = false;
        }

        return succeess;

    }

    /**
     * 文件集合生成文件名为key与文件绝对路径为value的map
     *
     * @param files
     * @return
     */
    public static Map<String, String> getFilesInfoMap(List<File> files) {
        HashMap<String, String> collect = files.stream()
                                               .collect(HashMap::new, (map, file) -> {
                                                   String[] fileNameSplit = file.getName().split("\\.");
                                                   map.put(fileNameSplit[0], file.getAbsolutePath());
                                               }, HashMap::putAll);
        return collect;
    }

}
