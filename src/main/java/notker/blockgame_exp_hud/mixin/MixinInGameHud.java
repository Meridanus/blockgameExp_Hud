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
        String expTag, coinTag, coinQuestTag;
        boolean enabled, hideExp, hideCoin;
        MessageType type;

        if (config != null) {
            expTag = config.advancedSettings.EXP_CHAT_TAG;
            coinTag = config.advancedSettings.COIN_CHAT_TAG;
            enabled = config.ENABLED;
            type = config.advancedSettings.MESSAGE_TYPE;
            hideExp = config.chatSettings.CHAT_EXP_ENABLED;
            hideCoin = config.chatSettings.CHAT_COIN_ENABLED;
            coinQuestTag = config.advancedSettings.COIN_QUEST_CHAT_TAG;
        } else {
            expTag = BlockgameExpHud.DEFAULT_EXP_CHAT_TAG;
            coinTag = BlockgameExpHud.DEFAULT_COIN_CHAT_TAG;
            enabled = BlockgameExpHud.DEFAULT_ENABLED;
            type = BlockgameExpHud.DEFAULT_MESSAGE_TYPE_VALUE;
            hideExp = BlockgameExpHud.DEFAULT_CHAT_EXP_ENABLED;
            hideCoin = BlockgameExpHud.DEFAULT_CHAT_COIN_ENABLED;
            coinQuestTag = BlockgameExpHud.DEFAULT_COIN_QUEST_CHAT_TAG;
        }


        if (enabled && messageType == type){
            String message = text.getString();

            if (message.endsWith(coinTag) || message.endsWith(coinQuestTag)) {
                BlockgameExpHud.getInstance().coinValueFromString(message);
                if (hideCoin) ci.cancel();
            }

            if (message.contains(expTag)) {
                BlockgameExpHud.getInstance().addExp(message);
                if (hideExp) ci.cancel();
            }
        }

    }
}
