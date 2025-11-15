package lopzinc.sprinthud;

public class Config {
    public int x;
    public int y;
    public boolean hudEnabled;
    public boolean modeDisplay;
    public String format;
    public Config() {
        this.x=10;
        this.y=10;
        this.hudEnabled=true;
        this.modeDisplay=true;
        this.format="§7Sprint: %status%";
    }
    public Config(int x, int y, boolean hudEnabled, boolean modeDisplay, String format) {
        this.x = x;
        this.y = y;
        this.hudEnabled = hudEnabled;
        this.modeDisplay = modeDisplay;
        this.format = format;
    }
}
