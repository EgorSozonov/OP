#ifndef ASCII_H
#define ASCII_H

typedef enum {
    emptyNULL = 0,             // NULL
    emptySOH = 1,              // Start of Heading
    emptySTX = 2,              // Start of Text
    emptyETX = 3,              // End of Text
    emptyEOT = 4,              // End of Transmission
    emptyENQ = 5,              // Enquiry
    emptyACK = 6,              // Acknowledgement
    emptyBEL = 7,              // Bell
    emptyBS = 8,               // Backspace
    emptyTAB = 9,              // Horizontal Tab
    emptyLF = 10,              // Line Feed
    emptyVT = 11,              // Vertical Tab
    emptyFF = 12,              // Form Feed
    emptyCR = 13,              // Carriage Return
    emptySO = 14,              // Shift Out
    emptySI = 15,              // Shift In
    emptyDLE = 16,             // Data Link Escape
    emptyDC1 = 17,             // Device Control 1
    emptyDC2 = 18,             // Device Control 2
    emptyDC3 = 19,             // Device Control 3
    emptyDC4 = 20,             // Device Control 4
    emptyNAK = 21,             // Negative Acknowledgement
    emptySYN = 22,             // Synchronous Idle
    emptyETB = 23,             // End of Transmission Block
    emptyCANCEL = 24,          // Cancel
    emptyENDMEDIUM = 25,       // End of Medium
    emptySUB = 26,             // Substitute
    emptyESCAPE = 27,          // Escape
    emptySF = 28,              // File Separator
    emptyGS = 29,              // Group Separator
    emptyRS = 30,              // Record Separator
    emptyUS = 31,              // Unit Separator

    //misc characters

    space = 32,                // space
    exclamationMark = 33,      // !
    quotationMarkDouble = 34,  // "
    hashtag = 35,              // #
    dollar = 36,               // $
    percent = 37,              // %
    ampersand = 38,            // &
    quotationMarkSingle = 39,  // '
    parenthesisOpen = 40,      // (
    parenthesisClose = 41,     // )
    asterisk = 42,             // *
    plus = 43,                 // +
    comma = 44,                //  = ,
    minus = 45,                // -
    dot = 46,                  // .
    slashForward = 47,         // /
    digit0 = 48,               // 0
    digit1 = 49,               // 1
    digit2 = 50,               // 2
    digit3 = 51,               // 3
    digit4 = 52,               // 4
    digit5 = 53,               // 5
    digit6 = 54,               // 6
    digit7 = 55,               // 7
    digit8 = 56,               // 8
    digit9 = 57,               // 9
    colon = 58,                // :
    colonSemi = 59,            // ,
    lessThan = 60,             // <
    equalTo = 61,              // =
    greaterThan = 62,          // >
    questionMark = 63,         // ?
    singAt = 64,               // @

    //upper case alphabet

    aUpper = 65,               // A
    bUpper = 66,               // B
    cUpper = 67,               // C
    dUpper = 68,               // D
    eUpper = 69,               // E
    fUpper = 70,               // F
    gUpper = 71,               // G
    hUpper = 72,               // H
    iUpper = 73,               // I
    jUpper = 74,               // J
    kUpper = 75,               // K
    lUpper = 76,               // L
    mUpper = 77,               // M
    nUpper = 78,               // N
    oUpper = 79,               // O
    pUpper = 80,               // P
    qUpper = 81,               // Q
    rUpper = 82,               // R
    sUpper = 83,               // S
    tUpper = 84,               // T
    uUpper = 85,               // U
    vUpper = 86,               // V
    wUpper = 87,               // W
    xUpper = 88,               // X
    yUpper = 89,               // Y
    zUpper = 90,               // Z

    //misc characters

    bracketOpen = 91,          // [
    slashBackward = 92,        // '\'
    bracketClose = 93,         // ]
    caret = 94,                // ^
    underscore = 95,           // _
    graveAccent = 96,          // `

    //lower case alphabet

    aLower = 97,               // a
    bLower = 98,               // b
    cLower = 99,               // c
    dLower = 100,              // d
    eLower = 101,              // e
    fLower = 102,              // f
    gLower = 103,              // g
    hLower = 104,              // h
    iLower = 105,              // i
    jLower = 106,              // j
    kLower = 107,              // k
    lLower = 108,              // l
    mLower = 109,              // m
    nLower = 110,              // n
    oLower = 111,              // o
    pLower = 112,              // p
    qLower = 113,              // q
    rLower = 114,              // r
    sLower = 115,              // s
    tLower = 116,              // t
    uLower = 117,              // u
    vLower = 118,              // v
    wLower = 119,              // w
    xLower = 120,              // x
    yLower = 121,              // y
    zLower = 122,              // z

    //misc characters

    curlyOpen = 123,           // {
    verticalBar = 124,         // |
    curlyClose = 125,          // }
    tilde = 126,               // ~
    emptyDel = 127,            // Delete
} ASCII;


#endif
