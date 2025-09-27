# YAML-Test Cohesion Improvement Strategies

## Overview

This document explores approaches to strengthen the binding between YAML behavior definitions and
test implementations, addressing the fundamental challenge of enforcing that developers implement
all scenarios defined in the YAML specifications.

## Current State

Our current implementation has good traceability but relies on developer discipline:

- ✅ YAML defines comprehensive test scenarios
- ✅ Tests implement most scenarios correctly
- ✅ Manual mapping between YAML and test methods
- ❌ No automatic enforcement of coverage
- ❌ No build-time validation of completeness
- ❌ Manual effort to ensure YAML changes are reflected in tests

## Problem Statement

**How can we bind the YAML with the test implementation more strictly?**

While it's ultimately the developer's responsibility to follow all use cases described in YAML, we
want to enforce this relationship programmatically to:

1. Prevent missing test scenarios
2. Ensure YAML changes drive test updates
3. Maintain traceability over time
4. Reduce human error in scenario coverage

## Proposed Solutions

### 1. YAML Parser + Test Generation

**Approach**: Parse YAML at build time and generate skeleton test methods

**Implementation**:

```kotlin
// Generated or enforced structure
@YamlScenario("successful_login")
@Priority("critical")
fun test_successful_login_validCredentialsLeadToDashboard() { 
    // Implementation required
}
```

**Pros**:

- ⭐⭐⭐⭐⭐ Maximum strictness
- Automatic skeleton generation
- Build fails if scenarios missing

**Cons**:

- Complex build integration
- Generated code maintenance
- Less flexible for custom implementations

### 2. Annotation-Based Validation

**Approach**: Annotate test methods with YAML scenario metadata and validate at build time

**Implementation**:

```kotlin
@Test
@YamlScenario(id = "invalid_email_format", priority = "high")
@RequiredSteps([
    "Enter invalid email 'invalid-email' in email field",
    "Verify email validation error message appears"
])
fun invalid_email_format_showsValidationError() { 
    // Test implementation
}
```

**Pros**:

- ⭐⭐⭐⭐ Good strictness
- Clear traceability
- Good developer experience
- IDE support possible

**Cons**:

- Requires annotation processor
- Metadata duplication

### 3. Test Discovery and Verification

**Approach**: Runtime/build-time scanner that validates test coverage against YAML

**Implementation**:

```kotlin
class YamlTestValidator {
    fun validateTestCoverage() {
        val yamlScenarios = parseYaml("bdd/user_login.yaml")
        val testMethods = scanTestMethods(LoginScreenTest::class)
        
        val missing = yamlScenarios - testMethods.scenarios
        val extra = testMethods.scenarios - yamlScenarios
        
        if (missing.isNotEmpty()) {
            fail("Missing tests for: $missing")
        }
    }
}
```

**Pros**:

- ⭐⭐⭐ Reasonable strictness
- Easy to implement
- Flexible implementation
- Good developer experience

**Cons**:

- Requires naming conventions
- Runtime validation overhead

### 4. Contract Testing Approach

**Approach**: YAML defines interfaces that tests must implement

**Implementation**:

```kotlin
interface LoginTestContract {
    fun test_successful_login()
    fun test_invalid_email_format()
    fun test_empty_credentials()
    // ... generated from YAML
}

class LoginScreenTest : LoginTestContract {
    // Must implement all contract methods
}
```

**Pros**:

- ⭐⭐⭐⭐⭐ Maximum strictness
- Compile-time enforcement
- Clear contract definition

**Cons**:

- Complex code generation
- Rigid structure
- Interface maintenance burden

### 5. AST Analysis + Linting

**Approach**: Custom lint rules that analyze both YAML and test source code

**Implementation**:

- Custom lint rules parse YAML files
- Analyze test method names, assertions, test tags
- Report missing scenarios or incorrect implementations
- IDE integration for real-time feedback

**Pros**:

- ⭐⭐⭐⭐ Good strictness
- IDE integration
- Real-time feedback

**Cons**:

- Complex AST parsing
- IDE-specific implementations
- Maintenance overhead

## Comparison Matrix

| Approach | Strictness | Dev Experience | Maintenance | Feasibility | Implementation Effort |
|----------|------------|----------------|-------------|-------------|----------------------|
| YAML Parser + Generation | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | High |
| Annotation-Based | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Medium |
| Test Discovery | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Low |
| Contract Testing | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐ | ⭐⭐ | High |
| AST Analysis | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ | ⭐⭐ | High |

## Recommended Hybrid Approach

**Combine Test Discovery + Annotation-Based validation for optimal balance**

### Phase 1: Test Discovery Runtime Validation

```kotlin
@Test
fun verify_all_yaml_scenarios_have_tests() {
    val yamlScenarios = YamlParser.parse("bdd/user_login.yaml").testScenarios
    val implementedTests = this::class.memberFunctions
        .filter { it.hasAnnotation<Test>() }
        .map { extractScenarioId(it.name) }
    
    val missing = yamlScenarios.keys - implementedTests.toSet()
    assertTrue("Missing tests for scenarios: $missing", missing.isEmpty())
}
```

### Phase 2: Annotation Metadata

```kotlin
@Test
@YamlScenario("successful_login")
@Priority("critical")
fun successful_login_with_valid_credentials() {
    // Implementation with clear traceability
}
```

### Phase 3: Build Integration

- Gradle task that runs validation
- Fail build on missing coverage
- Generate coverage reports

## Current YAML Structure Analysis

Based on our `bdd/user_login.yaml`, we have:

**Test Scenarios (11 scenarios)**:

- `successful_login` (critical)
- `invalid_email_format` (high)
- `empty_credentials` (high)
- `invalid_credentials` (critical)
- `network_error` (medium)
- `server_error_500` (high)
- `unauthorized_401` (critical)
- `service_unavailable_503` (medium)
- `rate_limited_429` (high)
- `malformed_response` (medium)
- `maintenance_mode` (low)
- `password_visibility_toggle` (medium)

**Validation Points**:

- 6 UI elements with test tags
- 6 business rules
- Data contracts for request/response

## Implementation Roadmap

### Immediate (Low Effort, High Value)

1. **Naming Convention Enforcement**
    - Standardize test method naming: `{scenario_id}_{description}`
    - Runtime validation of naming compliance

2. **Coverage Validation Test**
    - Single test that validates all scenarios are covered
    - Immediate feedback on missing tests

### Short Term (Medium Effort, High Value)

3. **Annotation Framework**
    - `@YamlScenario` annotation
    - Metadata validation
    - IDE integration

### Long Term (High Effort, Maximum Value)

4. **Build Integration**
    - Gradle plugin for validation
    - Automated coverage reports
    - CI/CD integration

5. **Advanced Features**
    - Step-by-step validation
    - Message assertion verification
    - Test tag compliance checking

## Benefits of Implementation

1. **Enforcement**: Build fails if YAML scenarios are not tested
2. **Traceability**: Clear mapping between YAML and test code
3. **Quality**: Reduced risk of missing edge cases
4. **Maintenance**: Automatic detection when YAML changes
5. **Documentation**: Tests serve as living documentation of YAML specs
6. **Collaboration**: QA and developers stay aligned on requirements

## Conclusion

The **Test Discovery + Annotation-Based** hybrid approach provides the best balance of:

- Practical enforceability
- Good developer experience
- Reasonable implementation effort
- Long-term maintainability

This approach would ensure that our YAML-driven development maintains strict cohesion between
specifications and implementation while remaining developer-friendly and maintainable.