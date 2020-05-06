package jtv.builder.partner.utils;

import jtv.builder.partner.entity.PartnerCentralEmployee;
import jtv.builder.partner.entity.PartnerCentralUser;
import jtv.jackson.JsonWriter;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import static org.junit.jupiter.api.Assertions.*;

class PartnerUtilsTest {

    @Test
    void testBuildPartnerCentralPrimaryAccountManager() {
        PartnerUtils partnerUtils = new PartnerUtils();

        PartnerCentralUser partnerCentralUser = partnerUtils.buildPartnerCentralPrimaryAccountManager();

        Assert.assertNotNull(partnerCentralUser.getEmailAddress());
        Assert.assertNotNull(partnerCentralUser.getPassword());

    }

    @Test
    void testBuildPartnerCentralAssociate() {
        PartnerUtils partnerUtils = new PartnerUtils();

        PartnerCentralUser partnerCentralUser = partnerUtils.buildPartnerCentralAssociate();

        Assert.assertNotNull(partnerCentralUser.getEmailAddress());
        Assert.assertNotNull(partnerCentralUser.getPassword());

    }

    @Test
    void testBuildPartnerCentralEmployee() {
        PartnerUtils partnerUtils = new PartnerUtils();

        PartnerCentralEmployee partnerCentralEmployee = partnerUtils.buildPartnerCentralEmployee();

        Assert.assertNotNull(partnerCentralEmployee.getEmailAddress());
        Assert.assertNotNull(partnerCentralEmployee.getPassword());
        Assert.assertNotNull(partnerCentralEmployee.getEmployeeUserId());
        
        System.out.println(JsonWriter.writeObjectAsJson(partnerCentralEmployee));
    }
}