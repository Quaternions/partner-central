package jtv.api.gateway.partner.utils;

import jtv.api.gateway.config.ApiGatewayPartnerProperties;
import jtv.utils.JwtUtil;
import org.aeonbits.owner.ConfigCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PartnerInvitationUtils {
    private long timeToLive = 2880;
    private String issuer = "JTV";

    private static final Logger log = LoggerFactory.getLogger(PartnerInvitationUtils.class);

    private final ApiGatewayPartnerProperties apiGatewayPartnerProperties;
    public PartnerInvitationUtils() {
        this.apiGatewayPartnerProperties = ConfigCache.getOrCreate(ApiGatewayPartnerProperties.class);
    }

    public PartnerInvitationUtils(ApiGatewayPartnerProperties apiGatewayPartnerProperties) {
        this.apiGatewayPartnerProperties = apiGatewayPartnerProperties;
    }

    public String generatePartnerInviteToken(String legalEntityName, String inviterUuid) {
        Map<String, Object> tokenClaims = new HashMap<>();

        tokenClaims.put("user-uuid", inviterUuid);

        return generatePartnerInvitationJws(legalEntityName, tokenClaims);
    }

    public String generateExpiredPartnerInviteToken(String legalEntityName, String inviterUuid) {
        Map<String, Object> tokenClaims = new HashMap<>();
        LocalDateTime issuedAt = LocalDateTime.now().minusDays(10);

        log.info("[PartnerInvitationUtils.generateExpiredPartnerInviteToken] issueAt = " + issuedAt);
        log.info("[PartnerInvitationUtils.generateExpiredPartnerInviteToken] timeToLive = " + timeToLive);

        tokenClaims.put("user-uuid", inviterUuid);

        return  JwtUtil.createJws(legalEntityName, issuedAt,timeToLive,tokenClaims,issuer,apiGatewayPartnerProperties.getApiGatewayPartnerInvitationSigningKey());
    }

    String generatePartnerInvitationJws(String subject, Map<String, Object> claimsMap) {
        return JwtUtil.createJws(subject, timeToLive, claimsMap, issuer, apiGatewayPartnerProperties.getApiGatewayPartnerInvitationSigningKey());
    }

}
