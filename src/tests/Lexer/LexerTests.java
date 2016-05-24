package Lexer;


import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by @author AlNat on 24.05.2016.
 * Licensed by Apache License, Version 2.0
 */
public class LexerTests {

    private Tokenizer tokenizer;

    @Test(timeOut = 100)
    @BeforeTest
    public void initTest() throws Exception {
        tokenizer = new Tokenizer();
        Assert.assertEquals(0, tokenizer.GetCurrentTokenNumber() );
    }

    @Test(timeOut = 100)
    public void setTokenTest() throws Exception {
        tokenizer = new Tokenizer();
        tokenizer.SetTokenPosition(10);
        Assert.assertEquals(10, tokenizer.GetCurrentTokenNumber() );
    }

    @Test(timeOut = 100)
    public void Test1() throws Exception {
        tokenizer = new Tokenizer();
        tokenizer.Parse("C:\\Users\\AlNat\\Source\\Studi\\CM\\src\\tests\\Lexer\\test 1.cmm");
        Assert.assertEquals(0, tokenizer.GetCurrentTokenNumber() );

        /*
        for (int i = 1; i < 9; i++) {
            tokenizer.GetNextToken();
            Assert.assertEquals(i, tokenizer.GetCurrentTokenNumber());
        }

        tokenizer.SetTokenPosition(0);
        */

        Assert.assertEquals("int", tokenizer.GetNextToken());

        Assert.assertEquals("main", tokenizer.GetNextToken());

        Assert.assertEquals("(", tokenizer.GetNextToken());

        Assert.assertEquals("void", tokenizer.GetNextToken());

        Assert.assertEquals(")", tokenizer.GetNextToken());

        Assert.assertEquals("{", tokenizer.GetNextToken());

        Assert.assertEquals("return", tokenizer.GetNextToken());

        Assert.assertEquals("0;", tokenizer.GetNextToken());

        Assert.assertEquals("}", tokenizer.GetNextToken());

    }

}
