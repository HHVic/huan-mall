package cn.huan.mall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseDoneVo {
    private Long id;
    private List<ItemInfo> items;
}
