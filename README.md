# AWS face rekognition


##  Setup
Within the repository there is both a comppiled JAR for use in the command line under /compiled and a InteliJ project of the uncompiled code.

## JAR command line arguments
### Detect Faces
Detects faces and draws bounding boxes saving it to img_bb.jpg in JAR directory
example: AWSREKOG.jar detect-faces [Absolute File Path]
### Create Collection
Creates a new colection
example: AWSREKOG.jar collection [Name]
### List All Collections
Lists all collections
example: AWSREKOG.jar list-collections
### Delete A Collection
Deletes a givene collection
example: AWSREKOG.jar delete-collection [Name]
### Describe a Collection
Describes a collection such as number of faces, internal AWS ID, collection name, and Date Created
example: AWSREKOG.jar describe-collection [Name]
### Index Faces
Adds Faces to collection
example: AWSREKOG.jar index-faces [Name] [Absolute File Path1] [Absolute File Path2] [etc]
### Search for a given face
Searches a given collection for a given face and returns of found or not and the confidence score if found.
example AWSREKOG.jar search-faces-by-image [Name] [Absolute File Path]
### Create a collection and add faces via a directory (Only if running on desktop not Command line only)
Creates a collection from  a folder where Collection name is equal to the folder and the folder contains faces
example AWSREKOG.jar createclass




