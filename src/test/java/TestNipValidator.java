import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNipValidator {

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
}
