package net.pl3x.map.httpd;

import io.undertow.Undertow;
import io.undertow.UndertowLogger;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.util.ETag;
import io.undertow.util.Headers;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.logger.LogFilter;
import net.pl3x.map.logger.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class IntegratedServer {
    private Undertow server;

    public void startServer() {
        if (!Config.HTTPD_ENABLED) {
            Logger.info(Lang.HTTPD_DISABLED);
            return;
        }

        try {
            ResourceManager resourceManager = PathResourceManager.builder()
                    .setBase(Paths.get(FileUtil.WEB_DIR.toFile().getAbsolutePath()))
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
                if (url.startsWith("/tiles") && url.endsWith(".png")) {
                    exchange.setStatusCode(200);
                    return;
                }
                exchange.setStatusCode(404);
                if (UndertowLogger.PREDICATE_LOGGER.isDebugEnabled()) {
                    UndertowLogger.PREDICATE_LOGGER.debugf("Response code set to [%s] for %s.", 404, exchange);
                }
            });

            LogFilter.ENABLED = true;
            server = Undertow.builder()
                    .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                    .addHttpListener(Config.HTTPD_PORT, Config.HTTPD_BIND)
                    .setHandler(exchange -> {
                        if (exchange.getRelativePath().startsWith("/tiles")) {
                            exchange.getResponseHeaders().put(Headers.CACHE_CONTROL, "max-age=0, must-revalidate, no-cache");
                        }
                        resourceHandler.handleRequest(exchange);
                    })
                    .build();
            server.start();
            LogFilter.ENABLED = false;

            Logger.info(Lang.HTTPD_STARTED
                    .replace("<bind>", Config.HTTPD_BIND)
                    .replace("<port>", Integer.toString(Config.HTTPD_PORT))
            );
        } catch (Exception e) {
            server = null;
            Logger.severe(Lang.HTTPD_START_ERROR, e);
        }
    }

    public void stopServer() {
        if (!Config.HTTPD_ENABLED) {
            return;
        }

        if (server == null) {
            Logger.warn(Lang.HTTPD_STOP_ERROR);
            return;
        }

        LogFilter.ENABLED = true;
        server.stop();
        LogFilter.ENABLED = false;

        server = null;
        Logger.info(Lang.HTTPD_STOPPED);
    }
}
