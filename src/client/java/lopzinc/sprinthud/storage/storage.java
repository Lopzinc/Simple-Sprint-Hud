package lopzinc.sprinthud.storage;

public class storage {
    private static boolean hudState = true;
    public static void cycleHudState() { hudState=!hudState; }
    public static boolean hudEnabled() { return hudState; }
}
