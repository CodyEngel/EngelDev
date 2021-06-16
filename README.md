# EngelDev
This is the code that powers engel.dev

## Project Setup

### Front End
The front end is comprised of HTML, CSS, and JavaScript. Simply open `index.html` in a web browser and you are good to go.

### API
In order to deliver dynamic content, this project includes an API layer which is built using Kotin + ktor. For the best experience, download and install IntelliJ IDEA if you haven't done so already.

#### Environment Variables
To keep secrets, secret, and to enable switching between the real Google Cloud and the emulated one, there are a few environment variables to set:

* MAILCHIMP_KEY=YOUR-MAILCHIMP-API-KEY
* YOUTUBE_KEY=YOUR-YOUTUBE-DATA-API-KEY
* KTOR_ENVIRONMENT=local OR production

#### Dependencies
In order to run this project locally you will want to make use of Google Cloud Emulators. More specifically this project uses Google Cloud Datastore to cache YouTube information so you will want to follow the instructions for installing and running the [Datastore Emulator](https://cloud.google.com/datastore/docs/tools/datastore-emulator).

Once you have the emulator set-up, you can run the following command to start the emulator: `gcloud beta emulators datastore start --no-store-on-disk`

**Note:** the `no-store-on-disk` flag is used to avoid storing unnecessary data between development cycles.

## Building The Project
Building and running the project is as simple as clicking the run button in IntelliJ IDEA and refreshing the `index.html` page in your web browser. Moving towards production builds and executing the build without IntelliJ IDEA running involves a couple extra steps.

1. Build the API ktor application: `./gradlew installDist`
2. Build the Docker container: `docker build . -t api`
3. Run the Docker container: `docker run -p 8080:8080 api`
4. Send code to Google Cloud Build: `gcloud builds submit --tag gcr.io/YOUR-PROJECT-HERE/api .`