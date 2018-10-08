/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.reficio.ws.test;

import com.google.common.base.Preconditions;
import org.reficio.ws.SoapContext;
import org.reficio.ws.SoapException;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.common.ResourceUtils;
import org.reficio.ws.server.core.SoapServer;
import org.reficio.ws.server.responder.AutoResponder;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p></p>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
public class ServerProcessor {

    private SoapServer server;

    private final String wsdl;
    private final String binding;
    private final String path;
    private final int port;
    private final Class testClass;

    public ServerProcessor(org.reficio.ws.test.junit.Server server, Class testClass) {
        this.wsdl = processUrl(server.wsdl());
        this.binding = server.binding();
        this.path = server.path();
        this.port = server.port();
        this.testClass = testClass;
    }

    public ServerProcessor(org.reficio.ws.test.spock.Server server, Class testClass) {
        this.wsdl = processUrl(server.wsdl());
        this.binding = server.binding();
        this.path = server.path();
        this.port = server.port();
        this.testClass = testClass;
    }

    private String processUrl(String wsdlUrl) {
        if(wsdlUrl == null) {
            return null;
        }
        if(wsdlUrl.startsWith("classpath:")) {
            return ResourceUtils.getResource(wsdlUrl.replace("classpath:","")).toString();
        } else {
            return wsdlUrl;
        }
    }

    public SoapServer initServer() {
        validate();
        URL wsdlUrl = getWsdlUrl(testClass);
        Wsdl parser = Wsdl.parse(wsdlUrl);
        SoapBuilder builder = getBuilder(parser);
        server = construct();
        AutoResponder responder = getAutoResponder(builder);
        registerService(server, responder);

        server.start();
        return server;
    }

    public void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    private SoapServer construct() {
        return SoapServer.builder()
                .httpPort(port)
                .build();
    }

    private void registerService(SoapServer server, AutoResponder responder) {
        server.registerRequestResponder(path, responder);
    }

    private AutoResponder getAutoResponder(SoapBuilder builder) {
        SoapContext context = SoapContext.builder()
                .exampleContent(true)
                .buildOptional(true)
                .alwaysBuildHeaders(true)
                .build();
        return new AutoResponder(builder, context);
    }

    private SoapBuilder getBuilder(Wsdl parser) {
        SoapBuilder builder = null;
        try {
            builder = parser.binding().name(binding).find();
        } catch (SoapException ex) {
            // ignore
        }
        if (builder == null) {
            builder = parser.binding().localPart(binding).find();
        }
        Preconditions.checkNotNull(builder, "Binding not found");
        return builder;
    }

    private URL getWsdlUrl(Class testClass) {
        URL wsdlUrl = null;
        try {
            wsdlUrl = ResourceUtils.getResource(testClass, wsdl);
        } catch (IllegalArgumentException ex) {
            // ignore
        }
        if (wsdlUrl == null) {
            try {
                wsdlUrl = new URL(wsdl);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Wrong wsdl url", e);
            }
        }
        return wsdlUrl;
    }

    private void validate() {
        Preconditions.checkNotNull(wsdl, "Wsdl url cannot be null");
        Preconditions.checkNotNull(binding, "Binding name cannot be null");
        Preconditions.checkArgument(port >= 0 && port < 65535, "Port has to be in range [0, 655535]");
    }

}
