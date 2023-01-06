# Infrastructure-as-Code Compliance Management Framework (IACMF) Core

## Example Compliance Rules

### 1. The Ubuntu operating system must not allow accounts configured with blank or null passwords

#### Source

_Canonical Ubuntu 20.04 LTS Security Technical Implementation Guide :: Version 1, Release: 6 Benchmark Date: 27 Oct
2022_ (STIG-ID: UBTU-20-010463)

#### Model Refinement Plugin

Please use the `bash-based-model-refinement-plugin` to create an attribute in Ubuntu-based VM nodes called `nullok` with
a boolean
that represents if the OS allows user accounts configured with blank or null passowrds.

The bash command to be executed should be:

```bash
sudo grep nullok /etc/pam.d/common-password
```

#### Compliance Rule

- __Type__: Subgraph Isomorphism
- __Selector__: Selects all `Compute` nodes that host an `ubuntu` OS.
- __Checker__: Confirms that the value of the attribute `nullok` is `false`.
- __IssueType__: `null-passwords-allowed`

#### Issue Fixing Plugin

Please use the `bash-based-issue-fixing-plugin`.
It must be mapped to IssueTypes of the value `null-passwords-allowed`.
The bash script to fix this issue is:

```bash
sudo sed -i -e 's/nullok//g' /etc/pam.d/common-password
```

This removes the occurrences of the `nullok` option in the configuration file.