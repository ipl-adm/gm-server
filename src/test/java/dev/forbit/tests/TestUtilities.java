package dev.forbit.tests;

import dev.forbit.server.utilities.GMLInputBuffer;
import dev.forbit.server.utilities.GMLOutputBuffer;
import dev.forbit.server.utilities.Utilities;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("1 - Test Utilities")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUtilities {

    private ByteBuffer createMockBuffer() {
        // create mock buffer
        ByteBuffer buffer = Utilities.newBuffer();
        buffer.putDouble(64.0d);
        buffer.put((byte) 8);
        buffer.putInt(-200);
        buffer.put("hello world".getBytes());
        buffer.put((byte) 0x00);
        buffer.putShort((short) 33);
        buffer.putFloat(43.5f);
        buffer.put((byte) 1);
        buffer.put((byte) 0);
        buffer.rewind();
        return buffer;
    }

    @Test
    @Order(1)
    @DisplayName("Test Creating New Buffer")
    public void testNewBuffer() {
        assertEquals(Utilities.newBuffer().limit(), Utilities.DEFAULT_PACKET_SIZE);
        assertEquals(createMockBuffer().limit(), Utilities.DEFAULT_PACKET_SIZE);
        assertEquals(Utilities.newBuffer().position(), 0);
        assertEquals(createMockBuffer().position(), 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123123", "1231232131", "Hello World!", "🎈🎈🎈🎈 emoji👓🥼🎨🖼", "Testing 123 -123123 ... asdasd", "Escape characters " + "go " + "\n\n\n" + "\r\t brrr"})
    @Order(2)
    @DisplayName("Test Getting String")
    public void testGettingString(String s) {
        System.out.println("value: " + s);
        ByteBuffer buffer = Utilities.newBuffer();
        buffer.put(s.getBytes());
        buffer.put((byte) 0x00);
        System.out.println("bytes: " + Arrays.toString(s.getBytes()));
        buffer.rewind();
        Optional<String> string = Utilities.getNextString(buffer);
        assertTrue(string.isPresent());
        System.out.println("obtained string: " + string.get());
        assertEquals(s, string.get());
    }

    @Test
    @Order(3)
    @DisplayName("Test Printing ByteBuffer")
    public void testPrintingByteBuffer() {
        ByteBuffer testBuffer = Utilities.newBuffer();
        testBuffer.put((byte) 32);
        testBuffer.put((byte) 16);
        testBuffer.put((byte) 8);
        testBuffer.put((byte) 65);
        testBuffer.rewind();
        String string = Utilities.getBuffer(testBuffer);
        assertEquals(string, "32,16,8,65,");
    }

    @Test
    @Order(4)
    @DisplayName("Test Printing ByteBuffer as hex value")
    public void testPrintingHexBuffer() {
        // first test
        ByteBuffer testBuffer = Utilities.newBuffer();
        testBuffer.put((byte) 32);
        testBuffer.put((byte) 16);
        testBuffer.put((byte) 8);
        testBuffer.put((byte) 65);
        testBuffer.rewind();
        String first = Utilities.getHexBuffer(testBuffer);
        assertEquals(first, "20, 10, 08, 41");
        // second test
        String second = Utilities.getHexBuffer(createMockBuffer());
        assertEquals(second, "50, 40, 08, 38, FF, FF, FF, 68, 65, 6C, 6C, 6F, 20, 77, 6F, 72, 6C, 64, 21, 2E, 42, 01");
    }

    @Test
    @Order(5)
    @DisplayName("Test GMLInputBuffer")
    public void testInputBuffer() {
        // construct input buffer
        GMLInputBuffer input = new GMLInputBuffer(createMockBuffer());

        // read from input buffer
        assertAll(
                () -> assertEquals(input.readF64(), 64.0d),
                () -> assertEquals(input.readS8(), 8),
                () -> assertEquals(input.readS32(), -200),
                () -> {
                    Optional<String> string = input.readString();
                    assertTrue(string.isPresent());
                    assertEquals(string.get(), "hello world");
                },
                () -> assertEquals(input.readS16(), 33),
                () -> assertEquals(input.readF32(), 43.5f),
                () -> assertTrue(input.readBool()),
                () -> assertFalse(input.readBool()));

    }

    @Test
    @DisplayName("Test GMLOutputBuffer")
    @Order(6)
    public void testOutputBuffer() {
        GMLOutputBuffer output = new GMLOutputBuffer();
        output.writeF64(64.0d);
        output.writeS8((byte) 8);
        output.writeS32(-200);
        output.writeString("hello world");
        output.writeS16((short) 33);
        output.writeF32(43.5f);
        output.writeBool(true);
        output.writeBool(false);
        assertEquals(output.getBuffer(), createMockBuffer());
    }
}
