package com.xixi.module.diary.controller;

import com.xixi.common.result.R;
import com.xixi.module.diary.dto.DiaryDto;
import com.xixi.module.diary.service.DiaryService;
import com.xixi.module.media.dto.MediaDto;
import com.xixi.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping
    public R<DiaryDto.DiaryResponse> create(@Valid @RequestBody DiaryDto.CreateRequest req) {
        return R.ok(diaryService.create(SecurityUtil.getCurrentUserId(), req));
    }
    @GetMapping("/list")
    public R<MediaDto.PageResult<DiaryDto.DiaryResponse>> list(
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(required=false) String month) {
        return R.ok(diaryService.list(page, size, month));
    }
    @GetMapping("/{id}")
    public R<DiaryDto.DiaryResponse> getById(@PathVariable Long id) {
        return R.ok(diaryService.getById(id));
    }
    @PutMapping("/{id}")
    public R<DiaryDto.DiaryResponse> update(@PathVariable Long id, @RequestBody DiaryDto.UpdateRequest req) {
        return R.ok(diaryService.update(id, SecurityUtil.getCurrentUserId(), SecurityUtil.isAdmin(), req));
    }
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        diaryService.delete(id, SecurityUtil.getCurrentUserId(), SecurityUtil.isAdmin());
        return R.ok("删除成功");
    }
}
