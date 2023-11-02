package org.pipservices4.expressions.csv;

import org.pipservices4.expressions.tokenizers.generic.GenericWordState;

import java.util.List;

public class CsvWordState extends GenericWordState {

    /**
     * Constructs this object with specified parameters.
     *
     * @param fieldSeparators Separators for fields in CSV stream.
     * @param quoteSymbols    Delimiters character to quote strings.
     */
    public CsvWordState(List<Integer> fieldSeparators, List<Integer> quoteSymbols) throws Exception {
        super();

        this.clearWordChars();
        this.setWordChars(0x0000, 0xfffe, true);

        this.setWordChars(CsvConstant.CR, CsvConstant.CR, false);
        this.setWordChars(CsvConstant.LF, CsvConstant.LF, false);

        for (var fieldSeparator : fieldSeparators)
            this.setWordChars(fieldSeparator, fieldSeparator, false);

        for (var quoteSymbol : quoteSymbols)
            this.setWordChars(quoteSymbol, quoteSymbol, false);
    }
}
