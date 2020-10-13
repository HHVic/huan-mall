package cn.huan.mall.search.web;

import cn.huan.mall.search.service.MallSearchService;
import cn.huan.mall.search.vo.SearchParam;
import cn.huan.mall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {
    @Autowired
    private MallSearchService searchService;

    @GetMapping(value = "/list.html")
    public String index(SearchParam searchParam, Model model){
        SearchResult result = searchService.search(searchParam);
        //System.out.println(result);
        model.addAttribute("result",result);
        return "list";
    }
}
