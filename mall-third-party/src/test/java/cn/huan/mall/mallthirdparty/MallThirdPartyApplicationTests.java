package cn.huan.mall.mallthirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class MallThirdPartyApplicationTests {

	@Autowired
	private OSSClient ossClient;
	@Test
	void contextLoads() {
	}

	@Test
	public void testUpload() throws FileNotFoundException {
		// Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-shanghai.aliyuncs.com";
//        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
//        String accessKeyId = "LTAI4GHhMGoDiox27MK2FrNW";
//        String accessKeySecret = "fAkmM7oZWpAr99m9RMw8s9Nqppsztr";
		String bucketName = "huan-mall";

		// <yourObjectName>上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
		String localFile = "D:/Desktop/xiaomi.png";
		String fileKeyName = "xiaomi1.png";
		// 创建OSSClient实例。

		InputStream inputStream = new FileInputStream(localFile);
		ossClient.putObject(bucketName, fileKeyName, inputStream);

		// 关闭OSSClient。
		ossClient.shutdown();
		System.out.println("上传成功。。。。");
	}

}
