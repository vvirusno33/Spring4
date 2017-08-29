package sample;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import sample.config.DataSourceConfig;
import sample.config.JpaConfig;
import sample.config.JpaJndiConfig;
import sample.jpa.business.service.PetDao;

public class JndiMain {

    public static void main(String[] args) throws Exception {
    	
    	EntityManagerFactory emf = createEntityManagerFactory();
        
        // Spring이 제공하는 JNDI구현(테스트용)을 사용해서 네이밍 컨텍키스트를 생성
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind("persistence/MyEntityManagerFactory", emf);
        builder.activate();
    	
    	//Spring 컨테이너 생성        
    	//JavaConfig로 Bean을정의 한경우
//        ApplicationContext ctx = new AnnotationConfigApplicationContext(
//                JpaJndiConfig.class);

    	//Spring 컨테이너 생성        
    	//XML로 Bean을 정의한 경우
        ApplicationContext ctx = new ClassPathXmlApplicationContext("sample/config/spring-jpa-jndi.xml");
        
        //트랜잭션 개시
        PlatformTransactionManager t = ctx.getBean(PlatformTransactionManager.class);
        TransactionStatus s = t.getTransaction(null);
        
        PetDao dao = ctx.getBean(PetDao.class);
        
        System.out.println(dao.findById(12).getPetName());
        
        //트랜잭션 커밋
        t.commit(s);
        
    }

	private static EntityManagerFactory createEntityManagerFactory() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setDatabase(Database.HSQL);
        
        Properties props = new Properties();
        props.setProperty("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
        
        LocalContainerEntityManagerFactoryBean emfb = 
            new LocalContainerEntityManagerFactoryBean();
        emfb.setJpaVendorAdapter(adapter);
        emfb.setJpaProperties(props);
        emfb.setDataSource(            
        		new EmbeddedDatabaseBuilder()
		        .setType(EmbeddedDatabaseType.HSQL)
		        .addScript("script/table.sql")
		        .addScript("script/data.sql")
		        .build()
        		);
        emfb.setPackagesToScan("sample.jpa.business.domain");
        emfb.afterPropertiesSet();
        return emfb.getObject();
	
	}

}
