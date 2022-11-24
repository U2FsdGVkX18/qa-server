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

/**
 * MeterSphere database config
 * MS服务数据源配置
 * 主要用来查询MeterSphere平台接口场景执行结果数据
 */
@Configuration
@MapperScan(
        basePackages = {"qa.mapper.ms"},
        sqlSessionTemplateRef = "msSqlSessionTemplate",
        sqlSessionFactoryRef = "msSqlSessionFactory"
)
public class MsMybatisDataSourceConfig {

    /**
     * 从配置文件获取配置信息创建数据源
     *
     * @return DataSource
     */
    @Bean("msDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.ms")
    public DataSource msDataSource() {
        return new DruidDataSource();
    }

    /**
     * 创建SqlSession工厂
     *
     * @param dataSource 数据源
     * @return SqlSessionFactory
     * @throws Exception 异常
     */
    @Bean("msSqlSessionFactory")
    public SqlSessionFactory msSqlSessionFactory(@Qualifier("msDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();

        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        mybatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);

        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
        mybatisSqlSessionFactoryBean.setConfiguration(mybatisConfiguration);
        //getResources方法表示resources路径,*表示所有
        mybatisSqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath*:mapper/ms/*.xml"));
        mybatisSqlSessionFactoryBean.setGlobalConfig(new GlobalConfig().setBanner(false));

        return mybatisSqlSessionFactoryBean.getObject();
    }

    /**
     * SqlSession模板
     *
     * @param sqlSessionFactory 工厂
     * @return SqlSessionTemplate
     */
    @Bean("msSqlSessionTemplate")
    public SqlSessionTemplate msSqlSessionTemplate(@Qualifier("msSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 数据源事务管理
     *
     * @param dataSource 数据源
     * @return DataSourceTransactionManager
     */
    @Bean("msTransactionManager")
    public DataSourceTransactionManager msTransactionManager(@Qualifier("msDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
