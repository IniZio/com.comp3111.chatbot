# Comp3111 Chatbot
[![Build Status](https://travis-ci.com/IniZio/com.comp3111.chatbot.svg?token=K1jYpqfP5ByUHboVHqqS&branch=master)](https://travis-ci.com/IniZio/com.comp3111.chatbot)

Chatbot project for Comp 3111

## Localhost setup
1. Git clone
2. Build the project to generate JAR: `./gradlew build`
3. Fire up both server and database with [docker-ce](https://docs.docker.com/engine/installation/) and [docker-compose](https://docs.docker.com/compose/install/): `docker-compose up -build`
4. Run [ngrok](https://ngrok.com/download) `ngrok http 8080`
5. Copy and paste the ngrok https url to Line control panel (Line@ Manager -> Message API Settings -> LINE Developeres -> Edit -> Webhook URL)

## Heroku setup
1. Click [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/IniZio/com.comp3111.chatbot)
2. Fill in `LINE_BOT_CHANNEL_TOKEN` and `LINE_BOT_CHANNEL_SECRET`, continue and deploy the app
