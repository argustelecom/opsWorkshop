package ru.argustelecom.box.env.numerationpattern.parser;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ru.argustelecom.system.inf.exception.BusinessException;

public class NumerationPatternLexerTest {

	@Test
	public void testLiteralSuccess() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("aaaabbbb_$");
		assertEquals("aaaabbbb_$", lexemes.get(0).getValue());
	}

	@Test(expected = BusinessException.class)
	public void testIllegalLiteral() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("aaaabbbb_${");
	}

	@Test(expected = BusinessException.class)
	public void testIllegalLiteral1() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("abc>");
	}

	@Test
	public void testSeqSuccess() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("<abc>");
		assertEquals("abc", lexemes.get(0).getValue());
	}

	@Test(expected = BusinessException.class)
	public void testSeqNoClosingBrace() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("<number");
	}

	@Test(expected = BusinessException.class)
	public void testSeqEmpty() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("<>");
	}

	@Test(expected = BusinessException.class)
	public void testSeqInvalidName() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("<numb/er>");
	}

	@Test
	public void testVarFull() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("{abc:YYYYMMDD}");
		assertEquals("abc:YYYYMMDD", lexemes.get(0).getValue());
	}

	@Test
	public void testVarNameOnly() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("{abc}");
		assertEquals("abc", lexemes.get(0).getValue());
	}

	@Test(expected = BusinessException.class)
	public void testVarEmpty() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("{}");
	}

	@Test(expected = BusinessException.class)
	public void testVarBracesNotMatching() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("{abc:YYYY");
	}

	@Test(expected = BusinessException.class)
	public void testVarNoFormat() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("{abc:}");
	}

	@Test(expected = BusinessException.class)
	public void testVarNoVarName() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("{:YYYY}");
	}

	@Test(expected = BusinessException.class)
	public void testVarInvalidFormatSymbol() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("{abc:YYY/Y}");
	}

	@Test
	public void testFullSuccess() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("aabbcc$_09{num:YYMMDD}<AB>");
		assertEquals("aabbcc$_09", lexemes.get(0).getValue());
		assertEquals("num:YYMMDD", lexemes.get(1).getValue());
		assertEquals("AB", lexemes.get(2).getValue());
	}

	@Test
	public void testFullSuccess1() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("{num}<AB12>aabbcc$_01239");
		assertEquals("num", lexemes.get(0).getValue());
		assertEquals("AB12", lexemes.get(1).getValue());
		assertEquals("aabbcc$_01239", lexemes.get(2).getValue());
	}

	@Test(expected = BusinessException.class)
	public void testFullNotMatchingBraces() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("aabbcc$_09<{num:YYMMDD}}<AB>");
	}

	@Test(expected = BusinessException.class)
	public void testFullNoClosingBrace() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("aabbcc$_09{num:YYMMDD<AB>");
	}

	@Test(expected = BusinessException.class)
	public void testFullSpaces() {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan("aabbcc$_09  {num:YYMMDD}<AB>");
	}
}