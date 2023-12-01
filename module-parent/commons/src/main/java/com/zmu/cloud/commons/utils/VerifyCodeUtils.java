package com.zmu.cloud.commons.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;

public class VerifyCodeUtils {

    //使用到Algerian字体，系统里没有的话需要安装字体，字体只显示大写，去掉了1,0,i,o几个容易混淆的字符
    public static final String VERIFY_CODES = "23456789abcdefghjklmnpqrstuvwxyz";
    private static Random random = new Random();


    /**
     * 使用系统默认字符源生成验证码
     *
     * @param verifySize 验证码长度
     * @return
     */
    public static String generateVerifyCode(int verifySize) {
        return generateVerifyCode(verifySize, VERIFY_CODES);
    }

    /**
     * 使用指定源生成验证码
     *
     * @param verifySize 验证码长度
     * @param sources    验证码字符源
     * @return
     */
    public static String generateVerifyCode(int verifySize, String sources) {
        if (sources == null || sources.length() == 0) {
            sources = VERIFY_CODES;
        }
        int codesLen = sources.length();
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder verifyCode = new StringBuilder(verifySize);
        for (int i = 0; i < verifySize; i++) {
            verifyCode.append(sources.charAt(rand.nextInt(codesLen - 1)));
        }
        return verifyCode.toString();
    }

    /**
     * 生成随机验证码文件,并返回验证码值
     *
     * @param w
     * @param h
     * @param outputFile
     * @param verifySize
     * @return
     * @throws IOException
     */
    public static String outputVerifyImage(int w, int h, File outputFile, int verifySize) throws IOException {
        String verifyCode = generateVerifyCode(verifySize);
        outputImage(w, h, outputFile, verifyCode);
        return verifyCode;
    }

    /**
     * 输出随机验证码图片流,并返回验证码值
     *
     * @param w
     * @param h
     * @param os
     * @param verifySize
     * @return
     * @throws IOException
     */
    public static String outputVerifyImage(int w, int h, OutputStream os, int verifySize) throws IOException {
        String verifyCode = generateVerifyCode(verifySize);
        outputImage(w, h, os, verifyCode);
        return verifyCode;
    }

