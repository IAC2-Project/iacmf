# Infrastructure-as-Code Compliance Management Framework (IACMF) Core

Existing Plugins
--

## Instance Model Creation Plugins

### OpenTOSCA Container Model Creation Plugin

**Summary**: This plugin creates an instance model for a cloud application deployed and managed by the OpenTOSCA
Container IaC technology (http://opentosca.github.io/container/).

**Plugin Identifier**: `opentosca-container-model-creation-plugin`

### Manual Model Creation Plugin

**Summary**: This plugin allows creating an EDMM-based instance model using an external tool (such as Winery) or even
manually.
The plugin only requires a URL to the EDMM file that represents the instance model.
This is especially helpful if there is still no model creation plugin for the used IaC technology, or if no IaC
technology for deployment management is used in the first place.

**Plugin Identifier**: `manual-model-creation-plugin`

## Instance Model Refinement Plugins

### Docker Container Model Refinement Plugin

**Summary**: This plugin allows identifying which reachable docker containers were expected or unexpected according to
the original instance model (i.e., before applying this plugin). Furthermore, it helps in detecting unexpectedly removed
docker containers.

**Plugin Identifier**: `docker-refinement-plugin`

### MySQL Database Model Refinement Plugin

**Summary**: This plugin allows refining the instance model with information about all the users that have permissions
on the MySQL database components present in the instance model. This information will be stored as a comma-separated
list of usernames assigned to a property called `users`.

**Plugin Identifier**: `mysql-db-model-refinement-plugin`

### Bash-based Ubuntu Model Refinement Plugin

**Summary**: This plugin is capable of running a user-defined bash script over ssh on an ubuntu-based (virtual-)machine
in order to retrieve information from it, and update the instance model with this information.

**Plugin Identifier**: `bash-refinement-plugin`

**Possible Inputs for the Plugin**

1. _From the Configuration Entries_

   The plugin has the following configuration entries:
    - `script`: the bash script to be executed via ssh. A value is expected to be returned from the execution of the
      script.
    - `username`: the username to be used when connecting to the ubuntu (virtual-)machine.
    - `output_property_name`: the name of the property that will be added to the affected `Compute` components of the
      instance model in order to hold the values that are retrieved from the ubuntu (virtual-)machines using the bash
      script. If this property already exists in the components, its value is updated with the retrieved values.
    - `output_property_type`: the type of the property that will be added to the affected `Compute` components of the
      instance model (see `output_property_name` above). The possible values for this configuration entry are:
        1. `STRING`
        2. `INT`
        3. `DECIMAL`
        4. `STRING_LIST`
        5. `BOOLEAN`
    - `ignore-missing-properties`: a boolean value that indicates whether the plugin will ignore the `Compute` nodes
      that represent ubuntu (virtual-)machines but do not provide enough information to facilitate communicating with
      them (e.g., missing `public_address` (see below)). If the value is `false`, the plugin will throw an exception if
      such a component is detected in the input instance model.
    - **(optional)** `default-private-key-path`: the path (on the iacmf server) to the private key that allows to
      connect to the ubuntu (virtual-)machine. This entry will be used iff the instance model node that has the issue
      does not define a property `private_key_path` (see below). At least one of these two values must be set.
    - **(optional)** `production-system-arguments`: a comma-separated list of production system parameter names. If this
      value is set, the plugin will retrieve the referenced attributes and pass their values to the bash script as
      command-line arguments in the same order specified in this list.

2. _From the Production System_

   If the plugin has a value for the optional configuration entry `production-system-arguments` (see above). Then the
   corresponding production system attributes will be used as command-line arguments to the script that will be executed
   on the ubuntu (vritual-)machine via ssh.

**Effects on the Instance Model**:

The plugin executes the bash script (`script`) on all `Compute` nodes of the input instance model that describe
an `ubuntu` (virtual-)machine and provide enough information to communicate with them via ssh. The `script` is expected
to return a value whose type must match `output_property_type`. The plugin then adds a property whose name corresponds
to the value of `output_property_name` to every accessible `ubuntu` component and assigns the returned value from the
script.

## Compliance Checking Plugins

### Subgraph-Matching Compliance Checking Plugin

**Summary**: This plugin checks the compliance of the reconstructed instance model to compliance rules of
type `subgraph-matching`.
Such compliance rules are modelled as graphs, and therefore, the compliance checking process uses a subgraph matching
algorithm.

**Plugin Identifier**: `subgraph-matching-checking-plugin`

## Compliance Issue Fixing Plugins

### Docker Container Issue Fixing Plugin

**Summary**: This plugin stops and removes unexpected docker containers (i.e., the ones that have a
property `structuralState`
with the value: `UNEXPECTED`)

**Plugin Identifier**: `docker-container-issue-fixing-plugin`

### Unexpected MySQL Database Users Removal Plugin

**Summary**: This plugin removes the permissions over MySQL databases from all the users that are not allowed to have
access
to these databases as determined by the compliance rule (as determined by the `ALLOWED_USERS` compliance rule property).

**Plugin Identifier**: `remove-mysql-db-users-fixing-plugin`

### Bash-based Ubuntu Issue Fixing Plugin

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

Example Compliance Rules
--

## 1. The Ubuntu operating system must not allow accounts configured with blank or null passwords

### Source

_Canonical Ubuntu 20.04 LTS Security Technical Implementation Guide :: Version 1, Release: 6 Benchmark Date: 27 Oct
2022_ (STIG-ID: UBTU-20-010463)

### Model Creation Plugin

Please use the [`manual-model-creation-plugin`](#manual-model-creation-plugin) to refer to a manually created
instance model (e.g., in winery).
The instance model must contain one or more `Compute` nodes that define the `public_address` and `private_key_path`
properties
so that ssh can be established with them.

### Model Refinement Plugin

Please use the [`bash-refinement-plugin`](#bash-based-ubuntu-model-refinement-plugin) to create an attribute
in Ubuntu-based VM nodes called `allowsNulls`
with a boolean that represents if the OS allows user accounts configured with blank or null passowrds.

The bash command to be executed should be:

```bash
[[ ! -z $(sudo grep nullok /etc/pam.d/common-password) ]] && echo 'true' || echo 'false'
```

### Compliance Rule

- __Type__: Subgraph Isomorphism
- __Selector__: Selects all `Compute` nodes that host an `ubuntu` OS.
- __Checker__: Confirms that the value of the attribute `allowsNulls` is `false`.
- __IssueType__: `null-passwords-allowed`

### Issue Fixing Plugin

Please use the [`bash-fixing-plugin`.](#bash-based-ubuntu-issue-fixing-plugin)
It must be mapped to IssueTypes of the value `null-passwords-allowed`.
The bash script to fix this issue is:

```bash
sudo sed -i -e 's/\s*nullok\s*/ /g' /etc/pam.d/common-password
```

This removes the occurrences of the `nullok` option in the configuration file.