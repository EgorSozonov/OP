namespace O7Test;
using NUnit.Framework;  
using O7;

[TestFixture]
public class UnitTest1 {
    [Test]
    public void TestMethod1() {
        var lexer = new Lexer();
        var input = new byte[] {1, 123, 20, 207, 147, 122};
        var i = lexer.findEndOfStringLiteral(1, input);
        Assert.AreEqual(i, 5);
    }
}