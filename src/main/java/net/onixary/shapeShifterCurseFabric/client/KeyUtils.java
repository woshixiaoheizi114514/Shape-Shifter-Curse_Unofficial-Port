package net.onixary.shapeShifterCurseFabric.client;

import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class KeyUtils {
    public static HashMap<String, KeyMapping> keyBindingRegistry = new HashMap<>();
    // 选主动技能的轮盘使用 由Mixin实现
    public static @Nullable KeyMapping nowKeyBinding = null;

    public static void registerKeyBinding(String name, KeyMapping keyBinding) {
        keyBindingRegistry.put(name, keyBinding);
    }
}
