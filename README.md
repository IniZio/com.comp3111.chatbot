# Comp3111 Chatbot
[![Build Status](https://travis-ci.com/IniZio/com.comp3111.chatbot.svg?token=K1jYpqfP5ByUHboVHqqS&branch=master)](https://travis-ci.com/IniZio/com.comp3111.chatbot)

Chatbot project for Comp 3111

## Localhost setup
1. Git clone
2. Fire up both server and database with [docker-ce](https://docs.docker.com/engine/installation/) and [docker-compose](https://docs.docker.com/compose/install/): `./gradlew build && docker-compose up -build`
3. Run [ngrok](https://ngrok.com/download) `ngrok http 8080`
4. Copy and paste the ngrok https url + '/callback' to Line control panel (Line@ Manager -> Message API Settings -> LINE Developeres -> Edit -> Webhook URL)

## Heroku setup
1. Click [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/IniZio/com.comp3111.chatbot)
2. Fill in `LINE_BOT_CHANNEL_TOKEN` and `LINE_BOT_CHANNEL_SECRET`, continue and deploy the app
