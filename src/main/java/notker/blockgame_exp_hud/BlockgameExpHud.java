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
import net.minecraft.network.MessageType;
import notker.blockgame_exp_hud.config.BlockgameExpHudConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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




    public static String[] professionNames = {"Archaeology", "Einherjar", "Fishing", "Herbalism", "Logging", "Mining", "Runecarving"};
    public float[] professionTotalSessionExp = new float[professionNames.length];
    public float[] professionAverageSessionExp = new float[professionNames.length];
    public float[] professionSessionAverageTotalExp = new float[professionNames.length];
    public int[] professionSessionExpCount = new int[professionNames.length];
    public float[][] professionsLastExpValues = new float[professionNames.length][DEFAULT_MAX_SAMPLE_VALUE];

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

    public void tick(MinecraftClient client) {
        //toggle render
        if (blockgameExpHudToggleKey.wasPressed()) hideOverlay = !hideOverlay;
        //toggle modi
        if (blockgameExpHudSwitchKey.wasPressed()) showGlobal = !showGlobal;

    }

    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        boolean enabled = config != null ? config.ENABLED : DEFAULT_ENABLED;
        if (!enabled || hideOverlay) return;

        float startVertical, startHorizontal, offset;
        int textColor, coinColor;
        boolean coinEnabled, backgroundEnabled;

        /* Multicolor text
        OrderedText test = (new LiteralText("Test").formatted(Formatting.RED)).asOrderedText();
        OrderedText test2 = (new LiteralText("Test").formatted(Formatting.WHITE)).asOrderedText();
        OrderedText oText = OrderedText.innerConcat(test, test2);
        renderer.drawWithShadow(matrixStack, oText, startHorizontal + 50, startVertical + 100, textColor);
        */

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
        int textHeight = 8;
        int textBoxHeight = textHeight;
        int borderWidth = 3;

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
                textList[i + 2] = professionNames[i] +": "+ total +" | "+ average;
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
        String value = "";

        for (int i = message.lastIndexOf("d") + 2; i < message.lastIndexOf(" "); i++) {
            value += message.charAt(i);
        }

        if (value.isEmpty()) return;
        //LOGGER.fatal("Coin: " + value);
        coins += Integer.parseInt(value);
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


}
