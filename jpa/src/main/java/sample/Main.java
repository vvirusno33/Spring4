package sample;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import sample.config.DataSourceConfig;
import sample.config.JpaConfig;
import sample.jpa.business.service.PetDao;

public class Main {

    public static void main(String[] args) {
    	//Spring의 컨테이너를 생성        
    	//JavaConfig로 Bean을 정의한 경우
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DataSourceConfig.class, JpaConfig.class);

    	//Spring의 컨테이너를 생성  
    	//XML로 Bean을 정의한 경우
        //ApplicationContext ctx = new ClassPathXmlApplicationContext("sample/config/spring-jpa.xml");

        //트랜잭션 시작
        PlatformTransactionManager t = ctx.getBean(PlatformTransactionManager.class);
        TransactionStatus s = t.getTransaction(null);
        
        PetDao dao = ctx.getBean(PetDao.class);
        
        System.out.println(dao.findById(12).getPetName());

        //트랜잭션 커밋
        t.commit(s);
        
    }

}
