package cn.huan.mall.ware.service;

import cn.huan.common.to.SkuStockTo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 02:02:08
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuStockTo> hasStock(List<Long> skuIds);
}

