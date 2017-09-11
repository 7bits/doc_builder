# Documentation builder

This utility is supposed to generate API documentation from markdown files with versions from a repo's git branches and tags.

Works fine with [this Documentation guide](https://smarttools.github.io/tutorials/thirdparty/documentation) for actors server.

## Usage

```rdoc
Usage: java -jar doc_builder.jar [options]
  Options:
    --destination, -d
      Path to the destination location
      Default: doc_build
    --git
      Use git and generate as many API versions as many git refs exists
      Default: false
    --pattern
      File name extractor pattern. Read how java Matcher and Pattern works
      Default: .*/(.*?)/README\.md
    --server
      Run server to host static ot not
      Default: false
    --source, -s
      Path to the Features location
      Default: Features
    --help, --usage, -h
      Display the help
      Default: false
```

For example, it'll read the `project_path` as git project, 
generates documentation for each branch and tag of this repo 
and puts site sources to the `project_path/docs`.

Do not worry, this utility will not do checkouts on your repository. All work with the git will use git DB directly without calls to the git command.

```bash
java -jar doc_builder.jar --git --server --source project_path --destination project_path/docs
```

### Pattern

The utility will look throw all markdown files in the path using the `pattern` regexp.
This allows you to setup flexible project structure.

For example, the default pattern `.*/(.*?)/README\.md` works with this project structure:

```
|-- Project
    |-- Features
    |   |-- pom.xml
    |   |-- SomeSuperFeature
    |   |   |-- SomeSuperActor
    |   |   |-- pom.xml
    |   |   |-- config.json
    |   |   |-- README.md
    |   |-- ...
```