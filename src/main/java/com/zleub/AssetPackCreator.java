package com.zleub;

import com.hypixel.hytale.common.plugin.AuthorInfo;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.common.semver.Semver;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.ui.browser.AssetPackSaveBrowser;
import com.hypixel.hytale.server.core.util.BsonUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static com.zleub.ExamplePlugin.LOGGER;

public final class AssetPackCreator {

    private static final Pattern INVALID_FILENAME_CHARS =
            Pattern.compile("[\\\\/:*?\"<>|.]");

    AssetPackCreator() {
    }

    @Nonnull
    static private List<AssetPackSaveBrowser.ModsDirectory> collectModsDirectories() {
        ObjectArrayList<AssetPackSaveBrowser.ModsDirectory> dirs = new ObjectArrayList<>();
        if (Constants.SINGLEPLAYER) {
            dirs.add(
                    new AssetPackSaveBrowser.ModsDirectory(
                            "server.customUI.assetPackBrowser.create.targetDir.world", "server.customUI.assetPackBrowser.filter.world", PluginManager.MODS_PATH
                    )
            );

            for (Path modsPath : Options.getOptionSet().valuesOf(Options.MODS_DIRECTORIES)) {
                dirs.add(
                        new AssetPackSaveBrowser.ModsDirectory(
                                "server.customUI.assetPackBrowser.create.targetDir.global", "server.customUI.assetPackBrowser.filter.global", modsPath
                        )
                );
            }
        } else {
            dirs.add(
                    new AssetPackSaveBrowser.ModsDirectory(
                            "server.customUI.assetPackBrowser.create.targetDir.server", "server.customUI.assetPackBrowser.filter.server", PluginManager.MODS_PATH
                    )
            );

            for (Path modsPath : Options.getOptionSet().valuesOf(Options.MODS_DIRECTORIES)) {
                dirs.add(
                        new AssetPackSaveBrowser.ModsDirectory(
                                "server.customUI.assetPackBrowser.create.targetDir.server", "server.customUI.assetPackBrowser.filter.server", modsPath
                        )
                );
            }
        }

        return dirs;
    }

    @Nonnull
    static private Path getDefaultTargetDirectory() {
        if (Constants.SINGLEPLAYER) {
            List<Path> cliDirs = Options.getOptionSet().valuesOf(Options.MODS_DIRECTORIES);
            if (!cliDirs.isEmpty()) {
                return cliDirs.getFirst();
            }
        }

        return PluginManager.MODS_PATH;
    }

    @Nullable
    static private Path resolveTargetDirectory(@Nullable String pathStr) {
        if (pathStr != null && !pathStr.isBlank()) {
            Path d = Path.of(pathStr).normalize();

            for (AssetPackSaveBrowser.ModsDirectory dir : collectModsDirectories()) {
                if (dir.path().normalize().equals(d)) {
                    return dir.path();
                }
            }

            return null;
        } else {
            return getDefaultTargetDirectory();
        }
    }

    @Nonnull
    static public void createAssetPack(
            @Nonnull String name,
            @Nonnull String group,
            @Nonnull String description,
            @Nonnull String version,
            @Nonnull String website,
            @Nonnull String authorName,
            @Nonnull String targetDirectory
    ) throws AssetPackCreatorError {
        if (name.isBlank()) {
            throw new AssetPackCreatorError("Name is required to not be blank");
        }

        if (group.isBlank()) {
            throw new AssetPackCreatorError("Group is required to not be blank");

        }

        PluginManifest manifest = new PluginManifest();
        manifest.setName(name.trim());
        manifest.setGroup(group.trim());

        if (!description.isBlank()) {
            manifest.setDescription(description.trim());
        }

        if (!version.isBlank()) {
            try {
                manifest.setVersion(Semver.fromString(version.trim()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (!website.isBlank()) {
            manifest.setWebsite(website.trim());
        }

        if (!authorName.isBlank()) {
            AuthorInfo author = new AuthorInfo();
            author.setName(authorName.trim());
            manifest.setAuthors(List.of(author));
        }

        String packId = new PluginIdentifier(manifest).toString();

        if (AssetModule.get().getAssetPack(packId) != null) {
            throw new AssetPackCreatorError("AssetPack %s already exists".formatted(packId));
        }

        String dirName = INVALID_FILENAME_CHARS
                .matcher(group.trim() + "." + name.trim())
                .replaceAll("");

        Path normalized = Path.of(dirName).normalize();
        if (dirName.isEmpty() || normalized.toString().isEmpty()) {
            throw new AssetPackCreatorError("Group is malformed");
        }

        Path modsPath = resolveTargetDirectory(targetDirectory);
        Path packPath = modsPath.resolve(normalized).normalize();

        if (!packPath.startsWith(modsPath)) {
            throw new AssetPackCreatorError("packPath <%s> should start with <%s>".formatted(packPath, modsPath));
        }

        if (Files.exists(packPath)) {
            throw new AssetPackCreatorError("AssetPack %s already exists".formatted(packPath));
        }

        try {
            Files.createDirectories(packPath);

            Path manifestPath = packPath.resolve("manifest.json");
            BsonUtil.writeSync(manifestPath, PluginManifest.CODEC, manifest, HytaleLogger.getLogger());

                try {
                    HytaleServerConfig serverConfig = HytaleServer.get().getConfig();
                    HytaleServerConfig.setBoot(serverConfig, new PluginIdentifier(manifest), true);
                    serverConfig.markChanged();
                    if (serverConfig.consumeHasChanged()) {
                        HytaleServerConfig.save(serverConfig).join();
                    }
                    AssetModule.get().registerPack(packId, packPath, manifest, true);
                    LOGGER.info("Created new asset pack: %s at %s".formatted(packId, packPath));
                } catch (Exception e) {
                    LOGGER.severe("Post-create registration failed for asset pack %s".formatted(packId));
                    LOGGER.severe(e.getMessage());
                }
        } catch (IOException e) {
            LOGGER.severe("Failed to create asset pack %s".formatted(packId));
            LOGGER.severe(e.getMessage());
        }
    }

    public static class AssetPackCreatorError extends Exception {
        public AssetPackCreatorError(String s) { super(s); }
    }
}