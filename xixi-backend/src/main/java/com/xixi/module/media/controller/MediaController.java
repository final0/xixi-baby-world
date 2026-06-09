package com.xixi.module.media.controller;

import com.xixi.common.result.R;
import com.xixi.module.media.dto.MediaDto;
import com.xixi.module.media.service.MediaService;
import com.xixi.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @PostMapping("/api/photo/upload")
    public R<MediaDto.PhotoResponse> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required=false) String title,
            @RequestParam(required=false) String description,
            @RequestParam(required=false) String takenAt) throws Exception {
        return R.ok(mediaService.uploadPhoto(SecurityUtil.getCurrentUserId(), file, title, description, takenAt));
    }
    @GetMapping("/api/photo/list")
    public R<MediaDto.PageResult<MediaDto.PhotoResponse>> listPhotos(
            @RequestParam(defaultValue="1") int page, @RequestParam(defaultValue="20") int size) {
        return R.ok(mediaService.listPhotos(page, size));
    }
    @DeleteMapping("/api/photo/{id}")
    public R<Void> deletePhoto(@PathVariable Long id) {
        mediaService.deletePhoto(id, SecurityUtil.getCurrentUserId(), SecurityUtil.isAdmin()); return R.ok("删除成功");
    }
    @PutMapping("/api/photo/{id}/featured")
    public R<Void> setFeatured(@PathVariable Long id, @RequestBody Map<String,Object> body) {
        boolean f = Boolean.TRUE.equals(body.get("isFeatured"));
        Integer s = body.get("sortOrder") instanceof Integer i ? i : 0;
        mediaService.setFeatured(id, f, s); return R.ok("设置成功");
    }
    @PostMapping("/api/video/upload")
    public R<MediaDto.VideoResponse> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required=false) MultipartFile cover,
            @RequestParam String title,
            @RequestParam(required=false) String description,
            @RequestParam(required=false) String takenAt) throws Exception {
        return R.ok(mediaService.uploadVideo(SecurityUtil.getCurrentUserId(), file, cover, title, description, takenAt));
    }
    @GetMapping("/api/video/list")
    public R<MediaDto.PageResult<MediaDto.VideoResponse>> listVideos(
            @RequestParam(defaultValue="1") int page, @RequestParam(defaultValue="12") int size) {
        return R.ok(mediaService.listVideos(page, size));
    }
    @GetMapping("/api/video/{id}/play-url")
    public R<Map<String,String>> getPlayUrl(@PathVariable Long id) throws Exception {
        return R.ok(mediaService.getVideoPlayUrl(id));
    }
    @DeleteMapping("/api/video/{id}")
    public R<Void> deleteVideo(@PathVariable Long id) {
        mediaService.deleteVideo(id, SecurityUtil.getCurrentUserId(), SecurityUtil.isAdmin()); return R.ok("删除成功");
    }
}
