package com.mlcloud.fusioncloud.ws;

import org.kaazing.net.ws.WsURLConnection;
import org.kaazing.net.ws.impl.WsURLConnectionImpl;
import org.kaazing.net.ws.impl.spi.WebSocketExtensionFactorySpi;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Map;

/**
 * @author ：hf
 * @date ：Created in 2020/12/8 5:41 下午
 * @description：
 * @modified By：
 * @version: $
 */
public class WsURLStreamHandlerImpl extends URLStreamHandler {

    private final Map<String, WebSocketExtensionFactorySpi> extensionFactories;

    private String scheme;

    public WsURLStreamHandlerImpl(Map<String, WebSocketExtensionFactorySpi> extensionFactories) {
        this.extensionFactories = extensionFactories;
    }

    @Override
    protected int getDefaultPort() {
        return 80;
    }

    @Override
    protected WsURLConnection openConnection(URL location) throws IOException {
        return new WsURLConnectionImpl(location, extensionFactories);
    }

    @Override
    protected void parseURL(URL location, String spec, int start, int limit) {
        scheme = spec.substring(0, spec.indexOf("://"));

        URI specUri = getSpecUri(spec);
        String host = specUri.getHost();
        int    port = specUri.getPort();
        String authority = specUri.getAuthority();
        String userInfo = specUri.getUserInfo();
        String path = specUri.getPath();
        String query = specUri.getQuery();

        setURL(location, scheme, host, port, authority, userInfo, path, query, null);
    }

    // This method is no longer needed as we are not supporting 'java:'
    // prefixes. Keeping it for time being.

    /**
     * This method is no longer needed as we are not supporting 'java:'
     * prefixes. Keeping it for time being.
     * @param location
     * @return
     */
    @Override
    protected String toExternalForm(URL location) {
        return super.toExternalForm(location);
    }

    // ----------------- Private Methods -----------------------------------
    // Creates a URI that can be used to retrieve various parts such as host,
    // port, etc. Based on whether the scheme includes ':', the method returns
    // the appropriate URI that can be used to retrieve the needed parts.

    /**
     * / ----------------- Private Methods -----------------------------------
     *  Creates a URI that can be used to retrieve various parts such as host,
     *  port, etc. Based on whether the scheme includes ':', the method returns
     *  the appropriate URI that can be used to retrieve the needed parts.
     * @param spec
     * @return
     */
    private URI getSpecUri(String spec) {
        URI specUri = URI.create(spec);

        if (scheme.indexOf(':') == -1) {
            return specUri;
        }

        String schemeSpecificPart = specUri.getSchemeSpecificPart();
        return URI.create(schemeSpecificPart);
    }
}
