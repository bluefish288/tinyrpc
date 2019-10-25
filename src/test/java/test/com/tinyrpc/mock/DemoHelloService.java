package test.com.tinyrpc.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DemoHelloService implements HelloService {
    @Override
    public String sayHello(String s) {
        if(null == s){
            throw new NullPointerException(s);
        }
        return "hello demo from server : "+s;
    }

    @Override
    public String sayHello2(String s) throws MockException {
        throw new MockException("test sayHello2");
    }

    @Override
    public String wrap(List<String> list) {
        List<String> resList = new ArrayList<>(list.size() * 10);
        for(int i=0;i<10;i++){
            resList.addAll(list);
        }
        return list.stream().collect(Collectors.joining(","));
    }
}