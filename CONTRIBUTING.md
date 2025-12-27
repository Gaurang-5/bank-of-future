# Contributing to Bank of Future

First off, thank you for considering contributing to Bank of Future! It's people like you that make this project better.

## Code of Conduct

This project and everyone participating in it is governed by respect and professionalism. By participating, you are expected to uphold this standard.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When you are creating a bug report, please include as many details as possible:

* **Use a clear and descriptive title**
* **Describe the exact steps to reproduce the problem**
* **Provide specific examples to demonstrate the steps**
* **Describe the behavior you observed and what you expected**
* **Include screenshots if relevant**
* **Include your Java version and OS**

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* **Use a clear and descriptive title**
* **Provide a detailed description of the proposed enhancement**
* **Explain why this enhancement would be useful**
* **List any alternative solutions you've considered**

### Pull Requests

* Fill in the pull request template
* Follow the Java coding style used throughout the project
* Include comments in your code where necessary
* Update the README.md with details of changes if needed
* Ensure all tests pass before submitting
* Update documentation for any changed functionality

## Development Setup

1. **Fork and clone the repository**
   ```bash
   git clone https://github.com/yourusername/bank-of-future.git
   cd bank-of-future
   ```

2. **Create a branch for your changes**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes and test**
   ```bash
   ./run.sh
   ```

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "Add: brief description of changes"
   ```

5. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Open a Pull Request**

## Coding Style

* Use meaningful variable and method names
* Follow Java naming conventions (camelCase for variables/methods, PascalCase for classes)
* Add comments for complex logic
* Keep methods focused and not too long
* Use appropriate access modifiers (private, public, protected)
* Handle exceptions appropriately
* Follow SOLID principles

## Testing

* Test your changes thoroughly before submitting
* Include test cases for new features
* Ensure existing functionality still works
* Test with and without MySQL enabled

## Project Structure

```
src/main/java/
├── BankingApplication.java    # Main entry point
├── Bank.java                  # Core business logic
├── Customer.java              # Customer entity
├── BankAccount.java           # Account management
├── DatabaseManager.java       # MySQL operations
└── PersistenceManager.java    # File-based storage
```

## Commit Message Guidelines

* Use present tense ("Add feature" not "Added feature")
* Use imperative mood ("Move cursor to..." not "Moves cursor to...")
* Start with a capital letter
* Keep the first line under 50 characters
* Add detailed description after a blank line if needed

Examples:
* `Add: Account freeze functionality`
* `Fix: Transaction balance calculation error`
* `Update: Improve customer search performance`
* `Remove: Deprecated validation method`

## Questions?

Feel free to open an issue with your question or reach out to the maintainers.

Thank you for contributing! 🎉
