package com.zmu.cloud.commons.sphservice.impl;

import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.config.ZmuCloudProperties;
import com.zmu.cloud.commons.constants.Constants;
import com.zmu.cloud.commons.entity.SphEmploy;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.SphEmployMapper;
import com.zmu.cloud.commons.mapper.SysUserMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.sphservice.SphEmployService;
import com.zmu.cloud.commons.utils.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SphEmployServiceImpl extends ServiceImpl<SphEmployMapper, SphEmploy> implements SphEmployService {

    final SphEmployMapper employMapper;
    final SysUserMapper sysUserMapper;
    final PasswordEncoder passwordEncoder;
    final ZmuCloudProperties properties;
    final RedissonClient redissonClient;
    final ZmuCloudProperties zmuCloudProperties;

    @Override
    public Map<String, Object> login(String code, String password) {
        Map<String, Object> tokenMap = userLogin(code, password);
        if (ObjectUtil.isNull(tokenMap)) {
            tokenMap = jxLogin(code, password);
        }
        Map<String,Object> cacheMap = MapUtil.newHashMap();
        cacheMap.putAll(tokenMap);
        cacheMap.remove("resinfo");
        RMap<String, Object> jxUser = redissonClient.getMap(CacheKey.Web.jx_user_info.key + tokenMap.get("token"));
        jxUser.putAll(MapUtil.filter(cacheMap, stringObjectEntry -> ObjectUtil.isNotNull(stringObjectEntry.getValue())));
        jxUser.expire(CacheKey.Web.jx_user_info.duration);
        tokenMap.remove("password");
        tokenMap.remove("pwd");
        return tokenMap;
    }

    private Map<String, Object> userLogin(String code, String password) {
        SphEmploy employ = employMapper.selectOne(Wrappers.lambdaQuery(SphEmploy.class)
                .eq(SphEmploy::getLoginId, code).isNotNull(SphEmploy::getPassword));
        if (ObjectUtil.isEmpty(employ)) {
            return null;
        }
        if (!passwordEncoder.matches(password, employ.getPassword())) {
            throw new BaseException("密码错误");
        }
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", employ.getId());
        tokenMap.put("employCode", employ.getEmployCode());
        tokenMap.put("register", employ.getRegister());
        tokenMap.put("name", employ.getName());
        tokenMap.put("phone", ObjectUtil.isNull(employ.getPhone())?"":employ.getPhone());
        tokenMap.put("icon", ObjectUtil.isNull(employ.getIcon())?"":employ.getIcon());
        tokenMap.put("nickName", ObjectUtil.isNull(employ.getNickName())?"":employ.getNickName());
        tokenMap.put("farmId", employ.getFarmId());
        tokenMap.put("farmName", employ.getFarmName());
        tokenMap.put("companyId", employ.getOrgId());
        tokenMap.put("companyName", employ.getOrgName());
        tokenMap.put("token", "xcZwXBAgew0zSBaFNiuDO9lMpHjDXQLnMUEYNgHg4PgfH9JOCOMi");
        return tokenMap;
    }

    @Override
    public void register(String code, String password) {
        SphEmploy employ = employMapper.selectOne(Wrappers.lambdaQuery(SphEmploy.class)
                .eq(SphEmploy::getLoginId, code).isNotNull(SphEmploy::getPassword));
        if (ObjectUtil.isNotEmpty(employ)) {
            throw new BaseException("用户名已存在！");
        }
        String uu = UUIDUtils.getUUIDShort();
        employ = SphEmploy.builder().employCode(uu)
                .name(uu).loginId(code).password(passwordEncoder.encode(password))
                .farmId(19238830L).register(1).build();
        employMapper.insert(employ);
    }

    public Map<String, Object> jxLogin(String code, String password) {
        Map<String, Object> tokenMap = new HashMap<>();
        Map<String, Object> loginMap = new HashMap<>();
        loginMap.put("logid", code);
        loginMap.put("pwd", password);
        loginMap.put("SystemType", "Android");
        HttpResponse response = HttpUtil.createPost(properties.getConfig().getJxAppBaseUrl().concat(Constants.LOGIN)).form(loginMap).execute();
        JSONObject obj = JSONUtil.parseObj(response.body());
        if (ObjectUtil.isNotEmpty(obj.get("token"))) {
            String token = obj.get("token").toString();
            JSONObject usrinfo = (JSONObject) obj.get("usrinfo");
            tokenMap.put("token", token);
            tokenMap.put("id", usrinfo.get("usrid"));
            tokenMap.put("usrid", usrinfo.get("usrid"));
            tokenMap.put("code", code);
            tokenMap.put("employCode", code);
            tokenMap.put("password", usrinfo.getStr("usr_pass"));
            tokenMap.put("pwd", password);

            tokenMap.put("dept_id", usrinfo.get("dept_id"));
            tokenMap.put("dept_nm", usrinfo.get("dept_nm"));
            String companyId = (ObjectUtil.isEmpty(usrinfo.getStr("m_org_id")) || "0".equals(usrinfo.getStr("m_org_id")))?usrinfo.getStr("managred_unit"):usrinfo.getStr("m_org_id");
            String companyName = (ObjectUtil.isEmpty(usrinfo.getStr("m_org_nm")) || "0".equals(usrinfo.getStr("m_org_nm")))?usrinfo.getStr("managred_unit_nm"):usrinfo.getStr("m_org_nm");
            tokenMap.put("m_org_id", usrinfo.get("m_org_id"));
            tokenMap.put("companyId", companyId);
            tokenMap.put("m_org_nm", usrinfo.get("m_org_nm"));
            tokenMap.put("companyName", companyName);
            tokenMap.put("z_zc_id", usrinfo.get("z_zc_id"));
            tokenMap.put("z_zc_nm", usrinfo.get("z_zc_nm"));
            String farmId = (ObjectUtil.isEmpty(usrinfo.getStr("z_zc_id")) || "0".equals(usrinfo.getStr("z_zc_id")))?usrinfo.getStr("dept_id"):usrinfo.getStr("z_zc_id");
            String farmName = (ObjectUtil.isEmpty(usrinfo.getStr("z_zc_nm")) || "0".equals(usrinfo.getStr("z_zc_nm")))?usrinfo.getStr("dept_nm"):usrinfo.getStr("z_zc_nm");

            tokenMap.put("farmId", farmId);
            tokenMap.put("farmName", farmName);
            tokenMap.put("manager_zc", usrinfo.get("manager_zc"));
            tokenMap.put("manager_zc_nm", usrinfo.get("manager_zc_nm"));
            tokenMap.put("staff_name", usrinfo.get("staff_name"));
            tokenMap.put("name", usrinfo.get("staff_name"));
            tokenMap.put("phone", "");
            tokenMap.put("icon", "");
            tokenMap.put("nickName", "");

            JSONArray resinfo = (JSONArray) obj.get("resinfo");
            List<JSONObject> objs = resinfo.stream().map(o -> {
                JSONObject jsonObject = (JSONObject) o;
                jsonObject.putOpt("resid", new BigDecimal(jsonObject.get("resid").toString()).longValue());
                jsonObject.putOpt("upresid", new BigDecimal(jsonObject.get("upresid").toString()).longValue());
                return jsonObject;
            }).collect(Collectors.toList());
            tokenMap.put("resinfo", objs);
            return tokenMap;
        } else {
            throw new BaseException(obj.get("message").toString());
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = request.getHeader(zmuCloudProperties.getConfig().getSphTokenHeaderName());
        redissonClient.getMap(CacheKey.Web.jx_user_info.key + token).delete();
    }

    @Override
    public Map<String, Object> info(HttpServletRequest request) {
        String token = request.getHeader(zmuCloudProperties.getConfig().getSphTokenHeaderName());
        return redissonClient.getMap(CacheKey.Web.jx_user_info.key + token);
    }
}
