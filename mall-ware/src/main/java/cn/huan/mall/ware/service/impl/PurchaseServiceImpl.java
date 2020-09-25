package cn.huan.mall.ware.service.impl;

import cn.huan.common.constant.WareConstant;
import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.mall.ware.dao.PurchaseDao;
import cn.huan.mall.ware.entity.PurchaseDetailEntity;
import cn.huan.mall.ware.entity.PurchaseEntity;
import cn.huan.mall.ware.service.PurchaseDetailService;
import cn.huan.mall.ware.service.PurchaseService;
import cn.huan.mall.ware.service.WareSkuService;
import cn.huan.mall.ware.vo.ItemInfo;
import cn.huan.mall.ware.vo.PurchaseDoneVo;
import cn.huan.mall.ware.vo.PurchaseItemVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnReceive(Map<String, Object> params) {
        //查询新建状态和已分配状态
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", WareConstant.PurchaseStatus.NEW.getCode())
                .or().eq("status",WareConstant.PurchaseStatus.DISTRIBUTION.getCode());
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void merge(PurchaseItemVo vo) {
        //判断purchaseId是否存在 若不存在 新增
        Long purchaseId = vo.getPurchaseId();
        if(purchaseId == null){
            //创建采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setPriority(0);
            purchaseEntity.setStatus(WareConstant.PurchaseStatus.NEW.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        //修改
        List<Long> items = vo.getItems();
        if(!CollectionUtils.isEmpty(items)){
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> purchaseDetailEntities = items.stream().map(item -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(item);
                purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatus.DISTRIBUTION.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
            //更新采购单修改日期
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }
    }

    @Transactional
    @Override
    public void purchaseReceive(List<Long> vo) {
        //领取采购单
        if(!CollectionUtils.isEmpty(vo)){

            List<PurchaseEntity> collect = vo.stream().map(purchaseId -> {
                List<PurchaseDetailEntity> detailEntities = purchaseDetailService.list(
                        new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", purchaseId)
                );
                if(!CollectionUtils.isEmpty(detailEntities)){
                    detailEntities.forEach(detailEntity -> {
                        detailEntity.setStatus(WareConstant.PurchaseDetailStatus.BUYING.getCode());
                    });
                    purchaseDetailService.updateBatchById(detailEntities);
                }
                PurchaseEntity purchaseEntity = new PurchaseEntity();
                purchaseEntity.setId(purchaseId);
                purchaseEntity.setUpdateTime(new Date());
                purchaseEntity.setStatus(WareConstant.PurchaseStatus.RECEIVE.getCode());
                return purchaseEntity;
            }).collect(Collectors.toList());
            this.updateBatchById(collect);
        }
    }

    @Transactional
    @Override
    public void purchaseDone(PurchaseDoneVo doneVo) {
        //获取采购单完成信息
        Long purchaseId = doneVo.getId();
        List<ItemInfo> items = doneVo.getItems();
        if(!CollectionUtils.isEmpty(items)){
            AtomicBoolean flag = new AtomicBoolean(true);
            List<PurchaseDetailEntity> purchaseDetailEntities = items.stream().map(item -> {
                Long itemId = item.getItemId();
                PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(itemId);
                purchaseDetailEntity.setStatus(item.getStatus());
                if (item.getStatus() == WareConstant.PurchaseDetailStatus.ERROR.getCode()) {
                    flag.set(false);
                }else{
                    //更新库存
                    Long wareId = purchaseDetailEntity.getWareId();
                    Long skuId = purchaseDetailEntity.getSkuId();
                    Integer skuNum = purchaseDetailEntity.getSkuNum();
                    wareSkuService.updateStock(skuId,wareId,skuNum);
                }
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            if (flag.get()){
                purchaseEntity.setStatus(WareConstant.PurchaseStatus.FINISHED.getCode());
            }else{
                purchaseEntity.setStatus(WareConstant.PurchaseStatus.ERROR.getCode());
            }
            baseMapper.updateById(purchaseEntity);
        }
    }

}