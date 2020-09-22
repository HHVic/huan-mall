package cn.huan.mall.product.vo;

import lombok.Data;

/**
 * 响应数据
 */
@Data
public class AttrRespVo extends AttrVo{
    private String catelogName;
    private String groupName;
    //分类路径
    private Long[] catelogPath;
}
