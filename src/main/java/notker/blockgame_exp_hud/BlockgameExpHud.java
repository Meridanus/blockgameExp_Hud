package notker.blockgame_exp_hud;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import notker.blockgame_exp_hud.config.BlockgameExpHudConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BlockgameExpHud implements ClientModInitializer {

    private static BlockgameExpHud instance;

    public static Logger LOGGER = LogManager.getLogger("blockgame_exp_hud");

    public static BlockgameExpHudConfig config;
    public static ConfigHolder<BlockgameExpHudConfig> configHolder;

    public static final String DEFAULT_CHAT_TAG = "[EXP]";
    public static final Byte DEFAULT_MESSAGE_TYPE_VALUE = 1;
    public static final Boolean DEFAULT_ENABLED_VALUE = true;
    public static final Integer DEFAULT_TEXT_COLOR = 0xffffff;
    public static final Integer DEFAULT_COIN_COLOR = 0xFFAA00;
    public static final Integer DEFAULT_MAX_SAMPLE_VALUE = 100;



    public static String[] professionNames = {"Herbalism", "Fishing", "Archaeology", "Logging", "Mining", "Runecarving"};
    public float[] professionTotalSessionExp = new float[professionNames.length];
    public float[] professionAverageSessionExp = new float[professionNames.length];
    public float[] professionSessionAverageTotalExp = new float[professionNames.length];
    public int[] professionSessionExpCount = new int[professionNames.length];
    public float[][] professionsLastExpValues = new float[professionNames.length][DEFAULT_MAX_SAMPLE_VALUE];

    public int coins = 0;




    @Override
    public void onInitializeClient() {

        if (instance == null) instance = this;

        configHolder = AutoConfig.register(BlockgameExpHudConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(BlockgameExpHudConfig.class).getConfig();

        LOGGER.info("Config Loaded");



        HudRenderCallback.EVENT.register(new HudRenderCallback() {

            @Override
            public void onHudRender(MatrixStack matrixStack, float tickDelta) {
                boolean enabled = config != null ? config.baseSettings.ENABLED : DEFAULT_ENABLED_VALUE;
                if (!enabled) return;

                TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
                float startVertical = 30;
                float startHorizontal = 3;
                float offset = 10;

                int color = config != null ? config.baseSettings.TEXT_COLOR : DEFAULT_TEXT_COLOR;
                int coinColor = config != null ? config.baseSettings.COIN_COLOR : DEFAULT_COIN_COLOR;


                if (coins > 0) {
                    renderer.drawWithShadow(matrixStack, "Coin's: " + coins, startHorizontal, startVertical, coinColor);
                }

                int row = 1;

                for (int i = 0; i < professionNames.length; i++) {

                    if (professionTotalSessionExp[i] > 0f) {
                        String total = String.format("%.1f",professionTotalSessionExp[i]) ;
                        String average = String.format("%.1f", professionAverageSessionExp[i]) + "⌀"; //μ
                        renderer.drawWithShadow(matrixStack, professionNames[i] +": "+ total +" | "+ average, startHorizontal, startVertical + ((row) * offset), color);
                        row++;
                    }

                }


            }
        });

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
