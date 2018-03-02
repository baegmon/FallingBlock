package baegmon.fallingblock.tools;

public class NumberUtils {

    public static boolean isInteger(String s) {
        try
        {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }

    public static boolean isDouble(String s) {
        try
        {
            Double.parseDouble(s);
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }

}
