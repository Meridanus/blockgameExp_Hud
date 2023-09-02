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
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Formatting;
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




    public static final String[] professionNames = {"Archaeology", "Einherjar", "Fishing", "Herbalism", "Logging", "Mining", "Runecarving"};
    public float[] professionTotalSessionExp = new float[professionNames.length];
    public float[] professionAverageSessionExp = new float[professionNames.length];
    public float[] professionSessionAverageTotalExp = new float[professionNames.length];
    public int[] professionSessionExpCount = new int[professionNames.length];
    public float[][] professionsLastExpValues = new float[professionNames.length][DEFAULT_MAX_SAMPLE_VALUE];

    public static final String[] nbtKeyNames = {
            "MMOITEMS_ADDITIONAL_EXPERIENCE_ARCHAEOLOGY",
            "MMOITEMS_ADDITIONAL_EXPERIENCE",
            "MMOITEMS_ADDITIONAL_EXPERIENCE_FISHING",
            "MMOITEMS_ADDITIONAL_EXPERIENCE_HERBALISM",
            "MMOITEMS_ADDITIONAL_EXPERIENCE_LOGGING",
            "MMOITEMS_ADDITIONAL_EXPERIENCE_MINING",
            "MMOITEMS_ADDITIONAL_EXPERIENCE_RUNECARVING"};
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
                float currentExp = expValueFromString(message);
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
        }
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
            for (ItemStack items : equippedItems) {
                for (int i = 0; i < nbtKeyNames.length; i++) {
                    newEquipmentBonusExp[i] += items.getOrCreateNbt().getFloat(nbtKeyNames[i]);
                }
            }

            equipmentBonusExp = newEquipmentBonusExp;

        }




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
