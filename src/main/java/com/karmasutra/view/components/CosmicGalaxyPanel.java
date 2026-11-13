package com.karmasutra.view.components;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Animated Space & Galaxy Canvas.
 * Paints procedural multi-layered nebulae, twinkling starfields, and gently floating planets with rings and atmospheric glows.
 */
public class CosmicGalaxyPanel extends JPanel {

    private final List<Star> stars = new ArrayList<>();
    private final List<Planet> planets = new ArrayList<>();
    private double time = 0;
    private Timer animationTimer;

    public CosmicGalaxyPanel() {
        setOpaque(true);
        setBackground(new Color(0x08, 0x07, 0x14));
        initCosmos();

        // 30 FPS smooth celestial animation timer
        animationTimer = new Timer(33, e -> {
            time += 0.025;
            for (Star s : stars) {
                s.twinkle(time);
            }
            for (Planet p : planets) {
                p.drift(time);
            }
            repaint();
        });
        animationTimer.start();
    }

    public void stopAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }

    private void initCosmos() {
        Random rand = new Random(42); // Seeded for pleasant star distribution

        // 1. Procedural Starfield (160 stars with varying depth & color)
        Color[] starTints = {
                new Color(255, 255, 255),
                new Color(200, 230, 255),
                new Color(255, 240, 200),
                new Color(220, 180, 255),
                new Color(150, 240, 255)
        };

        for (int i = 0; i < 180; i++) {
            stars.add(new Star(
                    rand.nextFloat(), // relative x
                    rand.nextFloat(), // relative y
                    1.0f + rand.nextFloat() * 2.2f, // size
                    starTints[rand.nextInt(starTints.length)],
                    rand.nextFloat() * 6.28f, // phase
                    0.5f + rand.nextFloat() * 1.5f // speed
            ));
        }

        // 2. Floating Planets in Celestial Muted Palette
        // Planet 1: Saturnian Auditor - Golden Gas Giant with Rings
        planets.add(new Planet(
                0.15f, 0.22f, 75,
                new Color(0xC5, 0xA8, 0x69), new Color(0x3D, 0x30, 0x1A),
                new Color(0xE2, 0xC9, 0x8E), true, 0.4f, 18
        ));

        // Planet 2: Terra Sancta - Azure Terrestrial World
        planets.add(new Planet(
                0.86f, 0.35f, 50,
                new Color(0x2A, 0x6F, 0x97), new Color(0x0B, 0x25, 0x45),
                new Color(0x61, 0xA5, 0xC2), false, 0.6f, 12
        ));

        // Planet 3: Dharma Ruby - Small Mystical Burgundy World
        planets.add(new Planet(
                0.82f, 0.82f, 34,
                new Color(0x8B, 0x26, 0x35), new Color(0x38, 0x10, 0x17),
                new Color(0xB5, 0x48, 0x58), false, 0.35f, 10
        ));

        // Planet 4: Neptune Sphere - Distant Ringed Planet
        planets.add(new Planet(
                0.28f, 0.85f, 42,
                new Color(0x45, 0x5E, 0x89), new Color(0x1A, 0x28, 0x44),
                new Color(0x89, 0xAA, 0xDC), true, 0.5f, 14
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // 1. Deep Obsidian Space Gradient
        LinearGradientPaint spaceGradient = new LinearGradientPaint(
                0, 0, w, h,
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{
                        new Color(0x0A, 0x0B, 0x0F), // Darkest Obsidian
                        new Color(0x13, 0x15, 0x1E), // Subtle Cosmic Charcoal
                        new Color(0x0E, 0x10, 0x15)  // Deep Void
                }
        );
        g2.setPaint(spaceGradient);
        g2.fillRect(0, 0, w, h);

        // 2. Translucent Nebulae Clouds (Subtle Celestial Hues)
        paintNebula(g2, (int)(w * 0.2), (int)(h * 0.3), (int)(w * 0.5), new Color(0x71, 0x3B, 0x45, 22)); // Muted Burgundy
        paintNebula(g2, (int)(w * 0.8), (int)(h * 0.4), (int)(w * 0.45), new Color(0xC5, 0xA8, 0x69, 18)); // Antique Gold
        paintNebula(g2, (int)(w * 0.5), (int)(h * 0.85), (int)(w * 0.6), new Color(0x45, 0x5E, 0x89, 16)); // Deep Azure

        // 3. Twinkling Starfield
        for (Star s : stars) {
            s.draw(g2, w, h);
        }

        // 4. Gently Floating Celestial Planets
        for (Planet p : planets) {
            p.draw(g2, w, h);
        }

        g2.dispose();
    }

    private void paintNebula(Graphics2D g2, int cx, int cy, int radius, Color color) {
        if (radius <= 0) return;
        RadialGradientPaint paint = new RadialGradientPaint(
                cx, cy, radius,
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{
                        color,
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 3),
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                }
        );
        g2.setPaint(paint);
        g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    // Star representation
    private static class Star {
        float relX, relY;
        float size;
        Color baseColor;
        float phase;
        float speed;
        float currentAlpha;

        Star(float x, float y, float size, Color c, float phase, float speed) {
            this.relX = x;
            this.relY = y;
            this.size = size;
            this.baseColor = c;
            this.phase = phase;
            this.speed = speed;
            this.currentAlpha = 0.8f;
        }

        void twinkle(double t) {
            currentAlpha = (float) (0.35 + 0.65 * (0.5 + 0.5 * Math.sin(t * speed + phase)));
        }

        void draw(Graphics2D g2, int w, int h) {
            int x = (int) (relX * w);
            int y = (int) (relY * h);
            int alpha = (int) (currentAlpha * 255);
            alpha = Math.max(20, Math.min(255, alpha));

            g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha));
            g2.fill(new Ellipse2D.Float(x, y, size, size));

            // Soft star glow for brighter stars
            if (size > 2.0f && currentAlpha > 0.7f) {
                g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), (int) (alpha * 0.25f)));
                g2.fill(new Ellipse2D.Float(x - 2, y - 2, size + 4, size + 4));
            }
        }
    }

    // Floating Planet representation
    private static class Planet {
        float baseX, baseY;
        float currentY;
        int radius;
        Color colorLight, colorDark, ringColor;
        boolean hasRings;
        float driftSpeed;
        float driftAmp;

        Planet(float x, float y, int radius, Color light, Color dark, Color ringColor, boolean rings, float speed, float amp) {
            this.baseX = x;
            this.baseY = y;
            this.currentY = y;
            this.radius = radius;
            this.colorLight = light;
            this.colorDark = dark;
            this.ringColor = ringColor;
            this.hasRings = rings;
            this.driftSpeed = speed;
            this.driftAmp = amp;
        }

        void drift(double t) {
            currentY = baseY + (float) (Math.sin(t * driftSpeed) * (driftAmp / 800.0));
        }

        void draw(Graphics2D g2, int w, int h) {
            int cx = (int) (baseX * w);
            int cy = (int) (currentY * h);

            // 1. Atmospheric Outer Glow
            int glowRad = radius + 14;
            RadialGradientPaint glow = new RadialGradientPaint(
                    cx, cy, glowRad,
                    new float[]{0.6f, 1.0f},
                    new Color[]{
                            new Color(colorLight.getRed(), colorLight.getGreen(), colorLight.getBlue(), 60),
                            new Color(colorLight.getRed(), colorLight.getGreen(), colorLight.getBlue(), 0)
                    }
            );
            g2.setPaint(glow);
            g2.fillOval(cx - glowRad, cy - glowRad, glowRad * 2, glowRad * 2);

            // 2. Back Half of Rings (if ringed)
            if (hasRings) {
                drawRings(g2, cx, cy, true);
            }

            // 3. Spherical 3D Shading on Planet Body
            int lightOffset = (int) (radius * 0.35);
            RadialGradientPaint bodyPaint = new RadialGradientPaint(
                    cx - lightOffset, cy - lightOffset, radius * 1.4f,
                    new float[]{0.0f, 0.45f, 0.9f, 1.0f},
                    new Color[]{
                            new Color(Math.min(255, colorLight.getRed() + 40), Math.min(255, colorLight.getGreen() + 40), Math.min(255, colorLight.getBlue() + 40)),
                            colorLight,
                            colorDark,
                            new Color(0x06, 0x05, 0x11) // Shadow side
                    }
            );
            g2.setPaint(bodyPaint);
            g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);

            // 4. Front Half of Rings (cut-through over planet body)
            if (hasRings) {
                drawRings(g2, cx, cy, false);
            }
        }

        private void drawRings(Graphics2D g2, int cx, int cy, boolean backHalf) {
            Graphics2D gRing = (Graphics2D) g2.create();
            gRing.translate(cx, cy);
            gRing.rotate(Math.toRadians(-22)); // Tilted celestial planetary angle

            int ringW = (int) (radius * 2.8);
            int ringH = (int) (radius * 0.7);

            // Clip either upper (back) or lower (front) half
            if (backHalf) {
                gRing.clipRect(-ringW, -ringH, ringW * 2, ringH);
            } else {
                gRing.clipRect(-ringW, 0, ringW * 2, ringH);
            }

            // Outer primary ring
            gRing.setStroke(new BasicStroke(4.0f));
            gRing.setColor(new Color(ringColor.getRed(), ringColor.getGreen(), ringColor.getBlue(), 160));
            gRing.drawOval(-ringW / 2, -ringH / 2, ringW, ringH);

            // Inner subtle ring
            gRing.setStroke(new BasicStroke(2.0f));
            gRing.setColor(new Color(ringColor.getRed(), ringColor.getGreen(), ringColor.getBlue(), 90));
            gRing.drawOval(-(ringW - 12) / 2, -(ringH - 4) / 2, ringW - 12, ringH - 4);

            gRing.dispose();
        }
    }
}
