#!/bin/sh
# Shim: If runner calls ./mvnw in ModernRepo root, redirect to start.sh
exec sh ./start.sh "$@"
