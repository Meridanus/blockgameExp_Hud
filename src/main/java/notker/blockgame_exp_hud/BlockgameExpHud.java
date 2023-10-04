package notker.blockgame_exp_hud;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.MessageType;
import notker.blockgame_exp_hud.config.BlockgameExpHudConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;


public class BlockgameExpHud extends DrawableHelper implements ClientModInitializer {

    private static BlockgameExpHud instance;
    public static boolean hideOverlay = false;
    public static boolean showGlobal = true;

    public static Logger LOGGER = LogManager.getLogger("blockgame_exp_hud");

    public static BlockgameExpHudConfig config;
    public static ConfigHolder<BlockgameExpHudConfig> configHolder;

    public static final String DEFAULT_EXP_CHAT_TAG = "[EXP]";
    public static final String DEFAULT_COIN_CHAT_TAG = " Coin.";
    public static final String DEFAULT_COIN_QUEST_CHAT_TAG = " Coin!";
    public static final MessageType DEFAULT_MESSAGE_TYPE_VALUE = MessageType.SYSTEM;
    public static final Boolean DEFAULT_ENABLED = true;
    public static final Boolean DEFAULT_CHAT_EXP_ENABLED = true;
    public static final Integer DEFAULT_TEXT_COLOR = 0xffffff;
    public static final Boolean DEFAULT_CHAT_COIN_ENABLED = true;
    public static final Boolean DEFAULT_COIN_ENABLED = true;
    public static final Integer DEFAULT_COIN_COLOR = 0xFFAA00;
    public static final Integer DEFAULT_MAX_SAMPLE_VALUE = 128;
    public static final Float DEFAULT_X_POS = 3f;
    public static final Float DEFAULT_Y_POS = 30f;
    public static final Float DEFAULT_SPACING = 10f;
    public static final Boolean DEFAULT_BACKGROUND_ENABLED = true;
    public static final Integer DEFAULT_BASE_BONUS_EXP = 10;
    public static final Integer DEFAULT_BASE_CLASS_EXP = 1;

    public static final String DEFAULT_RUNE_ITEM_TYPE_TAG = "MMOITEMS_ITEM_TYPE";
    public static final String DEFAULT_RUNE_ITEM_TYPE_VALUE = "RUNECARVING";




    public static final String[] professionNames = {"Archaeology", "Fishing", "Herbalism", "Logging", "Mining", "Runecarving", "Einherjar"};
    public static  final String[] professionSetNames = {"ARCHAEOLOGIST_", "FISHERMAN_", "BOTANIST_", "LUMBERJACK_", "MINER_"};
    public float[] professionTotalSessionExp = new float[professionNames.length];
    public float[] professionAverageSessionExp = new float[professionNames.length];
    public float[] professionSessionAverageTotalExp = new float[professionNames.length];
    public int[] professionSessionExpCount = new int[professionNames.length];
    public float[][] professionsLastExpValues = new float[professionNames.length][DEFAULT_MAX_SAMPLE_VALUE];

    public static final String[] nbtKeyNames = {
            "MMOITEMS_ADDITIONAL_EXPERIENCE_ARCHAEOLOGY",
            "MMOITEMS_ADDITIONAL_EXPERIENCE_FISHING",
            "MMOITEMS_ADDITIONAL_EXPERIENCE_HERBALISM",
            "MMOITEMS_ADDITIONAL_EXPERIENCE_LOGGING",
            "MMOITEMS_ADDITIONAL_EXPERIENCE_MINING",
            "MMOITEMS_ADDITIONAL_EXPERIENCE_RUNECARVING",
            "MMOITEMS_ADDITIONAL_EXPERIENCE"};
    float[] equipmentBonusExp = new float[professionNames.length];
    float[] mainHandBonusExp = new float[professionNames.length];

    public int coins = 0;


    private KeyBinding blockgameExpHudToggleKey;
    private KeyBinding blockgameExpHudSwitchKey;


    @Override
    public void onInitializeClient() {

        if (instance == null) instance = this;

        configHolder = AutoConfig.register(BlockgameExpHudConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(BlockgameExpHudConfig.class).getConfig();

        LOGGER.info("Config Loaded");

        blockgameExpHudToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.blockgame_exp_hud.toggle_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "Blockgame Exp Hud"));
        blockgameExpHudSwitchKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.blockgame_exp_hud.switch", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_HOME, "Blockgame Exp Hud"));


