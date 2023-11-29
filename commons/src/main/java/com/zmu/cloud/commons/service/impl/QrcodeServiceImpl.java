package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.zmu.cloud.commons.entity.PigHouse;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.entity.PigHouseRows;
import com.zmu.cloud.commons.entity.Qrcode;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.QrcodeMapper;
import com.zmu.cloud.commons.service.PigHouseColumnsService;
import com.zmu.cloud.commons.service.PigHouseRowsService;
import com.zmu.cloud.commons.service.PigHouseService;
import com.zmu.cloud.commons.service.QrcodeService;
import com.zmu.cloud.commons.utils.UUIDUtils;
import com.zmu.cloud.commons.vo.ColumnVo;
import com.zmu.cloud.commons.vo.QrcodeVO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QrcodeServiceImpl extends ServiceImpl<QrcodeMapper, Qrcode> implements QrcodeService {

    final QrcodeMapper qrcodeMapper;
    final PigHouseColumnsService columnsService;

    @Value("${file.qrcode.linux}")
    private String linux;
    @Value("${file.qrcode.windows}")
    private String windows;

    @Override
    public PigHouseColumns scan(String content) {
        QrcodeVO vo = rqCodeAnalysis(content);
        Optional<PigHouseColumns> opt = columnsService.findByQrcode(vo);
        if (opt.isPresent()) {
            return opt.get();
        }
        throw new BaseException("二维码错误或未绑定栏位！");
    }

    public static QrcodeVO rqCodeAnalysis(String content) {
        if (ObjectUtils.isEmpty(content)) {
            throw new BaseException("content不能为空");
        }
        QrcodeVO vo = JSONUtil.toBean(content, QrcodeVO.class);
        if (ObjectUtil.isEmpty(vo.getType()) ||
                ObjectUtil.isAllEmpty(vo.getCode(), vo.getClientId())) {
            throw new BaseException("二维码错误");
        }
        return vo;
    }

    @Override
    public void generate(Integer begin, Integer end) throws Exception {
        List<Qrcode> codes = Lists.newArrayList();
        for (int j=begin;j<=end;j++) {
            for (int k=1;k<=64;k++) {
                String field;
                if (k<10) {
                    field = "0" + k;
                } else {
                    field = k + "";
                }
                String code = generateCode("");
                String downLabel = code.concat(" - ").concat(String.valueOf(j)).concat("-").concat(field);
                JSONObject obj = new JSONObject(true);
                obj.putOpt("feederCode", k);
                obj.putOpt("type", "BreedingPigFeeder");
                obj.putOpt("code", code);
                codes.add(Qrcode.builder().code(code).feederCode(k).batch(j).build());
//                normalDrawStringSmall(obj, j, downLabel);
            }
        }
        super.saveBatch(codes);
    }

    @Override
    public void generate(Long houseId) {
        List<Qrcode> codes = Lists.newArrayList();
        List<PigHouseColumns> cols = columnsService.listByHouse(houseId);
        cols.forEach(col -> {
            Qrcode qrcode = qrcodeMapper.selectOne(Wrappers.lambdaQuery(Qrcode.class).eq(Qrcode::getPigHouseId, houseId).eq(Qrcode::getNo, col.getNo()));
            String code = "";
            if (ObjectUtil.isEmpty(qrcode)) {
                code = generateCode("");
                JSONObject obj = new JSONObject(true);
                obj.putOpt("feederCode", 0);
                obj.putOpt("type", "Column");
                obj.putOpt("code", code);
                obj.putOpt("no", col.getNo());
                codes.add(Qrcode.builder().companyId(col.getCompanyId()).pigFarmId(col.getPigFarmId()).pigHouseId(houseId).pigColumnId(col.getId())
                        .code(code).feederCode(0).no(col.getNo()).batch(0).build());
                String path = (SystemUtil.getOsInfo().isWindows()?windows:linux) + File.separator + houseId;
                try {
                    normalDrawStringSmall(obj, path, code, col.getNo());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BaseException("生成栏位二维码异常！");
                }
            } else {
                qrcode.setPigColumnId(col.getId());
                codes.add(qrcode);
            }
        });
        super.saveOrUpdateBatch(codes);
    }

    /**
     * 暂时只支持一个栏位一个饲喂器
     * @param code
     * @return
     */
    @Override
    public Qrcode findByCode(String code) {
        LambdaQueryWrapper<Qrcode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Qrcode::getCode, code);
        return qrcodeMapper.selectOne(wrapper);
    }

    private String generateCode(String code) {
        if (ObjectUtil.isEmpty(code)) {
            code = UUIDUtils.getUUIDShort();
        }
        LambdaQueryWrapper<Qrcode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Qrcode::getCode, code);
        if (qrcodeMapper.exists(wrapper)) {
            return generateCode(UUIDUtils.getUUIDShort());
        }
        return code;
    }

    public static void normalDrawStringSmall(JSONObject content, String path, String house, int no) throws Exception {
        //加载字体
        loadFonts();
        BufferedImage image = addWater(content.toString(), false);
        Graphics2D gd = image.createGraphics();

        gd.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // 5、设置水印文字颜色
        gd.setColor(Color.black);
        Font font = new Font("MiSans", Font.BOLD, 560);
        // 6、设置水印文字Font
        gd.setFont(font);
        String noStr = String.valueOf(no);
        FontMetrics fm = gd.getFontMetrics(font);
        int textWidth = fm.stringWidth(noStr);
        // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
        int x = (image.getWidth() - textWidth) / 2;
        gd.drawString(noStr, x, 660);
        gd.dispose();

        gd = image.createGraphics();
        // 3、设置对线段的锯齿状边缘处理
        gd.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // 5、设置水印文字颜色
        gd.setColor(Color.black);
        // 6、设置水印文字Font
        font = new Font("MiSans Medium", Font.PLAIN, 140);
        gd.setFont(font);
        fm = gd.getFontMetrics(font);

        textWidth = fm.stringWidth(house);
        x = (image.getWidth() - textWidth) / 2;
        // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
        gd.drawString(house, x, 1150);
        gd.dispose();

        // 输出图片
        path += File.separator + no + ".png";
        BufferedOutputStream outputStream = FileUtil.getOutputStream(path);
        ImageIO.write(image, "png",outputStream);
        outputStream.close();
    }

    public static void main(String[] args) throws Exception {
        normalDrawStringSmall(JSONUtil.createObj().putOpt("sdfas", "sdfasdf"), "D:\\泽牧科技\\qrcode\\43147911",
                "配怀", 10);
    }

    /***
     * 在一张背景图上添加二维码
     */
    private static BufferedImage addWater(String content, boolean big) throws Exception {
        // 读取原图片信息
        //文件转化为图片
        Image srcImg = ImageIO.read(resourceLoader("classpath:static/new_back.png"));
        //获取图片的宽
        int srcImgWidth = srcImg.getWidth(null);
        //获取图片的高
        int srcImgHeight = srcImg.getHeight(null);
        // 加水印
        BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufImg.createGraphics();
        g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
        //使用工具类生成二维码
        Image image;
        image = createQrCode(content,1000,1000);
        //将小图片绘到大图片上,500,300 .表示你的小图片在大图片上的位置。
        g.drawImage(image, 112, 1210, null);


        //设置颜色。
        g.setColor(Color.WHITE);
        g.dispose();
        return bufImg;
    }

    private static BufferedImage createQrCode(String content, int width, int height) throws IOException {
        QrConfig config = new QrConfig(width, height);

        Image image = ImageIO.read(resourceLoader("classpath:static/logo.png"));
        config.setImg(image);
        config.setMargin(0);
        config.setRatio(4);
        config.setErrorCorrection(ErrorCorrectionLevel.H);
        return QrCodeUtil.generate(
                content,
                config);
    }

    private static InputStream resourceLoader(String fileFullPath) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        return resourceLoader.getResource(fileFullPath).getInputStream();
    }

    private static void loadFonts() throws IOException, FontFormatException {
        // 读取外部字体资源，然后创建字体
        Font miSans = Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("static/font/MiSans-Bold.ttf"));
        Font medium = Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("static/font/MiSans-Medium.ttf"));
        // 注册字体
        GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        genv.registerFont(miSans);
        genv.registerFont(medium);
    }
}
