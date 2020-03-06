package cn.weli.svideo.baselib.helper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Root
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see RootHelper
 * @since [1.0.0]
 */
public class RootHelper {

    public static boolean isRootSystem() {
        try {
            if (isRootSystem1() || isRootSystem2()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isRootSystem1() {
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists() && f.canExecute()) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static boolean isRootSystem2() {
        List<String> pros = getPath();
        File f = null;
        try {
            for (int i = 0; i < pros.size(); i++) {
                f = new File(pros.get(i), "su");
                System.out.println("f.getAbsolutePath():" + f.getAbsolutePath());
                if (f != null && f.exists() && f.canExecute()) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static List<String> getPath() {
        return Arrays.asList(System.getenv("PATH").split(":"));
    }
}
