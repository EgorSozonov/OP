const std = @import("std");

pub fn main() anyerror!void {
    const a = 15;
    const b = a + 10;
    std.log.info("All your codebase are belong to us.", .{});
}

test "basic test" {
    try std.testing.expectEqual(10, 3 + 7);
}
