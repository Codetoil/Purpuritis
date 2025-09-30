package io.codetoil.purpuritis.data;

import com.mojang.serialization.Codec;
import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public record DataProviderPurpuritis(PackOutput output,
                                     CompletableFuture<HolderLookup.Provider> registries)
        implements DataProvider {

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        Path path = this.output.getOutputFolder(PackOutput.Target.REPORTS)
                .resolve("purpuritis.txt");
        return this.registries.thenCompose(provider -> {
            String data = "LOGGER=" +
                    Purpuritis.LOGGER +
                    "\npurpuredItems=" +
                    Purpuritis.purpuredItems +
                    "\npurpuredBlocks=" +
                    Purpuritis.purpuredBlocks +
                    "\npurpuredDimensionTypes=" +
                    Purpuritis.purpuredDimensionTypes;
            Purpuritis.LOGGER.info("Finished gathering data");
            return DataProvider.saveStable(cachedOutput, Codec.STRING, data, path);
        });
    }

    @Override
    public @NotNull String getName() {
        return "Purpuritis' Data Provider";
    }

    @Override
    public String toString() {
        return getName() + ":" + this.hashCode();
    }


}
