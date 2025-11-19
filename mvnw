#!/bin/sh
# Shim: If runner calls ./mvnw in ModernRepo root, redirect to start.sh
# This ensures launchers that insist on using ./mvnw still run the actual application correctly.
exec sh ./start.sh "$@"
