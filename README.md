# json-compare-java

A simple Vaadin web application that compares two JSON objects and highlights the differences.

## To Build for Development
Build via Gradle wrapper:

`./gradlew vaadinBuildFrontend build`

## To Build for Production
`./gradlew clean build -Pvaadin.productionMode`

## To Run Locally
`./gradlew bootRun`

For more information on Vaadin projects built with Gradle:
https://vaadin.com/docs/latest/guide/start/gradle.

## To Deploy to Heroku
`heroku deploy:jar build/libs/json-compare-java-1.0.0-RELEASE.jar --app appName`

To follow the app logs on the Heroku deployment:

`heroku logs --tail -a appName`

For more information on deploying a Vaadin applicatioin to Heroku: 
https://vaadin.com/learn/tutorials/cloud-deployment/heroku
