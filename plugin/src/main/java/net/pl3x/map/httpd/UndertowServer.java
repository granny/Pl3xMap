package net.pl3x.map.httpd;

import io.undertow.Undertow;
import io.undertow.UndertowLogger;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.util.ETag;
import io.undertow.util.Headers;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import net.pl3x.map.api.httpd.IntegratedServer;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.logger.LogFilter;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.world.MapWorld;

public class UndertowServer implements IntegratedServer {
    private Undertow server;

    public void startServer() {
        if (!Config.HTTPD_ENABLED) {
            Logger.info(Lang.HTTPD_DISABLED);
            return;
        }

        try {
            ResourceManager resourceManager = PathResourceManager.builder()
                    .setBase(Paths.get(MapWorld.WEB_DIR.toFile().getAbsolutePath()))
                    .setETagFunction((path) -> {
                        try {
                            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
                            long time = attr.lastModifiedTime().toMillis();
                            return new ETag(false, Long.toString(time));
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .build();
            ResourceHandler resourceHandler = new ResourceHandler(resourceManager, exchange -> {
                String url = exchange.getRelativePath();
                if (url.startsWith("/tiles") && (url.endsWith(".png") || url.endsWith(".gz"))) {
                    exchange.setStatusCode(200);
                    return;
                }
                exchange.setStatusCode(404);
                if (UndertowLogger.PREDICATE_LOGGER.isDebugEnabled()) {
                    UndertowLogger.PREDICATE_LOGGER.debugf("Response code set to [%s] for %s.", 404, exchange);
                }
            });

            LogFilter.HIDE_UNDERTOW_LOGS = true;
            this.server = Undertow.builder()
                    .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                    .addHttpListener(Config.HTTPD_PORT, Config.HTTPD_BIND)
                    .setHandler(exchange -> {
                        if (exchange.getRelativePath().startsWith("/tiles")) {
                            exchange.getResponseHeaders().put(Headers.CACHE_CONTROL, "max-age=0, must-revalidate, no-cache");
                        }
                        if (exchange.getRelativePath().endsWith(".gz")) {
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                            exchange.getResponseHeaders().put(Headers.CONTENT_ENCODING, "gzip");
                        }
                        resourceHandler.handleRequest(exchange);
                    })
                    .build();
            this.server.start();
            LogFilter.HIDE_UNDERTOW_LOGS = false;

            Logger.info(Lang.HTTPD_STARTED
                    .replace("<bind>", Config.HTTPD_BIND)
                    .replace("<port>", Integer.toString(Config.HTTPD_PORT))
            );
        } catch (Exception e) {
            this.server = null;
            Logger.severe(Lang.HTTPD_START_ERROR);
            e.printStackTrace();
        }
    }

    public void stopServer() {
        if (!Config.HTTPD_ENABLED) {
            return;
        }

        if (this.server == null) {
            Logger.warn(Lang.HTTPD_STOP_ERROR);
            return;
        }

        LogFilter.HIDE_UNDERTOW_LOGS = true;
        this.server.stop();
        LogFilter.HIDE_UNDERTOW_LOGS = false;

        this.server = null;
        Logger.info(Lang.HTTPD_STOPPED);
    }
}
