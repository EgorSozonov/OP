namespace O7;

enum ASCII : byte {
    emptyNULL,                                      // NULL
    emptySOH,                                       // Start of Heading
    emptySTX,                                       // Start of Text
    emptyETX,                                       // End of Text
    emptyEOT,                                       // End of Transmission
    emptyENQ,                                       // Enquiry
    emptyACK,                                       // Acknowledgement
    emptyBEL,                                       // Bell
    emptyBS,                                        // Backspace
    emptyTAB,                                       // Horizontal Tab
    emptyLF,                                        // Line Feed
    emptyVT,                                        // Vertical Tab
    emptyFF,                                        // Form Feed
    emptyCR,                                        // Carriage Return
    emptySO,                                        // Shift Out
    emptySI,                                        // Shift In
    emptyDLE,                                       // Data Link Escape
    emptyDC1,                                       // Device Control 1
    emptyDC2,                                       // Device Control 2
    emptyDC3,                                       // Device Control 3
    emptyDC4,                                       // Device Control 4
    emptyNAK,                                       // Negative Acknowledgement
    emptySYN,                                       // Synchronous Idle
    emptyETB,                                       // End of Transmission Block
    emptyCANCEL,                                    // Cancel
    emptyENDMEDIUM,                                 // End of Medium
    emptySUB,                                       // Substitute
    emptyESCAPE,                                    // Escape
    emptySF,                                        // File Separator
    emptyGS,                                        // Group Separator
    emptyRS,                                        // Record Separator
    emptyUS,                                        // Unit Separator

    //misc characters

    space,                                          // space
    exclamationMark,                                // !
    quotationMarkDouble,                            // "
    hashtag,                                        // #
    dollar,                                         // $
    percent,                                        // %
    ampersand,                                      // &
    quotationMarkSingle,                            // '
    parenthesisOpen,                                // (
    parenthesisClose,                               // )
    asterisk,                                       // *
    plus,                                           // +
    comma,                                          // ,
    minus,                                          // -
    dot,                                            // .
    slashForward,                                   // /
    digit0,                                         // 0
    digit1,                                         // 1
    digit2,                                         // 2
    digit3,                                         // 3
    digit4,                                         // 4
    digit5,                                         // 5
    digit6,                                         // 6
    digit7,                                         // 7
    digit8,                                         // 8
    digit9,                                         // 9
    colon,                                          // :
    colonSemi,                                      // ;
    lessThan,                                       // <
    equalTo,                                        // =
    greaterThan,                                    // >
    questionMark,                                   // ?
    singAt,                                         // @

    //upper case alphabet

    aUpper,                                         // A
    bUpper,                                         // B
    cUpper,                                         // C
    dUpper,                                         // D
    eUpper,                                         // E
    fUpper,                                         // F
    gUpper,                                         // G
    hUpper,                                         // H
    iUpper,                                         // I
    jUpper,                                         // J
    kUpper,                                         // K
    lUpper,                                         // L
    mUpper,                                         // M
    nUpper,                                         // N
    oUpper,                                         // O
    pUpper,                                         // P
    qUpper,                                         // Q
    rUpper,                                         // R
    sUpper,                                         // S
    tUpper,                                         // T
    uUpper,                                         // U
    vUpper,                                         // V
    wUpper,                                         // W
    xUpper,                                         // X
    yUpper,                                         // Y
    zUpper,                                         // Z

    //misc characters

    bracketOpen,                                    // [
    slashBackward,                                  // \
    bracketClose,                                   // ]
    caret,                                          // ^
    underscore,                                     // _
    graveAccent,                                    // `

    //lower case alphabet

    aLower,                                         // a
    bLower,                                         // b
    cLower,                                         // c
    dLower,                                         // d
    eLower,                                         // e
    fLower,                                         // f
    gLower,                                         // g
    hLower,                                         // h
    iLower,                                         // i
    jLower,                                         // j
    kLower,                                         // k
    lLower,                                         // l
    mLower,                                         // m
    nLower,                                         // n
    oLower,                                         // o
    pLower,                                         // p
    qLower,                                         // q
    rLower,                                         // r
    sLower,                                         // s
    tLower,                                         // t
    uLower,                                         // u
    vLower,                                         // v
    wLower,                                         // w
    xLower,                                         // x
    yLower,                                         // y
    zLower,                                         // z

    //misc characters

    curlyOpen,                                      // {
    verticalBar,                                    // |
    curlyClose,                                     // }
    tilde,                                          // ~

    emptyDel,                                       // Delete
}
