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

## Sprint 3 Completion

**Epic #3: Structural Rewriting**
- User Story 3.1: Read rewrite-rules from a file
  - Load rewrite rules from the resource file line by line
  - Parse each line into a predicate and one or more rewrite rules
  - Store the loaded rules in a lookup structure for later use
  
- User Story 3.2: Rewrite conceptual structures using the loaded rules
  - Parse individual rewrite rules into structured components
  - Support rule features such as negation, argument swapping, implicit argument expansion, inserted arguments, prepositions, and gerund wrappers
  - Rewrite an `AtlasNode` into one or more more-general conceptual structures using the loaded rules
  - Support recursive rewriting of nested structures

**Status**: Complete (10/10 tests passing)

**Packages**:
- `atlas.Rules` — Core classes: `RewriteRuleLoader`, `RewriteRule`, `RewriteStructure`
