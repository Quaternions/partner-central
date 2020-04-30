package jtv.api.gateway.partner.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jtv.api.gateway.config.ApiGatewayPartnerProperties;
import jtv.utils.JwtUtil;
import org.aeonbits.owner.ConfigCache;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartnerInvitationUtilsTest {

    @Test
    public void testGeneratePartnerInvitationJws() {
        PartnerInvitationUtils partnerInvitationUtils = new PartnerInvitationUtils();
        Map<String, Object> claims = new HashMap<>();

        claims.put("claim-1", "claim-value");

        String jwt = partnerInvitationUtils.generatePartnerInvitationJws("test-subject", claims);

        Assert.assertNotNull(jwt);

        LoggerFactory.getLogger(PartnerInvitationUtilsTest.class).info("JWT: " + jwt);

        Claims validatedClaims = JwtUtil.validateTokenAndReturnClaims(ConfigCache.getOrCreate(ApiGatewayPartnerProperties.class).getApiGatewayPartnerInvitationSigningKey(), jwt);

        Assert.assertEquals("claim-value", validatedClaims.get("claim-1", String.class));
        Assert.assertEquals("test-subject", validatedClaims.getSubject());
        Assert.assertEquals("JTV", validatedClaims.getIssuer());

    }

    @Test
    public void testPropertiesConstructor() {
        PartnerInvitationUtils partnerInvitationUtils = new PartnerInvitationUtils(ConfigCache.getOrCreate(ApiGatewayPartnerProperties.class));

        Assert.assertNotNull(partnerInvitationUtils);
    }

    @Test
    public void testGeneratePartnerInviteToken() {
        PartnerInvitationUtils partnerInvitationUtils = new PartnerInvitationUtils();
        Map<String, Object> claims = new HashMap<>();

        claims.put("claim-1", "claim-value");

        String jwt = partnerInvitationUtils.generatePartnerInviteToken("test-subject", UUID.randomUUID().toString());

        Assert.assertNotNull(jwt);

        LoggerFactory.getLogger(PartnerInvitationUtilsTest.class).info("JWT: " + jwt);

        Claims validatedClaims = JwtUtil.validateTokenAndReturnClaims(ConfigCache.getOrCreate(ApiGatewayPartnerProperties.class).getApiGatewayPartnerInvitationSigningKey(), jwt);

        Assert.assertEquals("test-subject", validatedClaims.getSubject());
        Assert.assertEquals("JTV", validatedClaims.getIssuer());
    }

    @Test(expectedExceptions = ExpiredJwtException.class)
    public void testGenerateExpiredPartnerInviteToken() {
        PartnerInvitationUtils partnerInvitationUtils = new PartnerInvitationUtils();
        Map<String, Object> claims = new HashMap<>();

        String jwt = partnerInvitationUtils.generateExpiredPartnerInviteToken("test-subject", UUID.randomUUID().toString());

        Assert.assertNotNull(jwt);

        LoggerFactory.getLogger(PartnerInvitationUtilsTest.class).info("JWT: " + jwt);

        Claims validatedClaims = JwtUtil.validateTokenAndReturnClaims(ConfigCache.getOrCreate(ApiGatewayPartnerProperties.class).getApiGatewayPartnerInvitationSigningKey(), jwt);

    }
}