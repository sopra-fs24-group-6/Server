# SoPra Group06 Server



## 1. Introduction



* The project's goal is to create an online multiplayer social deduction game that supports players speaking different languages play together. 
* Our main motivation is  to facilitate fun and engaging communication among players from diverse cultural backgrounds. 



## 2. Technologies



* Spring
* H2 Database
* Cloud Translation API
* Simple Message Template



## 3. High-level components



### (1) User



* The [User entity](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/User.java) stores all user information, such as their ID, password, avatar, and more. It serves as the foundation of our code, with all other functions based on this entity. For example, each user has a language attribute, and during each game, all messages will be translated according to the user's language attribute. The `User` entity is managed by the [User repository](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/repository/UserRepository.java).
* The client side can interact with user through [User Controller](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/UserController.java).
* For each user, we can perform the following operations:
  * Login and logout
  * Register
  * Update information and avatar
* The detailed implementation of these functions can be found in the [User Service](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java).
* The [Player](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Player.java) is the representation of a `User` in a specific `Game`. For reasons such as security, scalability, and data management, we extract key attributes from `User` (like ID and language) to create the `Player` entity. They are managed by the [Player repository](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/repository/PlayerRepository.java).



### (2) Lobby



* The [Lobby](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Lobby.java) is where we manage each game. Any user can create a lobby. For security, scalability, and data management reasons, we extract key attributes from `Users` (like ID and language) to create the `Player` entity. The player who creates the lobby is set as the host, who has the right to modify the lobby as they like. The `Lobby` entity is managed by the [Lobby repository](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/repository/LobbyRepository.java).
* The client side interacts with the lobby through the [Lobby Controller](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/LobbyController.java).
* The Host has the following rights:
  * Set the time (clue, discussion, round) for each game.
  * Set the limit (player, round) for each game.
  * Kick players or invite friends.
  * Set the lobby as private or public.
  * Change the name of the lobby.
  * Select themes for each game.
* The detailed implementation of these functions can be found in the [Lobby Service](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/LobbyService.java).



### (3) Game



* The [Game](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Game.java) is where we actually play the game. For performance concerns, we extract it from the `Lobby` and add many new features to `game` entity. It manages each `Player` in the game.

* The client side interacts with the game through the [Game Controller](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/GameController.java).

* The game performs the following operations:

  * Assign words and roles to each player.
  * Record the remaining time of each round and proceed to the next stage when the time runs out.
  * Notify important information to each player, such as the results or whether a player can speak at a certain stage of the game.

* The detailed implementation of these functions can be found in the [Game Service](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/GameService.java).

* Apparently game service function alone cannot do all the jobs. It also contains following services to help it perform its core operations: 

  * [Vote Service](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/VoteService.java) 
  * [Translation Service](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/TranslationService.java) 
  * [Timer Service](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/TimerService.java) 
  * [Chat Service](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/ChatService.java)

* The Translation service is supported by the Cloud Translation API.

* In the [Translation Service](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/TranslationService.java), you can choose the quality of the translation service by changing the attribute of the `model()` method.

  ```java
  public String translateText(String originalText, String targetLanguage) {
  		//Translates some text into the target language
          Translation translation = translate.translate(
                  originalText,
                  Translate.TranslateOption.targetLanguage(targetLanguage),
                  // Use "base" for standard model; "nmt" for the Neural Machine Translation model
                  // "base" is cost-effective but with lower quality while NMT is the opposite
                  Translate.TranslateOption.model("base")
          );
          return translation.getTranslatedText();
    }
  ```



### (4) Friend



* The [Friend](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Friend.java) entity records the relationships among users, allowing `Users` to invite their friends to play with them in the `Game`, enhancing the user experience. It is managed by the [Friend repository](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/repository/FriendRepository.java).
* The client side interacts with friends through the [Friend Controller](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/FriendController.java).
* It has the following functions:
  * Send friend requests for each user.
  * Accept or deny friend requests.
  * Get the detailed friend list.
* The detailed implementation of these functions can be found in the [Friend Service](https://github.com/sopra-fs24-group-6/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/FriendService.java).



## 4. Launch & Deployment



### (1) IDE

Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java)



#### IntelliJ



