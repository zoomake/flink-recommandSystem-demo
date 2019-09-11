package com.demo.controller;

import com.demo.dto.ProductDto;
import com.demo.service.KafkaService;
import com.demo.service.RecommandService;
import com.demo.util.Result;
import com.demo.util.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RecommandController {

    @Autowired
    RecommandService recommandService;

    @Autowired
    KafkaService kafkaService;
    /**
     * 返回推荐页面
     * @param userId
     * @return
     * @throws IOException
     */
    @GetMapping("/recommand")
    public String recommandByUserId(@RequestParam("userId") String userId,
                                    Model model) throws IOException {

        // 拿到不同推荐方案的结果
        List<ProductDto> hotList = recommandService.recommandByHotList();
        List<ProductDto> result1 = new ArrayList<>();
        for(ProductDto p : hotList){
            if(p.getContact()!=null){
                result1.add(p);
            }
        }
        List<ProductDto> itemCfCoeffList = recommandService.recomandByItemCfCoeff();
        List<ProductDto> result2 = new ArrayList<>();
        for(ProductDto p : itemCfCoeffList){
            if(p.getContact()!=null){
                result2.add(p);
            }
        }
        List<ProductDto> productCoeffList = recommandService.recomandByProductCoeff();
        List<ProductDto> result3 = new ArrayList<>();
        for(ProductDto p : productCoeffList){
            if(p.getContact()!=null){
                result3.add(p);
            }
        }

        // 将结果返回给前端
        model.addAttribute("userId", userId);
        model.addAttribute("hotList",result1);
        model.addAttribute("itemCfCoeffList", result2);
        model.addAttribute("productCoeffList", result3);

        return "user";
    }

    @GetMapping("/log")
    @ResponseBody
    public Result logToKafka(@RequestParam("id") String userId,
                             @RequestParam("prod") String productId,
                             @RequestParam("action") String action){

        String log = kafkaService.makeLog(userId, productId, action);
        kafkaService.send(null, log);
        return ResultUtils.success();
    }

}
