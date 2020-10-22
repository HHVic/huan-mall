package cn.huan.mall.member.controller;

import cn.huan.common.exception.BaseExceptionEnum;
import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.R;
import cn.huan.common.vo.MemberRespVo;
import cn.huan.mall.member.entity.MemberEntity;
import cn.huan.mall.member.feign.CouponFeignService;
import cn.huan.mall.member.service.MemberService;
import cn.huan.mall.member.vo.LoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 01:58:30
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    @RequestMapping("/coupons")
    public R test() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("huan");
        R memberCoupons = couponFeignService.memberCoupons();
        return R.ok().put("member", memberEntity).put("coupons", memberCoupons.get("coupons"));
    }

    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo) {
        MemberEntity entity = memberService.login(loginVo);
        if (entity == null) {
            return R.error(BaseExceptionEnum.USER_ACCOUNT_PASSWORD_INVALID.getCode(), BaseExceptionEnum.USER_ACCOUNT_PASSWORD_INVALID.getMessage());
        }
        MemberRespVo respVo = new MemberRespVo();
        BeanUtils.copyProperties(entity,respVo);
        R r = new R();
        r.addData(respVo);
        return r;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
