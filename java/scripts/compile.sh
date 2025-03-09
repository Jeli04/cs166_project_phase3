#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# PostgreSQL port (set manually if PGPORT is not defined)
PGPORT=${PGPORT:-5432}

# Compile the Java program
javac -d "$DIR/java/classes" "$DIR/java/src/PizzaStore.java"

# Run the Java program
# java -cp "$DIR/java/classes:$DIR/java/lib/pg73jdbc3.jar" PizzaStore "cs166_db" "$PGPORT" "$USER"
java -cp "$DIR/java/classes:$DIR/java/lib/postgresql-42.7.5.jar" PizzaStore "cs166_db" "$PGPORT" "$USER"