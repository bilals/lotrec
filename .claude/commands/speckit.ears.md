---
description: Convert natural language requirements into EARS (Easy Approach to Requirements Syntax) format structured requirement documents, providing high-quality input for subsequent SDD workflow.
handoffs:
  - label: Create Feature Spec
    agent: /speckit.specify
    prompt: Create a feature spec based on EARS requirement document. Document path:
  - label: Review Requirements
    agent: /speckit.clarify
    prompt: Review the completeness and clarity of EARS requirement document
---

## User Input

```text
$ARGUMENTS
```

Before proceeding, you **must** consider the user input (if not empty).

---

# EARS Requirements Conversion System

## Overview

This command converts user's natural language requirement descriptions into **EARS (Easy Approach to Requirements Syntax) format** structured requirement documents. EARS is a widely adopted requirements writing methodology in the industry that is more AI-friendly, helping AI understand requirements more accurately and generate higher quality feature specifications (specs).

### Why Use EARS Format?

| Problem | How EARS Solves It |
|---------|-------------------|
| Ambiguous requirements | Uses fixed sentence templates, forcing clear trigger conditions and system responses |
| AI misinterpretation | Structured format reduces ambiguity, improves AI comprehension accuracy |
| Missing requirements | Categorized templates ensure coverage of all requirement scenarios |
| Unclear acceptance criteria | Each requirement is inherently testable |

### Workflow Position

```
[User Requirements] → /speckit.ears → [EARS Doc] → /speckit.specify → [Feature Spec] → /speckit.plan → ...
                      ↑ Current command              Standard SDD workflow →
```

**Note**: EARS requirement conversion is an **optional pre-step** to the standard SDD workflow, suitable for:
- Complex or ambiguous requirement descriptions
- Need to confirm requirement details with stakeholders
- Desire to improve AI-generated spec quality

## ⚠️ Critical: Output Directory Structure

> **🚨 Mandatory Constraint**
>
> EARS requirement documents are stored in `.docs/EARS/` directory, **NOT** in `.specify/` or `specs/` directories.
> This is because EARS format requirements are an optional requirement organization tool, not part of the spec-kit standard workflow.

```
[Project Root]/
├── .docs/                             # Documentation directory (not SDD core)
│   └── EARS/                          # EARS requirement documents directory
│       ├── 001-user-authentication.md # Numbered requirement documents
│       ├── 002-payment-integration.md
│       └── ...
├── .specify/                          # SDD configuration directory (kept separate)
│   └── ...
└── specs/                             # Feature specs directory (kept separate)
    └── ...
```

---

## Execution Flow

### Phase 0: Requirement Input Parsing

**Goal**: Understand user input, determine operation mode

#### Step 0.1: Parse User Input

1. **Get user input**: Read content from `$ARGUMENTS`
2. **Determine input type**:
   - If empty: Prompt user "Please provide a requirement description, e.g., `/speckit.ears I need a user login feature supporting phone and email login`"
   - If file path: Check if it's an existing EARS document, enter modification mode
   - If requirement description: Enter creation mode

#### Step 0.2: Check Existing Documents

1. **Ensure directory exists**:
   ```bash
   mkdir -p .docs/EARS
   ```

2. **Scan existing documents**:
   ```bash
   # Get existing document list
   ls -1 .docs/EARS/*.md 2>/dev/null | sort -V
   ```

3. **Smart matching**:
   - Analyze keywords from user requirements
   - Compare similarity with existing document titles
   - If similarity > 80%, ask user if they want to modify existing document

---

### Phase 1: EARS Format Conversion

**Goal**: Convert natural language requirements to EARS format

#### EARS Five Requirement Patterns

EARS defines five standard requirement sentence patterns covering all common requirement scenarios:

| Pattern | Keyword | Use Case | Template |
|---------|---------|----------|----------|
| **Ubiquitous** | The system shall | Requirements system must always satisfy | `The system shall <function>` |
| **Event-Driven** | When...then | Responses triggered by specific events | `When <trigger condition>, the system shall <response>` |
| **State-Driven** | If...then | Behavior in specific states | `If <system state>, then the system shall <behavior>` |
| **Optional** | Where...user can | User-optional functionality | `Where <precondition>, the user can <operation>` |
| **Complex** | When...and...then | Multi-condition triggers | `When <trigger>, and <system state>, the system shall <response>` |

#### Step 1.1: Requirement Decomposition

Extract the following elements from user description:

