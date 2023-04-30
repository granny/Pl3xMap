/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.platform.forge;

import com.google.gson.JsonElement;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeServerAudiences implements AudienceProvider {
    private final MinecraftServer server;

    public ForgeServerAudiences(@NonNull MinecraftServer server) {
        this.server = server;
    }

    private net.minecraft.network.chat.@NonNull Component toNative(@NonNull Component component) {
        JsonElement tree = GsonComponentSerializer.gson().serializeToTree(component);
        return Objects.requireNonNull(net.minecraft.network.chat.Component.Serializer.fromJson(tree));
    }

    public @NonNull Audience audience(@NonNull CommandSource source) {
        if (source instanceof MinecraftServer) {
            return audience(this.server.createCommandSourceStack());
        } else if (source instanceof ServerPlayer player) {
            return audience(player.createCommandSourceStack());
        } else {
            return Audience.empty();
        }
    }

    public @NonNull Audience audience(@NonNull CommandSourceStack stack) {
        return new Audience() {
            @Override
            @SuppressWarnings({"UnstableApiUsage", "deprecation"})
            public void sendMessage(@NonNull Identity identity, @NonNull Component text, @NonNull MessageType type) {
                stack.sendSystemMessage(toNative(text));
            }
        };
    }

    @Override
    public @NonNull Audience all() {
        return Audience.audience(console(), players());
    }

    @Override
    public @NonNull Audience console() {
        return audience(this.server);
    }

    @Override
    public @NonNull Audience players() {
        return Audience.audience(this.server.getPlayerList().getPlayers().stream().map(this::audience).collect(Collectors.toList()));
    }

    @Override
    public @NonNull Audience player(@NonNull UUID uuid) {
        ServerPlayer player = this.server.getPlayerList().getPlayer(uuid);
        return player != null ? audience(player) : Audience.empty();
    }

    @Override
    public @NonNull Audience permission(@NonNull String permission) {
        return Audience.empty();
    }

    @Override
    public @NonNull Audience world(@NonNull Key world) {
        return Audience.empty();
    }

    @Override
    public @NonNull Audience server(@NonNull String serverName) {
        return Audience.empty();
    }

    @Override
    public @NonNull ComponentFlattener flattener() {
        throw new NotImplementedException();
    }

    @Override
    public void close() {
    }
}
