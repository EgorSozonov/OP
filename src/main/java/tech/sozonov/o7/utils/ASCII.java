package main.java.tech.sozonov.o7.utils;

public class ASCII {
    public static final byte emptyNULL = 0;                                      // NULL
    public static final byte emptySOH = 1;                                       // Start of Heading
    public static final byte emptySTX = 2;                                       // Start of Text
    public static final byte emptyETX = 3;                                       // End of Text
    public static final byte emptyEOT = 4;                                       // End of Transmission
    public static final byte emptyENQ = 5;                                       // Enquiry
    public static final byte emptyACK = 6;                                       // Acknowledgement
    public static final byte emptyBEL = 7;                                       // Bell
    public static final byte emptyBS = 8;                                        // Backspace
    public static final byte emptyTAB = 9;                                       // Horizontal Tab
    public static final byte emptyLF = 10;                                        // Line Feed
    public static final byte emptyVT = 11;                                        // Vertical Tab
    public static final byte emptyFF = 12;                                        // Form Feed
    public static final byte emptyCR = 13;                                        // Carriage Return
    public static final byte emptySO = 14;                                        // Shift Out
    public static final byte emptySI = 15;                                        // Shift In
    public static final byte emptyDLE = 16;                                       // Data Link Escape
    public static final byte emptyDC1 = 17;                                       // Device Control 1
    public static final byte emptyDC2 = 18;                                       // Device Control 2
    public static final byte emptyDC3 = 19;                                       // Device Control 3
    public static final byte emptyDC4 = 20;                                       // Device Control 4
    public static final byte emptyNAK = 21;                                       // Negative Acknowledgement
    public static final byte emptySYN = 22;                                       // Synchronous Idle
    public static final byte emptyETB = 23;                                       // End of Transmission Block
    public static final byte emptyCANCEL = 24;                                    // Cancel
    public static final byte emptyENDMEDIUM = 25;                                 // End of Medium
    public static final byte emptySUB = 26;                                       // Substitute
    public static final byte emptyESCAPE = 27;                                    // Escape
    public static final byte emptySF = 28;                                        // File Separator
    public static final byte emptyGS = 29;                                        // Group Separator
    public static final byte emptyRS = 30;                                        // Record Separator
    public static final byte emptyUS = 31;                                        // Unit Separator

    //misc characters

    public static final byte space = 32;                                          // space
    public static final byte exclamationMark = 33;                                // !
    public static final byte quotationMarkDouble = 34;                            // "
    public static final byte hashtag = 35;                                        // #
    public static final byte dollar = 36;                                         // $
    public static final byte percent = 37;                                        // %
    public static final byte ampersand = 38;                                      // &
    public static final byte quotationMarkSingle = 39;                            // '
    public static final byte parenthesisOpen = 40;                                // (
    public static final byte parenthesisClose = 41;                               // )
    public static final byte asterisk = 42;                                       // *
    public static final byte plus = 43;                                           // +
    public static final byte comma = 44;                                          //  = ;
    public static final byte minus = 45;                                          // -
    public static final byte dot = 46;                                            // .
    public static final byte slashForward = 47;                                   // /
    public static final byte digit0 = 48;                                         // 0
    public static final byte digit1 = 49;                                         // 1
    public static final byte digit2 = 50;                                         // 2
    public static final byte digit3 = 51;                                         // 3
    public static final byte digit4 = 52;                                         // 4
    public static final byte digit5 = 53;                                         // 5
    public static final byte digit6 = 54;                                         // 6
    public static final byte digit7 = 55;                                         // 7
    public static final byte digit8 = 56;                                         // 8
    public static final byte digit9 = 57;                                         // 9
    public static final byte colon = 58;                                          // :
    public static final byte colonSemi = 59;                                      // ;
    public static final byte lessThan = 60;                                       // <
    public static final byte equalTo = 61;                                        // =
    public static final byte greaterThan = 62;                                    // >
    public static final byte questionMark = 63;                                   // ?
    public static final byte singAt = 64;                                         // @

    //upper case alphabet

    public static final byte aUpper = 65;                                         // A
    public static final byte bUpper = 66;                                         // B
    public static final byte cUpper = 67;                                         // C
    public static final byte dUpper = 68;                                         // D
    public static final byte eUpper = 69;                                         // E
    public static final byte fUpper = 70;                                         // F
    public static final byte gUpper = 71;                                         // G
    public static final byte hUpper = 72;                                         // H
    public static final byte iUpper = 73;                                         // I
    public static final byte jUpper = 74;                                         // J
    public static final byte kUpper = 75;                                         // K
    public static final byte lUpper = 76;                                         // L
    public static final byte mUpper = 77;                                         // M
    public static final byte nUpper = 78;                                         // N
    public static final byte oUpper = 79;                                         // O
    public static final byte pUpper = 80;                                         // P
    public static final byte qUpper = 81;                                         // Q
    public static final byte rUpper = 82;                                         // R
    public static final byte sUpper = 83;                                         // S
    public static final byte tUpper = 84;                                         // T
    public static final byte uUpper = 85;                                         // U
    public static final byte vUpper = 86;                                         // V
    public static final byte wUpper = 87;                                         // W
    public static final byte xUpper = 88;                                         // X
    public static final byte yUpper = 89;                                         // Y
    public static final byte zUpper = 90;                                         // Z

    //misc characters

    public static final byte bracketOpen = 91;                                    // [
    public static final byte slashBackward = 92;                                  // \
    public static final byte bracketClose = 93;                                   // ]
    public static final byte caret = 94;                                          // ^
    public static final byte underscore = 95;                                     // _
    public static final byte graveAccent = 96;                                    // `

    //lower case alphabet

    public static final byte aLower = 97;                                         // a
    public static final byte bLower = 98;                                         // b
    public static final byte cLower = 99;                                         // c
    public static final byte dLower = 100;                                         // d
    public static final byte eLower = 101;                                         // e
    public static final byte fLower = 102;                                         // f
    public static final byte gLower = 103;                                         // g
    public static final byte hLower = 104;                                         // h
    public static final byte iLower = 105;                                         // i
    public static final byte jLower = 106;                                         // j
    public static final byte kLower = 107;                                         // k
    public static final byte lLower = 108;                                         // l
    public static final byte mLower = 109;                                         // m
    public static final byte nLower = 110;                                         // n
    public static final byte oLower = 111;                                         // o
    public static final byte pLower = 112;                                         // p
    public static final byte qLower = 113;                                         // q
    public static final byte rLower = 114;                                         // r
    public static final byte sLower = 115;                                         // s
    public static final byte tLower = 116;                                         // t
    public static final byte uLower = 117;                                         // u
    public static final byte vLower = 118;                                         // v
    public static final byte wLower = 119;                                         // w
    public static final byte xLower = 120;                                         // x
    public static final byte yLower = 121;                                         // y
    public static final byte zLower = 122;                                         // z

    //misc characters

    public static final byte curlyOpen = 123;                                      // {
    public static final byte verticalBar = 124;                                    // |
    public static final byte curlyClose = 125;                                     // }
    public static final byte tilde = 126;                                          // ~
    public static final byte emptyDel = 127;                                       // Delete
}
