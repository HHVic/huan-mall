package cn.huan.mall.product;

import cn.huan.mall.product.entity.BrandEntity;
import cn.huan.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("xiaomi");
        brandEntity.setName("xiaomi");
        brandService.save(brandEntity);
    }

}
