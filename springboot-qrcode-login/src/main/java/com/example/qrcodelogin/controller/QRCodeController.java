package com.example.qrcodelogin.controller;

import com.example.qrcodelogin.common.R;
import com.example.qrcodelogin.model.QRCodeStatus;
import com.example.qrcodelogin.model.UserInfo;
import com.example.qrcodelogin.service.QRCodeService;
import com.example.qrcodelogin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/qrcode")
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private UserService userService;

    /**
     * 生成二维码
     */
    @GetMapping("/generate")
    public R generateQRCode() {
        QRCodeStatus qrCodeStatus = qrCodeService.generateQRCode();
        log.info("Generated QR code: {}", qrCodeStatus.getQrCodeId());
        return R.success(qrCodeStatus);
    }

    /**
     * 根据二维码ID获取QR码图像
     * 该方法处理HTTP GET请求，生成并返回一个PNG格式的QR码图像
     *
     * @param qrCodeId 用于生成QR码的唯一标识符
     * @param request  用于获取基础URL的HTTP请求对象
     * @return 返回一个包含QR码图像的ResponseEntity对象，如果生成失败则返回错误状态
     */
    @GetMapping(value = "/image/{qrCodeId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCodeImage(@PathVariable String qrCodeId, HttpServletRequest request) {
        // 获取基础URL
        String baseUrl = request.getScheme() + "://" + request.getServerName();
        log.info("baseUrl:{}",baseUrl);
        // 如果服务器端口不是标准的HTTP或HTTPS端口，则添加端口到基础URL
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }

        // 调用服务生成QR码图像
        byte[] qrCodeImage = qrCodeService.generateQRCodeImage(qrCodeId, baseUrl);
        // 如果QR码图像成功生成，则返回HTTP状态200和图像内容
        if (qrCodeImage != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCodeImage);
        } else {
            // 如果QR码图像生成失败，则返回HTTP状态500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 扫描二维码
     *
     * 该方法通过POST请求接收一个包含二维码ID的请求体，然后更新数据库中对应二维码的状态为已扫描
     * 如果二维码ID不存在或更新失败，则返回错误信息；否则，记录日志并返回成功信息
     *
     * @param request 包含二维码ID的请求体
     * @return ResponseEntity<String> 返回扫描结果的HTTP响应
     */
    @PostMapping("/scan")
    public ResponseEntity<String> scanQRCode(@RequestBody Map<String, String> request) {
        // 从请求体中获取二维码ID
        String qrCodeId = request.get("qrCodeId");

        // 检查二维码ID是否为空，如果为空则返回错误响应
        if (qrCodeId == null) {
            return ResponseEntity.badRequest().body("QR code ID is required");
        }

        // 调用服务层方法更新二维码状态为已扫描
        boolean updated = qrCodeService.updateQRCodeStatus(qrCodeId, QRCodeStatus.SCANNED);

        // 如果更新失败，说明二维码无效，返回错误响应
        if (!updated) {
            return ResponseEntity.badRequest().body("Invalid QR code");
        }

        // 记录扫描日志
        log.info("QR code scanned: {}", qrCodeId);

        // 返回扫描成功响应
        return ResponseEntity.ok("Scanned successfully");
    }

    /**
     * 确认登录
     */
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmLogin(@RequestBody ConfirmLoginRequest request) {
        if (request.getQrCodeId() == null || request.getUserId() == null) {
            return ResponseEntity.badRequest().body("QR code ID and user ID are required");
        }

        // 模拟用户登录
        UserInfo userInfo = userService.login(request.getUserId());
        if (userInfo == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        boolean confirmed = qrCodeService.confirmLogin(request.getQrCodeId(), userInfo);
        if (!confirmed) {
            return ResponseEntity.badRequest().body("Invalid QR code or status");
        }

        log.info("Login confirmed: {}, user: {}", request.getQrCodeId(), request.getUserId());
        return ResponseEntity.ok("Login confirmed successfully");
    }

    /**
     * 取消登录
     */
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelLogin(@RequestBody Map<String, String> request) {
        String qrCodeId = request.get("qrCodeId");
        if (qrCodeId == null) {
            return ResponseEntity.badRequest().body("QR code ID is required");
        }

        boolean cancelled = qrCodeService.cancelLogin(qrCodeId);
        if (!cancelled) {
            return ResponseEntity.badRequest().body("Invalid QR code");
        }

        log.info("Login cancelled: {}", qrCodeId);
        return ResponseEntity.ok("Login cancelled successfully");
    }

    /**
     * 获取二维码状态
     */
    @GetMapping("/status/{qrCodeId}")
    public ResponseEntity<QRCodeStatus> getQRCodeStatus(@PathVariable String qrCodeId) {
        QRCodeStatus qrCodeStatus = qrCodeService.getQRCodeStatus(qrCodeId);
        if (qrCodeStatus == null) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(qrCodeStatus);
    }

    @Data
    public static class ConfirmLoginRequest {
        private String qrCodeId;
        private String userId;
    }
}