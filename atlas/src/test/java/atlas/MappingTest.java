package atlas;

import org.junit.jupiter.api.Test;

import atlas.Mapping.AtlasMapper;
import atlas.Mapping.Mapping;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MappingTest {


    // ---------- User Story 2.1 Tests -----------

    @Test
    void testIsMappableNestedTrue() {
        AtlasParser parser = new AtlasParser();
        AtlasMapper mapper = new AtlasMapper();

        AtlasNode node1 = parser.parse("(serve priest (some congregation (that (perform worship))))");
        AtlasNode node2 = parser.parse("(serve soldier (some army (that (perform conquest))))");

        assertTrue(mapper.isMappable(node1, node2));
    }

    @Test
    void testIsMappableFalseDifferentStructure() {
        AtlasParser parser = new AtlasParser();
        AtlasMapper mapper = new AtlasMapper();

        AtlasNode node1 = parser.parse("(serve priest)");
        AtlasNode node2 = parser.parse("(serve soldier (some army))");

        assertFalse(mapper.isMappable(node1, node2));
    }

    @Test
    void testIsMappableFalseDifferentPredicate() {
        AtlasParser parser = new AtlasParser();
        AtlasMapper mapper = new AtlasMapper();

        AtlasNode node1 = parser.parse("(serve priest)");
        AtlasNode node2 = parser.parse("(work soldier)");

        assertFalse(mapper.isMappable(node1, node2));
    }


    @Test
    void testIsMappableNullInput() {
        AtlasParser parser = new AtlasParser();
        AtlasMapper mapper = new AtlasMapper();

        AtlasNode node1 = parser.parse("(serve priest)");

        assertThrows(IllegalArgumentException.class, () -> mapper.isMappable(node1, null));
        assertThrows(IllegalArgumentException.class, () -> mapper.isMappable(null, node1));
    }

    @Test
    void testGetMappingsNested() {
        AtlasParser parser = new AtlasParser();
        AtlasMapper mapper = new AtlasMapper();

        AtlasNode node1 = parser.parse("(serve *priest (some congregation (that (perform worship))))");
        AtlasNode node2 = parser.parse("(serve *soldier (some army (that (perform conquest))))");

        HashMap<String, String> result = mapper.getMappings(node1, node2);

        assertEquals(3, result.size());
        assertEquals("*priest", result.get("*soldier"));
        assertEquals("congregation", result.get("army"));
        assertEquals("worship", result.get("conquest"));
    }

    @Test
    void testGetMappingsFailsStarMismatch() {
        AtlasParser parser = new AtlasParser();
        AtlasMapper mapper = new AtlasMapper();

        AtlasNode node1 = parser.parse("(serve *priest)");
        AtlasNode node2 = parser.parse("(serve soldier)");

        assertThrows(IllegalArgumentException.class, () -> mapper.getMappings(node1, node2));
    }


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