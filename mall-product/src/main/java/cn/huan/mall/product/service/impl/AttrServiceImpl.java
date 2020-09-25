package cn.huan.mall.product.service.impl;

import cn.huan.common.constant.ProductConstant;
import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.mall.product.dao.AttrDao;
import cn.huan.mall.product.dao.AttrGroupDao;
import cn.huan.mall.product.dao.CategoryDao;
import cn.huan.mall.product.entity.AttrAttrgroupRelationEntity;
import cn.huan.mall.product.entity.AttrEntity;
import cn.huan.mall.product.entity.AttrGroupEntity;
import cn.huan.mall.product.entity.CategoryEntity;
import cn.huan.mall.product.service.AttrAttrgroupRelationService;
import cn.huan.mall.product.service.AttrService;
import cn.huan.mall.product.service.CategoryService;
import cn.huan.mall.product.vo.AttrRespVo;
import cn.huan.mall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttrVo(AttrVo attr) {
        //保存基本信息
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr, entity);
        baseMapper.insert(entity);
        //保存规格参数分组关联,选择了分组才保存
        //基本属性才保存关联
        if(attr.getAttrType() == ProductConstant.AttrType.ATTR_BASE_TYPE.getCode()){
            Long groupId = attr.getAttrGroupId();
            if(groupId != null){
                AttrAttrgroupRelationEntity attrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                attrgroupRelationEntity.setAttrId(entity.getAttrId());
                attrgroupRelationEntity.setAttrGroupId(groupId);
                attrAttrgroupRelationService.save(attrgroupRelationEntity);
            }
        }
    }

    @Override
    public PageUtils queryPageBase(Map<String, Object> params, Long catelogId, String attrType) {
        //判断查询的是基本属性还是销售属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type","base".equals(attrType)
                        ? ProductConstant.AttrType.ATTR_BASE_TYPE.getCode()
                        : ProductConstant.AttrType.ATTR_SALE_TYPE.getCode()
                );
        if (catelogId != 0) {
            //传分类id
            wrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            //传入了查询条件
            wrapper.and(item -> item.eq("attr_id", key).or().like("attr_name", key).or().like("value_select", key));
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        List<AttrRespVo> attrRespVos = page.getRecords().stream().map(record -> {
            AttrRespVo respVo = new AttrRespVo();
            BeanUtils.copyProperties(record, respVo);
            //查询分类名称
            CategoryEntity categoryEntity = categoryDao.selectById(respVo.getCatelogId());
            if (categoryEntity != null) respVo.setCatelogName(categoryEntity.getName());
            //查询分组名称
            //通过pms_attr_attrgroup_relation找到分组id
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", record.getAttrId())
            );
            if (relationEntity != null) {
                //通过分组表查询分组名称
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) respVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
            return respVo;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        //封装响应对象
        AttrRespVo respVo = new AttrRespVo();
        //获取基本信息
        AttrEntity entity = baseMapper.selectById(attrId);
        BeanUtils.copyProperties(entity, respVo);
        //设置分组路径
        Long[] categoryPath = categoryService.getCategoryPath(entity.getCatelogId());
        respVo.setCatelogPath(categoryPath);
        //设置分组id
        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", entity.getAttrId())
        );
        if(relationEntity != null){
            respVo.setAttrGroupId(relationEntity.getAttrGroupId());
        }
        return respVo;
    }

    @Override
    public void updateInfo(AttrRespVo respVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(respVo,attrEntity);
        //更新自己
        baseMapper.updateById(attrEntity);
        //更新所属分组
        //基本属性才更新
        if(respVo.getAttrType() == ProductConstant.AttrType.ATTR_BASE_TYPE.getCode()){
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", respVo.getAttrId())
            );
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            entity.setAttrGroupId(respVo.getAttrGroupId());
            entity.setAttrId(respVo.getAttrId());
            if(relationEntity == null){
                //不存在分组信息，添加
                attrAttrgroupRelationService.save(entity);
            }else{
                //存在则修改
                attrAttrgroupRelationService.update(
                        entity,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", respVo.getAttrId())
                );
            }
        }
    }

    @Override
    public List<AttrEntity> listByGroupId(Long attrgroupId) {
        //获取所有的关联基本属性,通过分组id 拿到属性id
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId)
        );
        List<AttrEntity> attrEntities = null;
        if(relationEntities != null && relationEntities.size() > 0){
            //有分组的基本属性
            List<Long> attrIds = relationEntities.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());
            attrEntities = attrDao.selectBatchIds(attrIds);
        }
        return attrEntities;
    }

    @Override
    public PageUtils queryPageNoAttrRelation(Map<String, Object> params, Long attrGroupId) {
        //获取其他分类的所有关联
        List<Long> otherAttrIds = attrAttrgroupRelationService.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>().ne("attr_group_id", attrGroupId))
                .stream().map(item -> item.getAttrId()).collect(Collectors.toList());
        List<Long> exclude = new ArrayList<>(otherAttrIds);
        //获取当前分类的所有关联
        List<AttrEntity> currentAttrs = listByGroupId(attrGroupId);
        List<Long> currentAttrIds;
        if(currentAttrs != null){
            currentAttrIds = currentAttrs.stream().map(item -> item.getAttrId()).collect(Collectors.toList());
            exclude.addAll(currentAttrIds);
        }
        //查询所有基本属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type", ProductConstant.AttrType.ATTR_BASE_TYPE.getCode())
                .notIn("attr_id",exclude);
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<Long> getSearchableId() {
        return baseMapper.getSearchableId();
    }

}