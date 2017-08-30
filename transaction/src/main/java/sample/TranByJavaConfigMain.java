package sample;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sample.biz.domain.Pet;
import sample.biz.service.PetService;
import sample.config.TransactionConfig;

public class TranByJavaConfigMain {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(TransactionConfig.class);
        PetService petService = ctx.getBean(PetService.class);
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setPetName("나비");
        pet.setOwnerName("홍길동");
        pet.setPrice(10000);
        pet.setBirthDate(new Date());
        
        petService.updatePet(pet);

    }
}