        //Register Tick Callback
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        //Register HudRender Callback
        HudRenderCallback.EVENT.register(this::onHudRender);

    }

    //private int lastSelectedSlot = 0;
    int ticksToWait = 15;
    public void tick(MinecraftClient client) {
        //toggle render
        if (blockgameExpHudToggleKey.wasPressed()) {
            hideOverlay = !hideOverlay;
        }
        //toggle modi
        if (blockgameExpHudSwitchKey.wasPressed()) {
            showGlobal = !showGlobal;
        }
        /*
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null && lastSelectedSlot != player.getInventory().selectedSlot) {
            lastSelectedSlot = player.getInventory().selectedSlot;
            playerEquipmentBonusExp();
        }
         */

        if (ticksToWait <= 0) {
            // Reset Time
            ticksToWait = 15;
            //Update Bonus Exp information
            playerEquipmentBonusExp();
        } else {
            ticksToWait--;
        }


    }


    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        boolean enabled = config != null ? config.ENABLED : DEFAULT_ENABLED;
        if (!enabled || hideOverlay) return;

        float startVertical, startHorizontal, offset;
        int textColor, coinColor;
        boolean coinEnabled, backgroundEnabled;

        // Multicolor text
        //OrderedText test = (new LiteralText("Test").formatted(Formatting.RED)).asOrderedText();
        //OrderedText test2 = (new LiteralText("Test").asOrderedText());
        //OrderedText oText = OrderedText.innerConcat(test, test2);



        // get Config Values
        if (config != null) {
            startVertical = config.hudSettings.Y_POS;
            startHorizontal = config.hudSettings.X_POS;
            offset = config.hudSettings.SPACING;
            textColor = config.hudSettings.TEXT_COLOR;
            coinColor = config.hudSettings.COIN_COLOR;
            coinEnabled = config.hudSettings.COIN_ENABLED;
            backgroundEnabled = config.hudSettings.BACKGROUND_ENABLED;
        } else {
            startVertical = DEFAULT_Y_POS;
            startHorizontal = DEFAULT_X_POS;
            offset = DEFAULT_SPACING;
            textColor = DEFAULT_TEXT_COLOR;
            coinColor = DEFAULT_COIN_COLOR;
            coinEnabled = DEFAULT_COIN_ENABLED;
            backgroundEnabled = DEFAULT_BACKGROUND_ENABLED;
        }

        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        String[] textList = new String[professionNames.length + 2];
        int textBoxHeight = 8;
        int borderWidth = 3;

        //renderer.drawWithShadow(matrixStack, oText, startHorizontal + 50, startVertical + 100, coinColor);

        // Create all Text Strings
        textList[0] = showGlobal ? "Session EXP Stats:" : "Last " + DEFAULT_MAX_SAMPLE_VALUE + " EXP Stats:";
        int textBoxWidth = renderer.getWidth(textList[0]);
        if (coins > 0 && coinEnabled) {
            textList[1] = "Coin's: " + coins;
        }
        for (int i = 0; i < professionNames.length; i++) {
            if (professionTotalSessionExp[i] > 0f) {
                String total = showGlobal ? formatNumber(professionTotalSessionExp[i]) : formatNumber(professionSessionAverageTotalExp[i]);
                String average = showGlobal ? formatNumber(professionTotalSessionExp[i] / professionSessionExpCount[i]) + "⌀" : formatNumber(professionAverageSessionExp[i]) + "⌀"; //μ
                textList[i + 2] = getBonusEXPDescription(i) + total +" | "+ average;
                textBoxWidth = Math.max(textBoxWidth, renderer.getWidth(textList[i + 2]));
            }
        }

        if (backgroundEnabled) {
            // Calculate Background Height
            for (int i = 1; i < textList.length; i++){
                if (textList[i] != null){
                    textBoxHeight += offset;
                }
            }
            // Draw Background
            int backgroundColor = MinecraftClient.getInstance().options.getTextBackgroundColor(0.3f);
            fill(matrixStack,
                    (int) startHorizontal - borderWidth,
                    (int)startVertical - borderWidth,
                    (int) startHorizontal + textBoxWidth + borderWidth,
                    (int) startVertical + textBoxHeight + borderWidth,
                    backgroundColor);
        }



        //Draw Title
        renderer.drawWithShadow(matrixStack, textList[0], startHorizontal, startVertical, textColor);
        int row = 1;

        // Draw Coin Text
        if (coins > 0 && coinEnabled) {
            renderer.drawWithShadow(matrixStack, textList[1], startHorizontal, startVertical + ((row) * offset), coinColor);
            row++;
        }
        // Draw Profession Text
        for (int i = 0; i < professionNames.length; i++) {
            if (professionTotalSessionExp[i] > 0f) {
                renderer.drawWithShadow(matrixStack, textList[i + 2], startHorizontal, startVertical + ((row) * offset), textColor);
                row++;
            }

        }

    }



    public static BlockgameExpHud getInstance() {
        return instance;
    }

    public void coinValueFromString(String message){
        StringBuilder value = new StringBuilder();

        for (int i = message.lastIndexOf("d") + 2; i < message.lastIndexOf(" "); i++) {
            value.append(message.charAt(i));
        }

        if (value.length() == 0) return;
        //LOGGER.fatal("Coin: " + value);
        coins += Integer.parseInt(value.toString());
    }

    public float expValueFromString(String message){
        StringBuilder value = new StringBuilder();

        for (int i = message.indexOf("+") + 1; i < message.lastIndexOf(" "); i++) {
            value.append(message.charAt(i));
        }

        if (value.isEmpty()) return 0f;
        //LOGGER.info("EXP: " + value);
        return Float.parseFloat(value.toString());
    }

    public String formatNumber(Float input){
        DecimalFormat df = (DecimalFormat)NumberFormat.getNumberInstance(Locale.GERMANY);
        df.applyPattern("###,###,###.0");
        return df.format(input);
    }


    public void addExp(String message){

        for (int p = 0; p < professionNames.length; p++) {
            if (message.contains(professionNames[p])){
                // add base class exp for professions exept gained class exp
                int classProfession = professionNames.length - 1;
                if (p != classProfession) {
                    addExpToProfessionArrays(1f, classProfession);
                }
                float currentExp = expValueFromString(message);
                addExpToProfessionArrays(currentExp, p);
            }
        }
    }

    private void addExpToProfessionArrays(float currentExp, int p) {
        professionTotalSessionExp[p] += currentExp;

        int index = professionSessionExpCount[p];
        // Make sure index is inside Array Boundary
        if (index >= DEFAULT_MAX_SAMPLE_VALUE) {
            index = index % DEFAULT_MAX_SAMPLE_VALUE;
        }
        // Increment the count
        professionSessionExpCount[p]++;

        // subtract the old Value that's about to override
        professionSessionAverageTotalExp[p] -= professionsLastExpValues[p][index];
        // Add/override the current exp to the array [Profession type][Value]
        professionsLastExpValues[p][index] = currentExp;
        // Add current exp to the Sum for the Average
        professionSessionAverageTotalExp[p] += currentExp;

        // Calculate the Average
        professionAverageSessionExp[p] = professionSessionAverageTotalExp[p] / Math.min(DEFAULT_MAX_SAMPLE_VALUE, professionSessionExpCount[p]);
    }

    public void playerEquipmentBonusExp() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        // {"Archaeology", "Einherjar", "Fishing", "Herbalism", "Logging", "Mining", "Runecarving"};
        if (player != null) {
            //NbtCompound mainHand = player.getMainHandStack().getOrCreateNbt();
            NbtCompound offHand = player.getOffHandStack().getOrCreateNbt();
            ItemStack mainHand = player.getMainHandStack();
            float[] newEquipmentBonusExp = new float[professionNames.length];

            // offhand MMOITEMS_HANDWORN:b1

            // Check if mainhand item is in Mainhand slot
            if (!mainHand.getOrCreateNbt().contains("MMOITEMS_HANDWORN") && !(mainHand.getItem() instanceof ArmorItem)){
                for (int i = 0; i < nbtKeyNames.length; i++) {
                    mainHandBonusExp[i] = mainHand.getOrCreateNbt().getFloat(nbtKeyNames[i]);
                }
            } else {
                Arrays.fill(mainHandBonusExp, 0f);
            }

            // Check if offhand item is in Offhand slot
            if (offHand.contains("MMOITEMS_HANDWORN")){
                for (int i = 0; i < nbtKeyNames.length; i++) {
                    newEquipmentBonusExp[i] += offHand.getFloat(nbtKeyNames[i]);
                }
            }

            //Check Armor slots
            Iterable<ItemStack> equippedItems = player.getArmorItems();
            String[] setArmor = new String[4];
            int[] correspondingProfessionIndex = new int[4];
            int index = 0;
            for (ItemStack items : equippedItems) {
                //LOGGER.fatal(items.getOrCreateNbt());
                String setType = items.getOrCreateNbt().getString("MMOITEMS_ITEM_SET");
                for (int i = 0; i < professionSetNames.length; i++) {
                    // Make sure it's a Profession Type set
                    if (setType.contains(professionSetNames[i])){
                        //put Set Name in to array
                        setArmor[index] = setType;
                        correspondingProfessionIndex[index] = i;
                        // bump to next slot
                        index++;
                    }
                }
                // Add +EXP% values
                for (int i = 0; i < nbtKeyNames.length; i++) {
                    newEquipmentBonusExp[i] += items.getOrCreateNbt().getFloat(nbtKeyNames[i]);
                }
            }


            // Check for set bonus
            // Set Bonus ExP%
            String boots = setArmor[0] != null ? setArmor[0] : "0";
            String leggings = setArmor[1] != null ? setArmor[1] : "1";
            String chest = setArmor[2] != null ? setArmor[2] : "2";
            String helmet = setArmor [3] != null ? setArmor[3] : "3";

            //LOGGER.info(setArmor[0]);
            // T1 |  T2 |  T3 |  T4 |  T5
            // 10% | 20% | 30% | 40% | 50%
            if (helmet.equals(chest)
            && helmet.equals(leggings)
            && helmet.equals(boots)) {
                // 4 equal piece Matching Set Bonus
                //LOGGER.info("-> 4 Set");
                //LOGGER.info(professionNames[correspondingProfessionIndex[3]]);
                //LOGGER.fatal(Integer.parseInt(String.valueOf(setArmor[3].charAt(setArmor[3].length() - 1))) * 5 * 2 + "% Bonus");
                newEquipmentBonusExp[correspondingProfessionIndex[3]] += getSetBonusExpValue(setArmor[3], 1f);

            }

            /* else if (helmet.equals(chest) && helmet.equals(leggings)
            || helmet.equals(chest) && helmet.equals(boots)
            || helmet.equals(leggings) && helmet.equals(boots)) {
                // 3 piece Matching Sets
                newEquipmentBonusExp[correspondingProfessionIndex[3]] += getSetBonusExpValue(setArmor[3], 2f);

            } else if (chest.equals(leggings) && chest.equals(boots)) {
                // 3 piece Matching Sets
                newEquipmentBonusExp[correspondingProfessionIndex[2]] += getSetBonusExpValue(setArmor[2], 2f);

            } else {
                // only 2 pieces matching sets left
                if (helmet.equals(chest) && leggings.equals(boots) || helmet.equals(leggings) && chest.equals(boots)){
                    // 2 piece + 2 piece Matching Sets
                    newEquipmentBonusExp[correspondingProfessionIndex[3]] += getSetBonusExpValue(setArmor[3], 1f);
                    newEquipmentBonusExp[correspondingProfessionIndex[0]] += getSetBonusExpValue(setArmor[0], 1f);

                } else if (helmet.equals(boots) && chest.equals(leggings)) {
                    // 2 piece + 2 piece Matching Sets
                    newEquipmentBonusExp[correspondingProfessionIndex[3]] += getSetBonusExpValue(setArmor[3], 1f);
                    newEquipmentBonusExp[correspondingProfessionIndex[2]] += getSetBonusExpValue(setArmor[2], 1f);

                } else if (helmet.equals(chest) || helmet.equals(boots) || helmet.equals(leggings)) {
                    // 2 piece Matching Sets
                    newEquipmentBonusExp[correspondingProfessionIndex[3]] += getSetBonusExpValue(setArmor[3], 1f);

                } else if (chest.equals(leggings)) {
                    // 2 piece Matching Sets
                    newEquipmentBonusExp[correspondingProfessionIndex[2]] += getSetBonusExpValue(setArmor[2], 1f);

                } else if (boots.equals(leggings) || boots.equals(chest)) {
                    // 2 piece Matching Sets
                    newEquipmentBonusExp[correspondingProfessionIndex[0]] += getSetBonusExpValue(setArmor[0], 1f);
                }
            }*/
            // Set the new Bonus
            equipmentBonusExp = newEquipmentBonusExp;
        }
    }


    private float getSetBonusExpValue(String setType, float multiply) {
        if (setType == null) return 0f;
        // get the Set Tier lvl to multiply with
        int setLvl = Integer.parseInt(String.valueOf(setType.charAt(setType.length() - 1)));
        //LOGGER.info(setLvl * DEFAULT_BASE_BONUS_EXP * multiply);
        return setLvl * DEFAULT_BASE_BONUS_EXP * multiply;
    }

    private String getBonusEXPDescription(int index) {
        float bonusValue = mainHandBonusExp[index] + equipmentBonusExp[index];

        if (bonusValue > 0) {
            return professionNames[index] + ": +" + String.format("%.2f", bonusValue) + "% | ";
        } else {
            return professionNames[index] + ": 0% | ";
        }

    }




}

