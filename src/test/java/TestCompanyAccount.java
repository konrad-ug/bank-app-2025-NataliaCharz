import org.junit.jupiter.api.Test;
import validator.NipValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompanyAccount {

    private CompanyAccount companyAccount;

//    @BeforeEach
//    public void setUp(){
//        String companyName = "ABC";
//        String nip = "123456789012";
//        companyAccount = new CompanyAccount(companyName, nip);
//    }

    @Test
    public void testNipIsSetInvalidWhenTooShort(){
        //given
        String companyName = "ABC";
        String nip = "123";
        //when
        companyAccount = new CompanyAccount(companyName, nip);
        //then
        assertEquals("Invalid", companyAccount.getIdentification());
    }

    @Test
    public void testNipIsSetInvalidWhenTooLong(){
        //given
        String companyName = "ABC";
        String nip = "123123123123123";
        //when
        companyAccount = new CompanyAccount(companyName, nip);
        //then
        assertEquals("Invalid", companyAccount.getIdentification());
    }

    @Test
    public void testNipIsSetInvalidWhenContainsLetters(){
        //given
        String companyName = "ABC";
        String nip = "abc";
        //when
        companyAccount = new CompanyAccount(companyName, nip);
        //then
        assertEquals("Invalid", companyAccount.getIdentification());
    }

    @Test
    public void testNipIsCorrect(){
        //given
        String nip = "1234567890";
        //when
        String properNip = NipValidator.validateNip(nip);
        //then
        assertEquals("1234567890", properNip);
    }

    @Test
    public void testNipInvalidWhenIsTooLong(){
        //given
        String nip = "12345678901";
        //when
        String properNip = NipValidator.validateNip(nip);
        //then
        assertEquals("Invalid", properNip);
    }

    @Test
    public void testNipInvalidWhenIsTooShort(){
        //given
        String nip = "123";
        //when
        String properNip = NipValidator.validateNip(nip);
        //then
        assertEquals("Invalid", properNip);
    }

    @Test
    public void testNipInvalidWhenContainsLetters(){
        //given
        String nip = "ABCEDGFHIJ";
        //when
        String properNip = NipValidator.validateNip(nip);
        //then
        assertEquals("Invalid", properNip);
    }

    @Test
    public void testNipInvalidWhenNull() {
        //given
        String nip = null;
        //when
        String invalid = NipValidator.validateNip(nip);
        //then
        assertEquals("Invalid", invalid);
    }
}
