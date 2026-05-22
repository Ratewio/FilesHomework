package io.github.ratewio.fileshomework;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        GameFileSystem gfs = new GameFileSystem(new File(System.getProperty("user.home"), "Games\\SomeGame"));
        //инициализируем директорию игры, дерево папок и пишем логи в temp\temp.txt
        gfs.init();

        GameProgress Antony = new GameProgress(100, 2, 12, 1004.56);
        GameProgress Josh = new GameProgress(79, 1, 24, 950.23);
        GameProgress Jeff = new GameProgress(50, 4, 16, 869.12);

        //Создаём файлы сохранений, чтобы потом не создавать снова при загрузке с диска. Сохраняем игровой прогресс
        File save1 = new File(gfs.getSavegames(), "save1.dat");
        File save2 = new File(gfs.getSavegames(), "save2.dat");
        File save3 = new File(gfs.getSavegames(), "save3.dat");
        gfs.saveGame(save1, Antony);
        gfs.saveGame(save2, Josh);
        gfs.saveGame(save3, Jeff);

        gfs.zipFiles(new File(gfs.getSavegames(), "zip.zip"), save1, save2, save3);


        // Задание с *
        gfs.unzipFiles(new File(gfs.getSavegames(), "zip.zip"), gfs.getSavegames());
        //Выводим в консоль какие значения игрового прогресса были загружены с диска
        System.out.println("(*) прогресс Antony: " + gfs.openProgress(save1).toString());
        System.out.println("(*) прогресс Josh: " + gfs.openProgress(save2).toString());
        System.out.println("(*) прогресс Jeff: " + gfs.openProgress(save3).toString());

        // т.к последняя автоматическая запись была только на методе init() можно записать логи в temp.txt вызвав метод вручнуе если необходимо
        // gfs.writeLog();
    }
}
