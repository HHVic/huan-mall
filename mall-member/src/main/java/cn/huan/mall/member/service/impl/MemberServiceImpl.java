package cn.huan.mall.member.service.impl;

import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.mall.member.dao.MemberDao;
import cn.huan.mall.member.entity.MemberEntity;
import cn.huan.mall.member.service.MemberService;
import cn.huan.mall.member.vo.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public MemberEntity login(LoginVo loginVo) {
        //通过用户名拿到会员信息，在比较密码
        String account = loginVo.getLoginAccount();
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", account)
                .or().eq("mobile", account));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(entity != null && passwordEncoder.matches(loginVo.getPassword(),entity.getPassword())){
            return entity;
        }
        return null;
    }

}