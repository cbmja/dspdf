package com.daishin.pdf.controller;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.page.Page;
import com.daishin.pdf.service.MasterInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final MasterInfoService masterInfoService;


    @GetMapping("/mList")
    public String masterList(@RequestParam(value = "page" , required = false) int p ){

        List<Master> total = masterInfoService.selectAll();

        Page page = new Page(p , total.size());

        List<Master> mm = masterInfoService.selectMastersByPage(page);
        System.out.println(mm+"//////");

        return "masterList";
    }

}
