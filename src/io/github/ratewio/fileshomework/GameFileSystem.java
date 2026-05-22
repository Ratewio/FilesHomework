package io.github.ratewio.fileshomework;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GameFileSystem {
    private final Logger logger;

    private final File mainDir;
    // Directory Keys
    private final String SRC_KEY = "src";
    private final String SRC_MAIN_KEY = "src\\main";
    private final String SRC_TEST_KEY = "src\\test";
    private final String RES_KEY = "res";
    private final String SAVEGAMES_KEY = "savegames";
    private final String TEMP_KEY = "temp";
    private final String TEMP_FILE_KEY = "temp\\temp.txt";

    public GameFileSystem() {
        this(new File(System.getProperty("user.home"), "Games\\SomeGame"));
    }

    public GameFileSystem(File mainDir) {

        File parent = mainDir.getParentFile();

        logger = new Logger("GameFileSystem");

        this.mainDir = mainDir;

        logger.log("main dir assigned as " + mainDir.getPath());
    }

    //Create directory tree and log
    public void init() {
        handleMkdir(getMainDir());
        handleMkdir(getSrc());
        handleMkdir(getMain());
        handleMkdir(getTest());
        handleMkdir(getRes());
        handleMkdir(getSavegames());
        handleMkdir(getTemp());
        logger.saveToFile(getTempFile());
    }

    private void handleMkdir(File file) {
        logger.log(
                (file.exists()
                        ? "dir exists: "
                        : !file.mkdirs()
                        ? "cant create dir: "
                        : "dir created: ")
                        + file.getAbsolutePath()
        );
    }

    public File getMainDir() {
        return mainDir;
    }

    public File getSrc() {
        return new File(getMainDir(), SRC_KEY);
    }

    public File getMain() {
        return new File(getMainDir(), SRC_MAIN_KEY);
    }

    public File getTest() {
        return new File(getMainDir(), SRC_TEST_KEY);
    }

    public File getRes() {
        return new File(getMainDir(), RES_KEY);
    }

    public File getSavegames() {
        return new File(getMainDir(), SAVEGAMES_KEY);
    }

    public File getTemp() {
        return new File(getMainDir(), TEMP_KEY);
    }

    public File getTempFile() {
        return new File(getMainDir(), TEMP_FILE_KEY);
    }

    public void saveGame(File file, GameProgress gameProgress) {
        logger.log("saving progress to " + file.getPath() + "...");
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(gameProgress);
            logger.log("progress saved to " + file.getPath());
        } catch (IOException e) {
            logger.log("cant save progress due exception: " + e);
        }
    }

    public void zipFiles(File zipFile, File... files){
        logger.log("zip files to " + zipFile.getPath() + "...");
        try(ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))){
            for(File file : files){
                logger.log("zipping " + file.getPath() + "...");
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);

                int bufferSize = 8192;
                try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), bufferSize)){
                    byte[] buffer = new byte[bufferSize];
                    for(int len = 0; (len = bis.read(buffer)) != -1;){
                        zos.write(buffer, 0, len);
                    }
                }

                zos.closeEntry();
            }
            logger.log("zipped to " + zipFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(File file : files){
            logger.log("removed old file: " + file.getPath());
            file.delete();
        }
    }
}
