package cn.huan.mall.ware.service;

import cn.huan.mall.ware.vo.PurchaseDoneVo;
import cn.huan.mall.ware.vo.PurchaseItemVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 02:02:08
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnReceive(Map<String, Object> params);

    void merge(PurchaseItemVo vo);

    void purchaseReceive(List<Long> vo);

    void purchaseDone(PurchaseDoneVo doneVo);
}

