package cn.weli.svideo.advert.download;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadZipManager {

    /**
     * 解压文件到指定文件夹
     * filePath 需要解压的zip文件，
     * saveDir 解压保存的文件夹路径
     * return 如果是主题zip，得到该主题的根目录，否则返回"","-1"表示解压失败
     */
    public static String extnativeZipFileList(String filePath, String saveDir) throws Exception {
        /**如果是主题zip，得到该主题的根目录*/
//		String themePath="";

        File file = new File(filePath);
        if (!file.exists()) {
            return "-1";
        }
        FileInputStream in = new FileInputStream(file);
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry entry;
        // 解压
        File f = new File(saveDir);
        f.mkdirs();
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                File directory = new File(saveDir, entry.getName());
                if (!directory.exists())
                    if (!directory.mkdirs())
                        System.exit(0);
                zin.closeEntry();
//				if(directory.getParent().equals(f.getAbsolutePath())&&entry.getName().startsWith("skin")){
//					themePath=directory.getAbsolutePath();
//				}
            }
            if (!entry.isDirectory()) {
                File myFile = new File(entry.getName());
                FileOutputStream fout = new FileOutputStream(saveDir + myFile.getPath());
                BufferedOutputStream dout = new BufferedOutputStream(fout);
                byte[] b = new byte[1024];
                int len = 0;
                while ((len = zin.read(b)) != -1) {
                    dout.write(b, 0, len);
                }
                b = null;
                dout.close();
                fout.close();
                zin.closeEntry();
            }
        }
        if (in != null) {
            in.close();
        }
        if (zin != null) {
            zin.close();
        }
        return saveDir;//themePath;
    }
}
