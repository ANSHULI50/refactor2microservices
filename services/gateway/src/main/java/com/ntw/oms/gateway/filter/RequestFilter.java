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

import com.google.gson.Gson;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by anurag on 01/07/19.
 */
public class RequestFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        logger.debug("Request pre filter called");

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        String body = null;
        try {
            body = IOUtils.toString(request.getReader());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // context.set("any param for other filters", param value);

        Map<String, String> requestMap = new HashMap();
        requestMap.put("Uri", request.getRequestURI());
        requestMap.put("Url", request.getRequestURL().toString());
        requestMap.put("Protocol", request.getProtocol());
        requestMap.put("AuthHeader", request.getHeader("Authorization"));
        requestMap.put("Method", request.getMethod());
        requestMap.put("QueryString", request.getQueryString());
        requestMap.put("Body", body);
        requestMap.put("ContentType", request.getHeader("Content-Type"));

        logger.info("Request pre filter executed; route={}", (new Gson()).toJson(requestMap));

        return null;
    }

}