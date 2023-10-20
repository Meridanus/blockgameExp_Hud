package notker.blockgame_exp_hud.mixin;


import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import notker.blockgame_exp_hud.helper.MMOItemModifiersHelper;
import notker.blockgame_exp_hud.BlockgameExpHud;
import notker.blockgame_exp_hud.helper.ExpHudDataHelper;
import notker.blockgame_exp_hud.helper.MMOItemsItemTypesHelper;
import notker.blockgame_exp_hud.config.BlockgameExpHudConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow public float zOffset;


    @Inject(at = @At("HEAD"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {

        BlockgameExpHudConfig config = BlockgameExpHud.getConfig();
        // Check if the config is available and Enabled and stack size is 1
        if (config.ATTRIBUTES_ENABLED && !stack.isEmpty() && stack.getCount() == 1) {
            NbtCompound nbt = stack.getOrCreateNbt();

            // Checks which item Type to compare
            if (config.attributeSettings.ITEM_TYPES == MMOItemsItemTypesHelper.ALL ||
                nbt.getString(ExpHudDataHelper.DEFAULT_RUNE_ITEM_TYPE_TAG).equals(config.attributeSettings.ITEM_TYPES.tag())) {

                byte[] results = updateTagMatches(nbt, config.attributeSettings.Rune_TAG_0, config.attributeSettings.Rune_Value_0,
                                    updateTagMatches(nbt, config.attributeSettings.Rune_TAG_1, config.attributeSettings.Rune_Value_1,
                                        updateTagMatches(nbt, config.attributeSettings.Rune_TAG_2, config.attributeSettings.Rune_Value_2,
                                            updateTagMatches(nbt, config.attributeSettings.Rune_TAG_3, config.attributeSettings.Rune_Value_3,
                                                updateTagMatches(nbt, config.attributeSettings.Rune_TAG_4, config.attributeSettings.Rune_Value_4, new byte[2])))));

                // [0]Tags to match | [1]Tags Matched
                if (results[0] == results[1] && results[0] > 0 ) {
                    String string = config.attributeSettings.attributeSettingsOptions.Rune_String;
                    MatrixStack matrixStack = new MatrixStack();
                    float scale = config.attributeSettings.attributeSettingsOptions.HIGHLIGHT_SCALE;

                    matrixStack.scale(scale, scale, 1);
                    matrixStack.translate(0.0D, 0.0D, (this.zOffset + 200.0F));

                    VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
                    renderer.drawWithOutline(new LiteralText(string).asOrderedText(),
                            (x / scale) + 17 - renderer.getWidth(string) + config.attributeSettings.attributeSettingsOptions.X_OFFSET,
                            (y / scale) + 9 + config.attributeSettings.attributeSettingsOptions.Y_OFFSET,
                            config.attributeSettings.attributeSettingsOptions.STAR_COLOR,
                            0,
                            matrixStack.peek().getPositionMatrix(),
                            immediate,
                            15728880
                            );
                    immediate.draw();
                }
            }
        }
    }

    private byte[] updateTagMatches(NbtCompound nbt, MMOItemModifiersHelper MMOItemModifiersHelper, float minValue, byte[] result) {
        // NONE = ignore
        if (MMOItemModifiersHelper == MMOItemModifiersHelper.NONE) return result;
        // Tag Present -> TagsToMatch++
        result[0]++;
        // Attribute not found on Item
        if (!nbt.contains(MMOItemModifiersHelper.tag())) return result;
        // Match - Value found and over/equal Threshold -> TagsMatched++
        if (nbt.getFloat(MMOItemModifiersHelper.tag()) >= minValue)  result[1]++;
        // Match found but under Threshold
        return result;
    }
}
