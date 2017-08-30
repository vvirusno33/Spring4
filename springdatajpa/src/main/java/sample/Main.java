package sample;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import sample.config.AppConfig;
import sample.config.DataSourceConfig;
import sample.config.JpaConfig;
import sample.springdatajpa.business.domain.Pet;
import sample.springdatajpa.business.service.PetDao;

public class Main {

    public static void main(String[] args) {
    	
    	//Spring 컨테이너 생성        
    	//JavaConfig로 Bean을 정의한 경우
//        ApplicationContext ctx = new AnnotationConfigApplicationContext(
//        		DataSourceConfig.class, AppConfig.class, JpaConfig.class);

    	// Spring 컨테이너 생성        
    	//XML로 Bean을 정의한 경우
        ApplicationContext ctx = new ClassPathXmlApplicationContext("sample/config/spring-jpa.xml");
                        
        PetDao dao = ctx.getBean(PetDao.class);

        //Spring Data JPA가 자동생성한 메서드를 호출함
        Pet pet = dao.findOne(1);
        System.out.println(pet.getPetName());
        
        //명명 규칙에 따른 메서드를 호출함
        List<Pet> list = dao.findByPetNameAndPriceLessThanEqual("발바리", 5000);
        System.out.println(list.size());

        //JPAQL를 지정한 메서드를 호출함
        list = dao.findByOwnerName("홍길동");
        System.out.println(list.size());

        //트랜잭션을 개시
        PlatformTransactionManager t = ctx.getBean(PlatformTransactionManager.class);
        TransactionStatus s = t.getTransaction(null);

        //JPAQL을 지정한 메서드(갱신)을 호출함
        int count = dao.updatePetPrice(10000000, "발바리5");
        System.out.println("count="+count);

        //트랜잭션을 커밋
        t.commit(s);

        list = dao.findAll();
        for (Pet p : list) {
            System.out.println(p.getPrice());
        }

        //수동으로 구현한 메서드를 호출함
        dao.foo();

        
    }

}
