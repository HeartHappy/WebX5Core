package com.hearthappy.x5core_arm64_v8a.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class IOUtils {
    public static String SDCardRoot;
    public static File updateFile;
    static List<String> safeFiles = new ArrayList<String>();

    static {
        // 获取SD卡路�??
        SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 判断SDCard是否可用
     *
     * @return boolean
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }


    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return long
     */
    @SuppressWarnings("deprecation")
    public static long getFreeBytes() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(SDCardRoot);
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    public static void addSafeFile(String file) {
        if (safeFiles.contains(file)) return;
        safeFiles.add(file);
    }

    public static String getFileName(String url) {
        if (url != null) {
            int start = url.lastIndexOf("/") + 1;
            int end = url.length();
            return url.substring(start, end);
        }
        return "";
    }


    /**
     * 创建文件�??
     *
     * @throws IOException
     */
    public static File createFileInSDCard(String fileName, String dir) {
        File file = new File(SDCardRoot + dir + File.separator + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateFile = file;
        return file;
    }

    /**
     * 创建文件�??
     *
     * @throws IOException
     */
    public static File createFileInDataCard(String fileName, String dir) {
        File file = new File(dir + File.separator + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateFile = file;
        return file;
    }

    /**
     * 创建目录
     */
    public static File creatSDDir(String dir) {
        File dirFile = new File(SDCardRoot + dir + File.separator);
        dirFile.mkdirs();
        return dirFile;
    }

    /**
     * 创建目录
     */
    public static File creatDataDir(String dir) {
        File dirFile = new File(dir + File.separator);
        dirFile.mkdirs();
        return dirFile;
    }

    public static void deleteExtFile(String dir, String ext) {
        File file = new File(SDCardRoot + dir);
        if (file == null)
            return;

        if (!file.exists())
            return;

        if (file.isDirectory()) {
            for (File tFile2 : file.listFiles()) {
                if (tFile2.isDirectory()) {
                    continue;
                }

                if (tFile2.isFile()) {
                    String name = file.getAbsolutePath();
                    name = name.substring(name.lastIndexOf("/") + 1);
                    if (safeFiles.contains(name))
                        continue;
                }

                if (tFile2.getName().endsWith(ext))
                    tFile2.delete();
            }
        }
    }

    public static void deleteFile(String sdPath, String fileName) {
        File file = new File(SDCardRoot + sdPath + File.separator + fileName);
        safeDeleteFile(file);
    }

    public static void safeDeleteFile(File file) {
        if (file == null)
            return;

        if (!file.exists())
            return;

        if (file.isDirectory()) {
            for (File tFile2 : file.listFiles())
                safeDeleteFile(tFile2);
        }

        if (file.isFile()) {
            String name = file.getAbsolutePath();
            name = name.substring(name.lastIndexOf("/") + 1);
            if (safeFiles.contains(name))
                return;
        }

        file.delete();
    }

    /**
     * �??测文件是否存�??
     */
    public static boolean isFileExist(String path, String fileName) {
        File file = new File(SDCardRoot + path + File.separator + fileName);
        return file.exists();
    }

    /**
     * 通过流往文件里写东西
     */
    public static File writeToSDFromInput(String path, String fileName, InputStream input, boolean append) {

        File file = null;
        OutputStream output = null;
        try {
            file = createFileInSDCard(fileName, path);
            output = new FileOutputStream(file, append);
            byte buffer[] = new byte[4 * 1024];
            int temp;
            while ((temp = input.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            output = null;
        }
        return file;
    }

    public static void writeToSDFromInput(String sdPath, String fileName, String data) {
        writeToSDFromInput(sdPath, fileName, data, true);
    }

    /**
     * 把字符串写入文件
     */
    public static File writeToSDFromInput(String sdPath, String fileName, String data, boolean append) {
        File file = null;
        OutputStreamWriter outputWriter = null;
        OutputStream outputStream = null;
        try {
            creatSDDir(sdPath);
            file = createFileInSDCard(fileName, sdPath);
            outputStream = new FileOutputStream(file, append);
            outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(data);
            outputWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            outputWriter = null;
        }
        return file;
    }

    /**
     * 把字符串写入文件
     */
    public static File writeToDataFromInput(String fileName, String data, Context context) {

        File file = null;
        OutputStreamWriter outputWriter = null;
        OutputStream outputStream = null;
        try {
            // creatDataDir(path);
            // file = createFileInDataCard(fileName, path);
            outputStream = context.openFileOutput(fileName, Context.MODE_APPEND);
            outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(data);
            outputWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            outputWriter = null;
        }
        return file;
    }


    public static String readFile(String fullpath) {
        String resutl = "";
        File file = new File(fullpath);
        if (file.isFile() && file.exists()) {

            InputStreamReader read = null;
            try {
                read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    resutl += lineTxt;
                }

            } catch (Exception e) {
            } finally {
                if (read != null) {
                    try {
                        read.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    read = null;
                }
            }

        }
        return resutl;
    }

    public static List<File> getFiles(String fullpath, String ext) {
        List<File> tReFiles = new ArrayList<File>();
        try {
            File tFileDir = new File(fullpath);
            File[] tFiles = tFileDir.listFiles();
            if (ext == null || ext.equals("")) {
                for (File tFile : tFiles) {
                    //if(tFile.isFile())
                    tReFiles.add(tFile);
                }
            } else {
                for (File tFile : tFiles) {
                    if (tFile.isFile() && tFile.getName().endsWith(ext))
                        tReFiles.add(tFile);
                }
            }

        } catch (Exception e) {

        }
        return tReFiles;
    }

    public static void moveFile(String file, String target) {
        File tOldFile = new File(file);
        tOldFile.renameTo(new File(target));
    }




    /**
     * 　　*
     * 　　* @param myContext
     * 　　* @param ASSETS_NAME 要复制的文件名
     * 　　* @param savePath 要保存的路径
     * 　　* @param saveName 复制后的文件名
     * 　　* testCopy(Context context)是一个测试例子。
     */
    public static void copyAssetsFile(Context myContext, String assets_name,
                                      String savePath, String saveName) {
        String filename = savePath + "/" + saveName;
        File dir = new File(savePath);
// 如果目录不中存在，创建这个目录
        if (!dir.exists())
            dir.mkdirs();
        try {
            if (!(new File(filename)).exists()) {
                InputStream is = myContext.getResources().getAssets()
                        .open(assets_name);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[7168];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();

                File newFile = new File(filename);
                newFile.setWritable(true, false);
                newFile.setReadable(true, false);
                newFile.setExecutable(true, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void copyAssetsToSDCard(Context context, String sourceFolder, String destinationFolder) {
        AssetManager assetManager = context.getAssets();
        String[] files;
        try {
            // 获取assets文件夹下的所有文件和子文件夹
            files = assetManager.list(sourceFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 创建目标文件夹
        File destFolder = new File(destinationFolder);
        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                // 从assets中打开文件
                in = assetManager.open(sourceFolder + "/" + filename);
                // 指定输出目标文件
                File outFile = new File(destinationFolder, filename);
                out = new FileOutputStream(outFile);
                // 将文件内容复制到目标文件
                Log.i("Qbsdk","copy 开始");
                copyFile(in, out);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //文件拷贝
    public static boolean copySdcardFile(String fromFile, String toFile) {
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;

        } catch (Exception ex) {
            return false;
        }
    }




    public static List<String> getAllExternalSdcardPath() {
        List<String> PathList = new ArrayList<String>();

        String firstPath = Environment.getExternalStorageDirectory().getPath();

        try {
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                // 将常见的linux分区过滤掉
                Log.i("IOUtils", "line =" + line);
                if (line.contains("proc") || line.contains("tmpfs") || (line.contains("media") && !line.contains("media_rw")) || line.contains("asec") || line.contains("secure") || line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("misc") || line.contains("obb")) {
                    continue;
                }

                // 下面这些分区是我们需要的
                if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs") || (line.contains("media_rw")))) {
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                    String items[] = line.split(" ");

                    if (items != null && items.length > 1) {
                        String path = items[1].toLowerCase(Locale.getDefault());
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        if (path != null && !PathList.contains(path) && ((path.contains("sd") && !path.contains("internal_sd")) || (path.contains("usb_storage") && path.contains("udisk")))) {
                            PathList.add(items[1]);
                        }
                    }

                    if (items != null && items.length > 2) {
                        String path = items[2].toLowerCase(Locale.getDefault());
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        if (path != null && !PathList.contains(path) && path.contains("storage") && !path.contains("emulated")) {
                            PathList.add(items[2]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!PathList.contains(firstPath)) {
            PathList.add(firstPath);
        }

        if (PathList.contains(SDCardRoot.substring(0, SDCardRoot.length() - 1))) {
            PathList.remove(SDCardRoot.substring(0, SDCardRoot.length() - 1));
        }

        return PathList;
    }

    /**
     * 压缩文件和文件夹
     *
     * @param srcFileString 要压缩的文件或文件夹
     * @param zipFileString 压缩完成的Zip路径
     * @throws Exception
     */
    public static void ZipFolder(String srcFileString, String zipFileString) throws Exception {
        //创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
        //创建文件
        File file = new File(srcFileString);
        //压缩
        ZipFiles(file.getParent() + File.separator, file.getName(), outZip);
        //完成和关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //文件夹
            String fileList[] = file.list();
            //没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString + fileString + "/", fileList[i], zipOutputSteam);
            }
        }
    }

    /**
     * 判断眼镜和彩妆的区别
     *
     * @param code 0代表没有素材，1代表眼镜，2代表彩妆
     * @return
     */
    public static int isXml(int code) {
        int isxml = 0;//默认代表其他工具
        String xmlpath = IOUtils.SDCardRoot + "xyShj/fodder/xmls/" + code;
        File fqtimage = new File(xmlpath);
        if (fqtimage.exists()) {
            File[] files = fqtimage.listFiles();
            if (files != null && files.length > 0) {
                String filename = files[0].getName();
                if (filename.indexOf(".panxbundle") > 0) {//眼镜
                    isxml = 1;
                }
                if (filename.indexOf(".xml") > 0) {//彩妆
                    isxml = 2;
                }
            }
        }
        return isxml;
    }

    public static String getLinkPath(String path) {
        if ("/dev/ttyS1".equalsIgnoreCase(path)) {
            return "/dev/ttymxc1";
        } else if ("/dev/ttyS2".equalsIgnoreCase(path)) {
            return "/dev/ttymxc2";
        } else if ("/dev/ttyS3".equalsIgnoreCase(path)) {
            return "/dev/ttymxc3";
        } else if ("/dev/ttyS4".equalsIgnoreCase(path)) {
            return "/dev/ttymxc4";
        } else {
            return null;
        }
    }

    public static Object readObject(File f) {
        Object object = null;
        // 读取上一次更新的图片信息
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {

            fis = new FileInputStream(f);
            ois = new ObjectInputStream(
                    fis);
            object = ois.readObject();


        } catch (Exception e) {

        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (Exception e) {

                }
            ois = null;
            if (fis != null)
                try {
                    fis.close();
                } catch (Exception e) {

                }
            fis = null;
        }
        return object;
    }


    public static void writeObject(File f, Object object) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {

            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(
                    fos);
            oos.writeObject(object);
            oos.flush();

        } catch (Exception e) {

        } finally {
            if (oos != null)
                try {
                    oos.close();
                } catch (Exception e) {

                }
            oos = null;
            if (fos != null)
                try {
                    fos.close();
                } catch (Exception e) {

                }
            fos = null;
        }
    }

}