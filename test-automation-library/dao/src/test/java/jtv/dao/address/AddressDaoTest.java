package jtv.dao.address;

import jtv.dao.entity.address.DatabaseBaseAddress;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class AddressDaoTest {
    private static final AddressDao addressDao = new AddressDao();

    @Test
    public void testRandomRealAddress() {
        DatabaseBaseAddress address = addressDao.getRandomRealAddress();

        LoggerFactory.getLogger(AddressDaoTest.class).info(address.toString());
    }


}
