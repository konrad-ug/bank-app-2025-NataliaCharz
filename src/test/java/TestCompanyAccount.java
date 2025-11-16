import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
