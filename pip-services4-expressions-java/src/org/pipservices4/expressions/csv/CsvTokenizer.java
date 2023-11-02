package org.pipservices4.expressions.csv;

import org.pipservices4.expressions.tokenizers.AbstractTokenizer;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Implements a tokenizer class for CSV files.
 */
public class CsvTokenizer extends AbstractTokenizer {
    private List<Integer> _fieldSeparators = List.of(",".codePointAt(0));
    private List<Integer> _quoteSymbols = List.of("\"".codePointAt(0));
    private String _endOfLine = "\n\r";

    /**
     * Constructs this object with default parameters.
     */
    public CsvTokenizer() throws Exception {
        super();

        this.setNumberState(null);
        this.setWhitespaceState(null);
        this.setCommentState(null);
        this.setWordState(new CsvWordState(this.getFieldSeparators(), this.getQuoteSymbols()));
        this.setSymbolState(new CsvSymbolState());
        this.setQuoteState(new CsvQuoteState());
        this.assignStates();
    }


    /**
     * Separator for fields in CSV stream.
     */
    public List<Integer> getFieldSeparators() {
        return this._fieldSeparators;
    }

    /**
     * Separator for fields in CSV stream.
     */
    public void setFieldSeparators(List<Integer> value) throws Exception {
        if (value == null)
            throw new NullPointerException("value is null");


        for (var fieldSeparator : value) {
            if (fieldSeparator == CsvConstant.CR
                    || fieldSeparator == CsvConstant.LF
                    || fieldSeparator == CsvConstant.NIL) {
                throw new InvalidParameterException("Invalid field separator.");
            }

            for (var quoteSymbol : this.getQuoteSymbols()) {
                if (fieldSeparator.equals(quoteSymbol)) {
                    throw new InvalidParameterException("Invalid field separator.");
                }
            }
        }

        this._fieldSeparators = value;
        this.setWordState(new CsvWordState(value, this.getQuoteSymbols()));
        this.assignStates();
    }

    /**
     * Separator for rows in CSV stream.
     */
    public String getEndOfLine() {
        return this._endOfLine;
    }

    /**
     * Separator for rows in CSV stream.
     */
    public void setEndOfLine(String value) {
        this._endOfLine = value;
    }

    /**
     * Character to quote strings.
     */
    public List<Integer> getQuoteSymbols() {
        return this._quoteSymbols;
    }

    /**
     * Character to quote strings.
     */
    public void setQuoteSymbols(List<Integer> value) throws Exception {
        if (value == null)
            throw new NullPointerException("value is null");

        for (var quoteSymbol : value) {
            if (quoteSymbol == CsvConstant.CR
                    || quoteSymbol == CsvConstant.LF
                    || quoteSymbol == CsvConstant.NIL) {
                throw new InvalidParameterException("Invalid quote symbol.");
            }

            for (var fieldSeparator : this.getFieldSeparators()) {
                if (quoteSymbol.equals(fieldSeparator)) {
                    throw new InvalidParameterException("Invalid quote symbol.");
                }
            }
        }

        this._quoteSymbols = value;
        this.setWordState(new CsvWordState(this.getFieldSeparators(), value));
        this.assignStates();
    }

    /**
     * Assigns tokenizer states to correct characters.
     */
    private void assignStates() throws Exception {
        this.clearCharacterStates();
        this.setCharacterState(0x0000, 0xfffe, this.getWordState());
        this.setCharacterState(CsvConstant.CR, CsvConstant.CR, this.getSymbolState());
        this.setCharacterState(CsvConstant.LF, CsvConstant.LF, this.getSymbolState());

        for (var fieldSeparator : this.getFieldSeparators())
            this.setCharacterState(fieldSeparator, fieldSeparator, this.getSymbolState());


        for (var quoteSymbol : this.getQuoteSymbols())
            this.setCharacterState(quoteSymbol, quoteSymbol, this.getQuoteState());

    }
}
