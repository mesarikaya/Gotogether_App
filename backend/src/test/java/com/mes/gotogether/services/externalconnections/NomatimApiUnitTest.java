package com.mes.gotogether.services.externalconnections;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.client.RestTemplate;
import com.mes.gotogether.domains.Address;
import com.mes.gotogether.domains.NomatimOpenStreetMapQuery;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NomatimApiUnitTest {

    RestTemplate restTemplate;
    NomatimMapLocation nomatimMapLocation;
	
    @Mock
    private NomatimOpenStreetMapQuery nomatimOpenStreetMapQueries;
    @Mock
    private Address address;
    /*
    @BeforeEach
    public void setUp() {
    	restTemplate = new RestTemplate();
    	nomatimMapLocation = new NomatimMapLocation(restTemplate);
    	
        System.out.println("@BeforeEach is called!");
        MockitoAnnotations.initMocks(this);
        
        // Create User 1
        address = new Address();
        address.setStreetName("Parklaan");
        address.setHouseNumber("103");
        address.setCity("Sassenheim");
        address.setCountry("The Netherlands");
        address.setZipcode("2171ED");
        address.setId((ObjectId) new ObjectIdGenerator().generate());
    }

    @Test
    public void testMockCreation() {

        assertNotNull(address);
        assertNotNull(nomatimMapLocation);
        assertNotNull(restTemplate);
        assertNotNull(nomatimOpenStreetMapQueries);
    }

    @Test
    public void searchFreeTextAndReturnNonEmptyResult() {
    	
        // Search for free text input
        String searchAddress1 = "Parklaan 103, Sassenheim, 2171ED, The Netherlands";
        String searchAddress2 = "Parklaan 103, Sassenheim, 2171ED";
        String searchAddress3 = "Parklaan 103, 2171ED, The Netherlands";
        String searchAddress4 = "2171ED, The Netherlands";
        
        Optional<Double[]> queryResult1 = nomatimMapLocation
        											.getAddressLongitudeAndLatitude(searchAddress1);
        Optional<Double[]> queryResult2 = nomatimMapLocation
        											.getAddressLongitudeAndLatitude(searchAddress2); 
        Optional<Double[]> queryResult3 = nomatimMapLocation
        											.getAddressLongitudeAndLatitude(searchAddress3); 
        Optional<Double[]> queryResult4 = nomatimMapLocation
        											.getAddressLongitudeAndLatitude(searchAddress4); 
        
        assertFalse(queryResult1.isEmpty());
        assertArrayEquals(new Double[] {52.2282149,4.5296537}, queryResult1.get());
        assertFalse(queryResult2.isEmpty());
        assertArrayEquals(new Double[] {52.2282149,4.5296537}, queryResult2.get());
        assertFalse(queryResult3.isEmpty());
        assertArrayEquals(new Double[] {52.2282149,4.5296537}, queryResult3.get());
        assertFalse(queryResult4.isEmpty());
        assertArrayEquals(new Double[] {52.2275187181818,4.52883372727273}, queryResult4.get());
    }
    
    @Test
    public void getNullResultWithEmptyText() {
    	String searchAddress1 = "   ";
        
        Optional<Double[]> queryResult = nomatimMapLocation
        											.getAddressLongitudeAndLatitude(searchAddress1);
        
        assertTrue(queryResult.isEmpty());
    }

    @Test
    public void searchByAddressAndReturnNonEmptyResult() {
    	
        Optional<Double[]> queryResult = nomatimMapLocation
        											.getAddressLongitudeAndLatitude(address);
       
        assertFalse(queryResult.isEmpty());
        assertArrayEquals(new Double[] {52.2282149,4.5296537}, queryResult.get());
    }
    
    @Test
    public void getNullResultWithNullAddress() {
        Optional<Double[]> queryResult = nomatimMapLocation
				.getAddressLongitudeAndLatitude(null);

        assertTrue(queryResult.isEmpty());
    }
    
    @Test
    public void getNullResultWithAnObjectDifferentThanAddressAndString() {
        Optional<Double[]> queryResult = nomatimMapLocation
				.getAddressLongitudeAndLatitude(new Integer[] {1,2,3});

        assertTrue(queryResult.isEmpty());
    }


    */
}


