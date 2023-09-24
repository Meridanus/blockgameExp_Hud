package notker.blockgame_exp_hud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.network.MessageType;
import notker.blockgame_exp_hud.BlockgameExpHud;

@Config(name = "blockgame_exp_hud")
public class BlockgameExpHudConfig implements ConfigData {

    @Comment("Hud Enabled?")
    public boolean ENABLED = BlockgameExpHud.DEFAULT_ENABLED;

    @Comment("Hud Settings")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public HudSettings hudSettings = new HudSettings();

    public static class HudSettings {
        @Comment("Horizontal Position")
        public Float X_POS = BlockgameExpHud.DEFAULT_X_POS;

        @Comment("Vertical Position")
        public Float Y_POS = BlockgameExpHud.DEFAULT_Y_POS;

        @Comment("Line Spacing")
        public Float SPACING = BlockgameExpHud.DEFAULT_SPACING;

        @Comment("Show Hud Background")
        public boolean BACKGROUND_ENABLED = BlockgameExpHud.DEFAULT_BACKGROUND_ENABLED;

        @Comment("Show Coin In Hud")
        public boolean COIN_ENABLED = BlockgameExpHud.DEFAULT_COIN_ENABLED;

        @Comment("The EXP Text Color")
        @ConfigEntry.ColorPicker
        public Integer TEXT_COLOR = BlockgameExpHud.DEFAULT_TEXT_COLOR;

        @Comment("The Coin Text Color")
        @ConfigEntry.ColorPicker
        public Integer COIN_COLOR = BlockgameExpHud.DEFAULT_COIN_COLOR;

    }


    @Comment("Chat Settings")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public ChatSettings chatSettings = new ChatSettings();


    public static class ChatSettings {
        @Comment("remove Exp from Chat?")
        public boolean CHAT_EXP_ENABLED = BlockgameExpHud.DEFAULT_CHAT_EXP_ENABLED;


        @Comment("remove Coins from chat?")
        public boolean CHAT_COIN_ENABLED = BlockgameExpHud.DEFAULT_CHAT_COIN_ENABLED;
    }

    @Comment("Advanced Settings (Game Restart Required!)")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    public AdvancedSettings advancedSettings = new AdvancedSettings();


    public static class AdvancedSettings {
        @Comment("Message types: CHAT, SYSTEM, GAME_INFO")
        public MessageType MESSAGE_TYPE = BlockgameExpHud.DEFAULT_MESSAGE_TYPE_VALUE;
        @Comment("Chat Message Contains Text")
        public String EXP_CHAT_TAG = BlockgameExpHud.DEFAULT_EXP_CHAT_TAG;
        @Comment("Chat Message Ends With Text")
        public String COIN_CHAT_TAG = BlockgameExpHud.DEFAULT_COIN_CHAT_TAG;
        @Comment("Chat Message Ends With Text")
        public String COIN_QUEST_CHAT_TAG = BlockgameExpHud.DEFAULT_COIN_QUEST_CHAT_TAG;
    }
}

