package test.com.tinyrpc.mock;

import java.util.List;

public interface HelloService {

    public String sayHello(String s);

    public String sayHello2(String s) throws MockException;

    public String wrap(List<String> list);
}