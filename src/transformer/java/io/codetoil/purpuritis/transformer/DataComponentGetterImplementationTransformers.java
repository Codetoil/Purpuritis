package io.codetoil.purpuritis.transformer;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.MethodNode;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class DataComponentGetterImplementationTransformers implements ITransformer<MethodNode> {
    @Override
    public @NotNull MethodNode transform(MethodNode input, ITransformerVotingContext context) {
        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        var result = DataComponentGetterImplementationLocator.getDataComponentGetterSetPerMod().values()
                .stream().flatMap(Set::stream).map(Path::toString)
                .map(path -> Target.targetMethod(path, FMLLoader.isProduction() ? "m_318834_" : "get",
                        "(L" +
                                (FMLLoader.isProduction() ? "ko" : "net/minecraft/core/component/DataComponentType")
                                + ";)Ljava/lang/Object;"))
                .collect(Collectors.toSet());
        return result;
    }
}
