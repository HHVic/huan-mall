package cn.huan.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 01:58:30
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

