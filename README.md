# Multi-Instance Chat Application

This application is a multi-instance chat server where users can create new chats, join existing ones, and view previous chat history. Each server instance is assigned a port automatically, making it easy to run multiple instances simultaneously for testing.

## Features

- **Create a New Chat**: Start a new chat session with a unique ID.
- **Run Existing Chats**: Load and continue chatting in previously created chats.
- **Join a Chat**: Join any active chat session and view the chat history.

When joining a chat, previous chat messages are loaded automatically, providing context for ongoing conversations.

## Getting Started

### Prerequisites

- **PostgreSQL Database**: A PostgreSQL database must be set up before running the application. The required tables will be applied through migrations.
- **Kotlin Environment**: Ensure that you have Kotlin installed on your system.

### Setting Up the Database

1. Create a new PostgreSQL database for the chat application and add the credentials to .env file.

### Running the Application

To start the application:

1. Clone the repository:
   ```bash
   git clonehttps://github.com/l1ah1m/TCKApplication.git
   cd TCKApplication

2. Edit the .env file to add the credentials for the database.
3. Run multiple instances of the app for testing
4. Enjoy the show

