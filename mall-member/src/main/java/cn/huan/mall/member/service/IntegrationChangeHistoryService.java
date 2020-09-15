package cn.huan.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 01:58:30
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

