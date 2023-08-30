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


public class BlockgameExpHud implements ClientModInitializer {

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

        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        float startVertical, startHorizontal, offset;
        int color, coinColor;
        boolean coinEnabled;

        if (config != null) {
            startVertical = config.hudSettings.Y_POS;
            startHorizontal = config.hudSettings.X_POS;
            offset = config.hudSettings.SPACING;
            color = config.hudSettings.TEXT_COLOR;
            coinColor = config.hudSettings.COIN_COLOR;
            coinEnabled = config.hudSettings.COIN_ENABLED;
        } else {
            startVertical = BlockgameExpHud.DEFAULT_Y_POS;
            startHorizontal = BlockgameExpHud.DEFAULT_X_POS;
            offset = BlockgameExpHud.DEFAULT_SPACING;
            color = BlockgameExpHud.DEFAULT_TEXT_COLOR;
            coinColor = BlockgameExpHud.DEFAULT_COIN_COLOR;
            coinEnabled = BlockgameExpHud.DEFAULT_COIN_ENABLED;
        }



        //Description
        renderer.drawWithShadow(matrixStack, showGlobal ? "Session EXP Stats:" : "Last " + DEFAULT_MAX_SAMPLE_VALUE + " EXP Stats:", startHorizontal, startVertical, color);

        int row = 1;
        if (coins > 0 && coinEnabled) {
            renderer.drawWithShadow(matrixStack, "Coin's: " + coins, startHorizontal, startVertical + ((row) * offset), coinColor);
            row++;
        }

        for (int i = 0; i < professionNames.length; i++) {

            if (professionTotalSessionExp[i] > 0f) {
                String total = showGlobal ? formatNumber(professionTotalSessionExp[i]) : formatNumber(professionSessionAverageTotalExp[i]);
                String average = showGlobal ? formatNumber(professionTotalSessionExp[i] / professionSessionExpCount[i]) + "⌀" : formatNumber(professionAverageSessionExp[i]) + "⌀"; //μ
                renderer.drawWithShadow(matrixStack, professionNames[i] +": "+ total +" | "+ average, startHorizontal, startVertical + ((row) * offset), color);
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
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);
        DecimalFormat df = (DecimalFormat) nf;
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
