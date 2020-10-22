package cn.huan.mall.auth.feign;

import cn.huan.common.utils.R;
import cn.huan.mall.auth.vo.LoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author:HuanK
 * @create:2020-10-17 21:23
 */
@FeignClient("mall-member")
public interface MemberFeignService {


    @PostMapping("member/member/login")
    R login(@RequestBody LoginVo loginVo);
}
