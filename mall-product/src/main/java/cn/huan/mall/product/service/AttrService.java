package cn.huan.mall.product.service;

import cn.huan.common.utils.PageUtils;
import cn.huan.mall.product.entity.AttrEntity;
import cn.huan.mall.product.vo.AttrRespVo;
import cn.huan.mall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
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

    void saveAttrVo(AttrVo attr);

    PageUtils queryPageBase(Map<String, Object> params, Long catelogId, String attrType);

    AttrRespVo getAttrInfo(Long attrId);

    void updateInfo(AttrRespVo attr);

    List<AttrEntity> listByGroupId(Long attrgroupId);

    PageUtils queryPageNoAttrRelation(Map<String, Object> params, Long attrGroupId);

    List<Long> getSearchableId();
}

