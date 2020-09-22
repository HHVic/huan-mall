package cn.huan.mall.product.service.impl;

import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.mall.product.dao.AttrGroupDao;
import cn.huan.mall.product.entity.AttrEntity;
import cn.huan.mall.product.entity.AttrGroupEntity;
import cn.huan.mall.product.service.AttrAttrgroupRelationService;
import cn.huan.mall.product.service.AttrGroupService;
import cn.huan.mall.product.service.AttrService;
import cn.huan.mall.product.vo.AttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categoryId) {
        if (categoryId == null || categoryId == 0) return queryPage(params);
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id", categoryId);
        String key = (String) params.get("key");
        if (!StringUtils.isBlank(key)) {
            wrapper.and(item -> item.eq("attr_group_id", key).or().like("attr_group_name", key));
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<AttrGroupVo> listWithAttrByCatelogId(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //属性为空的分组过滤
        List<AttrGroupVo> attrGroupVos = attrGroupEntities.stream().map(attrGroup -> {
            AttrGroupVo attrGroupVo = new AttrGroupVo();
            BeanUtils.copyProperties(attrGroup, attrGroupVo);
            List<AttrEntity> attrEntities = attrService.listByGroupId(attrGroup.getAttrGroupId());
            attrGroupVo.setAttrs(attrEntities);
            return attrGroupVo;
        }).filter(item -> !CollectionUtils.isEmpty(item.getAttrs())).collect(Collectors.toList());
        return attrGroupVos;
    }
}