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

package com.ntw.oms.gateway;

import com.google.gson.Gson;
import com.ntw.common.config.ServiceID;
import com.ntw.common.status.ServiceAgent;
import com.ntw.common.status.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by anurag on 21/08/19.
 */
@RestController
public class LocalRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(LocalRequestHandler.class);

    @GetMapping(path = "/status", produces = MediaType.TEXT_PLAIN_VALUE)
    private ResponseEntity<String> getServiceStatus() {
        logger.debug("Status request received");
        ServiceStatus status = ServiceAgent.getServiceStatus(ServiceID.GatewaySvc);
        logger.debug("Status request response is {}",status);
        String statusStr = new Gson().toJson(status);
        return ResponseEntity.ok(statusStr);
    }

}
