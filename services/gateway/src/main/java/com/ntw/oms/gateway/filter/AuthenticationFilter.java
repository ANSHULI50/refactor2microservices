//////////////////////////////////////////////////////////////////////////////
// Copyright 2020 Anurag Yadav (anurag.yadav@newtechways.com)               //
//                                                                          //
// Licensed under the Apache License, Version 2.0 (the "License");          //
// you may not use this file except in compliance with the License.         //
// You may obtain a copy of the License at                                  //
//                                                                          //
//     http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                          //
// Unless required by applicable law or agreed to in writing, software      //
// distributed under the License is distributed on an "AS IS" BASIS,        //
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. //
// See the License for the specific language governing permissions and      //
// limitations under the License.                                           //
//////////////////////////////////////////////////////////////////////////////

package com.ntw.oms.gateway.filter;

import com.ntw.common.entity.UserAuth;
import com.ntw.common.security.AppAuthentication;
import com.ntw.common.security.JJwtUtility;
import com.ntw.common.security.JwtUtility;
import com.ntw.common.security.Secured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;

/**
 * Created by anurag on 24/03/17.
 */

@Secured
public class AuthenticationFilter {

    private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Bean("AuthenticationFilter")
    @Order(1)
    public GlobalFilter requestResponseFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (path.startsWith("/auth/token") ||
                    path.startsWith("/status") ||
                    path.startsWith("/admin/status") ||
                    path.startsWith("/actuator/prometheus") ||
                    HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
                return chain.filter(exchange);
            }
            ServerHttpResponse response = exchange.getResponse();
            String authHeader = exchange.getRequest().getHeaders().toSingleValueMap().get("Authorization");
            if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer")) {
                response.setRawStatusCode(HttpStatus.UNAUTHORIZED.value());
                return response.setComplete();
            }
            String authToken = authHeader.substring("Bearer".length()).trim();
            JwtUtility jwtUtility = new JJwtUtility();
            UserAuth userAuth = jwtUtility.parseToken(authToken);
            if (userAuth == null) {
                logger.info("Unable to find user with auth header {}", authHeader);
                return null;
            }
            if (userAuth == null) {
                response.setRawStatusCode(HttpStatus.FORBIDDEN.value());
                return response.setComplete();
            }
            // ToDo: appAuthentication object unused
            AppAuthentication appAuthentication = new AppAuthentication(userAuth, true, authHeader);
            appAuthentication.setAuthenticated(true);

            return chain.filter(exchange);
        };
    }

}

