# Bank_of_CLI:
Welcome to the Bank of CLI project! In this project, you will build a functional banking application that runs entirely within the terminal. Your goal is to move from a simple user interface down to a persistent database, applying what you've learned about Java, SQL, and Agile development.

# MVP features:
Resposible for delvering a "Core Ledger" that supports:

    - Secure Access: user must be able to regsiter/log in using a unique ID AcoountID & PIN (SQL is needed for this obvi)

    - Balance Management: users check their current balance any time

    - Transcation Engine: 
            * deposit: add money
            * withdraw: remove money
            * transfer: move money

    - Audit Trail: can see recent transcation history

    - System Logging: must have a log-in file, must have these:
            * INFO: record sucessful actions (be logged in)
            * ERROR: record failed or secuirty risks (incorrect pin OR database connection lost)

# Architecure & Techincal Requirements:
To build a scaleable and maintainable application, must follow a layered archieture.

    - API Layer (interface): what user sees, handles all inputs, menu navigation and printing messages. this only talks to the servive layer.

    - Business Layer (brains): banking lives, it receives call from the API and calls the repository layer

    - Repository Layer (vault): handles ALL communications w/SQL database. it converts SQL rows into java objects, vise versa. ONLY recieves calls from business layer.

The Tech Stack:
    - Language: Java
    - Build Tool: Maven
    - Database: Postgres
    - Testing: JUnit 5
    - Verisoin Control: Git & GitHub

# Quality Standards: the "2 test rule":
every single method that is written in the service and repository layers, must provide 2 JUnit 5 tests:

    - The Positive Test: prove the methods works when everything goes right (like successful despoits)

    - The Negative Test: prove the method handles error correctly (like trying to withdraw more money than whats available)

# Professional implemenattion guidelines:
keep these "gold standards" in mind:

    - Atomicity (All or Nothing): when performing a Transfer, the decution from one account and the addition to another must happen in one single unit. If one part fails, the entire operation must fail so money doesnt "vanish"

    - Smart Error Handling:
            * User-Friendly Messages: if a user makes a mistake (like wrong PIN), show a clear and helpful message
            * Security First: if database crash, do NOT show user a scary techincal stack trace. instead, log the technical error and show the user a nice "Service unavailable" message