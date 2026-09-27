# Personal Finance & Portfolio Tracker

A lightweight, robust desktop application developed in Java to track personal expenses, manage asset portfolios (stocks, commodities, crypto, foreign currencies), calculate profit/loss, and analyze financial health locally.

Designed with clean architecture principles (MVC + Repository Pattern) and built using Java, SQLite, and JavaFX.

## Features

![Demo](demo.gif)

Transaction Management: Record daily expenses, asset purchases, and sales with dedicated transaction types (BUY, SELL, EXPENSE).

Portfolio Analytics:
- Real-time tracking of asset holding quantities.
- Automatic calculation of Weighted Average Cost.
- Realized and unrealized Profit & Loss (PnL) computation with percentage return indicators.

Categorization: Modular grouping for budget management (Food, Transportation, Bills, Housing, etc.) and investment asset classes.

Local & Offline Storage: Embedded SQLite database engine requiring zero external configuration.

Clean Architecture: Separation of concerns between Data Models, Repository abstraction layers, Business/Financial Services, and UI Controllers.

## Architecture & Project Structure

The project follows a layered architecture to keep business logic isolated from database operations and the presentation layer:

src/
├── main/
│   ├── java/
│   │   ├── model/          # Domain entities (Transaction, PortfolioItem, Category, Enums)
│   │   ├── repository/     # Data access layer & SQLite connection manager
│   │   ├── service/        # Financial logic, validation rules, and analytics
│   │   └── ui/             # JavaFX views, FXML layouts, and controllers

## Data Flow Architecture

The data flows cleanly between the application layers without tight coupling:
1. **UI Layer (JavaFX)**: Collects user inputs (e.g. buying a stock, adding an expense).
2. **Service Layer**: 
    - `ValidationService` ensures rules are met.
    - `PortfolioService` & `ExpenseService` apply business rules, calculate weighted averages and realized PnL.
3. **Repository Layer**: `SqliteTransactionRepository` talks to the embedded SQLite database via JDBC, reading and persisting `Transaction` records.
4. **Data Models**: Pure POJOs like `Transaction` serve as the data carriers between all these layers.

## Tech Stack & Prerequisites

Language: Java 17+
Build Tool: Apache Maven
Database: SQLite (via org.xerial:sqlite-jdbc)
GUI Framework: JavaFX & Scene Builder
Testing: JUnit 5

## Getting Started

1. Clone the repository
`git clone https://github.com/ThisTugi/personal-finance-tracker`
`cd TradeApp`

2. Build the project with Maven
`mvn clean install`

3. Run the application
`mvn javafx:run`
(Or run the main entry point class from your IDE)

## License

This project is licensed under the MIT License - feel free to use and modify it.