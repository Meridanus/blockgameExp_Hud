package notker.blockgame_exp_hud.mixin;


import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import notker.blockgame_exp_hud.AttributeTags;
import notker.blockgame_exp_hud.BlockgameExpHud;
import notker.blockgame_exp_hud.MMOITEMS_ITEM_TYPES;
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
        NbtCompound nbt = stack.getOrCreateNbt();

        BlockgameExpHudConfig config = BlockgameExpHud.config;
        // Check if the config is available
        if (config != null && config.ATTRIBUTES_ENABLED) {
            // Checks which item Type
            if (config.attributeSettings.ITEM_TYPES == MMOITEMS_ITEM_TYPES.ALL ||
                nbt.getString(BlockgameExpHud.DEFAULT_RUNE_ITEM_TYPE_TAG).equals(config.attributeSettings.ITEM_TYPES.tag())) {

                boolean tags;
                // Check if Tags Exist and exists on Item
                tags = config.attributeSettings.Rune_TAG_0 != AttributeTags.NONE && nbt.contains(config.attributeSettings.Rune_TAG_0.tag());
                tags = config.attributeSettings.Rune_TAG_1 != AttributeTags.NONE ? nbt.contains(config.attributeSettings.Rune_TAG_1.tag()) : tags;
                tags = config.attributeSettings.Rune_TAG_2 != AttributeTags.NONE ? nbt.contains(config.attributeSettings.Rune_TAG_2.tag()) : tags;
                tags = config.attributeSettings.Rune_TAG_3 != AttributeTags.NONE ? nbt.contains(config.attributeSettings.Rune_TAG_3.tag()) : tags;
                tags = config.attributeSettings.Rune_TAG_4 != AttributeTags.NONE ? nbt.contains(config.attributeSettings.Rune_TAG_4.tag()) : tags;

                if (tags && !stack.isEmpty() && stack.getCount() == 1) {

                    MatrixStack matrixStack = new MatrixStack();
                    String string = config.attributeSettings.Rune_String;

                    matrixStack.translate(0.0D, 0.0D, (this.zOffset + 200.0F));

                    VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
                    renderer.draw(
                            string,
                            (float)(x + 19 - 2 - renderer.getWidth(string) +  config.attributeSettings.X_OFFSET),
                            (float)(y + 6 + 3 + config.attributeSettings.Y_OFFSET),
                            config.attributeSettings.STAR_COLOR,
                            true,
                            matrixStack.peek().getPositionMatrix(),
                            immediate,
                            false,
                            0,
                            15728880);
                    immediate.draw();
                }
            }
        }
    }
}
