package notker.blockgame_exp_hud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import notker.blockgame_exp_hud.BlockgameExpHud;

@Config(name = "blockgame_exp_hud")
public class BlockgameExpHudConfig implements ConfigData {


    @Comment("Base Settings")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public BaseSettings baseSettings = new BaseSettings();


    public static class BaseSettings {
        @Comment("Hud Enabled?")
        public boolean ENABLED = BlockgameExpHud.DEFAULT_ENABLED_VALUE;

        @Comment("The color for the Text")
        @ConfigEntry.ColorPicker
        public Integer TEXT_COLOR = BlockgameExpHud.DEFAULT_TEXT_COLOR;

        @Comment("The color for the Coin Text")
        @ConfigEntry.ColorPicker
        public Integer COIN_COLOR = BlockgameExpHud.DEFAULT_COIN_COLOR;
    }

    @Comment("Advanced Settings")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    public AdvancedSettings advancedSettings = new AdvancedSettings();


    public static class AdvancedSettings {
        @Comment("Chat String")
        public String CHAT_TAG = BlockgameExpHud.DEFAULT_CHAT_TAG;

        @Comment("Chat Message ID")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 2)
        public Byte MESSAGE_TYPE = BlockgameExpHud.DEFAULT_MESSAGE_TYPE_VALUE;

        @Comment("Sampels to calculate Average")
        @ConfigEntry.BoundedDiscrete(min = 10, max = 100)
        public Integer SAMPLES = BlockgameExpHud.DEFAULT_MAX_SAMPLE_VALUE;

    }
}

