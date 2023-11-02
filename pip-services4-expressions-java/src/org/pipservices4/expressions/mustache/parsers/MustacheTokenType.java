package org.pipservices4.expressions.mustache.parsers;

/**
 * Define types of mustache tokens.
 */
public enum MustacheTokenType {
    Unknown,
    Value,
    Variable,
    EscapedVariable,
    Section,
    InvertedSection,
    SectionEnd,
    Partial,
    Comment
}
