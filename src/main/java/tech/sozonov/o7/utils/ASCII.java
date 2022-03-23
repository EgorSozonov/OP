package main.java.tech.sozonov.o7.utils;

public class ASCII {
    static final byte emptyNULL = 0;                                      // NULL
    static final byte emptySOH = 1;                                       // Start of Heading
    static final byte emptySTX = 2;                                       // Start of Text
    static final byte emptyETX = 3;                                       // End of Text
    static final byte emptyEOT = 4;                                       // End of Transmission
    static final byte emptyENQ = 5;                                       // Enquiry
    static final byte emptyACK = 6;                                       // Acknowledgement
    static final byte emptyBEL = 7;                                       // Bell
    static final byte emptyBS = 8;                                        // Backspace
    static final byte emptyTAB = 9;                                       // Horizontal Tab
    static final byte emptyLF = 10;                                        // Line Feed
    static final byte emptyVT = 11;                                        // Vertical Tab
    static final byte emptyFF = 12;                                        // Form Feed
    static final byte emptyCR = 13;                                        // Carriage Return
    static final byte emptySO = 14;                                        // Shift Out
    static final byte emptySI = 15;                                        // Shift In
    static final byte emptyDLE = 16;                                       // Data Link Escape
    static final byte emptyDC1 = 17;                                       // Device Control 1
    static final byte emptyDC2 = 18;                                       // Device Control 2
    static final byte emptyDC3 = 19;                                       // Device Control 3
    static final byte emptyDC4 = 20;                                       // Device Control 4
    static final byte emptyNAK = 21;                                       // Negative Acknowledgement
    static final byte emptySYN = 22;                                       // Synchronous Idle
    static final byte emptyETB = 23;                                       // End of Transmission Block
    static final byte emptyCANCEL = 24;                                    // Cancel
    static final byte emptyENDMEDIUM = 25;                                 // End of Medium
    static final byte emptySUB = 26;                                       // Substitute
    static final byte emptyESCAPE = 27;                                    // Escape
    static final byte emptySF = 28;                                        // File Separator
    static final byte emptyGS = 29;                                        // Group Separator
    static final byte emptyRS = 30;                                        // Record Separator
    static final byte emptyUS = 31;                                        // Unit Separator

    //misc characters

    static final byte space = 32;                                          // space
    static final byte exclamationMark = 33;                                // !
    static final byte quotationMarkDouble = 34;                            // "
    static final byte hashtag = 35;                                        // #
    static final byte dollar = 36;                                         // $
    static final byte percent = 37;                                        // %
    static final byte ampersand = 38;                                      // &
    static final byte quotationMarkSingle = 39;                            // '
    static final byte parenthesisOpen = 40;                                // (
    static final byte parenthesisClose = 41;                               // )
    static final byte asterisk = 42;                                       // *
    static final byte plus = 43;                                           // +
    static final byte comma = 44;                                          //  = ;
    static final byte minus = 45;                                          // -
    static final byte dot = 46;                                            // .
    static final byte slashForward = 47;                                   // /
    static final byte digit0 = 48;                                         // 0
    static final byte digit1 = 49;                                         // 1
    static final byte digit2 = 50;                                         // 2
    static final byte digit3 = 51;                                         // 3
    static final byte digit4 = 52;                                         // 4
    static final byte digit5 = 53;                                         // 5
    static final byte digit6 = 54;                                         // 6
    static final byte digit7 = 55;                                         // 7
    static final byte digit8 = 56;                                         // 8
    static final byte digit9 = 57;                                         // 9
    static final byte colon = 58;                                          // :
    static final byte colonSemi = 59;                                      // ;
    static final byte lessThan = 60;                                       // <
    static final byte equalTo = 61;                                        // =
    static final byte greaterThan = 62;                                    // >
    static final byte questionMark = 63;                                   // ?
    static final byte singAt = 64;                                         // @

    //upper case alphabet

    static final byte aUpper = 65;                                         // A
    static final byte bUpper = 66;                                         // B
    static final byte cUpper = 67;                                         // C
    static final byte dUpper = 68;                                         // D
    static final byte eUpper = 69;                                         // E
    static final byte fUpper = 70;                                         // F
    static final byte gUpper = 71;                                         // G
    static final byte hUpper = 72;                                         // H
    static final byte iUpper = 73;                                         // I
    static final byte jUpper = 74;                                         // J
    static final byte kUpper = 75;                                         // K
    static final byte lUpper = 76;                                         // L
    static final byte mUpper = 77;                                         // M
    static final byte nUpper = 78;                                         // N
    static final byte oUpper = 79;                                         // O
    static final byte pUpper = 80;                                         // P
    static final byte qUpper = 81;                                         // Q
    static final byte rUpper = 82;                                         // R
    static final byte sUpper = 83;                                         // S
    static final byte tUpper = 84;                                         // T
    static final byte uUpper = 85;                                         // U
    static final byte vUpper = 86;                                         // V
    static final byte wUpper = 87;                                         // W
    static final byte xUpper = 88;                                         // X
    static final byte yUpper = 89;                                         // Y
    static final byte zUpper = 90;                                         // Z

    //misc characters

    static final byte bracketOpen = 91;                                    // [
    static final byte slashBackward = 92;                                  // \
    static final byte bracketClose = 93;                                   // ]
    static final byte caret = 94;                                          // ^
    static final byte underscore = 95;                                     // _
    static final byte graveAccent = 96;                                    // `

    //lower case alphabet

    static final byte aLower = 97;                                         // a
    static final byte bLower = 98;                                         // b
    static final byte cLower = 99;                                         // c
    static final byte dLower = 100;                                         // d
    static final byte eLower = 101;                                         // e
    static final byte fLower = 102;                                         // f
    static final byte gLower = 103;                                         // g
    static final byte hLower = 104;                                         // h
    static final byte iLower = 105;                                         // i
    static final byte jLower = 106;                                         // j
    static final byte kLower = 107;                                         // k
    static final byte lLower = 108;                                         // l
    static final byte mLower = 109;                                         // m
    static final byte nLower = 110;                                         // n
    static final byte oLower = 111;                                         // o
    static final byte pLower = 112;                                         // p
    static final byte qLower = 113;                                         // q
    static final byte rLower = 114;                                         // r
    static final byte sLower = 115;                                         // s
    static final byte tLower = 116;                                         // t
    static final byte uLower = 117;                                         // u
    static final byte vLower = 118;                                         // v
    static final byte wLower = 119;                                         // w
    static final byte xLower = 120;                                         // x
    static final byte yLower = 121;                                         // y
    static final byte zLower = 122;                                         // z

    //misc characters

    static final byte curlyOpen = 123;                                      // {
    static final byte verticalBar = 124;                                    // |
    static final byte curlyClose = 125;                                     // }
    static final byte tilde = 126;                                          // ~
    static final byte emptyDel = 127;                                       // Delete
}
