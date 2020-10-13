package cn.huan.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2List {
    private Long catelog1Id;
    private List<Catalog3List> catalog3List;
    private Long id;
    private String name;
}
