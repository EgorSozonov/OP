const std = @import("std");

/// The type for non-zero-terminated strings carrying their length.
/// Encoding is UCS-2 Big-Endian.
const String = struct {
    len: i32,
    content: *[]u16,
};

/// Flag that describes what to do when encountering a Unicode char that cannot
/// be represented in UCS-2: ignore it, substitute it with a question mark char,
/// or panic and stop the conversion.
const ErrorHandling = enum {
    Ignore,
    Substitute,
    Panic,
};

pub fn ucs(utf8: *const[:0] u8, errorHandling: ErrorHandling, alloc: Allocator) !String {
    const resultLength = determineLengthOfUnicode(utf8);
    const string = try allocator.alloc([]const u16, @sizeOf(String)); 
    const bytes = try allocator.alloc([]const u16, resultLength);
    string.len = resultLength;
    string.content = &bytes;
    return string;
}

const StringLiteralError = error {
    zeroChar,
    invalidSequence,
};

fn determineLengthOfUnicode(utf8: *const[:0] u8) StringLiteralError!i32 {
    var result = 0;
    // Length of the current byte sequence (1 to 4) determined by its first byte as follows:
    // 0...     => 1
    // 110...   => 2
    // 1110...  => 3
    // 11110... => 4
    var lenCurrSequence = 1;
    var indInSequence = 0;

    for (utf8) | byte, i | {
        if (byte == '\0' && (i < utf8.length - 1)) {
            return StringLiteralError.zeroChar;
        } else if (indInSequence == lenCurrSequence - 1) {
            lenCurrSequence = if (byte & 0x80 == 0) 1 
                              else if (byte & 0xC0 == 0xC0) 2
                              else if (byte & 0xE0 == 0xE0) 3
                              else if (byte & 0xF0 == 0xF0) 4
                              else {
                                  return StringLiteralError.invalidSequence;
                              };
            indInSequence = 0;
            result += 1;
        }
        indInSequence += 1;
        
    }
    return result;

}

test "basic test" {
    try std.testing.expectEqual(10, 3 + 7);
}