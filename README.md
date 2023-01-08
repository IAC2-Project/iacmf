# Infrastructure-as-Code Compliance Management Framework (IACMF) Core

## Existing Plugins

### Model Creation Plugins

### Model Refinement Plugins

### Compliance Checking Plugins

### Compliance Issue Fixing Plugins

#### Bash-based Ubuntu Issue Fixing Plugin

**Summary**: This plugin allows executing bash commands on an ubuntu (virtual-)machine via ssh for the purpose of fixing
a detected compliance issue.

**Plugin Identifier:** `bash-fixing-plugin`

**Possible Inputs for the Plugin**

1. _From the Issue properties_

   The plugin expects the `"CHECKER_COMPONENT_ID"` property. This property points to a component of type `Compute` in
   the instance model that has the issue.
   The referred component is expected to have the following properties:
    - `os_family`: with the value `linux`.
    - `machine_image`: with the value `ubuntu`.
    - `public_address`: with the hostname/ip address of the ubuntu (virtual-)machine.
    - (optional) `private_key_path`: with the path (on the server) to the private key that allows connecting to the (
      virtual-)machine via ssh. If this is property has a value, the plugin configuration
      entry `default-private-key-path` (see below) is ignored.


2. _From the Configuration Entries_

   The plugin has the following configuration entries:
    - `script`: the bash script to be executed via ssh.
    - `username`: the username to be used when connecting to the ubuntu (virtual-)machine.
    - (optional) `default-private-key-path`: the path (on the iacmf server) to the private key that allows to connect to
      the ubuntu (virtual-)machine. This entry will be used iff the instance model node that has the issue does not
      define a property `private_key_path` (see above). At least one of these two values must be set.
    - (optional) `compliance-rule-arguments`: a comma-separated list of compliance rule parameter names. If this value
      is set, the plugin will retrieve the referenced attributes and pass their values to the bash script as
      command-line arguments in the same order specified in this list.


3. _From the Compliance Rule_

   If the plugin has a value for the optional configuration entry `compliance-rule-arguments` (see above). Then the
   corresponding compliance rule attributes will be used as command-line arguments to the script that will be executed
   on the ubuntu (vritual-)machine via ssh.

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