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

/**
 * Created by anurag on 17/08/19.
 */
import com.ntw.common.config.EnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableDiscoveryClient
@PropertySource(value = { "classpath:config.properties" })
public class WebApplication {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @Bean
    public EnvConfig envConfig() throws Exception {
        // Added this bean to view env vars on console/log
        EnvConfig envConfig = new EnvConfig(environment);
        String configName = envConfig.getProperty("spring.config.name");
        if ( configName == null || !configName.equals("config")) {
            throw new Exception("ERROR: To read routes specify java property -Dspring.config.name=config " +
                    "or set it as an environment variable. Aborting!!!");
        }
        return envConfig;
    }

    @Bean
    @ConditionalOnProperty(value="opentracing.jaeger.enabled", havingValue="false", matchIfMissing=false)
    public io.opentracing.Tracer jaegerTracer() {
        // This bean is a workaround to avoid service name exception when jaeger tracing is disabled
        return io.opentracing.noop.NoopTracerFactory.create();
    }

}
