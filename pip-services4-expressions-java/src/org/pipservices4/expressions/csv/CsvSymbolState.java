package org.pipservices4.expressions.csv;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.generic.GenericSymbolState;

/**
 * Implements a symbol state to tokenize delimiters in CSV streams.
 */
public class CsvSymbolState extends GenericSymbolState {
    /**
     * Constructs this object with specified parameters.
     */
    public CsvSymbolState() throws Exception {
        super();
        this.add("\n", TokenType.Eol);
        this.add("\r", TokenType.Eol);
        this.add("\r\n", TokenType.Eol);
        this.add("\n\r", TokenType.Eol);
    }

    @Override
    public Token nextToken(IScanner scanner, ITokenizer tokenizer) throws Exception {
        // Optimization...
        var nextSymbol = scanner.read();
        var line = scanner.line();
        var column = scanner.column();

        if (nextSymbol != CsvConstant.LF && nextSymbol != CsvConstant.CR) {
            return new Token(TokenType.Symbol, String.valueOf((char)nextSymbol), line, column);
        } else {
            scanner.unread();
            return super.nextToken(scanner, tokenizer);
        }
    }
}
