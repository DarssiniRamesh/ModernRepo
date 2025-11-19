#!/usr/bin/env sh
# POSIX shim: if runner calls ./mvnw in ModernRepo root, redirect to start.sh (no bashisms).
exec sh ./start.sh "$@"
