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


## Sprint 4 Completion

**Epic #4: Analogue Retrieval**
- User Story 4.1: Load a knowledge-base of conceptual structures into memory and index it for efficient access and retrieval
  - Read conceptual structures from the knowledge base file
  - Parse each structure into an `AtlasNode`
  - Extract starred topics and index structures by topic in memory
  
- User Story 4.2: Retrieve all conceptual structures about a topic T from the knowledge-base
  - Return all structures associated with a given topic
  - A structure is treated as being about topic `T` if it contains the symbol `*T`
  
- User Story 4.3: Retrieve all possible source concepts S for a target concept T from the knowledge-base
  - Compare structures about `T` with structures about all other topics
  - Return every topic that has at least one structurally mappable pair with the target topic
  
- User Story 4.4: Rank the possible analogies S for T by structural richness
  - Compute a structural richness score for mappable structure pairs
  - Sum richness scores across all shared mappable structures
  - Return source concepts ranked from highest to lowest analogy score

**Status**: Complete (9/9 tests passing)

**Packages**:
- `atlas.Retrieval` — Core classes: `ConceptualLoad`, `AtlasRetriever`


## Sprint 5 Completion

**Epic #5: Rich Analogies**
- User Story 5.1: Produce the largest and richest analogy from a source concept S to a target concept T
  - Compare all conceptual structures for the source and target topics
  - Find all mappable structure pairs and extract their mappings
  - Coalesce consistent mappings across multiple structures
  - Return the largest consistent composite analogy between S and T
  
- User Story 5.2: Rank composite analogies from S to T in terms of mapping richness
  - Retrieve all consistent composite analogies between a source and target topic
  - Measure mapping richness as the number of distinct mapped elements
  - Sort composite analogies in descending order of richness
  
- User Story 5.3: Return the top n source concepts for a target concept T
  - Retrieve all possible source concepts for the target topic
  - Compute the richest composite analogy for each source concept
  - Score each source concept by the size of its best composite mapping
  - Return the top n source concepts ranked by analogy richness

**Status**: Complete (14/14 tests passing)

**Packages**:
- `atlas.Analogy` — Core classes: `RichAnalogy`, `AnalogyRanker`, `TopAnalogyRetriever`
