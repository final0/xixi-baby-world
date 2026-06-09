package com.xixi.module.diary.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xixi.common.exception.BusinessException;
import com.xixi.common.result.ResultCode;
import com.xixi.config.MinioService;
import com.xixi.module.diary.dto.DiaryDto;
import com.xixi.module.diary.entity.Diary;
import com.xixi.module.diary.mapper.DiaryMapper;
import com.xixi.module.media.dto.MediaDto;
import com.xixi.module.user.entity.User;
import com.xixi.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryMapper diaryMapper;
    private final UserMapper userMapper;
    private final MinioService minioService;

    public DiaryDto.DiaryResponse create(Long userId, DiaryDto.CreateRequest req) {
        Diary diary = new Diary();
        diary.setAuthorId(userId); diary.setTitle(req.getTitle()); diary.setContent(req.getContent());
        diary.setDiaryDate(req.getDiaryDate()); diary.setMood(req.getMood()); diary.setStatus(1);
        diaryMapper.insert(diary); return toResp(diary);
    }

    public MediaDto.PageResult<DiaryDto.DiaryResponse> list(int page, int size, String month) {
        Page<Diary> p = new Page<>(page, size);
        LambdaQueryWrapper<Diary> w = new LambdaQueryWrapper<Diary>().eq(Diary::getStatus,1).orderByDesc(Diary::getDiaryDate);
        if (StringUtils.hasText(month)) w.apply("DATE_FORMAT(diary_date,'%Y-%m') = {0}", month);
        diaryMapper.selectPage(p, w);
        MediaDto.PageResult<DiaryDto.DiaryResponse> r = new MediaDto.PageResult<>();
        r.setTotal(p.getTotal()); r.setPages((int)p.getPages());
        r.setList(p.getRecords().stream().map(this::toResp).toList()); return r;
    }

    public DiaryDto.DiaryResponse getById(Long id) {
        Diary diary = diaryMapper.selectById(id);
        if (diary == null) throw new BusinessException(ResultCode.NOT_FOUND);
        return toResp(diary);
    }

    public DiaryDto.DiaryResponse update(Long id, Long userId, boolean isAdmin, DiaryDto.UpdateRequest req) {
        Diary diary = diaryMapper.selectById(id);
        if (diary == null) throw new BusinessException(ResultCode.NOT_FOUND);
        if (!isAdmin && !diary.getAuthorId().equals(userId)) throw new BusinessException(ResultCode.FORBIDDEN);
        if (StringUtils.hasText(req.getTitle())) diary.setTitle(req.getTitle());
        if (StringUtils.hasText(req.getContent())) diary.setContent(req.getContent());
        if (req.getDiaryDate() != null) diary.setDiaryDate(req.getDiaryDate());
        if (req.getMood() != null) diary.setMood(req.getMood());
        diaryMapper.updateById(diary); return toResp(diary);
    }

    public void delete(Long id, Long userId, boolean isAdmin) {
        Diary diary = diaryMapper.selectById(id);
        if (diary == null) throw new BusinessException(ResultCode.NOT_FOUND);
        if (!isAdmin && !diary.getAuthorId().equals(userId)) throw new BusinessException(ResultCode.FORBIDDEN);
        diaryMapper.deleteById(id);
    }

    private DiaryDto.DiaryResponse toResp(Diary d) {
        DiaryDto.DiaryResponse r = new DiaryDto.DiaryResponse();
        r.setId(d.getId()); r.setTitle(d.getTitle()); r.setContent(d.getContent());
        r.setDiaryDate(d.getDiaryDate()); r.setMood(d.getMood()); r.setAuthorId(d.getAuthorId());
        r.setCreatedAt(d.getCreatedAt()); r.setUpdatedAt(d.getUpdatedAt());
        User author = userMapper.selectById(d.getAuthorId());
        if (author != null) {
            r.setAuthorNickname(author.getNickname());
            r.setAuthorAvatarUrl(minioService.getFileUrl(author.getAvatarUrl()));
        }
        return r;
    }
}
