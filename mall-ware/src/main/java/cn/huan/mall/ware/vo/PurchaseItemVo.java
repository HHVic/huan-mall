package cn.huan.mall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseItemVo {

    private Long purchaseId;
    private List<Long> items;
}
