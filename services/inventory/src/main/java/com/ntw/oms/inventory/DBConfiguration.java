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

package com.ntw.oms.inventory;

import com.datastax.oss.driver.api.core.CqlSession;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by anurag on 28/07/20.
 */
@Configuration
@PropertySource(value = { "classpath:config.properties" })
public class DBConfiguration {

    @Autowired
    private Environment environment;

    protected String getKeyspaceName() {
        return environment.getProperty("database.cassandra.keySpace");
    }

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "CQL")
    public CqlSessionFactoryBean session() {
        CqlSessionFactoryBean session = new CqlSessionFactoryBean();
        session.setContactPoints(environment.getProperty("database.cassandra.hosts"));
        session.setKeyspaceName(getKeyspaceName());
        session.setLocalDatacenter("datacenter1");
        return session;
    }

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "CQL")
    public CassandraMappingContext mappingContext(CqlSession cqlSession) {
        CassandraMappingContext mappingContext =  new CassandraMappingContext();
        mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cqlSession));
        return mappingContext;
    }

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "CQL")
    public CassandraConverter converter(CassandraMappingContext mappingContext) {
        return new MappingCassandraConverter(mappingContext);
    }

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "CQL")
    public CassandraOperations cassandraTemplate(CqlSession cqlSession) throws Exception {
        return new CassandraTemplate(cqlSession);
    }

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "CQL")
    public CqlTemplate cqlTemplate(CqlSession cqlSession) throws Exception {
        return new CqlTemplate(cqlSession);
    }

    private String getDriverClass() {
        return "org.postgresql.Driver";
    }

    private String getPostgresUrl() {
        String host = environment.getProperty("database.postgres.host");
        String port = environment.getProperty("database.postgres.port");
        String database = environment.getProperty("database.postgres.schema");
        return "jdbc:postgresql://"+host+":"+port+"/"+database;
    }

    public String getUser() {
        return environment.getProperty("database.postgres.user.name");
    }

    public String getPassword() {
        return environment.getProperty("database.postgres.user.password");
    }

    public int getInitialPoolSize() {
        return Integer.parseInt(environment.getProperty("database.postgres.cp.size.min"));
    }

    public int getMaxPoolSize() {
        return Integer.parseInt(environment.getProperty("database.postgres.cp.size.max"));
    }

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "SQL")
    public DataSource postgresInvDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(getDriverClass());
        dataSource.setUrl(getPostgresUrl());
        dataSource.setUsername(getUser());
        dataSource.setPassword(getPassword());
        dataSource.setInitialSize(getInitialPoolSize());
        dataSource.setMaxTotal(getMaxPoolSize());
        return dataSource;
    }

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "SQL")
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}

