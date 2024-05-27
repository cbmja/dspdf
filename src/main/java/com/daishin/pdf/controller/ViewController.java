package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.page.Page;
import com.daishin.pdf.page.Search;
import com.daishin.pdf.service.MasterInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final MasterInfoService masterInfoService;
    private final HttpServletRequest request;


    @GetMapping("/mList")
    public String masterList(@ModelAttribute Search search, Model model){

        int total = masterInfoService.countSearch(search.getSearch());

        Page page = new Page(search.getPage() , total);
        page.setSearch(search.getSearch());

        model.addAttribute("p" , page);

        List<Master> masterList = masterInfoService.selectMastersByPage(page);


        model.addAttribute("masterList" , masterList);



        return "masterList";
    }

}