If you consider to use IntelliJ as your IDE of choice, you can make use of your free educational license [here](https://www.jetbrains.com/community/education/#students).

1. File -> Open... -> sopra-fs24-group-6/Server
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`



#### VS Code



The following extensions can help you get started more easily:

- `vmware.vscode-spring-boot`
- `vscjava.vscode-spring-initializr`
- `vscjava.vscode-spring-boot-dashboard`
- `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the *Gradle Tasks* extension. Then check the *Spring Boot Dashboard* extension if it already shows `soprafs24` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.



### (2) Building with Gradle



You can use the local Gradle Wrapper to build the application.

- macOS: `./gradlew`
- Linux: `./gradlew`
- Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).



#### Build



```
./gradlew build
```



#### Run



```
./gradlew bootRun
```



You can verify that the server is running by visiting `localhost:8080` in your browser.



#### Test



```
./gradlew test
```



### (3) Development Mode



You can start the backend in development mode, this will automatically trigger a new build and reload the application once the content of a file has been changed.

Start two terminal windows and run:

```bash
./gradlew build --continuous
```

and in the other one:

```bash
./gradlew bootRun
```

If you want to avoid running all tests with every change, use the following command instead:

```bash
./gradlew build --continuous -xtest
```



### (4) Debugging



If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time



### (5) Testing



Have a look here: https://www.baeldung.com/spring-boot-testing



### (6) Environment settings for Cloud Translation API



To use the cloud translation API, first you need to enable this API in your google cloud. You can find a detailed Youtube tutorial [here](https://www.youtube.com/watch?v=l2TlFyXmiBQ). 

1. Create a new project in you google cloud console
2. In the Google Cloud Console, navigate to the API Library by selecting "APIs & Services" > "Library."
3. Search for "Cloud Translation API" and enable it.
4. Go to "APIs & Services" > "Credentials" in the Cloud Console. Click "Create Credentials" and select "Service Account." Create a service account as an editor
5.  Click "Add Key" > "Create new key" and choose JSON format. Download the JSON file.

After you download the JSON file, you need to set it in your local environment

6. store the JSON file in a secure place in your computer
7. Set an environment variable to point to the JSON key file. (This allows your application to authenticate with Google Cloud using the service account).

* On Unix-based systems (Linux, macOS), open your terminal and run

  ```bash
  export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your/service-account-file.json"
  ```

* On Windows, use the Command Prompt

  ```cmd
  set GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\your\service-account-file.json"
  ```

If you are using an IDE like IntelliJ IDEA, configure your run/debug configurations to include the `GOOGLE_APPLICATION_CREDENTIALS` environment variable.

1. Go to "Run" > "Edit Configurations..."
2. Select your run configuration or create a new one.
3. In the "Environment variables" field, add `GOOGLE_APPLICATION_CREDENTIALS` and set it to the path of your JSON key file.

Also, you need to enable a dependency in `build.gradle`:

```groovy
dependencies {
    // Other dependencies
    implementation 'com.google.cloud:google-cloud-translate:2.41.0'
}

```



## 5. Road Map

### (1) Game Record



We want new developers to help us enhance the game record function. Currently, it only shows the number of wins and losses for a player. We want to improve this function to include the exact time of each game, the users involved, their roles, and the entire game dialog.



### (2) Word Filter 



We want new developers to introduce a word filter API to prevent inappropriate words from being used.



### (3) Voice Message 



We want new developers to enable voice messages to be delivered among players. The corresponding voice messages should be translated according to the recipient player's language.



## 6.  Authors and acknowledgment



| Name               | Email                                 | Student Number | GitHub     |
| ------------------ | ------------------------------------- | -------------- | ---------- |
| SacramentoJoao     | joaofilipe.sacramentodealmeida@uzh.ch | 20-347-241     | Js18x      |
| Nico Plüss         | nico.pluess@uzh.ch                    | 18-915-256     | Elekidding |
| Alisher Dauysbekov | alisher.dauysbekov@uzh.ch             | 23-751-142     | alishddd   |
| Aiqi Shuai         | aiqi.shuai@uzh.ch                     | 23-732-514     | MrRunShu   |
| Kei Murakami       | kei.murakami@uzh.ch                   | 22-735-963     | kmkm0113   |





