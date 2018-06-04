package me.robin.cloud;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

@RestController
@RequestMapping("admin")
public class AdminController {
    @Resource
    RemoteRegionWhiteListConfigureService configureService;

    @GetMapping("/white_list/{region}")
    public Set<String> getWhiteList(@PathVariable("region") String region) {
        return configureService.getAppWhiteList(region);
    }

    @GetMapping("/white_list/{region}/{appName}")
    public String addWhiteList(@PathVariable("region") String region, @PathVariable("appName") String serviceName) {
        configureService.addAppWhiteList(region, serviceName);
        return "已添加";
    }
}
