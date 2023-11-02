package org.pipservices4.expressions.calculator.tokenizers;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.generic.GenericWordState;

import java.util.Objects;

public class ExpressionWordState extends GenericWordState {
    /**
     * Supported expression keywords.
     */
    public final String[] keywords = new String[]{
            "AND", "OR", "NOT", "XOR", "LIKE", "IS", "IN", "NULL", "TRUE", "FALSE"
    };


    /**
     * Constructs an instance of this class.
     */
    public ExpressionWordState() throws Exception {
        super();

        this.clearWordChars();
        this.setWordChars('a', 'z', true);
        this.setWordChars('A', 'Z', true);
        this.setWordChars('0', '9', true);
        this.setWordChars('_', '_', true);
        this.setWordChars(0x00c0, 0x00ff, true);
        this.setWordChars(0x0100, 0xfffe, true);
    }

    /**
     * Gets the next token from the stream started from the character linked to this state.
     *
     * @param scanner   A textual string to be tokenized.
     * @param tokenizer A tokenizer class that controls the process.
     * @return The next token from the top of the stream.
     */
    @Override
    public Token nextToken(IScanner scanner, ITokenizer tokenizer) throws Exception {
        var line = scanner.peekLine();
        var column = scanner.peekColumn();
        var token = super.nextToken(scanner, tokenizer);
        var value = token.getValue().toUpperCase();

        for (var keyword : this.keywords) {
            if (Objects.equals(keyword, value))
                return new Token(TokenType.Keyword, token.getValue(), line, column);
        }
        return token;
    }
}
