package cn.huan.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-14 23:09:39
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

