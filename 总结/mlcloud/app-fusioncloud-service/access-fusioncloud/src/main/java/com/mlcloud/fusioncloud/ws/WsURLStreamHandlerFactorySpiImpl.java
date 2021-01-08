package com.mlcloud.fusioncloud.ws;


import org.kaazing.net.URLStreamHandlerFactorySpi;
import org.kaazing.net.ws.impl.spi.WebSocketExtensionFactorySpi;

import java.net.URLStreamHandler;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * @author ：hf
 * @date ：Created in 2020/12/8 5:40 下午
 * @modified By：
 * @version: $
 */
public class WsURLStreamHandlerFactorySpiImpl extends URLStreamHandlerFactorySpi {

    private static final Collection<String> SUPPORTED_PROTOCOLS = unmodifiableList(asList("ws", "wse", "wsn"));
    private static final Map<String, WebSocketExtensionFactorySpi> EXTENSION_FACTORIES;

    static {
        Class<WebSocketExtensionFactorySpi> clazz = WebSocketExtensionFactorySpi.class;
        ServiceLoader<WebSocketExtensionFactorySpi> loader = ServiceLoader.load(clazz);
        Map<String, WebSocketExtensionFactorySpi> factories = new HashMap<>();

        for (WebSocketExtensionFactorySpi factory: loader) {
            String extensionName = factory.getExtensionName();

            if (extensionName != null)
            {
                factories.put(extensionName, factory);
            }
        }
        EXTENSION_FACTORIES = unmodifiableMap(factories);
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (!SUPPORTED_PROTOCOLS.contains(protocol)) {
            //
            // http https file jar protocol use jre default url stream handler.
            //
            return null;
        }

        return new WsURLStreamHandlerImpl(EXTENSION_FACTORIES);
    }

    @Override
    public Collection<String> getSupportedProtocols() {
        return SUPPORTED_PROTOCOLS;
    }

    // ----------------- Package Private Methods -----------------------------
    Map<String, WebSocketExtensionFactorySpi> getExtensionFactories() {
        return EXTENSION_FACTORIES;
    }


    // ----------------- Private Methods -------------------------------------
}
