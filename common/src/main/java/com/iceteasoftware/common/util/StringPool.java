package com.iceteasoftware.common.util;

public class StringPool {

    public static final String AMPERSAND = "&";
    public static final String AMPERSAND_ENCODED = "&amp;";
    public static final String APOSTROPHE = "'";
    public static final String ASC = "ASC";
    public static String[] ASCII_TABLE = new String[128];
    public static final String AT = "@";
    public static final String BACK_SLASH = "\\";
    public static final String BETWEEN = "BETWEEN";
    public static final String BLANK = "";
    public static final String CARET = "^";
    public static final String CDATA_CLOSE = "]]>";
    public static final String CDATA_OPEN = "<![CDATA[";
    public static final String CLOSE_AND_OPEN_BRACKET = "][";
    public static final String CLOSE_BRACKET = "]";
    public static final String CLOSE_CURLY_BRACE = "}";
    public static final String CLOSE_PARENTHESIS = ")";
    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String COMMA_AND_SPACE = ", ";
    public static final String DASH = "-";
    public static final String DESC = "DESC";
    public static final String DOLLAR = "$";
    public static final String DOLLAR_AND_OPEN_CURLY_BRACE = "${";
    public static final String DOUBLE_APOSTROPHE = "''";
    public static final String DOUBLE_CLOSE_BRACKET = "]]";
    public static final String DOUBLE_CLOSE_CURLY_BRACE = "}}";
    public static final String DOUBLE_DASH = "--";
    public static final String DOUBLE_OPEN_BRACKET = "[[";
    public static final String DOUBLE_OPEN_CURLY_BRACE = "{{";
    public static final String DOUBLE_PERIOD = "..";
    public static final String DOUBLE_QUOTE = "\"\"";
    public static final String DOUBLE_SLASH = "//";
    public static final String DOUBLE_SPACE = "  ";
    public static final String DOUBLE_UNDERLINE = "__";
    public static final String END_ROOT = "</root>";
    public static final String EQUAL = "=";
    public static final String ESCAPE = "escape '!'";
    public static final String EXCLAMATION = "!";
    public static final String FALSE = "false";
    public static final String FORWARD_SLASH = "/";
    public static final String FOUR_SPACES = "    ";
    public static final String GRAVE_ACCENT = "`";
    public static final String GREATER_THAN = ">";
    public static final String GREATER_THAN_OR_EQUAL = ">=";
    public static final String GT = "&gt;";
    public static final String INVERTED_EXCLAMATION = "\u00A1";
    public static final String INVERTED_QUESTION = "\u00BF";
    public static final String IS_NOT_NULL = "IS NOT NULL";
    public static final String IS_NULL = "IS NULL";
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String LESS_THAN = "<";
    public static final String LESS_THAN_OR_EQUAL = "<=";
    public static final String LOCALHOST = "localhost";
    public static final String LIKE = "LIKE";
    public static final String LT = "&lt;";
    public static final String MINUS = "-";
    public static final String N_A = "N/A";
    public static final String NBSP = "&nbsp;";
    public static final String NEW_LINE = "\n";
    public static final String NONE = "none";
    public static final String NOT_EQUAL = "!=";
    public static final String NOT_LIKE = "NOT LIKE";
    public static final String NULL = "null";
    public static final String OPEN_BRACKET = "[";
    public static final String OPEN_CURLY_BRACE = "{";
    public static final String OPEN_PARENTHESIS = "(";
    public static final String OS_EOL = System.getProperty("line.separator");
    public static final String PERCENT = "%";
    public static final String PERIOD = ".";
    public static final String PIPE = "|";
    public static final String PLUS = "+";
    public static final String POUND = "#";
    public static final String QUESTION = "?";
    public static final String QUOTE = "\"";
    public static final String RETURN = "\r";
    public static final String RETURN_NEW_LINE = "\r\n";
    public static final String ROOT = "<root>";
    public static final String SEMICOLON = ";";
    public static final String SLASH = FORWARD_SLASH;
    public static final String SPACE = " ";
    public static final String STAR = "*";
    public static final String TAB = "\t";
    public static final String THREE_PERIOD = "...";
    public static final String THREE_SPACES = "   ";
    public static final String TILDE = "~";
    public static final String TRUE = "true";
    public static final String UNDERLINE = "_";
    public static final String UTC = "UTC";
    public static final String UTF8 = "UTF-8";
    public static final String XLSX = ".xlsx";
    public static final String ZUL = ".zul";

    static {
        for (int i = 0; i < 128; i++) {
            ASCII_TABLE[i] = String.valueOf((char) i);
        }
    }

}
