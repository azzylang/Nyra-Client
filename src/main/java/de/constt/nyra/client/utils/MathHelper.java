package de.constt.nyra.client.utils;

import net.minecraft.world.phys.Vec3;

import java.security.SecureRandom;
import java.util.Random;

public class MathHelper {

    private static final Random RANDOM = new SecureRandom();

    public static float wrapDegrees(float angle) {
        angle %= 360f;
        if (angle >= 180f)  angle -= 360f;
        if (angle < -180f)  angle += 360f;
        return angle;
    }

    public static double wrapDegrees(double angle) {
        angle %= 360.0;
        if (angle >= 180.0)  angle -= 360.0;
        if (angle < -180.0)  angle += 360.0;
        return angle;
    }

    public static float angleDelta(float current, float target) {
        return wrapDegrees(target - current);
    }

    public static float lerpAngle(float from, float to, float t) {
        return from + angleDelta(from, to) * t;
    }

    public static double angleBetween(Vec3 v1, Vec3 v2) {
        double dot = v1.dot(v2);
        double len = v1.length() * v2.length();
        if (len == 0) return 0;
        return Math.acos(dot / len);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int randomInt(int min, int max) {
        if (min >= max) return min;
        return min + RANDOM.nextInt(max - min + 1);
    }

    public static float randomFloat(float min, float max) {
        if (min >= max) return min;
        return min + RANDOM.nextFloat() * (max - min);
    }

    public static double randomDouble(double min, double max) {
        if (min >= max) return min;
        return min + RANDOM.nextDouble() * (max - min);
    }
}