package me.gavin.rcefix;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

public class RCECoreMod implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { ASMTransformer.class.getName() };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        boolean isObfuscated = (boolean) data.get("runtimeDeobfuscationEnabled");
        ASMTransformer.isObfuscated = isObfuscated;
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