/*
[21:04:30] [Render thread/FATAL] (blockgame_exp_hud) {AttributeModifiers:[{Amount:0.0d,AttributeName:"minecraft:generic.attack_speed",Name:"mmoitemsDecoy",Operation:0,UUID:[I;-2021319128,-1357757450,-1987131861,-563359508]}],Damage:0,HSTRY_ENCHANTS:'{"Stat":"ENCHANTS","OGStory":[{"MMOITEMS_ENCHANTS_ñstr":"[]"}]}',HideFlags:67,MMOITEMS_ADDITIONAL_EXPERIENCE_LOGGING:40.29762747305084d,MMOITEMS_DEFENSE:4.143900061136107d,MMOITEMS_DISABLE_CRAFTING:1b,MMOITEMS_DISABLE_ENCHANTING:1b,MMOITEMS_DISABLE_SMELTING:1b,MMOITEMS_DISABLE_SMITHING:1b,MMOITEMS_DYNAMIC_LORE:'["&7« Armor »","","&3 &7⛨ Defense: &f&2+4.1","","&3 &7🪓 Logging EXP: &f&2+40.3%","","&3 &7ᚱ Empty &2Runecarving&7 Socket","","&7He sleeps all night","&7and he works all day!","","&7[&6Termite&7] &fSet Bonus:","&8[2] +10% Logging EXP","&8[3] +10% Logging EXP","&8[4] +4 Logging Skill","","&7Tier: §aUNCOMMON","&7Durability: 800 / 800"]',MMOITEMS_ENCHANTS:"[]",MMOITEMS_GEM_STONES:'{"EmptySlots":["Runecarving"],"Gemstones":[]}',MMOITEMS_ITEM_ID:"LUMBERJACK_BOOTS_2",MMOITEMS_ITEM_SET:"LUMBERJACK_2",MMOITEMS_ITEM_TYPE:"ARMOR",MMOITEMS_LORE:'["&7He sleeps all night","&7and he works all day!"]',MMOITEMS_MAX_DURABILITY:800,MMOITEMS_NAME:"<tier-color>Initiate Lumberjacks Boots",MMOITEMS_REPAIR_TYPE:"ALL",MMOITEMS_REVISION_ID:2,MMOITEMS_TIER:"UNCOMMON_GEAR",display:{Lore:['{"italic":false,"color":"gray","text":"« Armor »"}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"dark_aqua","text":" "},{"color":"gray","text":"⛨ Defense: "},{"color":"dark_green","text":"+4.1"}],"text":""}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"dark_aqua","text":" "},{"color":"gray","text":"🪓 Logging EXP: "},{"color":"dark_green","text":"+40.3%"}],"text":""}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"dark_aqua","text":" "},{"color":"gray","text":"ᚱ Empty "},{"color":"dark_green","text":"Runecarving"},{"color":"gray","text":" Socket"}],"text":""}','{"italic":false,"text":""}','{"italic":false,"color":"gray","text":"He sleeps all night"}','{"italic":false,"color":"gray","text":"and he works all day!"}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"gray","text":"["},{"color":"gold","text":"Termite"},{"color":"gray","text":"] "},{"color":"white","text":"Set Bonus:"}],"text":""}','{"italic":false,"color":"dark_gray","text":"[2] +10% Logging EXP"}','{"italic":false,"color":"dark_gray","text":"[3] +10% Logging EXP"}','{"italic":false,"color":"dark_gray","text":"[4] +4 Logging Skill"}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"gray","text":"Tier: "},{"color":"green","text":"UNCOMMON"}],"text":""}','{"italic":false,"color":"gray","text":"Durability: 800 / 800"}'],Name:'{"italic":false,"color":"green","text":"Initiate Lumberjacks Boots"}',color:14448720}}
[21:04:30] [Render thread/FATAL] (blockgame_exp_hud) {AttributeModifiers:[{Amount:0.0d,AttributeName:"minecraft:generic.attack_speed",Name:"mmoitemsDecoy",Operation:0,UUID:[I;-2021319128,-1357757450,-1987131861,-563359508]}],Damage:0,HSTRY_ENCHANTS:'{"Stat":"ENCHANTS","OGStory":[{"MMOITEMS_ENCHANTS_ñstr":"[]"}]}',HideFlags:67,MMOITEMS_ADDITIONAL_EXPERIENCE_LOGGING:38.83850025748161d,MMOITEMS_DEFENSE:11.675715937700524d,MMOITEMS_DISABLE_CRAFTING:1b,MMOITEMS_DISABLE_ENCHANTING:1b,MMOITEMS_DISABLE_SMELTING:1b,MMOITEMS_DISABLE_SMITHING:1b,MMOITEMS_DYNAMIC_LORE:'["&7« Armor »","","&3 &7⛨ Defense: &f&2+11.7","","&3 &7🪓 Logging EXP: &f&2+38.84%","","&3 &7ᚱ Empty &2Runecarving&7 Socket","","&7He\'s a lumberjack","&7and he\'s ok!","","&7[&6Termite&7] &fSet Bonus:","&8[2] +10% Logging EXP","&8[3] +10% Logging EXP","&8[4] +4 Logging Skill","","&7Tier: §aUNCOMMON","&7Durability: 800 / 800"]',MMOITEMS_ENCHANTS:"[]",MMOITEMS_GEM_STONES:'{"EmptySlots":["Runecarving"],"Gemstones":[]}',MMOITEMS_ITEM_ID:"LUMBERJACK_LEGGINGS_2",MMOITEMS_ITEM_SET:"LUMBERJACK_2",MMOITEMS_ITEM_TYPE:"ARMOR",MMOITEMS_LORE:'["&7He\'s a lumberjack","&7and he\'s ok!"]',MMOITEMS_MAX_DURABILITY:800,MMOITEMS_NAME:"<tier-color>Initiate Lumberjacks Pants",MMOITEMS_REPAIR_TYPE:"ALL",MMOITEMS_REVISION_ID:2,MMOITEMS_TIER:"UNCOMMON_GEAR",display:{Lore:['{"italic":false,"color":"gray","text":"« Armor »"}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"dark_aqua","text":" "},{"color":"gray","text":"⛨ Defense: "},{"color":"dark_green","text":"+11.7"}],"text":""}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"dark_aqua","text":" "},{"color":"gray","text":"🪓 Logging EXP: "},{"color":"dark_green","text":"+38.84%"}],"text":""}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"dark_aqua","text":" "},{"color":"gray","text":"ᚱ Empty "},{"color":"dark_green","text":"Runecarving"},{"color":"gray","text":" Socket"}],"text":""}','{"italic":false,"text":""}','{"italic":false,"color":"gray","text":"He\\u0027s a lumberjack"}','{"italic":false,"color":"gray","text":"and he\\u0027s ok!"}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"gray","text":"["},{"color":"gold","text":"Termite"},{"color":"gray","text":"] "},{"color":"white","text":"Set Bonus:"}],"text":""}','{"italic":false,"color":"dark_gray","text":"[2] +10% Logging EXP"}','{"italic":false,"color":"dark_gray","text":"[3] +10% Logging EXP"}','{"italic":false,"color":"dark_gray","text":"[4] +4 Logging Skill"}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"gray","text":"Tier: "},{"color":"green","text":"UNCOMMON"}],"text":""}','{"italic":false,"color":"gray","text":"Durability: 800 / 800"}'],Name:'{"italic":false,"color":"green","text":"Initiate Lumberjacks Pants"}',color:14448720}}
[21:04:30] [Render thread/FATAL] (blockgame_exp_hud) {}
[21:04:30] [Render thread/FATAL] (blockgame_exp_hud) {AttributeModifiers:[{Amount:0.0d,AttributeName:"minecraft:generic.attack_speed",Name:"mmoitemsDecoy",Operation:0,UUID:[I;-2021319128,-1357757450,-1987131861,-563359508]}],Damage:1,Enchantments:[{id:"minecraft:infinity",lvl:1s}],HSTRY_ADDITIONAL_EXPERIENCE:'{"Stat":"ADDITIONAL_EXPERIENCE","Gemstory":[{"8a56e783-7a22-4f50-ad84-cb57d59a6136":[{"MMOITEMS_ADDITIONAL_EXPERIENCE_ñdbl":1.734}]}]}',HSTRY_ADDITIONAL_EXPERIENCE_FISHING:'{"Stat":"ADDITIONAL_EXPERIENCE_FISHING","OGStory":[{"MMOITEMS_ADDITIONAL_EXPERIENCE_FISHING_ñdbl":39.9065}],"Gemstory":[{"8a56e783-7a22-4f50-ad84-cb57d59a6136":[{"MMOITEMS_ADDITIONAL_EXPERIENCE_FISHING_ñdbl":1.5637}]}]}',HSTRY_ADDITIONAL_EXPERIENCE_MINING:'{"Stat":"ADDITIONAL_EXPERIENCE_MINING","Gemstory":[{"8a56e783-7a22-4f50-ad84-cb57d59a6136":[{"MMOITEMS_ADDITIONAL_EXPERIENCE_MINING_ñdbl":1.6902}]}]}',HSTRY_ENCHANTS:'{"Stat":"ENCHANTS","OGStory":[{"MMOITEMS_ENCHANTS_ñstr":"[\\"infinity 1\\"]"}]}',HideFlags:67,MMOITEMS_ADDITIONAL_EXPERIENCE:1.734d,MMOITEMS_ADDITIONAL_EXPERIENCE_FISHING:41.4702d,MMOITEMS_ADDITIONAL_EXPERIENCE_MINING:1.6902d,MMOITEMS_DEFENSE:4.2d,MMOITEMS_DISABLE_CRAFTING:1b,MMOITEMS_DISABLE_ENCHANTING:1b,MMOITEMS_DISABLE_SMELTING:1b,MMOITEMS_DISABLE_SMITHING:1b,MMOITEMS_DURABILITY:772,MMOITEMS_DYNAMIC_LORE:'["&7« Armor »","","&3 &7⛨ Defense: &f&2+4.2","","&3 &7∞ Class EXP: &f&2+1.73%","&3 &7🎣 Fishing EXP: &f&2+41.47%","&3 &7⛏ Mining EXP: &f&2+1.69%","","&3 &7ᚱ §aClarity - Rank 2","","&7WOMEN FEAR ME","&7FISH FEAR ME","&7MEN TURN THEIR EYES","&7AWAY FROM ME","&7AS I WALK","&7NO BEASTS DARE","&7MAKE A SOUND","&7IN MY PRESENCE","&7I AM ALONE ON","&7THIS BARREN EARTH","","&7[&6Pescatarian&7] &fSet Bonus:","&8[2] +10% Fishing EXP","&8[3] +10% Fishing EXP","&8[4] +4 Fishing Skill","","&7Tier: §aUNCOMMON","&7Durability: 779 / 800"]',MMOITEMS_ENCHANTS:'["infinity 1"]',MMOITEMS_GEM_STONES:'{"EmptySlots":[],"Gemstones":[{"Name":"§aClarity - Rank 2","History":"8a56e783-7a22-4f50-ad84-cb57d59a6136","Id":"RUNECARVING_ADVANCEMENT_2","Type":"RUNECARVING","Color":"Runecarving"}]}',MMOITEMS_ITEM_ID:"FISHERMAN_HELMET_2",MMOITEMS_ITEM_SET:"FISHERMAN_2",MMOITEMS_ITEM_TYPE:"ARMOR",MMOITEMS_LORE:'["&7WOMEN FEAR ME","&7FISH FEAR ME","&7MEN TURN THEIR EYES","&7AWAY FROM ME","&7AS I WALK","&7NO BEASTS DARE","&7MAKE A SOUND","&7IN MY PRESENCE","&7I AM ALONE ON","&7THIS BARREN EARTH"]',MMOITEMS_MAX_DURABILITY:800,MMOITEMS_NAME:"<tier-color>Initiate Fishermans Cap",MMOITEMS_REPAIR_TYPE:"ALL",MMOITEMS_REVISION_ID:2,MMOITEMS_TIER:"UNCOMMON_GEAR",display:{Lore:['{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"« Armor »"}],"text":""}','{"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_aqua","text":" "},{"italic":false,"color":"gray","text":"⛨ Defense: "},{"italic":false,"color":"dark_green","text":"+4.2"}],"text":""}','{"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_aqua","text":" "},{"italic":false,"color":"gray","text":"∞ Class EXP: "},{"italic":false,"color":"dark_green","text":"+1.73%"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_aqua","text":" "},{"italic":false,"color":"gray","text":"🎣 Fishing EXP: "},{"italic":false,"color":"dark_green","text":"+41.47%"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_aqua","text":" "},{"italic":false,"color":"gray","text":"⛏ Mining EXP: "},{"italic":false,"color":"dark_green","text":"+1.69%"}],"text":""}','{"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_aqua","text":" "},{"italic":false,"color":"gray","text":"ᚱ "},{"italic":false,"color":"green","text":"Clarity - Rank 2"}],"text":""}','{"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"WOMEN FEAR ME"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"FISH FEAR ME"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"MEN TURN THEIR EYES"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"AWAY FROM ME"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"AS I WALK"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"NO BEASTS DARE"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"MAKE A SOUND"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"IN MY PRESENCE"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"I AM ALONE ON"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"THIS BARREN EARTH"}],"text":""}','{"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"["},{"italic":false,"color":"gold","text":"Pescatarian"},{"italic":false,"color":"gray","text":"] "},{"italic":false,"color":"white","text":"Set Bonus:"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_gray","text":"[2] +10% Fishing EXP"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_gray","text":"[3] +10% Fishing EXP"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_gray","text":"[4] +4 Fishing Skill"}],"text":""}','{"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"Tier: "},{"italic":false,"color":"green","text":"UNCOMMON"}],"text":""}','{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"Durability: 772 / 800"}],"text":""}'],Name:'{"italic":false,"color":"green","text":"Initiate Fishermans Cap"}',color:6579430}}
[21:04:30] [Render thread/INFO] (blockgame_exp_hud) LUMBERJACK_2
[21:04:30] [Render thread/INFO] (blockgame_exp_hud) LUMBERJACK_2
[21:04:30] [Render thread/INFO] (blockgame_exp_hud)
[21:04:30] [Render thread/INFO] (blockgame_exp_hud) FISHERMAN_2
[21:04:30] [Render thread/INFO] (Minecraft) [STDOUT]: [1 leather_boots, 1 leather_leggings, 1 air, 1 leather_helmet]
[21:11:41] [Render thread/INFO] (blockgame_exp_hud) LUMBERJACK_2
[21:11:41] [Render thread/INFO] (blockgame_exp_hud) MINER_1
[21:11:41] [Render thread/INFO] (blockgame_exp_hud) ARCHAEOLOGIST_3
[21:11:41] [Render thread/INFO] (blockgame_exp_hud) BOTANIST_5


{
   AttributeModifiers:[
      {
         Amount:0.0d,
         AttributeName:"minecraft:generic.attack_speed",
         Name:"mmoitemsDecoy",
         Operation:0,
         UUID:[
            I;-2021319128,
            -1357757450,
            -1987131861,
            -563359508
         ]
      }
   ],
   Damage:0,
   HSTRY_ADDITIONAL_EXPERIENCE_LOGGING:'{"Stat":"ADDITIONAL_EXPERIENCE_LOGGING","OGStory":[{"MMOITEMS_ADDITIONAL_EXPERIENCE_LOGGING_ñdbl":36.25728685200693}],"Mod":[{"e8b60611-a064-43d7-a69e-e317a836fe07":[{"MMOITEMS_ADDITIONAL_EXPERIENCE_LOGGING_ñdbl":10.187900680177876}]}]}',
   HSTRY_ENCHANTS:'{"Stat":"ENCHANTS","OGStory":[{"MMOITEMS_ENCHANTS_ñstr":"[]"}]}',
   HSTRY_NAME:'{"Stat":"NAME","OGStory":[{"MMOITEMS_NAME_ñstr":"<tier-color>Initiate Lumberjacks Vest"}],"Mod":[{"e8b60611-a064-43d7-a69e-e317a836fe07":[{"MMOITEMS_NAME_ñstr":""},{"MMOITEMS_NAME_SUF_ñstr":"[\\"&6of the Termite\\"]"}]}]}',
   HideFlags:67,
   MMOITEMS_ADDITIONAL_EXPERIENCE_LOGGING:46.445187532184804d,
   MMOITEMS_DEFENSE:15.729548319166332d,
   MMOITEMS_DISABLE_CRAFTING:1b,
   MMOITEMS_DISABLE_ENCHANTING:1b,
   MMOITEMS_DISABLE_SMELTING:1b,
   MMOITEMS_DISABLE_SMITHING:1b,
   MMOITEMS_DYNAMIC_LORE:'["&7« Armor »","","&3 &7⛨ Defense: &f&2+15.7","","&3 &7🪓 Logging EXP: &f&2+46.45%","","&3 &7ᚱ Empty &2Runecarving&7 Socket","","&7I sleep all night","&7and I work all day!","","&7[&6Termite&7] &fSet Bonus:","&8[2] +10% Logging EXP","&8[3] +10% Logging EXP","&8[4] +4 Logging Skill","","&7Tier: §aUNCOMMON","&7Durability: 800 / 800"]',
   MMOITEMS_ENCHANTS:"[]",
   MMOITEMS_GEM_STONES:'{"EmptySlots":["Runecarving"],"Gemstones":[]}',
   MMOITEMS_ITEM_ID:"LUMBERJACK_CHESTPLATE_2",
   MMOITEMS_ITEM_SET:"LUMBERJACK_2",
   MMOITEMS_ITEM_TYPE:"ARMOR",
   MMOITEMS_LORE:'["&7I sleep all night","&7and I work all day!"]',
   MMOITEMS_MAX_DURABILITY:800,
   MMOITEMS_NAME:"<tier-color>Initiate Lumberjacks Vest",
   MMOITEMS_NAME_SUF:'["&6of the Termite"]',
   MMOITEMS_REPAIR_TYPE:"ALL",
   MMOITEMS_REVISION_ID:2,
   MMOITEMS_TIER:"UNCOMMON_GEAR",
   display:{
      Lore:[
         '{"italic":false,"color":"gray","text":"« Armor »"}',
         '{"italic":false,"text":""}',
         '{"italic":false,"extra":[{"color":"dark_aqua","text":" "},{"color":"gray","text":"⛨ Defense: "},{"color":"dark_green","text":"+15.7"}],"text":""}',
         '{"italic":false,"text":""}',
         '{"italic":false,"extra":[{"color":"dark_aqua","text":" "},{"color":"gray","text":"🪓 Logging EXP: "},{"color":"dark_green","text":"+46.45%"}],"text":""}',
         '{"italic":false,"text":""}',
         '{"italic":false,"extra":[{"color":"dark_aqua","text":" "},{"color":"gray","text":"ᚱ Empty "},{"color":"dark_green","text":"Runecarving"},{"color":"gray","text":" Socket"}],"text":""}',
         '{"italic":false,"text":""}',
         '{"italic":false,"color":"gray","text":"I sleep all night"}',
         '{"italic":false,"color":"gray","text":"and I work all day!"}',
         '{"italic":false,"text":""}',
         '{"italic":false,"extra":[{"color":"gray","text":"["},{"color":"gold","text":"Termite"},{"color":"gray","text":"] "},{"color":"white","text":"Set Bonus:"}],"text":""}',
         '{"italic":false,"color":"dark_gray","text":"[2] +10% Logging EXP"}',
         '{"italic":false,"color":"dark_gray","text":"[3] +10% Logging EXP"}',
         '{"italic":false,"color":"dark_gray","text":"[4] +4 Logging Skill"}',
         '{"italic":false,"text":""}',
         '{"italic":false,"extra":[{"color":"gray","text":"Tier: "},{"color":"green","text":"UNCOMMON"}],"text":""}',
         '{"italic":false,"color":"gray","text":"Durability: 800 / 800"}'
      ],
      Name:'{"italic":false,"color":"white","extra":[{"color":"green","text":"Initiate Lumberjacks Vest "},{"color":"gold","text":"of the Termite"}],"text":""}',
      color:14448720
   }
}
 */