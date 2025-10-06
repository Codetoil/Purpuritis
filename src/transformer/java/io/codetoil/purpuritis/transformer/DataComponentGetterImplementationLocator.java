package io.codetoil.purpuritis.transformer;

import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DataComponentGetterImplementationLocator implements IDependencyLocator {
    @Override
    public List<IModFile> scanMods(Iterable<IModFile> loadedMods) {
        return List.of();
    }

    @Override
    public String name() {
        return "Data Component Getter Implementation Locator";
    }

    @Override
    public void scanFile(IModFile modFile, Consumer<Path> pathConsumer) {

    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }

    @Override
    public boolean isValid(IModFile modFile) {
        return false;
    }
}