```markdown
## Requirement Decomposition Worksheet

**Original Requirement**: [User's requirement description]

### Actor Identification
- **Primary User**: [Who is using this feature?]
- **Secondary User**: [Who else is affected?]
- **System Role**: [What role does the system play?]

### Action Identification
- **User Actions**: [What operations will users perform?]
- **System Responses**: [How should the system respond?]
- **Post-Actions**: [What happens after completion?]

### Condition Identification
- **Preconditions**: [Under what circumstances can this be executed?]
- **Trigger Conditions**: [What events trigger this feature?]
- **Exception Conditions**: [Under what circumstances will it fail?]

### Data Identification
- **Input Data**: [What inputs are needed?]
- **Output Data**: [What outputs are produced?]
- **State Changes**: [What data will be modified?]
```

#### Step 1.2: EARS Conversion

Convert decomposed requirements into EARS format sentences:

**Conversion Rules**:

1. **Main Flow → Event-Driven Requirements**
   - User's core operation flow
   - Format: `When <user performs action>, the system shall <execute corresponding response>`

2. **Preconditions → State-Driven Requirements**
   - Prerequisite states for feature availability
   - Format: `If <condition is met>, then the system shall <allow/execute behavior>`

3. **Exception Handling → Complex Requirements**
   - Handling of exceptional situations
   - Format: `When <user performs action>, and <exception occurs>, the system shall <error handling>`

4. **Universal Constraints → Ubiquitous Requirements**
   - Constraints the system must always satisfy
   - Format: `The system shall <constraint>`

5. **Optional Features → Optional Requirements**
   - Features users can choose to use
   - Format: `Where <condition>, the user can <choose to perform operation>`

#### Step 1.3: Requirement Numbering

Assign unique identifier to each EARS requirement:

```
[Feature Prefix]-[Type]-[Sequence]

Feature Prefix: 2-4 uppercase letters representing the feature domain
Type Codes:
  - UB: Ubiquitous
  - EV: Event-Driven
  - ST: State-Driven
  - OP: Optional
  - CX: Complex
Sequence: Three digits, starting from 001

Examples: AUTH-EV-001, PAY-ST-002
```

---

### Phase 2: Generate EARS Document

**Goal**: Generate structured EARS requirement document

#### Step 2.1: Determine Filename

1. **Generate short name**:
   - Extract 2-4 keywords from requirement description
   - Use kebab-case, keep concise
   - Examples: "user-authentication", "payment-integration", "order-management"

2. **Assign document number**:
   - Scan `.docs/EARS/` directory
   - Find maximum number N
   - New document uses N+1
   - If directory is empty, start from 001

3. **Generate file path**:
   ```
   .docs/EARS/[NNN]-[short-name].md
   Example: .docs/EARS/001-user-authentication.md
   ```

#### Step 2.2: Generate Document Content

Create EARS requirement document using the following template:

