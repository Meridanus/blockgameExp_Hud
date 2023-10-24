package notker.blockgame_exp_hud;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import notker.blockgame_exp_hud.config.BlockgameExpHudConfig;
import notker.blockgame_exp_hud.module.EquipmentBonusTick;
import notker.blockgame_exp_hud.module.ExpHudRender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class BlockgameExpHud extends DrawableHelper implements ClientModInitializer {

    private static BlockgameExpHud instance;
    public static Logger LOGGER = LogManager.getLogger("blockgame_exp_hud");
    public static BlockgameExpHudConfig config;
    public static ConfigHolder<BlockgameExpHudConfig> configHolder;

    public static KeyBinding blockGameExpHudToggleKey;
    public static KeyBinding blockGameExpHudSwitchKey;


    @Override
    public void onInitializeClient() {

        if (instance == null) instance = this;

        configHolder = AutoConfig.register(BlockgameExpHudConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(BlockgameExpHudConfig.class).getConfig();

        LOGGER.info("Config Loaded");

        blockGameExpHudToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.blockgame_exp_hud.toggle_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "Blockgame Exp Hud"));
        blockGameExpHudSwitchKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.blockgame_exp_hud.switch", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_HOME, "Blockgame Exp Hud"));


        //Register Tick Callback
        ClientTickEvents.END_CLIENT_TICK.register(EquipmentBonusTick::tick);
        //Register HudRender Callback
        HudRenderCallback.EVENT.register(ExpHudRender::onHudRender);

    }

    public static BlockgameExpHudConfig getConfig() { return config; }

    public static BlockgameExpHud getInstance() {
        return instance;
    }

}