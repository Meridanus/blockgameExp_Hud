package notker.blockgame_exp_hud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.network.MessageType;
import notker.blockgame_exp_hud.AttributeTags;
import notker.blockgame_exp_hud.BlockgameExpHud;
import notker.blockgame_exp_hud.ExpHudData;
import notker.blockgame_exp_hud.MMOITEMS_ITEM_TYPES;


@Config(name = "blockgame_exp_hud")
public class BlockgameExpHudConfig implements ConfigData {

    @Comment("Hud Enabled?")
    public boolean ENABLED = true;

    @Comment("Attributes Overlay Enabled?")
    public boolean ATTRIBUTES_ENABLED = true;

    @Comment("Hud Settings")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    public HudSettings hudSettings = new HudSettings();

    public static class HudSettings {
        @Comment("Horizontal Position")
        public Float X_POS = 3f;

        @Comment("Vertical Position")
        public Float Y_POS = 30f;

        @Comment("Line Spacing")
        public Float SPACING = 10f;

        @Comment("Show Coin In Hud")
        public boolean COIN_ENABLED = true;

        @Comment("The EXP Text Color")
        @ConfigEntry.ColorPicker
        public Integer TEXT_COLOR = 0xAAAAAA;

        @Comment("The Coin Text Color")
        @ConfigEntry.ColorPicker
        public Integer COIN_COLOR = 0xFFAA00;

        @Comment("HUD Scale")
        public float HUD_SCALE = 0.9f;

        @Comment("HUD Opacity")
        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        public float HUD_OPACITY = 0.3f;

    }


    @Comment("Chat Settings")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    public ChatSettings chatSettings = new ChatSettings();


    public static class ChatSettings {
        @Comment("remove Exp from Chat?")
        public boolean CHAT_EXP_ENABLED = true;


        @Comment("remove Coins from chat?")
        public boolean CHAT_COIN_ENABLED = true;
    }

    @Comment("Attribute Highlighting Settings")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public AttributeSettings attributeSettings = new AttributeSettings();

    public static class AttributeSettings {
        @Comment("Item Type")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public MMOITEMS_ITEM_TYPES ITEM_TYPES = MMOITEMS_ITEM_TYPES.RUNES;
        @Comment("Matching Attribute 1")
        public AttributeTags Rune_TAG_0 = AttributeTags.NONE;
        @Comment("Min Attribute Value 1")
        public float Rune_Value_0 = 0.0f;
        @Comment("Matching Attribute 2")
        public AttributeTags Rune_TAG_1 = AttributeTags.NONE;
        @Comment("Min Attribute Value 2")
        public float Rune_Value_1 = 0.0f;
        @Comment("Matching Attribute 3")
        public AttributeTags Rune_TAG_2 = AttributeTags.NONE;
        @Comment("Min Attribute Value 3")
        public float Rune_Value_2 = 0.0f;
        @Comment("Matching Attribute 4")
        public AttributeTags Rune_TAG_3 = AttributeTags.NONE;
        @Comment("Min Attribute Value 4")
        public float Rune_Value_3 = 0.0f;
        @Comment("Matching Attribute 5")
        public AttributeTags Rune_TAG_4 = AttributeTags.NONE;
        @Comment("Min Attribute Value 5")
        public float Rune_Value_4 = 0.0f;

        @Comment("Attribute Settings Options")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public AttributeSettingsOptions attributeSettingsOptions = new AttributeSettingsOptions();

    }

    public static class AttributeSettingsOptions {
        @Comment("Highlight Text")
        public String Rune_String = "★";
        @Comment("Highlight Text Scale")
        public float HIGHLIGHT_SCALE = 1.0f;
        @Comment("Highlight Color")
        @ConfigEntry.ColorPicker
        public Integer STAR_COLOR = 0xFFEE00;
        @Comment("Horizontal Offset For The Attribute Highlight")
        public float X_OFFSET = -11.0f;
        @Comment("Vertical Offset For The Attribute Highlight")
        public float Y_OFFSET = -11.0f;
    }

    @Comment("Advanced Settings (Game Restart Required!)")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    public AdvancedSettings advancedSettings = new AdvancedSettings();


    public static class AdvancedSettings {
        @Comment("Message types: CHAT, SYSTEM, GAME_INFO")
        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public MessageType MESSAGE_TYPE = MessageType.SYSTEM;
        @Comment("Chat Message Contains Text")
        @ConfigEntry.Gui.RequiresRestart
        public String EXP_CHAT_TAG = "[EXP]";
        @Comment("Chat Message Ends With Text")
        @ConfigEntry.Gui.RequiresRestart
        public String COIN_CHAT_TAG = " Coin.";
        @Comment("Chat Message Ends With Text")
        @ConfigEntry.Gui.RequiresRestart
        public String COIN_QUEST_CHAT_TAG = " Coin!";

    }


}

