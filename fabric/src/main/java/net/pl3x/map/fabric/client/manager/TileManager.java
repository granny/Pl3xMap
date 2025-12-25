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
package net.pl3x.map.fabric.client.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileDownload;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import net.minecraft.util.Util;
import net.pl3x.map.core.scheduler.Task;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.util.TickUtil;
import net.pl3x.map.fabric.client.Pl3xMapFabricClient;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TileManager {
    private static final BufferedImage EMPTY_IMAGE = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);

    private final Map<String, LoadingCache<Long, BufferedImage>> tiles = new ConcurrentHashMap<>();
    private final Pl3xMapFabricClient mod;

    private Task task;

    public TileManager(Pl3xMapFabricClient mod) {
        this.mod = mod;
    }

    public void initialize() {
        // remove any old tasks (just in case)
        if (this.task != null) {
            this.task.cancel();
        }
        // update once next tick
        this.mod.getScheduler().addTask(0, this::update);
        // setup repeating task to update every 5 seconds
        this.task = new Task(TickUtil.toTicks(5), true) {
            @Override
            public void run() {
                update();
            }
        };
        // schedule task
        this.mod.getScheduler().addTask(this.task);
    }

    public BufferedImage get(String world, int regionX, int regionZ) {
        try {
            return this.tiles.computeIfAbsent(world, k -> Loader.create(this.mod, world)).get(Mathf.asLong(regionX, regionZ));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        this.tiles.forEach((world, cache) -> cache.invalidateAll());
        this.tiles.clear();
    }

    public void update() {
        if (RenderSystem.isOnRenderThread()) {
            this.mod.getExecutor().submit(this::update);
            return;
        }
        for (Map.Entry<String, LoadingCache<Long, BufferedImage>> entry : this.tiles.entrySet()) {
            LoadingCache<Long, BufferedImage> cache = entry.getValue();
            Collections.unmodifiableCollection(cache.asMap().keySet()).forEach(cache::refresh);
        }
        this.mod.updateAllMapTextures();
    }

    private static class Loader extends CacheLoader<Long, BufferedImage> {
        private static LoadingCache<Long, BufferedImage> create(Pl3xMapFabricClient mod, String world) {
            return CacheBuilder.newBuilder().maximumSize(100).build(new Loader(mod, world));
        }

        private final Pl3xMapFabricClient mod;
        private final String world;

        private Loader(Pl3xMapFabricClient mod, String world) {
            this.mod = mod;
            this.world = world;
        }

        @Override
        public BufferedImage load(Long region) {
            if (this.mod.getServerUrl() == null) {
                return EMPTY_IMAGE;
            }

            String url = String.format("%s/tiles/%s/%d/%s/%d_%d.png",
                    this.mod.getServerUrl(),
                    this.world,
                    0,
                    "vintage_story",
                    Mathf.longToX(region),
                    Mathf.longToZ(region)
            );

            BufferedImage image = null;
            try (HttpClient httpClient = createClient()) {
                HttpResponse<InputStream> httpResponse = httpClient.send(createRequest(url).GET().build(), HttpResponse.BodyHandlers.ofInputStream());
                image = ImageIO.read(httpResponse.body());
            } catch (IOException ignore) {
            } catch (InterruptedException ignore) {
            }

            return image == null ? EMPTY_IMAGE : image;
        }
    }

    private static HttpClient createClient() {
        return HttpClient.newBuilder().executor(Util.ioPool()).connectTimeout(Duration.ofMinutes(2L)).build();
    }

    private static HttpRequest.Builder createRequest(String string) {
        return HttpRequest.newBuilder(URI.create(string)).timeout(Duration.ofMinutes(2L));
    }
}
