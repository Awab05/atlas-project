# atlas-project

Implementation of symbolic structure representations for analogy mapping.

## Team
- Awab
- Spencer

## Current Sprint
Sprint 3: Structural Rewriting

## Sprint 1 Completion

**Epic #1: Nested List Structures**
- User Story 1.1: Parse flat strings into OOP tree representation
- User Story 1.2: Convert trees back to flat/prettified strings with error handling
- User Story 1.3: Abstract non-predicates to indexed positions

**Status**: Complete (9/9 tests passing)

**Package**: `atlas` — Core classes: `AtlasNode`, `AtlasParser`, `AtlasPrinter`, `AtlasAbstractor`

---

## Sprint 2 Completion

**Epic #2: Structure Mapping**
- User Story 2.1: Structure-Mapping of two structured representations
  - Boolean test: Check if one structure is mappable to another with consistent, isomorphic 1-to-1 mapping
  - Functional test: Return the set of 1-to-1 mappings between two structures
  - Starred symbols (central topic) map only to starred symbols; unstarred symbols map only to unstarred symbols
  
- User Story 2.2: Mapping of two flat-string expressions
  - Direct string processing mapping without building intermediate OOP structures
  - Returns 1-to-1 mappings between flat-string representations
  - More efficient alternative to structure-based mapping

**Status**: Complete (10/10 tests passing)

**Packages**: 
- `atlas.Mapping` — Core classes: `AtlasMapper`, `Mapping`
