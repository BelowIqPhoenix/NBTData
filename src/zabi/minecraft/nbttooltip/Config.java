package zabi.minecraft.nbttooltip;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

/* loaded from: nbttooltip-0.4.jar:zabi/minecraft/nbttooltip/Config.class */
public class Config {
    public static Configuration configuration;
    public static int ticksBeforeScroll = 20;
    public static int maxLinesShown = 10;
    public static boolean requiresf3 = true;

    public static void init(File config) {
        configuration = new Configuration(config);
        configuration.load();
        loadValues();
        if (configuration.hasChanged()) {
            save();
        }
    }

    public static void loadValues() {
        ticksBeforeScroll = configuration.getInt("ticksBeforeScroll", "General", 20, 1, 3600, "How many ticks have to pass before the next line is shown");
        maxLinesShown = configuration.getInt("maxLinesShown", "General", 10, 1, 3600, "How many lines are shown at once. Anything greater than this will scroll");
        requiresf3 = configuration.getBoolean("requiresf3H", "General", true, "If set to false it will show the NBT tag regardless of the F3+H status");
    }

    public static void save() {
        configuration.save();
    }
}
