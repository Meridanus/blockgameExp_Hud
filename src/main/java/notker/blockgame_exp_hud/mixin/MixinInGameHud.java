package notker.blockgame_exp_hud.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import notker.blockgame_exp_hud.BlockgameExpHud;
import notker.blockgame_exp_hud.config.BlockgameExpHudConfig;
import notker.blockgame_exp_hud.helper.ExpHudDataHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "addChatMessage", at = @At("HEAD"), cancellable = true)
    public void addChatMessage(MessageType messageType, Text text, UUID uUID, CallbackInfo ci) {

        BlockgameExpHudConfig config = BlockgameExpHud.getConfig();

        String expTag = config.advancedSettings.EXP_CHAT_TAG;
        String coinTag = config.advancedSettings.COIN_CHAT_TAG;
        String coinQuestTag = config.advancedSettings.COIN_QUEST_CHAT_TAG;

        MessageType type = config.advancedSettings.MESSAGE_TYPE;

        boolean enabled = config.ENABLED;
        boolean hideExp = config.chatSettings.CHAT_EXP_ENABLED;
        boolean hideCoin = config.chatSettings.CHAT_COIN_ENABLED;

        if (enabled && messageType == type){
            String message = text.getString();

            if (message.endsWith(coinTag) || message.endsWith(coinQuestTag)) {
                ExpHudDataHelper.addCoin(message);
                if (hideCoin) ci.cancel();
            }

            if (message.contains(expTag)) {
                ExpHudDataHelper.addExp(message);
                if (hideExp) ci.cancel();
            }
        }

    }
}
