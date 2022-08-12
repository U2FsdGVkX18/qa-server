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

/**
 * master qa-server database config
 * qa-server主数据源配置
 * 扫描主包是mapper.root
 * 因为mock服务使用的是qa-server主数据库,所以这里额外配置扫描mapper.mock包
 */
@Configuration
@MapperScan(basePackages = {"qa.mapper.mock"}, sqlSessionTemplateRef = "rootSqlSessionTemplate", sqlSessionFactoryRef = "rootSqlSessionFactory")
public class RootMybatisDataSourceConfig {

    /**
     * 从配置文件获取配置信息创建数据源
     *
     * @return DataSource
     */
    @Bean("rootDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.root")
    public DataSource rootDataSource() {
        return new DruidDataSource();
    }

    /**
     * 创建SqlSession工厂
     *
     * @param dataSource 数据源
     * @return SqlSessionFactory
     * @throws Exception 异常
     */
    @Bean("rootSqlSessionFactory")
    @Primary
    public SqlSessionFactory rootSqlSessionFactory(@Qualifier("rootDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();

        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        mybatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);

        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
        mybatisSqlSessionFactoryBean.setConfiguration(mybatisConfiguration);
        //getResources方法表示resources路径,*表示所有
        mybatisSqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath*:mapper/*.xml"));
        mybatisSqlSessionFactoryBean.setGlobalConfig(new GlobalConfig().setBanner(false));

        return mybatisSqlSessionFactoryBean.getObject();
    }

    /**
     * SqlSession模板
     *
     * @param sqlSessionFactory 工厂
     * @return SqlSessionTemplate
     */
    @Bean("rootSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate rootSqlSessionTemplate(@Qualifier("rootSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 数据源事务管理
     *
     * @param dataSource 数据源
     * @return DataSourceTransactionManager
     */
    @Bean("rootTransactionManager")
    @Primary
    public DataSourceTransactionManager rootTransactionManager(@Qualifier("rootDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
