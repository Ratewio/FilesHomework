package io.github.ratewio.fileshomework;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        GameFileSystem gfs = new GameFileSystem();
        gfs.init();

        GameProgress Antony = new GameProgress(100, 2, 12, 1004.56);
        GameProgress Josh = new GameProgress(79, 1, 24, 950.23);
        GameProgress Jeff = new GameProgress(50, 4, 16, 869.12);

        File save1 = new File(gfs.getSavegames(), "save1.dat");
        File save2 = new File(gfs.getSavegames(), "save2.dat");
        File save3 = new File(gfs.getSavegames(), "save3.dat");
        gfs.saveGame(save1, Antony);
        gfs.saveGame(save2, Josh);
        gfs.saveGame(save3, Jeff);

        gfs.zipFiles(new File(gfs.getSavegames(), "zip.zip"), save1, save2, save3);
    }
}
