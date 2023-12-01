package com.zmu.cloud.commons.dto.commons;

import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: RequestInfo
 * @Date 2018-11-02 9:06
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestInfo implements Serializable {

    private static final long serialVersionUID = 4585767529338307782L;

    private Long userId;

    private Long companyId;

    private Long pigFarmId;

    @Builder.Default
    private String loginAccount = "";

    private String ip = "";

    private Long requestStartTime = 0L;

    private Long requestEndTime = 0L;

    private String requestUrl = "";

    private String reqId;

    @ApiModelProperty("设备id")
    private String deviceId = "";

    @ApiModelProperty("客户端版本")
    private int version;

    @ApiModelProperty("客户端版本")
    private String versionStr = "";

    @ApiModelProperty("请求的客户端类型：0-未知，1-android,2-iOS,3-web")
    @Builder.Default
    private UserClientTypeEnum clientType = UserClientTypeEnum.Unknown;

    @Builder.Default
    private UserRoleTypeEnum userRoleType = UserRoleTypeEnum.COMMON_USER;

    @ApiModelProperty("用户来源")
    private ResourceType resourceType;

    private String os;

    private HttpServletRequest httpServletRequest;

    @Builder.Default
    private Map<String, Object> map = new HashMap<>();

    private String token;

    public void setOs(String os) {
        if (StringUtils.isNotBlank(os) && os.contains("or")) {
            try {
                this.os = os.split("or")[0].trim();
            } catch (Exception e) {
                this.os = os;
            }
            return;
        }
        this.os = os;
    }

}
