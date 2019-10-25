package test.com.tinyrpc.codec;

import com.tinyrpc.codec.Serialization;
import com.tinyrpc.codec.serialize.Hessian2Serialization;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;

public class SerializationTest{

    @Test
    public void testSerialize(){
        Serialization serialization =  new Hessian2Serialization();
        SerializeObject object = new SerializeObject();
        object.setId(1);
        object.setName("test2");
        object.setTmp("temp");

        try {
            byte[] bytes = serialization.serialize(object);

            Assert.assertTrue(null!=bytes && bytes.length > 0);

            SerializeObject deObject = serialization.deserialize(bytes, SerializeObject.class);

            assert null!=deObject;

            assert deObject.getId() == object.getId() && deObject.getName().equals(object.getName());

            System.out.println(deObject);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class SerializeObject implements Serializable{

    private int id;
    private String name;

    private transient String tmp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    @Override
    public String toString() {
        return "SerializeObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tmp='" + tmp + '\'' +
                '}';
    }
}