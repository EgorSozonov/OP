#include "Lexer.h"
#include "../utils/String.h"
#include "../utils/Either.h"
#include "../utils/EitherHeader.h"
#include "Expr.h"
#include <stdbool.h>

LexResult lexicallyAnalyze(String* inp) {
    String *errMsg = NULL;


    if (inp == NULL || inp->length < 1) return (LexResult){ .result = NULL, .errMsg = errMsg};


    Expr tmp = (Expr){.tag = IntToken, .content = {.intToken = 5}};
    return (LexResult){ .result = NULL, .errMsg = errMsg};
}

/**
 * The ASCII notation for the lowest 64-bit integer, -9_223_372_036_854_775_808
 */
const char minInt[] = {
        57,
        50, 50, 51,
        51, 55, 50,
        48, 51, 54,
        56, 53, 52,
        55, 55, 53,
        56, 48, 56,
    };

/**
 * The ASCII notation for the highest 64-bit integer, 9_223_372_036_854_775_807
 */
const char maxInt[] = {
        57,
        50, 50, 51,
        51, 55, 50,
        48, 51, 54,
        56, 53, 52,
        55, 55, 53,
        56, 48, 55,
    };

DEFINE_EITHER(String, ExprInd)

EitherStringExprInd lexNumber(String* inp, int start, int walkLen, Arena* ar) {
    if (start > walkLen) return leftStringExprInd(allocateString(ar, sizeof "Unexpected end of input" - 1, "Unexpected end of input"));
    int ind = start;
    char currByte = inp->content[start];

}



// static Either<LexError, Tuple<ExprBase, Integer>> lexNumber(byte[] inp, int start, int walkLen) {


//     int ind = start;
//     byte currByte = inp[start];

//     boolean isNegative = currByte == ASCII.underscore;
//     if (isNegative) {
//         ++ind;
//         currByte = inp[ind];
//         if (currByte < ASCII.digit0 || currByte > ASCII.digit9) {
//             return Either.left(new LexError("Unexpected symbol error: Expected a digit but got char " + currByte));
//         }
//     }
//     boolean isFloating = false;

//     while (ind <= walkLen &&
//         ((currByte >= ASCII.digit0 && currByte <= ASCII.digit9)
//         || currByte == ASCII.underscore)) {
//         ++ind;
//         if (ind <= walkLen) currByte = inp[ind];
//     }

//     // In case the next symbol instanceof a dot, this will be a floating-point number
//     if (ind < walkLen && inp[ind] == ASCII.dot) {
//         val nextBt = inp[ind + 1];
//         if (nextBt >= ASCII.digit0 && nextBt <= ASCII.digit9) {
//             isFloating = true;
//             // Skipping the dot
//             ++ind;
//             currByte = nextBt;
//             while (ind <= walkLen &&
//                     ((currByte >= ASCII.digit0 && currByte <= ASCII.digit9)
//                     || currByte == ASCII.underscore)) {
//                 ++ind;
//                 if (ind <= walkLen) currByte = inp[ind];
//             }
//         }
//     }
//     int startingDigit = isNegative ? start + 1 : start;
//     val mbNumber = isFloating ? lexFloat(inp, startingDigit, ind - 1, isNegative)
//                               : lexInt(inp, startingDigit, ind - 1, isNegative);
//     final int finalInd = ind;
//     return mbNumber.bimap(x -> x, x -> new Tuple<ExprBase, Integer>(x, finalInd));
// }
