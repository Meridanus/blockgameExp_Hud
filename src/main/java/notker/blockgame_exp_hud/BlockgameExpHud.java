package notker.blockgame_exp_hud;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import notker.blockgame_exp_hud.config.BlockgameExpHudConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BlockgameExpHud implements ClientModInitializer {

    private static BlockgameExpHud instance;

    public static Logger LOGGER = LogManager.getLogger("blockgame_exp_hud");

    public static BlockgameExpHudConfig config;
    public static ConfigHolder<BlockgameExpHudConfig> configHolder;

    public static final String DEFAULT_MAX_DURABILITY_TAG = "MMOITEMS_MAX_DURABILITY";
    public static final String DEFAULT_DURABILITY_TAG = "MMOITEMS_DURABILITY";
    public static final String DEFAULT_MAX_CONSUME_TAG = "MMOITEMS_MAX_CONSUME";
    public static final Integer DEFAULT_FULL_CONSUME_VALUE = 5;
    public static final Boolean DEFAULT_SINGLE_COLOR = false;
    public static final Integer DEFAULT_CONSUME_COLOR = 2149464;



    public float herbalismExp = 0f;
    public float miningExp = 0f;
    public float fishingExp = 0f;
    public float archeologyExp = 0f;
    public float loggingExp = 0f;
    public float runecraftingExp = 0f;




    @Override
    public void onInitializeClient() {

        if (instance == null) instance = this;

        configHolder = AutoConfig.register(BlockgameExpHudConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(BlockgameExpHudConfig.class).getConfig();

        LOGGER.info("Config Loaded");



        HudRenderCallback.EVENT.register(new HudRenderCallback() {

            @Override
            public void onHudRender(MatrixStack matrixStack, float tickDelta) {
                TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
                float startVertical = 30;
                float startHorizontal = 3;
                float offset = 10;

                renderer.drawWithShadow(matrixStack, "Session EXP:", startHorizontal, startVertical, 0xffffff, true);
                renderer.drawWithShadow(matrixStack, "Herbalism: " + herbalismExp, startHorizontal, startVertical + offset, 0xffffff);
                renderer.drawWithShadow(matrixStack, "Mining: " + miningExp, startHorizontal, startVertical + (2 * offset), 0xffffff);
                renderer.drawWithShadow(matrixStack, "Logging: " + loggingExp, startHorizontal, startVertical + (3 * offset), 0xffffff);
                renderer.drawWithShadow(matrixStack, "Archaeology: " + archeologyExp, startHorizontal, startVertical + (4 * offset), 0xffffff);
                renderer.drawWithShadow(matrixStack, "Fishing: " + fishingExp, startHorizontal, startVertical + (5 * offset), 0xffffff);
                renderer.drawWithShadow(matrixStack, "Runecarving: " + runecraftingExp, startHorizontal, startVertical + (6 * offset), 0xffffff);
            }
        });

    }



    public static BlockgameExpHud getInstance() {
        return instance;
    }


    public float getExpValue(String message){
        StringBuilder value = new StringBuilder();

        for (int i = message.indexOf("+") + 1; i < message.lastIndexOf(" "); i++) {
            value.append(message.charAt(i));
        }

        if (value.isEmpty()) return 0f;

        return Float.parseFloat(value.toString());
    }


    public void addExp(String message){
        if (message.contains("Herbalism")) {
            Float value = getExpValue(message);
            System.out.println(value);
            herbalismExp += value;
            System.out.println("Herbalism Session EXP: " + herbalismExp);
            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("Herbalism Session EXP: " + Float.toString(herbalismExp)), false);
            return;
        }

        if (message.contains("Fishing")) {
            Float value = getExpValue(message);
            System.out.println(value);
            fishingExp += value;
            System.out.println("Fishing Session EXP: " + fishingExp);
            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("Fishing Session EXP: " + Float.toString(fishingExp)), false);
            return;
        }

        if (message.contains("Archaeology")) {
            Float value = getExpValue(message);
            System.out.println(value);
            archeologyExp += value;
            System.out.println("Archaeology Session EXP: " + archeologyExp);
            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("Archaeology Session EXP: " + Float.toString(archeologyExp)), false);
            return;
        }

        if (message.contains("Logging")) {
            Float value = getExpValue(message);
            System.out.println(value);
            loggingExp += value;
            System.out.println("Logging Session EXP: " + loggingExp);
            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("Logging Session EXP: " + Float.toString(loggingExp)), false);
            return;
        }

        if (message.contains("Mining")) {
            Float value = getExpValue(message);
            System.out.println(value);
            miningExp += value;
            System.out.println("Mining Session EXP: " + miningExp);
            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("Mining Session EXP: " + Float.toString(miningExp)), false);
            return;
        }

        if (message.contains("Runecarving")) {
            Float value = getExpValue(message);
            System.out.println(value);
            runecraftingExp += value;
            System.out.println("Runecarving Session EXP: " + runecraftingExp);
            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("Runecarving Session EXP: " + Float.toString(runecraftingExp)), false);
            return;
        }
    }
}
