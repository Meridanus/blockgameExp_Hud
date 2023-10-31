package notker.blockgame_exp_hud.module;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import notker.blockgame_exp_hud.BlockgameExpHud;
import notker.blockgame_exp_hud.helper.ExpHudDataHelper;
import notker.blockgame_exp_hud.config.BlockgameExpHudConfig;
import notker.blockgame_exp_hud.helper.HudTextHelper;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class ExpHudRender {
    public static void onHudRender(MatrixStack matrixStack, float tickDelta) {
        BlockgameExpHudConfig config = BlockgameExpHud.getConfig();

        if (!config.ENABLED || ExpHudDataHelper.hideOverlay) return;
        final Identifier BARS_TEXTURE = new Identifier("blockgame_exp_hud", "textures/gui/exp_bars.png");
        final int WIDTH = 160;
        final int HEIGHT = 5;

        float startVertical = config.hudSettings.Y_POS;
        float startHorizontal = config.hudSettings.X_POS;
        float offset = config.hudSettings.SPACING;
        float scale = config.hudSettings.HUD_SCALE;
        float opacity = config.hudSettings.HUD_OPACITY;

        int textColor = config.hudSettings.TEXT_COLOR;
        int coinColor = config.hudSettings.COIN_COLOR;

        boolean coinEnabled = config.hudSettings.COIN_ENABLED;



        //Apply the Hud Scale
        matrixStack.scale(scale, scale, 1);

        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

        //renderer.drawWithShadow(matrixStack, HudTextHelper.getProfessionHudText(professionNames[1], 0f, 0f,1000f, 5.64f, textColor), startHorizontal + 50, startVertical + 100, textColor);
        // List of all hud text to calculate the background size and draw
        MutableText[] textList = new MutableText[ExpHudDataHelper.professionNames.length + 2];

        // Create Title Text
        textList[0] = ExpHudDataHelper.showGlobal ? HudTextHelper.getTitleHudText("Session EXP Stats:", textColor) : HudTextHelper.getTitleHudText("Last " + ExpHudDataHelper.DEFAULT_MAX_SAMPLE_VALUE + " EXP Stats:", textColor);
        // Create Coin Text
        if (ExpHudDataHelper.coins > 0 && coinEnabled) {
            textList[1] = HudTextHelper.getCoinHudText(ExpHudDataHelper.coins, coinColor, textColor);
        }
        // Create Profession Text
        for (int i = 0; i < ExpHudDataHelper.professionNames.length; i++) {
            // Only Add professions with Exp
            if (ExpHudDataHelper.professionTotalExpValues[i] > 0f) {
                // Create the Profession Texts
                textList[i + 2] = HudTextHelper.getProfessionHudText(
                        ExpHudDataHelper.professionNames[i],
                        ExpHudDataHelper.professionLevelValues[i],
                        ExpHudDataHelper.equipmentBonusExpValues[i],
                        ExpHudDataHelper.showGlobal ? ExpHudDataHelper.professionTotalExpValues[i] : ExpHudDataHelper.professionSampleTotalExpValues[i],
                        ExpHudDataHelper.showGlobal ? ExpHudDataHelper.professionTotalAverageValues[i] : ExpHudDataHelper.professionSampleAverages[i],
                        textColor,
                        ExpHudDataHelper.professionTextColors[i]

                );
            }
        }


        // Create the Background
        if (opacity > 0f) {
            int textBoxHeight = ExpHudDataHelper.DEFAULT_TEXT_HEIGHT;
            int textBoxWidth = 0;
            int borderWidth = ExpHudDataHelper.getHudBackgroundBorderSize();

            for (float lvlValues : ExpHudDataHelper.professionLevelValues) {
                if (lvlValues > 0) {
                    textBoxHeight += offset;
                    textBoxWidth = WIDTH;
                }
            }

            // Calculate Background Height
            for (MutableText mutableText : textList) {
                if (mutableText != null) {
                    // Change the Background box width if the text is wider then the current box
                    textBoxWidth = Math.max(textBoxWidth, renderer.getWidth(mutableText));
                    // Add Line Height
                    textBoxHeight += offset;
                }
            }

            // Draw Background
            int backgroundColor = MinecraftClient.getInstance().options.getTextBackgroundColor(opacity);
            fill(matrixStack,
                    (int) startHorizontal - borderWidth,
                    (int) startVertical - borderWidth ,
                    (int) startHorizontal + textBoxWidth + borderWidth,
                    (int) startVertical + textBoxHeight - (borderWidth * 2),
                    backgroundColor);
        }



        //Draw Title
        renderer.drawWithShadow(matrixStack, textList[0], startHorizontal, startVertical, textColor);
        int row = 1;

        // Draw Coin Text
        if (ExpHudDataHelper.coins > 0 && coinEnabled) {
            renderer.drawWithShadow(matrixStack, textList[1], startHorizontal, startVertical + (row * offset), coinColor);
            row++;
        }
        // Draw Profession Text
        for (int i = 0; i < ExpHudDataHelper.professionNames.length; i++) {
            if (ExpHudDataHelper.professionTotalExpValues[i] > 0f) {
                renderer.drawWithShadow(matrixStack, textList[i + 2], startHorizontal, startVertical + (row * offset), 0x000000);
                row++;
            }
            if (ExpHudDataHelper.professionLevelValues[i] > 0){

                RenderSystem.setShaderTexture(0, BARS_TEXTURE);
                DrawableHelper.drawTexture(matrixStack, (int)startHorizontal, (int)(startVertical + (row * offset)),  WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT,WIDTH ,70);
                DrawableHelper.drawTexture(matrixStack, (int)startHorizontal , (int)(startVertical + (row * offset)), 0, 0, (i + 1) * HEIGHT, (int)(1.6f * ExpHudDataHelper.professionLevelValues[i]), HEIGHT, WIDTH, 70); // XP Bar
                row ++;
            }
        }
        // Restore the Default scale after drawing the Hud
        matrixStack.scale(1f / scale, 1f / scale, 1);

    }
}
