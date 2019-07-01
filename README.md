# AWS face rekognition


##  Setup

**IMPORTANT!!!: You must install AWS CLI and configure your access keys to use the Rekognition API

Within the repository there is both a comppiled JAR for use in the command line under /compiled and a InteliJ project of the uncompiled code.<br>

## JAR command line arguments
### Detect Faces
Detects faces and draws bounding boxes saving it to img_bb.jpg in JAR directory<br>
example: AWSREKOG.jar detect-faces [Absolute File Path]<br>
### Create Collection
Creates a new colection<br>
example: AWSREKOG.jar collection [Name]<br>
### List All Collections
Lists all collections<br>
example: AWSREKOG.jar list-collections<br>
### Delete A Collection
Deletes a givene collection<br>
example: AWSREKOG.jar delete-collection [Name]<br>
### Describe a Collection
Describes a collection such as number of faces, internal AWS ID, collection name, and Date Created<br>
example: AWSREKOG.jar describe-collection [Name]<br>
### Index Faces
Adds Faces to collection<br>
example: AWSREKOG.jar index-faces [Name] [Absolute File Path1] [Absolute File Path2] [etc]<br>
### Search for a given face
Searches a given collection for a given face and returns of found or not and the confidence score if found.<br>
example AWSREKOG.jar search-faces-by-image [Name] [Absolute File Path]<br>
### Create a collection and add faces via a directory (Only if running on desktop not Command line only)
Creates a collection from  a folder where Collection name is equal to the folder and the folder contains faces<br>
example AWSREKOG.jar createclass<br>




