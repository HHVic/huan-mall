package cn.huan.mall.member.service;

import cn.huan.mall.member.vo.LoginVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.huan.common.utils.PageUtils;
import cn.huan.mall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 01:58:30
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    MemberEntity login(LoginVo loginVo);
}

