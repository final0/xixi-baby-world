package com.xixi.util;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

@Component
public class CaptchaUtil {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final Random RANDOM = new Random();

    public record CaptchaResult(String captchaId, String base64Image, String answer) {}

    public CaptchaResult generate() {
        int a = RANDOM.nextInt(10) + 1;
        int b = RANDOM.nextInt(10) + 1;
        int op = RANDOM.nextInt(2);
        String expression;
        int answer;
        if (op == 0) {
            expression = a + " + " + b + " = ?";
            answer = a + b;
        } else {
            if (a < b) { int tmp = a; a = b; b = tmp; }
            expression = a + " - " + b + " = ?";
            answer = a - b;
        }

        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 248, 251));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setStroke(new BasicStroke(1));
        for (int i = 0; i < 4; i++) {
            g.setColor(randomLightColor());
            g.drawLine(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT), RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
        }
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(new Color(233, 30, 122));
        FontMetrics fm = g.getFontMetrics();
        int textX = (WIDTH - fm.stringWidth(expression)) / 2;
        int textY = (HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(expression, textX, textY);
        for (int i = 0; i < 30; i++) {
            g.setColor(randomLightColor());
            g.fillOval(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT), 2, 2);
        }
        g.dispose();

        String base64;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("验证码生成失败", e);
        }
        String captchaId = java.util.UUID.randomUUID().toString().replace("-", "");
        return new CaptchaResult(captchaId, base64, String.valueOf(answer));
    }

    private Color randomLightColor() {
        return new Color(200 + RANDOM.nextInt(55), 200 + RANDOM.nextInt(55), 200 + RANDOM.nextInt(55));
    }
}
