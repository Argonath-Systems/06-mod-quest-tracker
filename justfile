# Quest Tracker UI Justfile

set windows-shell := ["powershell.exe", "-NoLogo", "-Command"]

# Default recipe
default:
    @just --list

# Build the project
build:
    mvn clean package -DskipTests

# Build with tests
build-test:
    mvn clean package

# Run tests
test:
    mvn test

# Install to local Maven repository
install:
    mvn clean install -DskipTests

# Run checkstyle
lint:
    mvn checkstyle:check

# Generate Javadoc
docs:
    mvn javadoc:javadoc

# Clean build artifacts
clean:
    mvn clean

# Run a quick compile check
check:
    mvn compile -DskipTests

# Package for release
release:
    mvn clean package -P release -DskipTests
