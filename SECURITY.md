# OpenISS Security Policy

We use [dependabot](https://docs.github.com/en/code-security/supply-chain-security/managing-vulnerabilities-in-your-projects-dependencies/configuring-dependabot-security-updates)
for most of our dependencies tracking in Python and JavaScript modules.

## Supported Versions

The following versions of OpenISS are
currently being supported with security updates:

| Version                       | Supported          |
| ----------------------------- | ------------------ |
| HEAD                          | :white_check_mark: |
| <= v0.1.0-2018.02-java-js-ws  | :x:                |

## Reporting a Vulnerability

- For less severe and hard to exploit vulnerabilities in the context of OpenISS please create a [pull request](https://github.com/OpenISS/OpenISS/pulls) (e.g., after running `npm audit fix`) with title to update dependencies.
- The same applies if there are vulnerabilities in the git submodules linked in the repo.
- For C/C++/Python components, especially network- or driver-related that are developed by the OpenISS team or anything related, please reach out to mokhov at cse.concordia.ca 

We should be able to respond within 24-48 hours. Thank you.
