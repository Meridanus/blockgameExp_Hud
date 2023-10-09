package notker.blockgame_exp_hud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.network.MessageType;
import notker.blockgame_exp_hud.AttributeTags;
import notker.blockgame_exp_hud.BlockgameExpHud;
import notker.blockgame_exp_hud.MMOITEMS_ITEM_TYPES;


@Config(name = "blockgame_exp_hud")
public class BlockgameExpHudConfig implements ConfigData {

    @Comment("Hud Enabled?")
    public boolean ENABLED = BlockgameExpHud.DEFAULT_ENABLED;

    @Comment("Attributes Overlay Enabled?")
    public boolean ATTRIBUTES_ENABLED = true;

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

    @Comment("Attribute Settings")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public AttributeSettings attributeSettings = new AttributeSettings();

    public static class AttributeSettings {


        @Comment("The Attribute Text")
        public String Rune_String = "★";
        @Comment("The Attribute Text Color")
        @ConfigEntry.ColorPicker
        public Integer STAR_COLOR = 0xFFEE00;
        @Comment("Item Type")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public MMOITEMS_ITEM_TYPES ITEM_TYPES = MMOITEMS_ITEM_TYPES.RUNES;
        @Comment("Matching Attribute 1")
        public AttributeTags Rune_TAG_0 = AttributeTags.NONE;
        @Comment("Matching Attribute 2")
        public AttributeTags Rune_TAG_1 = AttributeTags.NONE;
        @Comment("Matching Attribute 3")
        public AttributeTags Rune_TAG_2 = AttributeTags.NONE;
        @Comment("Matching Attribute 4")
        public AttributeTags Rune_TAG_3 = AttributeTags.NONE;
        @Comment("Matching Attribute 5")
        public AttributeTags Rune_TAG_4 = AttributeTags.NONE;
        @Comment("Horizontal Offset For The Attribute Text")
        public Integer X_OFFSET = -11;
        @Comment("Vertical Offset For The Attribute Text")
        public Integer Y_OFFSET = -11;
    }

    @Comment("Advanced Settings (Game Restart Required!)")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    public AdvancedSettings advancedSettings = new AdvancedSettings();


    public static class AdvancedSettings {
        @Comment("Message types: CHAT, SYSTEM, GAME_INFO")
        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public MessageType MESSAGE_TYPE = BlockgameExpHud.DEFAULT_MESSAGE_TYPE_VALUE;
        @Comment("Chat Message Contains Text")
        @ConfigEntry.Gui.RequiresRestart
        public String EXP_CHAT_TAG = BlockgameExpHud.DEFAULT_EXP_CHAT_TAG;
        @Comment("Chat Message Ends With Text")
        @ConfigEntry.Gui.RequiresRestart
        public String COIN_CHAT_TAG = BlockgameExpHud.DEFAULT_COIN_CHAT_TAG;
        @Comment("Chat Message Ends With Text")
        @ConfigEntry.Gui.RequiresRestart
        public String COIN_QUEST_CHAT_TAG = BlockgameExpHud.DEFAULT_COIN_QUEST_CHAT_TAG;

    }


}

