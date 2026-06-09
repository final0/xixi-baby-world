package com.xixi.module.home.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xixi.config.MinioService;
import com.xixi.module.baby.entity.BabyInfo;
import com.xixi.module.baby.mapper.BabyInfoMapper;
import com.xixi.module.media.entity.Photo;
import com.xixi.module.media.mapper.PhotoMapper;
import com.xixi.module.motto.entity.Motto;
import com.xixi.module.motto.mapper.MottoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final BabyInfoMapper babyInfoMapper;
    private final PhotoMapper photoMapper;
    private final MottoMapper mottoMapper;
    private final MinioService minioService;

    public Map<String, Object> getHomeInfo() {
        Map<String, Object> result = new HashMap<>();
        BabyInfo baby = babyInfoMapper.selectById(1L);
        if (baby != null) {
            Map<String, Object> babyMap = new LinkedHashMap<>();
            babyMap.put("name", baby.getName());
            babyMap.put("birthday", baby.getBirthday());
            babyMap.put("age", calcAge(baby.getBirthday()));
            babyMap.put("birthWeight", baby.getBirthWeight());
            babyMap.put("birthHeight", baby.getBirthHeight());
            babyMap.put("avatarUrl", minioService.getFileUrl(baby.getAvatarUrl()));
            babyMap.put("intro", baby.getIntro());
            result.put("baby", babyMap);
        }
        List<Photo> featured = photoMapper.selectList(new LambdaQueryWrapper<Photo>()
                .eq(Photo::getIsFeatured,1).eq(Photo::getStatus,1)
                .orderByAsc(Photo::getSortOrder).last("LIMIT 9"));
        result.put("featuredPhotos", featured.stream().map(p -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", p.getId()); m.put("url", minioService.getFileUrl(p.getUrl()));
            m.put("thumbnailUrl", minioService.getFileUrl(p.getThumbnail())); m.put("title", p.getTitle());
            return m;
        }).toList());
        List<Motto> all = mottoMapper.selectList(new LambdaQueryWrapper<Motto>().eq(Motto::getIsActive, 1));
        Collections.shuffle(all);
        result.put("mottos", all.stream().limit(5).toList());
        return result;
    }

    private String calcAge(LocalDate birthday) {
        if (birthday == null) return "";
        Period p = Period.between(birthday, LocalDate.now());
        int months = p.getYears() * 12 + p.getMonths();
        if (months < 1) return p.getDays() + "天";
        if (months < 12) return months + "个月";
        return p.getYears() + "岁" + p.getMonths() + "个月";
    }
}
