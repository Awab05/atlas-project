package atlas;

import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MappingTest {
    // ---------User Story 2.2 Tests---------
    @Test
    public void testMismatchedLengthThrows() {
        Mapping mapping = new Mapping();

        String flat1 = "(serve *priest (some congregation))";
        String flat2 = "(serve *soldier (some army (extra)))"; // more tokens than the other string

        assertThrows(IllegalArgumentException.class, () -> {
            mapping.mapTwoFlatStrings(flat1, flat2);
        });
    }

    @Test
    public void testNestedMapping() {
        Mapping mapping = new Mapping();
        String flat1 = "(serve *priest (some congregation (that (perform (for (some god)) (some worship)))))";
        String flat2 = "(serve *soldier (some army (that (perform (for (some leader)) (some conquest)))))";

        HashMap<String, String> result = mapping.mapTwoFlatStrings(flat1, flat2);

        assertEquals(4, result.size());
        assertEquals("*priest", result.get("*soldier"));
        assertEquals("congregation", result.get("army"));
        assertEquals("god", result.get("leader"));
        assertEquals("worship", result.get("conquest"));
    }

    @Test
    public void testInvalidMappingThrows() {
        Mapping mapping = new Mapping();
        String flat1 = "(serve *priest)";
        String flat2 = "(serve priest)"; // missing asterisk

        assertThrows(IllegalArgumentException.class, () -> {
            mapping.mapTwoFlatStrings(flat1, flat2);
        });
    }

    @Test
    public void testNonOneToOneMappingThrows() {
        Mapping mapping = new Mapping();
        String flat1 = "(serve *priest (some god) (some spirit))";
        String flat2 = "(serve *soldier (some leader) (some leader))";

        assertThrows(IllegalArgumentException.class, () -> {
            mapping.mapTwoFlatStrings(flat1, flat2);
        });
    }
}