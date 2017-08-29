package sample;

import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import sample.config.DataSourceConfig;
import sample.config.HibernateConfig;
import sample.hibernate.business.domain.Pet;
import sample.hibernate.business.service.PetDao;

public class Main {

    public static void main(String[] args) {
    	//Spring 컨테이너를 생성        
    	//JavaConfig으로Bean을 정의 하는 경우
//        ApplicationContext ctx = new AnnotationConfigApplicationContext(
//                DataSourceConfig.class, HibernateConfig.class);

    	//Spring  컨테이너를 생성 
    	//XML로 Bean을 정의 하는 경우
        ApplicationContext ctx = new ClassPathXmlApplicationContext("sample/config/spring-hibernate.xml");
        
        //트랜잭션 개시
        PlatformTransactionManager t = ctx.getBean(PlatformTransactionManager.class);
        TransactionStatus s = t.getTransaction(null);
        
        PetDao dao = ctx.getBean(PetDao.class);
        
        List<Pet> list = dao.findAll();
        
        System.out.println(list.get(0).getPetName());
        
        Pet p = dao.findById(12);
        System.out.println(p.getPetName());
        
        //트랜잭션 커밋
        t.commit(s);
        
    }

}
