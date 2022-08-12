package qa.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"qa.mapper.mock"}, sqlSessionTemplateRef = "rootSqlSessionTemplate", sqlSessionFactoryRef = "rootSqlSessionFactory")
public class RootMybatisDataSourceConfig {

    @Bean("rootDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.root")
    public DataSource rootDataSource() {
        return new DruidDataSource();
    }

    @Bean("rootSqlSessionFactory")
    @Primary
    public SqlSessionFactory rootSqlSessionFactory(@Qualifier("rootDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();

        mybatisSqlSessionFactoryBean.setDataSource(dataSource);

        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        mybatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);

        mybatisSqlSessionFactoryBean.setConfiguration(mybatisConfiguration);

        mybatisSqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath*:mapper/*.xml"));
        mybatisSqlSessionFactoryBean.setGlobalConfig(new GlobalConfig().setBanner(false));

        return mybatisSqlSessionFactoryBean.getObject();
    }

    @Bean("rootSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate rootSqlSessionTemplate(@Qualifier("rootSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean("rootTransactionManager")
    @Primary
    public DataSourceTransactionManager rootTransactionManager(@Qualifier("rootDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
