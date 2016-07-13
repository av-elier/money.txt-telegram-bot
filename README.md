# money.txt-telegram-bot

Environment variables shoud contain something like this:
```
BOT_TOKEN               <your_bot_token>
DROPBOX_APP_KEY         <your_dropbox_key>
DROPBOX_APP_SECRET      <your_dropbox_secret>
DATABASE_URL            jdbc:postgresql://localhost:5432/postgres
DATABASE_PASS           moneytxt
DATABASE_USER           moneytxt
```

To run this you need:
- java8
- to add database with some user (env: `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASS`)
- to create a telegram bot (env: `DROPBOX_APP_KEY`)
- to create a dropbox app (env: `DROPBOX_APP_KEY`, `DROPBOX_APP_SECRET`)
- to create manually money.txt in dropbox app folder after connecting to dropbox (why?)
