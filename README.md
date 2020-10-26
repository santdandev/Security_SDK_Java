# Key Generator App

An app that will create RSA public and private key and will shard the private key into K shards.

All the keys will be stored as file and few of them can be used to recreate the private key and decrypt any value provided.

This program is using maven as build tool 
Steps to Run : 

Step 1 :
```
Install JDK 1.7
Install Maven
```

Step 2 :
```
1. To Run Unit Tests : mvn clean test  
2. To Create a Build and packaged artifact : mvn clean compile assembly:single 
```

Artifact 
``` 
security-sdk-1.0.0-SNAPSHOT-jar-with-dependencies.jar
```

Command To Run :  

java -jar security-sdk-1.0.0-SNAPSHOT-jar-with-dependencies.jar

1. Once you run the command then it will prompt to enter plain text as an input.    
2. Press Enter after you entered the input.

or 

Simply you can run entrypoint.sh  

Type : 
```
sh entrypoint.sh
```  
