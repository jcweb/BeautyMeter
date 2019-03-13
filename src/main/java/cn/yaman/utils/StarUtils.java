package cn.yaman.utils;

/**
 * @author timpkins
 */
public class StarUtils {

    public static int getStarNum(int progress) {
        if (progress < 20) {
            return 1;
        } else if (progress >= 20 && progress < 50) {
            return 2;
        } else if (progress >= 50 && progress < 70) {
            return 3;
        } else if (progress >= 70 && progress < 90) {
            return 4;
        } else if (progress >= 90 && progress <= 100) {
            return 5;
        }

        return 0;
    }
}
