package com.xixi.module.media.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xixi.common.exception.BusinessException;
import com.xixi.common.result.ResultCode;
import com.xixi.config.MinioService;
import com.xixi.module.media.dto.MediaDto;
import com.xixi.module.media.entity.Photo;
import com.xixi.module.media.entity.Video;
import com.xixi.module.media.mapper.PhotoMapper;
import com.xixi.module.media.mapper.VideoMapper;
import com.xixi.module.user.entity.User;
import com.xixi.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final PhotoMapper photoMapper;
    private final VideoMapper videoMapper;
    private final UserMapper userMapper;
    private final MinioService minioService;

    private static final Set<String> ALLOWED_IMAGE = Set.of("image/jpeg","image/png","image/webp","image/gif");
    private static final Set<String> ALLOWED_VIDEO = Set.of("video/mp4","video/quicktime","video/x-msvideo");
    private static final long MAX_PHOTO = 20L*1024*1024;
    private static final long MAX_VIDEO = 500L*1024*1024;

    public MediaDto.PhotoResponse uploadPhoto(Long userId, MultipartFile file, String title, String description, String takenAt) throws Exception {
        validate(file, ALLOWED_IMAGE, MAX_PHOTO);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String objectName = minioService.upload(file, "photos/" + today);
        String thumbnailName = minioService.uploadThumbnail(file, objectName);
        Photo photo = new Photo();
        photo.setUploaderId(userId); photo.setTitle(title); photo.setDescription(description);
        photo.setUrl(objectName); photo.setThumbnail(thumbnailName);
        photo.setIsFeatured(0); photo.setSortOrder(0); photo.setStatus(1);
        if (takenAt != null && !takenAt.isBlank()) photo.setTakenAt(LocalDate.parse(takenAt));
        photoMapper.insert(photo);
        return toPhotoResp(photo);
    }

    public MediaDto.PageResult<MediaDto.PhotoResponse> listPhotos(int page, int size) {
        Page<Photo> p = new Page<>(page, size);
        photoMapper.selectPage(p, new LambdaQueryWrapper<Photo>().eq(Photo::getStatus,1).orderByDesc(Photo::getCreatedAt));
        MediaDto.PageResult<MediaDto.PhotoResponse> r = new MediaDto.PageResult<>();
        r.setTotal(p.getTotal()); r.setPages((int)p.getPages());
        r.setList(p.getRecords().stream().map(this::toPhotoResp).toList()); return r;
    }

    public void deletePhoto(Long id, Long userId, boolean isAdmin) {
        Photo photo = photoMapper.selectById(id);
        if (photo == null) throw new BusinessException(ResultCode.NOT_FOUND);
        if (!isAdmin && !photo.getUploaderId().equals(userId)) throw new BusinessException(ResultCode.FORBIDDEN);
        photoMapper.deleteById(id); minioService.delete(photo.getUrl()); minioService.delete(photo.getThumbnail());
    }

    public void setFeatured(Long id, boolean isFeatured, Integer sortOrder) {
        Photo p = new Photo(); p.setId(id); p.setIsFeatured(isFeatured?1:0); p.setSortOrder(sortOrder!=null?sortOrder:0);
        photoMapper.updateById(p);
    }

    public MediaDto.VideoResponse uploadVideo(Long userId, MultipartFile file, MultipartFile cover, String title, String description, String takenAt) throws Exception {
        validate(file, ALLOWED_VIDEO, MAX_VIDEO);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String objectName = minioService.upload(file, "videos/" + today);
        String coverName = (cover != null && !cover.isEmpty()) ? minioService.upload(cover, "covers/" + today) : null;
        Video video = new Video();
        video.setUploaderId(userId); video.setTitle(title); video.setDescription(description);
        video.setUrl(objectName); video.setCoverUrl(coverName); video.setStatus(1);
        if (takenAt != null && !takenAt.isBlank()) video.setTakenAt(LocalDate.parse(takenAt));
        videoMapper.insert(video); return toVideoResp(video);
    }

    public MediaDto.PageResult<MediaDto.VideoResponse> listVideos(int page, int size) {
        Page<Video> p = new Page<>(page, size);
        videoMapper.selectPage(p, new LambdaQueryWrapper<Video>().eq(Video::getStatus,1).orderByDesc(Video::getCreatedAt));
        MediaDto.PageResult<MediaDto.VideoResponse> r = new MediaDto.PageResult<>();
        r.setTotal(p.getTotal()); r.setPages((int)p.getPages());
        r.setList(p.getRecords().stream().map(this::toVideoResp).toList()); return r;
    }

    public Map<String,String> getVideoPlayUrl(Long id) throws Exception {
        Video v = videoMapper.selectById(id);
        if (v == null) throw new BusinessException(ResultCode.NOT_FOUND);
        return Map.of("playUrl", minioService.getPresignedUrl(v.getUrl()));
    }

    public void deleteVideo(Long id, Long userId, boolean isAdmin) {
        Video v = videoMapper.selectById(id);
        if (v == null) throw new BusinessException(ResultCode.NOT_FOUND);
        if (!isAdmin && !v.getUploaderId().equals(userId)) throw new BusinessException(ResultCode.FORBIDDEN);
        videoMapper.deleteById(id); minioService.delete(v.getUrl()); minioService.delete(v.getCoverUrl());
    }

    private void validate(MultipartFile file, Set<String> allowed, long maxSize) {
        if (!allowed.contains(file.getContentType())) throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED);
        if (file.getSize() > maxSize) throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED);
    }

    private MediaDto.PhotoResponse toPhotoResp(Photo p) {
        MediaDto.PhotoResponse r = new MediaDto.PhotoResponse();
        r.setId(p.getId()); r.setUrl(minioService.getFileUrl(p.getUrl()));
        r.setThumbnailUrl(minioService.getFileUrl(p.getThumbnail()));
        r.setTitle(p.getTitle()); r.setDescription(p.getDescription());
        r.setTakenAt(p.getTakenAt()); r.setUploaderId(p.getUploaderId());
        r.setIsFeatured(p.getIsFeatured()); r.setCreatedAt(p.getCreatedAt());
        User u = userMapper.selectById(p.getUploaderId());
        if (u != null) r.setUploaderNickname(u.getNickname()); return r;
    }

    private MediaDto.VideoResponse toVideoResp(Video v) {
        MediaDto.VideoResponse r = new MediaDto.VideoResponse();
        r.setId(v.getId()); r.setTitle(v.getTitle()); r.setDescription(v.getDescription());
        r.setCoverUrl(minioService.getFileUrl(v.getCoverUrl())); r.setDuration(v.getDuration());
        r.setTakenAt(v.getTakenAt()); r.setUploaderId(v.getUploaderId()); r.setCreatedAt(v.getCreatedAt());
        User u = userMapper.selectById(v.getUploaderId());
        if (u != null) r.setUploaderNickname(u.getNickname()); return r;
    }
}
