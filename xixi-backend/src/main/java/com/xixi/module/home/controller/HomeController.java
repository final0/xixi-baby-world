package com.xixi.module.home.controller;

import com.xixi.common.result.R;
import com.xixi.module.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @GetMapping("/info")
    public R<?> getHomeInfo() { return R.ok(homeService.getHomeInfo()); }
}
