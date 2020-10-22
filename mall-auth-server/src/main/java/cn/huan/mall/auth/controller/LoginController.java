package cn.huan.mall.auth.controller;

import cn.huan.common.constant.AuthConstant;
import cn.huan.common.utils.R;
import cn.huan.common.vo.MemberRespVo;
import cn.huan.mall.auth.feign.MemberFeignService;
import cn.huan.mall.auth.vo.LoginVo;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/**
 * @author:HuanK
 * @create:2020-10-17 20:36
 */
@Controller
public class LoginController {
    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/login.html")
    public String login(HttpSession session){
        if(session.getAttribute(AuthConstant.USER_SESSION) != null){
            //已登录 去首页
            return "redirect:http://huan.mall.com";
        }
        //去登陆页
        return "login";
    }

    @PostMapping("/doLogin")
    public String login(LoginVo loginVo, RedirectAttributes redirectAttributes, HttpSession session){
        R r = memberFeignService.login(loginVo);
        if(!r.isOk()){
            redirectAttributes.addFlashAttribute("msg",r.get("msg").toString());
            return "redirect:http://auth.mall.com/login.html";
        }
        MemberRespVo data = r.getData(new TypeReference<MemberRespVo>() {
        });
        session.setAttribute("loginUser",data);
        return "redirect:http://huan.mall.com";
    }
}