```markdown
# EARS Requirement Document: [Feature Name]

**Document Number**: [NNN]
**Created**: [YYYY-MM-DD]
**Status**: Draft | Confirmed | Converted
**Original Requirement**: 
> [User's original requirement description]

---

## 1. Requirement Overview

### 1.1 Background
[Feature background and business value description]

### 1.2 Objectives
[Main objectives the feature should achieve]

### 1.3 Scope
- **In Scope**: [What's included in the feature scope]
- **Out of Scope**: [What's explicitly excluded]

---

## 2. Actors

| Actor | Type | Description |
|-------|------|-------------|
| [Actor 1] | Primary User | [Description] |
| [Actor 2] | Secondary User | [Description] |
| System | System | [System role description] |

---

## 3. EARS Requirements List

### 3.1 Ubiquitous Requirements

| ID | Requirement Description | Verification Method |
|----|------------------------|---------------------|
| [XX-UB-001] | The system shall [function description] | [How to verify] |

### 3.2 Event-Driven Requirements

| ID | Trigger Condition | System Response | Verification Method |
|----|-------------------|-----------------|---------------------|
| [XX-EV-001] | When [trigger condition] | The system shall [response] | [How to verify] |
| [XX-EV-002] | When [trigger condition] | The system shall [response] | [How to verify] |

### 3.3 State-Driven Requirements

| ID | Precondition State | System Behavior | Verification Method |
|----|-------------------|-----------------|---------------------|
| [XX-ST-001] | If [system state] | Then the system shall [behavior] | [How to verify] |

### 3.4 Optional Requirements

| ID | Precondition | Optional Operation | Verification Method |
|----|--------------|-------------------|---------------------|
| [XX-OP-001] | Where [precondition] | The user can [operation] | [How to verify] |

### 3.5 Complex Requirements

| ID | Trigger Condition | System State | System Response | Verification Method |
|----|-------------------|--------------|-----------------|---------------------|
| [XX-CX-001] | When [trigger] | And [state] | The system shall [response] | [How to verify] |

---

## 4. Business Rules

| Rule ID | Rule Description | Applicable Requirements |
|---------|-----------------|------------------------|
| BR-001 | [Business rule description] | [Related requirement IDs] |

---

## 5. Data Requirements

### 5.1 Input Data

| Data Item | Type | Required | Constraints | Notes |
|-----------|------|----------|-------------|-------|
| [Data item] | [Type] | Yes/No | [Constraints] | [Notes] |

### 5.2 Output Data

| Data Item | Type | Notes |
|-----------|------|-------|
| [Data item] | [Type] | [Notes] |

---

## 6. Non-Functional Requirements

### 6.1 Performance Requirements
- [Performance-related requirements]

### 6.2 Security Requirements
- [Security-related requirements]

### 6.3 Usability Requirements
- [Usability-related requirements]

---

## 7. Items to Clarify

| ID | Question | Impact Scope | Suggested Options |
|----|----------|--------------|-------------------|
| Q1 | [Question to confirm] | [Which requirements affected] | A: ... / B: ... |

---

## 8. Revision History

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0 | [Date] | Initial creation | AI |

---

## Next Steps

✅ **Requirement document generated**

Please review the above EARS format requirements to ensure they accurately reflect your requirement intent.

**If modifications needed**: Tell me what needs to be adjusted, and I will update the document.

**If confirmed**: You can continue the SDD development workflow with the following command:

```bash
/speckit.specify .docs/EARS/[current-document-name]
```
```

---

### Phase 3: Modification Mode (If Updating Existing Document)

**Goal**: Make modifications based on existing EARS document

#### Step 3.1: Load Existing Document

1. Read the specified EARS document
2. Parse document structure
3. Display current requirements list

#### Step 3.2: Understand Modification Intent

Determine modification type based on user input:

| Modification Type | Keywords | Action |
|-------------------|----------|--------|
| Add requirement | "add", "include", "new" | Add new EARS requirement to corresponding section |
| Modify requirement | "modify", "update", "change" | Update specified requirement |
| Delete requirement | "delete", "remove", "drop" | Remove specified requirement |
| Clarify question | "Q1", "question 1", "option A" | Handle items to clarify |

#### Step 3.3: Execute Modification

1. Update document according to modification type
2. Update revision history
3. Re-check requirement numbering continuity
4. Save document

---

## General Guidelines

### EARS Writing Best Practices

1. **One requirement, one thing**: Each EARS requirement describes only one specific functional point
2. **Testability**: Each requirement must be verifiable
3. **No ambiguity**: Avoid using "maybe", "generally", "etc." and other vague words
4. **Active voice**: Use "The system shall" instead of "should be"
5. **Specific values**: Replace "fast", "large" with specific numbers

### Common Conversion Examples

**Original Requirement**: "Users can log into the system"

**EARS Conversion**:
```markdown
| AUTH-EV-001 | When user enters correct username and password and clicks login button | The system shall verify credentials and redirect to homepage |
| AUTH-EV-002 | When user enters incorrect username or password and clicks login button | The system shall display error message and retain username input |
| AUTH-ST-001 | If user is already logged in | Then the system shall redirect to homepage instead of login page |
| AUTH-CX-001 | When user attempts login, and has failed consecutively more than 5 times | The system shall lock account for 15 minutes and send security notification |
```

### Integration with SDD Workflow

Relationship between EARS requirement documents and spec-kit:

| EARS Document Content | Corresponding Spec Section |
|----------------------|---------------------------|
| Event-Driven Requirements | User Scenarios and Tests |
| State-Driven Requirements | Edge Cases |
| Business Rules | Functional Requirements |
| Data Requirements | Key Entities |
| Non-Functional Requirements | Success Criteria |

### Error Handling

| Scenario | Handling Approach |
|----------|-------------------|
| Requirement description too vague | Ask user to supplement key information (actors, triggers, expected results) |
| Requirement scope too large | Suggest splitting into multiple EARS documents |
| Technical implementation mixed in | Extract business essence, remove technical details |
| Numbering conflict | Automatically reassign numbers |

---

## Context

{ARGS}
