# Library Management System (Java, CLI, In-Memory)

This project is a **Library Management System** implemented in **Java** using **OOP**, **SOLID principles**, Java **collections**, and two design patterns (**Strategy** + **Factory**).

## Features
- **Book management**: add, update, remove, list, search (title/author/ISBN)
- **Patron management**: add, update, list, view borrowing history
- **Recommendation system**: suggests books using borrowing history (implicit preferences)
- **Lending**: checkout/return books with **due date** and **borrow limit**
- **Reservations**: patrons can reserve books that are currently checked out (FIFO queue per ISBN)
- **Notifications**: patron gets a notification when a reserved book becomes available (after return)
- **Inventory tracking (derived)**: available vs borrowed based on active loans
- **Logging**: important actions and errors are logged via `java.util.logging`

## Design highlights
- **Single-copy per ISBN**: a book is either available or borrowed.
- **Inventory derived (no Inventory class)**:
  - Available = book exists AND no active loan
  - Borrowed = book exists AND has active loan (`returnDate == null`)
- **Repositories (in-memory)**: `Map`-backed storage (`BookRepository`, `PatronRepository`, `LoanRepository`)

### Design patterns
- **Strategy**: `BookSearchStrategy` (`search` package) for title/author/ISBN search.
- **Factory**: `LoanFactory` encapsulates how `Loan` objects are created.

## Package structure
Base: `src/main/java/com/airtribe/librarymanagement`
- `entity` (Book, Patron, Loan)
- `repository` / `repository.impl` (interfaces + in-memory implementations)
- `service` (BookService, PatronService, LendingService)
- `policy` (LendingPolicy)
- `search` / `search.impl` (Strategy)
- `factory` (LoanFactory)
- `exception` (custom exceptions)
- `cli` (LibraryCli)
- `util` (validators, id generator)

## How to use the menu-driven system (CLI)
Start the app and follow the prompts.

### Main menu
- `1) Book management`
- `2) Patron management`
- `3) Lending`
- `q) Quit`

### Book management menu
- `1) Add book` (ISBN, title, author, publication year)
- `2) Update book`
- `3) Remove book` (blocked if the book is currently borrowed)
- `4) Search books` (by Title/Author/ISBN)
- `5) List all books`
- `b) Back`

### Patron management menu
- `1) Add patron` (patronId, name, contact)
- `2) Update patron`
- `3) List all patrons`
- `4) View patron borrow history`
- `5) Get recommendations` (uses borrowing history)
- `6) View my reservations`
- `7) View my notifications`
- `b) Back`

### Lending menu
- `1) Checkout book` (enforces availability + borrow limit + reservation queue)
- `2) Return book` (returns and triggers reservation notification if queue exists)
- `3) Reserve book (only if checked out)` (FIFO queue per ISBN)
- `4) List active loans (borrowed books)`
- `5) List available books`
- `b) Back`

### Example: reservation + notification flow
1. Add a book `B1`
2. Add patrons `P1` and `P2`
3. `P1` checks out `B1`
4. `P2` reserves `B1` (allowed because it is checked out)
5. `P1` returns `B1`
6. `P2` opens “View my notifications” and sees a message that `B1` is available

## How to run (PowerShell, no Maven needed)
From the project root:

```powershell
# Compile
javac -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })

# Run
java -cp out com.airtribe.librarymanagement.LibraryManagementApplication
```

## Notes / Defaults
- Default `LendingPolicy` is currently set to **max 3 books** and **14 days** (see `LibraryManagementApplication`).

## Class diagram

![Class diagram](LibraryManagementSystem.png)

The class diagram image file `LibraryManagementSystem.png` is stored at the **project root** (same level as `README.md`) and can be accessed from there.