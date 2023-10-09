package notker.blockgame_exp_hud.mixin;


import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
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
        // Check if the config is available and Enabled
        if (config != null && config.ATTRIBUTES_ENABLED) {
            // Checks which item Type to compare
            if (config.attributeSettings.ITEM_TYPES == MMOITEMS_ITEM_TYPES.ALL ||
                nbt.getString(BlockgameExpHud.DEFAULT_RUNE_ITEM_TYPE_TAG).equals(config.attributeSettings.ITEM_TYPES.tag())) {


                byte containTag = 0;
                byte matchTags = 0;
                // Check if Tag Exist
                containTag += config.attributeSettings.Rune_TAG_0 != AttributeTags.NONE ? 1 : 0;
                containTag += config.attributeSettings.Rune_TAG_1 != AttributeTags.NONE ? 1 : 0;
                containTag += config.attributeSettings.Rune_TAG_2 != AttributeTags.NONE ? 1 : 0;
                containTag += config.attributeSettings.Rune_TAG_3 != AttributeTags.NONE ? 1 : 0;
                containTag += config.attributeSettings.Rune_TAG_4 != AttributeTags.NONE ? 1 : 0;

                // Check if Tag exists on Item
                matchTags += nbt.contains(config.attributeSettings.Rune_TAG_0.tag()) ? 1 : 0;
                matchTags += nbt.contains(config.attributeSettings.Rune_TAG_1.tag()) ? 1 : 0;
                matchTags += nbt.contains(config.attributeSettings.Rune_TAG_2.tag()) ? 1 : 0;
                matchTags += nbt.contains(config.attributeSettings.Rune_TAG_3.tag()) ? 1 : 0;
                matchTags += nbt.contains(config.attributeSettings.Rune_TAG_4.tag()) ? 1 : 0;

                if (containTag == matchTags && matchTags > 0 && !stack.isEmpty() && stack.getCount() == 1) {

                    MatrixStack matrixStack = new MatrixStack();
                    String string = config.attributeSettings.Rune_String;

                    matrixStack.translate(0.0D, 0.0D, (this.zOffset + 200.0F));

                    VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
                    renderer.drawWithOutline(new LiteralText(string).asOrderedText(),
                            (float)(x + 19 - 2 - renderer.getWidth(string) + config.attributeSettings.X_OFFSET) ,
                            (float)(y + 6 + 3 + config.attributeSettings.Y_OFFSET),
                            config.attributeSettings.STAR_COLOR,
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
}
