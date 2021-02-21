package com.alkaid.servicea.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = com.alkaid.servicea.config.DataSourceConfig.PACKAGE, sqlSessionFactoryRef = "demoSqlSessionFactory")
public class DataSourceConfig {
    static final String PACKAGE = "com.alkaid.servicea.dao.mapper";    // 放XXXMapper.java类的包
    static final String MAPPER_LOCATION = "classpath:mapping/*.xml";        // 放XXXMapper.xml配置的包

    @Value("${spring.datasource.url}")
    private String dbName1Url;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;

    @Primary
    @Qualifier("demoDataSource")
    @Bean(name = "demoDataSource")
    public DataSource dbName1DataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driverClass);
        druidDataSource.setUrl(dbName1Url);
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(password);
        // TODO 配置的druid参数
        return druidDataSource;
    }

    @Bean(name = "demoTransactionManager")
    public DataSourceTransactionManager dbName1TransactionManager() {
        return new DataSourceTransactionManager(dbName1DataSource());
    }

    @Primary
    @Bean(name = "demoSqlSessionFactory")
    public SqlSessionFactory clusterSqlSessionFactory(@Qualifier("demoDataSource") DataSource pgDataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(pgDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                                                  .getResources(com.alkaid.servicea.config.DataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }

}
