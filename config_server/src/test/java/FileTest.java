import java.io.File;
import java.io.FileFilter;

public class FileTest {


    private final static String[] APK_INSTALL_DIRS = new String[]{"F:\\mimax2", "F:\\mi4a"};

    public static void main(String[] args) {

        for (String dir : APK_INSTALL_DIRS) {
            File installDir = new File(dir);
            File[] files = installDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if(pathname.getName().equals("TWRP-3.2.1.img")){
                        return true;
                    }else if(pathname.getName().contains("flashmi4a")){
                        return true;
                    }

                    return false;
                }
            });
            if (null != files && files.length > 0) {
                File file = files[0];
                if (!file.getName().endsWith(".apk")) {
                    file = new File(files[0], "base.apk");
                }
                System.out.println(file.getAbsolutePath());
            }
        }
    }
}
