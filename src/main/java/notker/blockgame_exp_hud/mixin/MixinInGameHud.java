package notker.blockgame_exp_hud.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import notker.blockgame_exp_hud.BlockgameExpHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "addChatMessage", at = @At("HEAD"), cancellable = true)
    public void addChatMessage(MessageType messageType, Text text, UUID uUID, CallbackInfo ci) {

        if (text.getString().contains("[EXP]")) {
            String expString = text.getString();
            System.out.println("message -> " + text.getString());
            BlockgameExpHud.getInstance().addExp(expString);
            ci.cancel();
            return;
        }
    }
}
