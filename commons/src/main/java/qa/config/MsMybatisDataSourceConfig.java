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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"qa.mapper.ms"}, sqlSessionTemplateRef = "msSqlSessionTemplate", sqlSessionFactoryRef = "msSqlSessionFactory")
public class MsMybatisDataSourceConfig {

    @Bean("msDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.ms")
    public DataSource msDataSource() {
        return new DruidDataSource();
    }

    @Bean("msSqlSessionFactory")
    public SqlSessionFactory msSqlSessionFactory(@Qualifier("msDataSource") DataSource dataSource) throws Exception {
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

    @Bean("msSqlSessionTemplate")
    public SqlSessionTemplate msSqlSessionTemplate(@Qualifier("msSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean("msTransactionManager")
    public DataSourceTransactionManager msTransactionManager(@Qualifier("msDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


}
