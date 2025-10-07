package io.codetoil.purpuritis.transformer;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassVisitor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DataComponentGetterImplementationLocator implements IDependencyLocator {
    @Override
    public List<IModFile> scanMods(Iterable<IModFile> loadedMods) {
        var result = Streams.stream(loadedMods).filter(Objects::nonNull).map(modFile ->
                        Pair.of(modFile, modFile.getSecureJar().getPackages().stream().map((packageDir) -> {
                            try (var filesInModStream = Files.find(modFile.getSecureJar()
                                            .getPath(packageDir.replace(".", "/")), 1,
                                    (path, basicFileAttributes) ->
                                            basicFileAttributes.isRegularFile())) {
                                return filesInModStream.filter(Files::exists).collect(Collectors.toSet());
                            } catch (IOException e) {
                                LogManager.getLogger().error(e);
                                return Set.<Path>of();
                            }
                        }).reduce(Sets::union)))
                .filter(pair -> pair.getRight().isPresent())
                .map(pair -> Pair.of(pair.getLeft(), (Set<Path>) pair.getRight().get()))
                .map(pair ->
                        Pair.of(pair.getLeft(), pair.getRight().stream().filter((path) -> {
                            ByteBuffer buffer;
                            try (SeekableByteChannel channel =
                                         path.getFileSystem().provider().newByteChannel(path, Set.of())) {
                                if (!channel.isOpen())
                                {
                                    throw new IllegalStateException("ByteChannel for " +
                                            pair.getLeft() + " is closed");
                                }
                                buffer = ByteBuffer.allocate((int) channel.size());
                                channel.read(buffer);
                            } catch (IOException e)
                            {
                                LogManager.getLogger().error(e);
                                return false;
                            }
                            buffer.flip();
                            return false;
                        }).collect(Collectors.toSet())))
                .filter(pair -> !pair.getRight().isEmpty())
                .map(Pair::getLeft)
                .toList();
        return result;
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
