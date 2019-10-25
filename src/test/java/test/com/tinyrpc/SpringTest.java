package test.com.tinyrpc;

import org.junit.Assert;
import org.junit.Test;
import test.com.tinyrpc.mock.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTest {

    @Test
    public void test(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:tinyrpc.xml");
        for(String beanName : context.getBeanDefinitionNames()){
            System.out.println(beanName);
        }

        HelloService helloService = (HelloService) context.getBean("helloReference");

        String s = helloService.sayHello("hello from spring");
        System.out.println(s);

        Assert.assertNotNull(s);

    }
}
