package cn.huan.mall.product;

import cn.huan.mall.product.entity.BrandEntity;
import cn.huan.mall.product.service.BrandService;
import cn.huan.mall.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class MallProductApplicationTests {

    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;

    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("xiaomi");
        brandEntity.setName("xiaomi");
        brandService.save(brandEntity);
    }

    @Test
    public void categoryPath(){
        Long[] categoryPath = categoryService.getCategoryPath(165l);
        System.out.println(Arrays.toString(categoryPath));
    }

}
