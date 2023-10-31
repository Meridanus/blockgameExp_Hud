package notker.blockgame_exp_hud.helper;


import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public class HudTextHelper {
    public static final int positiveTextColor = 0x55FF55; // Minecraft Green
    public static final int neutralTextColor = 0xFFFFFF; // Minecraft White
    public static final int negativeTextColor = 0xFF5555; // Minecraft Red

    public static MutableText getTitleHudText(String title, int textColor) {
        // Setup Color Styles
        Style configColor = Style.EMPTY.withColor(textColor).withBold(true);

        return new LiteralText(title).setStyle(configColor);
    }

    public static MutableText getCoinHudText(int coins, int coinColor, int textColor) {
        // Setup Color Styles
        Style configCoinColor = Style.EMPTY.withColor(coinColor);
        Style configColor = Style.EMPTY.withColor(textColor);

        return new LiteralText("Coin's: ").setStyle(configColor).append(
                new LiteralText(formatCoinNumber(coins) + "$").setStyle(configCoinColor)
        );
    }

    public static MutableText getProfessionHudText(String profession, float curLvl, float bonus, float total, float average, int textColor, int professionTextColor) {
        // Setup Color Styles
        Style professionColor = Style.EMPTY.withColor(professionTextColor);
        Style configColor = Style.EMPTY.withColor(textColor);
        Style positiveColor = Style.EMPTY.withColor(positiveTextColor);
        Style neutralColor = Style.EMPTY.withColor(neutralTextColor);

        //Base Profession String
        MutableText returnText = new LiteralText(profession).setStyle(professionColor);

        // Add Spacer & Color based on the Users choice
        returnText.append(new LiteralText(" |").setStyle(configColor));

        // Add Bonus String and the color based on the value
        returnText.append(new LiteralText(
                bonus > 0f ? getFormattedPercentage(" +", bonus) : " 0%"
        ).setStyle(
                bonus > 0f ? positiveColor : neutralColor
        ));

        // Add the Total and Average Values
        returnText.append(new LiteralText(" | "+ formatNumber(total) + " | " + formatNumber(average) + "⌀").setStyle(configColor));

        return returnText;
    }

    public static String getFormattedPercentage(String prefix, float value) {
        return prefix + String.format("%.2f", value) + "%";
    }

    public static String formatNumber(Float input){
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
        df.applyPattern("###,###,###.0");
        return df.format(input);
    }

    public static String formatCoinNumber(int input){
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
        df.applyPattern("###,###,###");
        return df.format(input);
    }

    public static float expValueFromString(String message){
        return valueFromString(message, "+", " EXP");
    }

    public static float curLvlValueFromString(String message){
        return valueFromString(message, "- ", "%");
    }

    public static int coinValueFromString(String message){
        return (int) valueFromString(message, "deposited ", " ");
    }

    private static float valueFromString(String message, String searchPrefix, String searchSuffix){
        StringBuilder value = new StringBuilder();
        //System.out.println(searchPrefix + " <--> " + message + " <--> " + searchSuffix);
        for (int i = message.lastIndexOf(searchPrefix) + searchPrefix.length(); i < message.lastIndexOf(searchSuffix); i++) {
            value.append(message.charAt(i));
        }
        //System.out.println(value);
        if (value.isEmpty()) return 0f;
        return Float.parseFloat(value.toString());
    }






}
/*
 private static final Identifier BASE_TEXTURE = new Identifier("blockgame_exp_hud", "textures/gui/hotbar/hud_textures.png");

    public ImmersiveGameHud(MinecraftClient client) {
        super(client);
    }

    public ImmersiveGameHud() {
        super(MinecraftClient.getInstance());
    }

    @Override
    public void render(MatrixStack matrixStack, float tickDelta) {

        PlayerEntity player = MinecraftClient.getInstance().player;
        HungerManager hunger = player.getHungerManager();



        float hp = Math.max(0.0F, 1 - (20f - player.getHealth()) / 20f);
        float food = Math.max(0.0F, 1 - (20f - hunger.getFoodLevel()) / 20f);
        float saturation = Math.max(0.0F, 1 - (20f - hunger.getSaturationLevel()) / 20f);


        int baseX = 255;
        int baseY = 473;
        String lvl = "49";
        float expLength = 1f;
        int hpLength = (int)(32 * hp);
        int saturationLength = (int)(32 * saturation);
        int hydrationLength = (int)(32 * food);

        float latencyValue = .5f;
        int latency = (int)(20 * 1f);

        RenderSystem.setShaderTexture(0, BASE_TEXTURE);
        RenderSystem.enableBlend();

        //DrawableHelper.drawTexture(matrixStack, baseX, baseY - 49, 0, 0, 399, 317, 49, 512, 512); // Base


        DrawableHelper.drawTexture(matrixStack, baseX + 9, baseY - 40, 0, 0, 49, 297, 40, 512, 512); // Base
        RenderSystem.disableBlend();

        DrawableHelper.drawTexture(matrixStack, baseX + 57, baseY- 29, 0, 0, 89, (int)(209 * expLength), 5, 512, 512); // XP Bar



        RenderSystem.setShaderColor(0.0f, 1.0f, 0.0f, 1.0f);
        DrawableHelper.drawTexture(matrixStack, baseX + 75, baseY - latency - 1, 0, 0, 190 + (20 - latency), 5, latency, 512, 512); // Latency
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
 */