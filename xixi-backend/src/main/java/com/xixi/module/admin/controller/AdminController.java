package com.xixi.module.admin.controller;

import com.xixi.common.result.R;
import com.xixi.module.admin.service.AdminService;
import com.xixi.module.media.dto.MediaDto;
import com.xixi.module.motto.entity.Motto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PutMapping("/baby")
    public R<Void> updateBaby(@RequestBody Map<String, Object> body) {
        adminService.updateBabyInfo(body); return R.ok("宝宝信息更新成功");
    }
    @GetMapping("/config")
    public R<Map<String, String>> getConfig(@RequestParam(required = false) List<String> keys) {
        return R.ok(adminService.getConfigs(keys));
    }
    @PutMapping("/config")
    public R<Void> updateConfig(@RequestBody Map<String, String> body) {
        adminService.updateConfig(body.get("configKey"), body.get("configValue")); return R.ok("配置更新成功");
    }
    @PostMapping("/config/test-email")
    public R<Void> testEmail(@RequestBody Map<String, String> body) {
        adminService.testEmail(body.get("toEmail")); return R.ok("测试邮件发送成功 📧");
    }
    @GetMapping("/users")
    public R<MediaDto.PageResult<Map<String, Object>>> listUsers(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return R.ok(adminService.listUsers(page, size));
    }
    @PutMapping("/users/{id}/status")
    public R<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        adminService.updateUserStatus(id, body.get("status")); return R.ok("用户状态更新成功");
    }
    @GetMapping("/mottos")
    public R<List<Motto>> listMottos() { return R.ok(adminService.listMottos()); }
    @PostMapping("/mottos")
    public R<Motto> addMotto(@RequestBody Map<String, String> body) {
        return R.ok(adminService.addMotto(body.get("content"), body.get("author")));
    }
    @DeleteMapping("/mottos/{id}")
    public R<Void> deleteMotto(@PathVariable Long id) { adminService.deleteMotto(id); return R.ok("删除成功"); }
    @PutMapping("/mottos/{id}/status")
    public R<Void> updateMottoStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        adminService.updateMottoStatus(id, body.get("isActive")); return R.ok("状态更新成功");
    }
}
