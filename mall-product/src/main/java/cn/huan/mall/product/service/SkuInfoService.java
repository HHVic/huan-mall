package cn.huan.mall.product.service;

import cn.huan.mall.product.vo.ItemDescVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.product.entity.SkuInfoEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-14 23:09:39
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    ItemDescVo itemDesc(Long skuId) throws ExecutionException, InterruptedException;
}

