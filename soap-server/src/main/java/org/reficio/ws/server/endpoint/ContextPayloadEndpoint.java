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
package org.reficio.ws.server.endpoint;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.PayloadEndpoint;

import javax.xml.transform.Source;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public interface ContextPayloadEndpoint extends PayloadEndpoint {

    /**
     * Invokes the endpoint with the given request and possibly returns a response.
     * It extends the functionality of a @see org.springframework.ws.server.endpoint.PayloadEnpoint
     * by decorating the invocation of the invoke method - it get the context of the message as an argument
     * enabling the user to control the invocation in a more detailed way.
     *
     * @param messageContext the context of the message containing the request and (possible response)
     * @return the payload of the response message, may be <code>null</code> to indicate no response
     */
    Source invoke(MessageContext messageContext);

}
