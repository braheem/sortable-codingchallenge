organization := "org.braheem.codingchallenge"

name := "codingchallenge"

version := "1.0"

description := "Sortable Coding Challenge"

publishMavenStyle := true

crossPaths := false

autoScalaLibrary := false

externalPom()

mainClass in (Compile, run) := Some("org.braheem.codingchallenge.main.Main")