    /**
     * 生成指定验证码图像文件
     *
     * @param w
     * @param h
     * @param outputFile
     * @param code
     * @throws IOException
     */
    public static void outputImage(int w, int h, File outputFile, String code) throws IOException {
        if (outputFile == null) {
            return;
        }
        File dir = outputFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            outputFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outputFile);
            outputImage(w, h, fos, code);
            fos.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 输出指定验证码图片流
     *
     * @param w
     * @param h
     * @param os
     * @param code
     * @throws IOException
     */
    public static void outputImage(int w, int h, OutputStream os, String code) throws IOException {
        int verifySize = code.length();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Random rand = new Random();
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color[] colors = new Color[5];
        Color[] colorSpaces = new Color[]{Color.WHITE, Color.CYAN,
                Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE,
                Color.PINK, Color.YELLOW};
        float[] fractions = new float[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorSpaces[rand.nextInt(colorSpaces.length)];
            fractions[i] = rand.nextFloat();
        }
        Arrays.sort(fractions);

        g2.setColor(Color.GRAY);// 设置边框色
        g2.fillRect(0, 0, w, h);

        Color c = getRandColor(200, 250);
        g2.setColor(c);// 设置背景色
        g2.fillRect(0, 2, w, h - 4);

        //绘制干扰线
        Random random = new Random();
        g2.setColor(getRandColor(160, 200));// 设置线条的颜色
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(w - 1);
            int y = random.nextInt(h - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        // 添加噪点
        float yawpRate = 0.05f;// 噪声率
        int area = (int) (yawpRate * w * h);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }

        shear(g2, w, h, c);// 使图片扭曲

        g2.setColor(getRandColor(100, 160));
        int fontSize = h - 4;
        Font font = new Font("Algerian", Font.ITALIC, fontSize);
        g2.setFont(font);
        char[] chars = code.toCharArray();
        for (int i = 0; i < verifySize; i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), (w / verifySize) * i + fontSize / 2, h / 2);
            g2.setTransform(affine);
            g2.drawChars(chars, i, 1, ((w - 10) / verifySize) * i + 5, h / 2 + fontSize / 2 - 10);
        }

        g2.dispose();
        ImageIO.write(image, "jpg", os);
    }

    private static Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }

    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static void shearX(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(2);

        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }

    }

    private static void shearY(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(40) + 10; // 50;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }

        }

    }

    /**
     * 验证码字符个数
     */
    private final static int codeCount = 5;

    /**
     * 干扰线条数
     */
    private final static int lineCount = 5;

    /**
     * codeSequence
     */
    private final static String[] codeSequence = {"2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f",
            "g", "h", "j", "k", "m", "n", "p", "r", "s", "t", "u", "v", "w", "x", "y"};

    public static Code getVerifyCodeImage(int... nums) throws Exception {
        int width = 100;
        int height = 40;
        if (nums != null && nums.length == 2) {
            width = nums[0];
            height = nums[1];
        }
        int xx = width / (codeCount + 2); // 生成随机数的水平距离
        int fontHeight = height - 12; // 生成随机数的数字高度
        int codeY = height - 8; // 生成随机数的垂直距离
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D gd = buffImg.createGraphics();

        // 创建一个随机数生成器类
        Random random = new Random();

        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, width, height);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.PLAIN, fontHeight);
        // 设置字体。
        gd.setFont(font);

        // 画边框。
        gd.setColor(Color.GRAY);
        gd.drawRect(0, 0, width - 1, height - 1);

        int red = 0;
        int green = 0;
        int blue = 0;
        // 随机产生干扰线，使图象中的认证码不易被其它程序探测到。
        for (int i = 0; i < lineCount; i++) {
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(125);
            green = random.nextInt(255);
            blue = random.nextInt(200);

            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));

            int y = random.nextInt(height);
            int y2 = random.nextInt(height);
            gd.drawLine(0, y, width, y2);
        }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuilder randomCode = new StringBuilder();

        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String strRand = codeSequence[random.nextInt(codeSequence.length)];
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(125);
            green = random.nextInt(255);
            blue = random.nextInt(200);

            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(strRand, (i + 1) * xx, codeY);

            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        String code = randomCode.toString();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(buffImg, "jpeg", os);
        return new Code(code, new Base64().encode(os.toByteArray()));
    }

    public static class Code {
        private String code;
        private String codeImg;

        public Code(String code, String codeImg) {
            this.code = code;
            this.codeImg = codeImg;
        }

        public String getCode() {
            return code;
        }

        public String getCodeImg() {
            return codeImg;
        }
    }

    private static final class Base64 {

        private final int BASELENGTH = 128;
        private final int LOOKUPLENGTH = 64;
        private final int TWENTYFOURBITGROUP = 24;
        private final int EIGHTBIT = 8;
        private final int SIXTEENBIT = 16;
        private final int FOURBYTE = 4;
        private final int SIGN = -128;
        private char PAD = '=';
        private byte[] base64Alphabet = new byte[BASELENGTH];
        private char[] lookUpBase64Alphabet = new char[LOOKUPLENGTH];

        public Base64() {
            for (int i = 0; i < BASELENGTH; ++i) {
                base64Alphabet[i] = -1;
            }
            for (int i = 'Z'; i >= 'A'; i--) {
                base64Alphabet[i] = (byte) (i - 'A');
            }
            for (int i = 'z'; i >= 'a'; i--) {
                base64Alphabet[i] = (byte) (i - 'a' + 26);
            }

            for (int i = '9'; i >= '0'; i--) {
                base64Alphabet[i] = (byte) (i - '0' + 52);
            }

            base64Alphabet['+'] = 62;
            base64Alphabet['/'] = 63;

            for (int i = 0; i <= 25; i++) {
                lookUpBase64Alphabet[i] = (char) ('A' + i);
            }

            for (int i = 26, j = 0; i <= 51; i++, j++) {
                lookUpBase64Alphabet[i] = (char) ('a' + j);
            }

            for (int i = 52, j = 0; i <= 61; i++, j++) {
                lookUpBase64Alphabet[i] = (char) ('0' + j);
            }
            lookUpBase64Alphabet[62] = (char) '+';
            lookUpBase64Alphabet[63] = (char) '/';
        }

        private boolean isWhiteSpace(char octect) {
            return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9);
        }

        private boolean isPad(char octect) {
            return (octect == PAD);
        }

        private boolean isData(char octect) {
            return (octect < BASELENGTH && base64Alphabet[octect] != -1);
        }

        /**
         * Encodes hex octects into Base64
         *
         * @param binaryData Array containing binaryData
         * @return Encoded Base64 array
         */
        public String encode(byte[] binaryData) {

            if (binaryData == null) {
                return null;
            }

            int lengthDataBits = binaryData.length * EIGHTBIT;
            if (lengthDataBits == 0) {
                return "";
            }

            int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
            int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
            int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1 : numberTriplets;
            char encodedData[] = null;

            encodedData = new char[numberQuartet * 4];

            byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;

            int encodedIndex = 0;
            int dataIndex = 0;

            for (int i = 0; i < numberTriplets; i++) {
                b1 = binaryData[dataIndex++];
                b2 = binaryData[dataIndex++];
                b3 = binaryData[dataIndex++];

                l = (byte) (b2 & 0x0f);
                k = (byte) (b1 & 0x03);

                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
                byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
                byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc);

                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[(l << 2) | val3];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[b3 & 0x3f];
            }

            // form integral number of 6-bit groups
            if (fewerThan24bits == EIGHTBIT) {
                b1 = binaryData[dataIndex];
                k = (byte) (b1 & 0x03);

                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[k << 4];
                encodedData[encodedIndex++] = PAD;
                encodedData[encodedIndex++] = PAD;
            } else if (fewerThan24bits == SIXTEENBIT) {
                b1 = binaryData[dataIndex];
                b2 = binaryData[dataIndex + 1];
                l = (byte) (b2 & 0x0f);
                k = (byte) (b1 & 0x03);

                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
                byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);

                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[l << 2];
                encodedData[encodedIndex++] = PAD;
            }

            return new String(encodedData);
        }

        /**
         * Decodes Base64 data into octects
         *
         * @param encoded string containing Base64 data
         * @return Array containind decoded data.
         */
        public byte[] decode(String encoded) {

            if (encoded == null) {
                return null;
            }

            char[] base64Data = encoded.toCharArray();
            // remove white spaces
            int len = removeWhiteSpace(base64Data);

            if (len % FOURBYTE != 0) {
                return null;// should be divisible by four
            }

            int numberQuadruple = (len / FOURBYTE);

            if (numberQuadruple == 0) {
                return new byte[0];
            }

            byte decodedData[] = null;
            byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;
            char d1 = 0, d2 = 0, d3 = 0, d4 = 0;

            int i = 0;
            int encodedIndex = 0;
            int dataIndex = 0;
            decodedData = new byte[(numberQuadruple) * 3];

            for (; i < numberQuadruple - 1; i++) {

                if (!isData((d1 = base64Data[dataIndex++])) || !isData((d2 = base64Data[dataIndex++]))
                        || !isData((d3 = base64Data[dataIndex++])) || !isData((d4 = base64Data[dataIndex++]))) {
                    return null;
                } // if found "no data" just return null

                b1 = base64Alphabet[d1];
                b2 = base64Alphabet[d2];
                b3 = base64Alphabet[d3];
                b4 = base64Alphabet[d4];

                decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
            }

            if (!isData((d1 = base64Data[dataIndex++])) || !isData((d2 = base64Data[dataIndex++]))) {
                return null;// if found "no data" just return null
            }

            b1 = base64Alphabet[d1];
            b2 = base64Alphabet[d2];

            d3 = base64Data[dataIndex++];
            d4 = base64Data[dataIndex++];
            if (!isData((d3)) || !isData((d4))) {// Check if they are PAD characters
                if (isPad(d3) && isPad(d4)) {
                    if ((b2 & 0xf) != 0)// last 4 bits should be zero
                    {
                        return null;
                    }
                    byte[] tmp = new byte[i * 3 + 1];
                    System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                    tmp[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                    return tmp;
                } else if (!isPad(d3) && isPad(d4)) {
                    b3 = base64Alphabet[d3];
                    if ((b3 & 0x3) != 0)// last 2 bits should be zero
                    {
                        return null;
                    }
                    byte[] tmp = new byte[i * 3 + 2];
                    System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                    tmp[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                    tmp[encodedIndex] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                    return tmp;
                } else {
                    return null;
                }
            } else { // No PAD e.g 3cQl
                b3 = base64Alphabet[d3];
                b4 = base64Alphabet[d4];
                decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);

            }

            return decodedData;
        }

        /**
         * remove WhiteSpace from MIME containing encoded Base64 data.
         *
         * @param data the byte array of base64 data (with WS)
         * @return the new length
         */
        private int removeWhiteSpace(char[] data) {
            if (data == null) {
                return 0;
            }

            // count characters that's not whitespace
            int newSize = 0;
            int len = data.length;
            for (int i = 0; i < len; i++) {
                if (!isWhiteSpace(data[i])) {
                    data[newSize++] = data[i];
                }
            }
            return newSize;
        }
    }

}