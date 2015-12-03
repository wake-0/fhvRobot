package app.robo.fhv.roboapp.utils;

/**
 * Created by Kevin on 29.11.2015.
 */
public class ProgressMapper {

    private static int minProgress = 0;
    private static int maxProgress = 200;

    public static int progressToDriveValue(int progress) {
        int usedProgress = progress;
        if (progress < minProgress) {
            usedProgress = minProgress;
        } else if (progress > maxProgress) {
            usedProgress = maxProgress;
        }

        // Map operation
        return usedProgress - 100;
    }

}
