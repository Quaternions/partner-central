package jtv.dao.entity.address.mapper;

import jtv.dao.entity.address.DatabaseAddress;
import jtv.dao.entity.address.DatabaseBaseAddress;

public class DatabaseAddressMapper {

    private DatabaseAddressMapper() {
    }

    public static DatabaseAddress mapDatabaseBaseAddressToDatabaseAddress(DatabaseBaseAddress baseAddress) {
        DatabaseAddress address = new DatabaseAddress();
        address.setCity(baseAddress.getCity());
        address.setState(baseAddress.getState());
        address.setPostalCode(baseAddress.getPostalCode());
        address.setZipFour(baseAddress.getZipFour());
        address.setStateIsoTwo(baseAddress.getStateIsoTwo());
        address.setCountry(baseAddress.getCountry());
        address.setCounty(baseAddress.getCounty());
        address.setCountryCode(baseAddress.getCountryCode());

        return address;
    }
}
