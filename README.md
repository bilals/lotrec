# LoTREC - Tableaux Theorem Prover

[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-GPL--2.0-blue.svg)](LICENSE)
[![Latest Release](https://img.shields.io/github/v/release/bilals/lotrec)](https://github.com/bilals/lotrec/releases/latest)

**LoTREC** is an automated theorem prover for modal and description logics using the tableau method. It enables students and researchers to define custom logics with Kripke semantics and verify formula properties (satisfiability, validity) through interactive proof visualization.

![LoTREC Main Interface](docs/images/tableau-result.png)

## Features

- **38 predefined logics** including K, KT, S4, S5, and description logics
- **Visual tableau construction** with interactive graph display
- **Custom logic definition** via XML-based rule specification
- **Step-by-step debugging** with breakpoints on rules
- **Model checking** capabilities

## Quick Start

### 1. Download & Run

[**Download the latest release**](https://github.com/bilals/lotrec/releases/latest), extract the ZIP, and run:
- **Windows:** `bin/LoTREC.bat`
- **Linux/macOS:** `bin/LoTREC`

> Requires Java 8 or later. Get it from [Eclipse Adoptium](https://adoptium.net/).

### 2. Choose a Logic

On startup, select a predefined logic like **Modal logic K** or open your own logic file.

![Task Pane](docs/images/task-pane.png)

### 3. Test a Formula

Enter a formula and click **Build Premodels** to see the tableau proof tree.

## Documentation

| Guide | Description |
|-------|-------------|
| [Getting Started](docs/getting-started.md) | Installation and your first proof |
| [User Guide](docs/user-guide.md) | Complete interface reference |
| [Defining Logics](docs/defining-logics.md) | Create custom logics with XML |
| [Predefined Logics](docs/predefined-logics.md) | List of 38 built-in logics |

## For Developers

Clone the repository and build with Gradle:

```bash
git clone https://github.com/bilals/lotrec.git
cd lotrec
./gradlew build    # Build and test
./gradlew run      # Run the application
```

See [CLAUDE.md](CLAUDE.md) for detailed build commands and architecture overview.

## Memory Allocation

For extensive debugging sessions or large proofs, increase memory allocation.

Edit `bin/LoTREC.bat` (Windows) or `bin/LoTREC` (Linux/macOS):
```
DEFAULT_JVM_OPTS="-Xmx512M"   # 512MB (or -Xmx2048M for 2GB)
```

## Contributing

We welcome contributions! Please:
- Fork the repository
- Create a feature branch
- Submit a pull request

Report issues on [GitHub Issues](https://github.com/bilals/lotrec/issues).

## Links

- **Repository:** https://github.com/bilals/lotrec
- **Legacy Site:** https://www.irit.fr/Lotrec/

## License

LoTREC is released under the [GPL-2.0 License](LICENSE).
