# Binance Trade Tracker

Trades and profits tracking for all daytrades and bots in one easy-to-use web front end. Just import all your trades from Binance, add your partial trades together and calculates the money you made daily or overall. More features are planned (See TODO-s). This App is build using Spring Boot, Thymeleaf and Bootstrap. Trade Tracker uses the [webcerebrium binance api](https://github.com/webcerebrium/java-binance-api) and [Java CryptoCompare Unofficial API Client](https://github.com/jeffreytai/cryptocompare-java-api-wrapper) wrappers.
![Profits Page](/img/profit_screenshot.png?raw=true "Profits Page Screenshot")

## Getting Started

First download and install [mariadb](https://mariadb.org/), if you haven't already. Create a new user and start it up. Trade Tracker won't start without a running db instance.

Installing mariadb on linux and creating a new user:

```
sudo apt install mariadb-server
sudo service mysql stop
sudo mysqld_safe --skip-grant-tables --skip-networking &
mysql -u root
use mysql;
update user set password=PASSWORD("new_password_here") where User='root' and Host ='localhost';
update user set plugin="mysql_native_password";
Ctrl-C
sudo service mysql restart
```

On Windows install HeidiSQL for easy access.

Alternatively: Binance Trade Tracker will also work with other SQL DB-s but you need to change the `pom.xml` and `application.properties` files accordingly.

### Installing

Easiest way to install Trade Tracker is to clone the repo and build from source. If you are not familiar with Java and Maven or don't want to build from source code yourself, just download the release [compiled jar](https://github.com/Arghh/binance-trade-tracker/releases) and run that. You will still need to create and fill out the `app.properties` file and place it in the same folder as the jar.

#### Application.properties

#### MANDATORY FIELDS:

* __spring.datasource.username, password__ - Your user and pw from mariaDB that you created.
* __server.port__ - on what port would you like to access the App.

```
server.port=50001 => localhost:50001
```
* __TradeHistory__ - In order to import the trades you need to add the full path to the location of your `TradesHistory.xlsx` spreadsheet into `tradeHistory.path=`.

For example Windows:

```
C:/Users/Argh/Downloads/TradeHistory.xlsx
```

For example Linux:

```
home/Argh/Downloads/TradeHistory.xlsx
```

* __Prefered fiat__ - In order to see profits in $ or € set the `preferedFiat=` to currency of your choice.

Currently supported values are: `EUR` and `USD`.



#### OPTIONAL FIELDS:
* BINANCE_API_KEY - Please make sure you only use a READ-ONLY key.

__Note:__ The API trade import is not yet ready.


### Deployment

In order to run Binance Trade Tracker from the jar just navigate to the folder where you downloaded it and run:

```
java -jar binance-profit-tracker-0.0.5-SNAPSHOT.jar --spring.config.location="app.properties"
```

## Todos

* Error handling
* Proper logging
* integration- and unit-tests
* performance

### Special Thanks

[Jeffery](https://github.com/jeffreytai) for uploading hes project to maven central.

Everyone who has given feedback.

## Digital tip jar

ETH: 0xBeA6fC790Ad1171132fC257aA8173dA20847eB92

XLM: GBJUUZKDU3PIPMX6LPS34RVAL7EBBSBP2EWU45LO4Z5PH3WFBJ55HOWA

## License

MIT