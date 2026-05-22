package io.github.ratewio.fileshomework;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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

    public GameFileSystem(File mainDir) {
        logger = new Logger("GameFileSystem");

        this.mainDir = mainDir;

        logger.log("main dir assigned as " + mainDir.getPath());
    }

    // Создаёт директорию игры и всё дерево директорий, сохраняет лог.
    public void init() {
        handleMkdir(getMainDir());
        handleMkdir(getSrc());
        handleMkdir(getMain());
        handleMkdir(getTest());
        handleMkdir(getRes());
        handleMkdir(getSavegames());
        handleMkdir(getTemp());
        writeLog();
    }


    //вспомогательный метод логирующий создание директорий.
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
        } catch (IOException e) {
            logger.log("cant save progress due exception: " + e);
        }
    }

    public GameProgress openProgress(File file) {
        logger.log("loading progress from " + file.getPath() + "...");
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            GameProgress gameProgress = (GameProgress) ois.readObject();
            return gameProgress;
        } catch (IOException | ClassNotFoundException e) {
            logger.log("cant load progress due exception: " + e);
        }
        return null;
    }

    public void zipFiles(File zipFile, File... files) {
        logger.log("zip files to " + zipFile.getPath() + "...");
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {
            for (File file : files) {
                logger.log("zipping " + file.getPath() + "...");
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);

                int bufferSize = 8192; //default from BufferedInputStream.DEFAULT_BUFFER_SIZE
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), bufferSize)) {
                    byte[] buffer = new byte[bufferSize];
                    for (int len; (len = bis.read(buffer)) != -1; ) {
                        zos.write(buffer, 0, len);
                    }
                }

                zos.closeEntry();
            }
            logger.log("zipped to " + zipFile.getPath());

            for (File file : files) {
                if (file.delete()) {
                    logger.log("removed old file: " + file.getPath());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void unzipFiles(File zipFile, File dir) {
        logger.log("unzip files to " + dir + "...");
        int bufferSize = 8192; //default from BufferedInputStream.DEFAULT_BUFFER_SIZE
        try (ZipInputStream zos = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), 8192))) {
            byte[] buffer = new byte[bufferSize];

            ZipEntry entry;
            while ((entry = zos.getNextEntry()) != null) {
                File file = new File(dir, entry.getName());
                logger.log("unzipping " + file.getPath() + "...");
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                    for (int len; (len = zos.read(buffer)) != -1; ) {
                        bos.write(buffer, 0, len);
                    }
                }
                zos.closeEntry();
            }
            logger.log("unzipped " + zipFile.getPath());

            zos.close();

            if (zipFile.delete()) {
                logger.log("removed zip: " + zipFile.getPath());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLog() {
        logger.saveToFile(getTempFile());
    }
}
