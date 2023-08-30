package notker.blockgame_exp_hud.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import notker.blockgame_exp_hud.BlockgameExpHud;
import notker.blockgame_exp_hud.config.BlockgameExpHudConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "addChatMessage", at = @At("HEAD"), cancellable = true)
    public void addChatMessage(MessageType messageType, Text text, UUID uUID, CallbackInfo ci) {

        BlockgameExpHudConfig config = BlockgameExpHud.config;
        String tag;
        boolean enabled;
        byte id;

        if (config != null) {
            tag = config.advancedSettings.CHAT_TAG;
            enabled = config.baseSettings.ENABLED;
            id = config.advancedSettings.MESSAGE_TYPE;
        } else {
            tag = BlockgameExpHud.DEFAULT_CHAT_TAG;
            enabled = BlockgameExpHud.DEFAULT_ENABLED_VALUE;
            id = BlockgameExpHud.DEFAULT_MESSAGE_TYPE_VALUE;
        }


        if (enabled && messageType.getId() == id){

            if (text.getString().endsWith(" Coin.")) {
                BlockgameExpHud.getInstance().coinValueFromString(text.getString());
            }

            if (text.getString().contains(tag)) {
                BlockgameExpHud.getInstance().addExp(text.getString());

            }

            ci.cancel();
            return;
        }




    }
}
