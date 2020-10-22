package cn.huan.mall.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;

//@SpringBootTest
class MallAuthServerApplicationTests {

	@Test
	void contextLoads() throws ClassNotFoundException {
		TypeReference<TestDemo> reference = new TypeReference<TestDemo>() {};
		String typeName = reference.getType().getTypeName();
		Class<?> name = Class.forName(typeName);
		System.out.println(typeName);
		System.out.println(name);
	}

	@Test
	void testR(){
		R r = new R();
		TestDemo demo = new TestDemo("AAA", "11");
		r.addData(demo);
		System.out.println(r.getData());
	}

}
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
class TestDemo{
	private String name;
	private String age;
}
